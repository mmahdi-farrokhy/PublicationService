package mmf.publication.app.controller;

import mmf.publication.app.dto.PublicationDTO;
import mmf.publication.app.dto.PublicationRequest;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import mmf.publication.app.service.IPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    private final IPublicationService publicationService;

    @Autowired
    public PublicationController(IPublicationService publicationService) {
        this.publicationService = publicationService;
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

    @PostMapping
    public ResponseEntity<PublicationDTO> createPublication(@RequestBody PublicationRequest request) {
        return ResponseEntity.ok(publicationService.createPublication(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<PublicationDTO>> updatePublication(
            @PathVariable Long id, @RequestBody PublicationRequest request) {
        return ResponseEntity.ok(publicationService.updatePublication(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Optional<PublicationDTO>> updatePublicationStatus(
            @PathVariable Long id, @RequestParam PublicationStatus status) {
        return ResponseEntity.ok(publicationService.updatePublicationStatus(id, status));
    }

    @PatchMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        publicationService.incrementViewCount(id);
        return ResponseEntity.noContent().build();
    }
}
