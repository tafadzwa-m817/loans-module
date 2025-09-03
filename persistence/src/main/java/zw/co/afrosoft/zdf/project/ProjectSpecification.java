package zw.co.afrosoft.zdf.project;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.enums.Category;
import zw.co.afrosoft.zdf.enums.ProjectStatus;

import java.util.ArrayList;
import java.util.List;


public class ProjectSpecification {

    public static Specification<Project> getProperties(Category categoryPerSqm, Long provinceId, Long townId,
                                                       String farm, String developer, ProjectStatus status,
                                                       String projectName, Long currencyId,String serialNumber) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (projectName != null && !projectName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("projectName")), "%" + projectName.toLowerCase() + "%"));
            }
            if (farm != null && !farm.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("farm")), "%" + farm.toLowerCase() + "%"));
            }
            if (developer != null && !developer.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("developer")), "%" + developer.toLowerCase() + "%"));
            }
            if (serialNumber != null && !serialNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("serialNumber")), "%" + serialNumber.toLowerCase() + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (categoryPerSqm != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryPerSqm"), categoryPerSqm));
            }
            if (provinceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("provinceId"), provinceId));
            }
            if (townId != null) {
                predicates.add(criteriaBuilder.equal(root.get("townId"), townId));
            }
            if (currencyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("currencyId"),currencyId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
