package mmf.publication.app.service;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import mmf.publication.app.exceptions.PublicationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface IPublicationService {
    PublicationDTO createPublication(PublicationRequest request);

    Page<PublicationDTO> getPublications(String search, PublicationStatus status,
                                         PublicationType type, LocalDateTime startDate,
                                         LocalDateTime endDate, Pageable pageable);

    PublicationDTO updatePublication(Long id, PublicationRequest request) throws PublicationNotFoundException;

    PublicationDTO updatePublicationStatus(Long id, PublicationStatus status) throws PublicationNotFoundException;


    void incrementViewCount(Long id);

    Map<String, Integer> findFrequentWordsOfPublication(String publicationDescription);

    PublicationDTO getPublication(Long id) throws PublicationNotFoundException;
}