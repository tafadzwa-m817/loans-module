package zw.co.afrosoft.zdf.project.statuslog;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.project.ProjectStatusLogs;

import java.util.ArrayList;
import java.util.List;


public class ProjectStatusLogsSpecification {
    public static Specification<ProjectStatusLogs> getProperties(Long projectId, ProjectStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (projectId != null) {
                predicates.add(criteriaBuilder.equal(root.get("project").get("id"), projectId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
