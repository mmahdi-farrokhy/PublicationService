package mmf.publication.app.dto;

import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

import java.time.LocalDateTime;
import java.util.Map;

public class PublicationDTO {
    private Long id;
    private String title;
    private String description;
    private int viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private PublicationType type;
    private PublicationStatus status;
    private Map<String, Integer> frequentWords;

    public PublicationDTO(Long id, String title, String description, int viewCount, LocalDateTime publishedAt, LocalDateTime updatedAt, PublicationType type, PublicationStatus status, Map<String, Integer> frequentWords) {
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

    public PublicationDTO(String title, String description, int viewCount, LocalDateTime publishedAt, LocalDateTime updatedAt, PublicationType type, PublicationStatus status, Map<String, Integer> frequentWords) {
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getViewCount() {
        return viewCount;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public PublicationType getType() {
        return type;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public Map<String, Integer> getFrequentWords() {
        return frequentWords;
    }
}
