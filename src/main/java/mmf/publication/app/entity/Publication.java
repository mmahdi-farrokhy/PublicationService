package mmf.publication.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "publication")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationStatus status;

    @ElementCollection
    @CollectionTable(name = "publication_keywords", joinColumns = @JoinColumn(name = "publication_id"))
    @MapKeyColumn(name = "keyword")
    @Column(name = "frequency")
    private Map<String, Integer> frequentWords;

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
