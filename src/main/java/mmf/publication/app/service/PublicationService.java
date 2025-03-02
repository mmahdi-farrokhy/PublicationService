package mmf.publication.app.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PublicationService implements IPublicationService {
    private final PublicationRepository publicationRepository;

    @Override
    public Page<PublicationDTO> getPublications(String search, PublicationStatus status, PublicationType type, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Specification<Publication> spec = Specification
                .where(PublicationSpecification.hasTitleOrDescriptionContaining(search))
                .and(PublicationSpecification.hasStatus(status))
                .and(PublicationSpecification.publishedBetween(startDate, endDate))
                .and(PublicationSpecification.hasType(type));

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
        publication.setPublishedAt(LocalDateTime.now());
        publication.setUpdatedAt(LocalDateTime.now());
        publication.setStatus(PublicationStatus.ACTIVE);
        publication.setViewCount(0);
        Publication savedPublication = publicationRepository.save(publication);
        return convertToDTO(savedPublication);
    }
}
