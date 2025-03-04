package mmf.publication.app.entity;

import jakarta.persistence.*;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "publication")
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

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Publication() {
    }

    public Publication(Long id, String title, String description, int viewCount, LocalDateTime publishedAt, LocalDateTime updatedAt, PublicationType type, PublicationStatus status, Map<String, Integer> frequentWords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.viewCount = viewCount;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.status = status;
        this.frequentWords = frequentWords;
    }

    public Publication(String title, String description, int viewCount, LocalDateTime publishedAt, LocalDateTime updatedAt, PublicationType type, PublicationStatus status, Map<String, Integer> frequentWords) {
        this.title = title;
        this.description = description;
        this.viewCount = viewCount;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.status = status;
        this.frequentWords = frequentWords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PublicationType getType() {
        return type;
    }

    public void setType(PublicationType type) {
        this.type = type;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public void setStatus(PublicationStatus status) {
        this.status = status;
    }

    public Map<String, Integer> getFrequentWords() {
        return frequentWords;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public void setFrequentWords(Map<String, Integer> frequentWords) {
        this.frequentWords = frequentWords;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
