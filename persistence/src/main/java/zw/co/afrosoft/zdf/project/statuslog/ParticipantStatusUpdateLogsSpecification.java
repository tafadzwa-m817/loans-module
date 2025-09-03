package zw.co.afrosoft.zdf.project.statuslog;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;
import zw.co.afrosoft.zdf.project.ProjectParticipantStatusLog;

import java.util.ArrayList;
import java.util.List;


public class ParticipantStatusUpdateLogsSpecification {
    public static Specification<ProjectParticipantStatusLog> getProperties(Long projectParticipationId, ProjectParticipantStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (projectParticipationId != null) {
                predicates.add(criteriaBuilder.equal(root.get("projectParticipant").get("id"), projectParticipationId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
