package zw.co.afrosoft.zdf.loans;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.project.ProjectStatusLogs;

import java.util.ArrayList;
import java.util.List;


public class LoanLogsSpecification {
    public static Specification<LoanStatusLogs> getProperties(Long loanId, LoanStatus loanStatus) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (loanId != null) {
                predicates.add(criteriaBuilder.equal(root.get("loan").get("id"), loanId));
            }

            if (loanStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("loanStatus"), loanStatus));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
