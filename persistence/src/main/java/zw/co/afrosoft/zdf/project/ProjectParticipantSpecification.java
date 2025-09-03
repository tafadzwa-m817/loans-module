package zw.co.afrosoft.zdf.project;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;

import java.util.ArrayList;
import java.util.List;


public class ProjectParticipantSpecification {

    public static Specification<ProjectParticipant> getProperties(String fullName, String standNumber,
                                                                  String serialNumber, String forceNumber,
                                                                  Long projectId, ProjectParticipantStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (fullName != null && !fullName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%"));
            }
            if (standNumber != null && !standNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("standNumber")), "%" + standNumber.toLowerCase() + "%"));
            }
            if (serialNumber != null && !serialNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("serialNumber")), "%" + serialNumber.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (projectId != null) {
                predicates.add(criteriaBuilder.equal(root.get("project").get("id"), projectId));
            }

            if (forceNumber != null && !forceNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("member").get("forceNumber")), "%" + forceNumber.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
