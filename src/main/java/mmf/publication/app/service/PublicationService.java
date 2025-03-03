package mmf.publication.app.service;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.entity.Publication;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import mmf.publication.app.repository.PublicationRepository;
import mmf.publication.app.specifications.PublicationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

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
        Publication publication = new Publication();
        publication.setTitle(request.getTitle());
        publication.setDescription(request.getDescription());
        publication.setType(request.getType());
        publication.setStatus(request.getStatus());
        publication.setPublishedAt(LocalDateTime.now());
        publication.setUpdatedAt(LocalDateTime.now());
        publication.setViewCount(0);
        Publication savedPublication = publicationRepository.save(publication);
        return convertToDTO(savedPublication);
    }

    @Override
    public Optional<PublicationDTO> updatePublication(Long id, PublicationRequest request) {
        Optional<Publication> publicationById = publicationRepository.findById(id);

        if (publicationById.isPresent()) {
            Publication publication = publicationById.get();
            publication.setTitle(request.getTitle());
            publication.setDescription(request.getDescription());
            publication.setType(request.getType());
            publication.setStatus(request.getStatus());

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
    public Optional<Map<String, Integer>> findFrequentWordsOfPublication(Long id) {
        Optional<Publication> publicationById = publicationRepository.findById(id);

        if(publicationById.isPresent()){
            Publication publication = publicationById.get();
            Map<String, Integer> frequentWords = publication.findFrequentWords();
            return Optional.of(frequentWords);
        }
        else {
            return Optional.empty();
        }
    }
}
