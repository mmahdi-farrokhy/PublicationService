package mmf.publication.app.dto;

import lombok.Data;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

@Data
public class PublicationRequest {
    private String title;
    private String description;
    private PublicationType type;
    private PublicationStatus status;
}
