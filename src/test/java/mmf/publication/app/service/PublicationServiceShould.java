package mmf.publication.app.service;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.entity.Publication;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import mmf.publication.app.repository.PublicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PublicationServiceShould {
    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private PublicationService publicationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_a_new_publication() {
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Test Title");
        request.setDescription("Test Description");
        request.setType(PublicationType.NEWS);
        request.setStatus(PublicationStatus.ACTIVE);

        Publication publication = new Publication();
        publication.setId(1L);
        publication.setTitle(request.getTitle());
        publication.setDescription(request.getDescription());
        publication.setType(request.getType());
        publication.setStatus(request.getStatus());
        publication.setPublishedAt(LocalDateTime.now());
        publication.setUpdatedAt(LocalDateTime.now());
        publication.setViewCount(0);

        when(publicationRepository.save(any(Publication.class))).thenReturn(publication);

        PublicationDTO result = publicationService.createPublication(request);
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(PublicationType.NEWS, result.getType());
        assertEquals(PublicationStatus.ACTIVE, result.getStatus());
    }

    @Test
    void update_an_existing_publication_in_database() {
        Long publicationId = 1L;
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setType(PublicationType.JOURNAL);
        request.setStatus(PublicationStatus.INACTIVE);

        Publication publication = new Publication();
        publication.setId(publicationId);
        publication.setTitle("Old Title");
        publication.setDescription("Old Description");
        publication.setType(PublicationType.NEWS);
        publication.setStatus(PublicationStatus.ACTIVE);
        publication.setUpdatedAt(LocalDateTime.now());

        when(publicationRepository.findById(publicationId)).thenReturn(Optional.of(publication));
        when(publicationRepository.save(any(Publication.class))).thenReturn(publication);

        Optional<PublicationDTO> reslut = publicationService.updatePublication(publicationId, request);

        assertTrue(reslut.isPresent());
        assertEquals("Updated Title", reslut.get().getTitle());
        assertEquals("Updated Description", reslut.get().getDescription());
        assertEquals(PublicationType.JOURNAL, reslut.get().getType());
        assertEquals(PublicationStatus.INACTIVE, reslut.get().getStatus());
    }

    @Test
    void return_empty_when_updating_a_non_existing_publication() {
        Long publicationId = 1L;
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setType(PublicationType.NEWS);
        request.setStatus(PublicationStatus.ACTIVE);

        when(publicationRepository.findById(publicationId)).thenReturn(Optional.empty());

        Optional<PublicationDTO> result = publicationService.updatePublication(publicationId, request);

        assertTrue(result.isEmpty());
    }

    @Test
    void increment_number_of_views_for_a_publication() {
        Long publicationId = 1L;

        Publication publication = new Publication();
        publication.setId(publicationId);
        publication.setViewCount(5);

        when(publicationRepository.findById(publicationId)).thenReturn(Optional.of(publication));

        publicationService.incrementViewCount(publicationId);

        assertEquals(6, publication.getViewCount());
        verify(publicationRepository, times(1)).save(publication);
    }

    @Test
    void get_publications_with_pagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Publication publication1 = new Publication();
        publication1.setId(1L);
        publication1.setTitle("Title 1");
        publication1.setDescription("Description 1");

        Publication publication2 = new Publication();
        publication2.setId(2L);
        publication2.setTitle("Title 2");
        publication2.setDescription("Description 2");

        List<Publication> publications = List.of(publication1, publication2);
        Page<Publication> page = new PageImpl<>(publications, pageable, publications.size());

        when(publicationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<PublicationDTO> result = publicationService.getPublications(null, null, null, null, null, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Title 1", result.getContent().get(0).getTitle());
        assertEquals("Title 2", result.getContent().get(1).getTitle());
    }
}
