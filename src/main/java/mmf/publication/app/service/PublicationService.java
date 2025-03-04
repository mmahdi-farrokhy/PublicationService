package mmf.publication.app.service;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.entity.AppUser;
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
    public static final int FREQUENT_WORDS_COUNT = 5;
    private final PublicationRepository publicationRepository;
    private final AppUserService appUserService;

    public PublicationService(PublicationRepository publicationRepository, AppUserService appUserService) {
        this.publicationRepository = publicationRepository;
        this.appUserService = appUserService;
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
    public PublicationDTO createPublication(PublicationRequest request, String username) {
        AppUser userFromDb = appUserService.findByUsername(username);


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
        publication.setAppUser(userFromDb);

        Map<String, Integer> frequentWordsOfPublication = findFrequentWordsOfPublication(description);
        publication.setFrequentWords(frequentWordsOfPublication);

        Publication savedPublication = publicationRepository.save(publication);
        return convertToDTO(savedPublication);
    }

    @Override
    public PublicationDTO updatePublication(Long id, PublicationRequest request) throws PublicationNotFoundException {
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

            Map<String, Integer> frequentWordsOfPublication = findFrequentWordsOfPublication(description);
            publication.setFrequentWords(frequentWordsOfPublication);

            Publication updatedPublication = publicationRepository.save(publication);
            return convertToDTO(updatedPublication);
        } else {
            throw new PublicationNotFoundException("Publication with id " + id + " does not exist");
        }
    }

    @Override
    public PublicationDTO updatePublicationStatus(Long id, PublicationStatus status) throws PublicationNotFoundException {
        Optional<Publication> publicationById = publicationRepository.findById(id);
        if (publicationById.isPresent()) {
            Publication publication = publicationById.get();
            publication.setStatus(status);
            Publication updatedPublication = publicationRepository.save(publication);
            return convertToDTO(updatedPublication);
        } else {
            throw new PublicationNotFoundException("Publication with id " + id + " does not exist");
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
    public Map<String, Integer> findFrequentWordsOfPublication(String publicationDescription) {
        if (isInvalidDescription(publicationDescription)) {
            return Collections.emptyMap();
        }

        String[] words = removeSpecialCharacters(publicationDescription).split("\\s+");

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

            if (minHeap.size() > FREQUENT_WORDS_COUNT) {
                minHeap.poll();
            }
        }

        return minHeap.stream()
                .sorted(IncrementalSort())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private static Comparator<Map.Entry<String, Integer>> IncrementalSort() {
        return (a, b) -> b.getValue().compareTo(a.getValue());
    }

    private static String removeSpecialCharacters(String publicationDescription) {
        publicationDescription = publicationDescription.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
        return publicationDescription;
    }

    private static boolean isInvalidDescription(String publicationDescription) {
        return publicationDescription == null || publicationDescription.isEmpty();
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
