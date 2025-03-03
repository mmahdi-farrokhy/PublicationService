package mmf.publication.app.service;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.entity.Publication;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import mmf.publication.app.exceptions.PublicationNotFoundException;
import mmf.publication.app.repository.PublicationRepository;
import mmf.publication.app.specifications.PublicationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublicationService implements IPublicationService {
    private final PublicationRepository publicationRepository;

    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @Override
    public Page<PublicationDTO> getPublications(String search, PublicationStatus status, PublicationType type,
                                                LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Start with an empty specification
        Specification<Publication> spec = Specification.where(null);

        // Add each filter only if the parameter is provided
        if (search != null && !search.isEmpty()) {
            spec = spec.and(PublicationSpecification.hasTitleOrDescriptionContaining(search));
        }

        if (status != null) {
            spec = spec.and(PublicationSpecification.hasStatus(status));
        }

        if (type != null) {
            spec = spec.and(PublicationSpecification.hasType(type));
        }

        if (startDate != null || endDate != null) {
            spec = spec.and(PublicationSpecification.publishedBetween(startDate, endDate));
        }

        return publicationRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    private PublicationDTO convertToDTO(Publication publication) {
        return new PublicationDTO(
                publication.getId(),
                publication.getTitle(),
                publication.getDescription(),
                publication.getViewCount(),
                publication.getPublishedAt(),
                publication.getUpdatedAt(),
                publication.getType(),
                publication.getStatus(),
                publication.getFrequentWords()
        );
    }

    @Override
    public PublicationDTO createPublication(PublicationRequest request) {
        String description = request.getDescription();
        String title = request.getTitle();
        PublicationType type = request.getType();
        PublicationStatus status = request.getStatus();

        Publication publication = new Publication();
        publication.setTitle(title);
        publication.setDescription(description);
        publication.setType(type);
        publication.setStatus(status);
        publication.setPublishedAt(LocalDateTime.now());
        publication.setUpdatedAt(LocalDateTime.now());
        publication.setViewCount(0);

        Optional<Map<String, Integer>> frequentWordsOfPublication = findFrequentWordsOfPublication(description);
        frequentWordsOfPublication.ifPresent(publication::setFrequentWords);

        Publication savedPublication = publicationRepository.save(publication);
        return convertToDTO(savedPublication);
    }

    @Override
    public Optional<PublicationDTO> updatePublication(Long id, PublicationRequest request) {
        Optional<Publication> publicationById = publicationRepository.findById(id);

        if (publicationById.isPresent()) {
            String description = request.getDescription();
            String title = request.getTitle();
            PublicationType type = request.getType();
            PublicationStatus status = request.getStatus();

            Publication publication = publicationById.get();
            publication.setTitle(title);
            publication.setDescription(description);
            publication.setType(type);
            publication.setStatus(status);

            Optional<Map<String, Integer>> frequentWordsOfPublication = findFrequentWordsOfPublication(description);
            frequentWordsOfPublication.ifPresent(publication::setFrequentWords);

            Publication updatedPublication = publicationRepository.save(publication);
            return Optional.of(convertToDTO(updatedPublication));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PublicationDTO> updatePublicationStatus(Long id, PublicationStatus status) {
        Optional<Publication> publicationById = publicationRepository.findById(id);
        if (publicationById.isPresent()) {
            Publication publication = publicationById.get();
            publication.setStatus(status);
            Publication updatedPublication = publicationRepository.save(publication);
            return Optional.of(convertToDTO(updatedPublication));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void incrementViewCount(Long id) {
        publicationRepository.findById(id).ifPresent(publication -> {
            publication.incrementViewCount();
            publication.setUpdatedAt(LocalDateTime.now());
            publicationRepository.save(publication);
        });
    }

    @Override
    public Optional<Map<String, Integer>> findFrequentWordsOfPublication(String publicationDescription) {
        if (publicationDescription == null || publicationDescription.isEmpty()) {
            return Optional.of(Collections.emptyMap());
        }

        publicationDescription = publicationDescription.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
        String[] words = publicationDescription.split("\\s+");

        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 5) {
                minHeap.poll();
            }
        }

        Map<String, Integer> frequentWords = minHeap.stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return Optional.of(frequentWords);
    }

    @Override
    public PublicationDTO getPublication(Long id) throws PublicationNotFoundException {
        Optional<Publication> byId = publicationRepository.findById(id);
        if (byId.isPresent()) {
            return convertToDTO(byId.get());
        } else {
            throw new PublicationNotFoundException("Wrong ID!");
        }
    }
}
