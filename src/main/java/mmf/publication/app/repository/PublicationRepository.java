package mmf.publication.app.repository;

import mmf.publication.app.entity.Publication;
import mmf.publication.app.enums.PublicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findAll(Pageable pageable);

    Page<Publication> findByStatus(PublicationStatus status, Pageable pageable);
}
