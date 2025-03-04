package mmf.publication.app.controller;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import mmf.publication.app.exceptions.PublicationNotFoundException;
import mmf.publication.app.service.IPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    private final IPublicationService publicationService;

    @Autowired
    public PublicationController(IPublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable Long id) {
        try {
            PublicationDTO publication = publicationService.getPublication(id);
            return ResponseEntity.ok(publication);
        } catch (PublicationNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<PublicationDTO>> getPublications(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PublicationStatus status,
            @RequestParam(required = false) PublicationType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(publicationService.getPublications(search, status, type, startDate, endDate, pageable));
    }

    @PostMapping("/{username}")
    public ResponseEntity<PublicationDTO> createPublication(@RequestBody PublicationRequest request, @PathVariable String username) {
        return ResponseEntity.ok(publicationService.createPublication(request, username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublicationDTO> updatePublication(
            @PathVariable Long id, @RequestBody PublicationRequest request) {
        try {
            return ResponseEntity.ok(publicationService.updatePublication(id, request));
        } catch (PublicationNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PublicationDTO> updatePublicationStatus(
            @PathVariable Long id, @RequestParam PublicationStatus status) {
        try {
            return ResponseEntity.ok(publicationService.updatePublicationStatus(id, status));
        } catch (PublicationNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        publicationService.incrementViewCount(id);
        return ResponseEntity.noContent().build();
    }
}
