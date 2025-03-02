package mmf.publication.app.specifications;

import mmf.publication.app.entity.Publication;
import mmf.publication.app.enums.PublicationStatus;
import mmf.publication.app.enums.PublicationType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PublicationSpecification {
    public static Specification<Publication> hasTitleOrDescriptionContaining(String search) {
        return (root, query, criteriaBuilder) ->
                (search == null || search.isEmpty()) ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + search.toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                        );
    }

    public static Specification<Publication> hasStatus(PublicationStatus status) {
        return (root, query, criteriaBuilder) ->
                (status == null) ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Publication> hasType(PublicationType type) {
        return (root, query, criteriaBuilder) ->
                (type == null) ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Publication> publishedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            } else if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("publishedAt"), endDate);
            } else if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("publishedAt"), startDate);
            } else {
                return criteriaBuilder.between(root.get("publishedAt"), startDate, endDate);
            }
        };
    }
}
