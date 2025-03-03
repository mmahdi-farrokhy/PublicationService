package mmf.publication.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public void incrementViewCount() {
        this.viewCount++;
    }

    public Map<String, Integer> findFrequentWords() {
        if (description == null || description.isEmpty()) {
            return Collections.emptyMap();
        }

        // تبدیل به حروف کوچک و حذف علائم نگارشی
        description = description.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");

        // تقسیم متن به کلمات
        String[] words = description.split("\\s+");

        // شمارش تعداد تکرار هر کلمه
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // استفاده از PriorityQueue (Min Heap) برای پیدا کردن 5 کلمه پرتکرار
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 5) {
                minHeap.poll(); // حذف کوچک‌ترین مقدار
            }
        }

        // انتقال 5 کلمه پرتکرار به LinkedHashMap (برای حفظ ترتیب)
        return minHeap.stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // مرتب‌سازی نزولی
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
