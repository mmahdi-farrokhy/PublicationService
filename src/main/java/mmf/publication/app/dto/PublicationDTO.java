package mmf.publication.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
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
}
