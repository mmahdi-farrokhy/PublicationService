package mmf.publication.app.service;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IPublicationService {
    PublicationDTO createPublication(PublicationRequest request);

    Page<PublicationDTO> getPublications(String search, PublicationStatus status,
                                         PublicationType type, LocalDateTime startDate,
                                         LocalDateTime endDate, Pageable pageable);

    Optional<PublicationDTO> updatePublication(Long id, PublicationRequest request);

    Optional<PublicationDTO> togglePublicationStatus(Long id, PublicationStatus status);


    void incrementViewCount(Long id);
}