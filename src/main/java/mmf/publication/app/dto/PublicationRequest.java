package mmf.publication.app.dto;

import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;

public class PublicationRequest {
    private String title;
    private String description;
    private PublicationType type;
    private PublicationStatus status;

    public PublicationRequest() {
    }

    public PublicationRequest(String title, String description, PublicationType type, PublicationStatus status) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
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
}
