package zw.co.afrosoft.zdf.transaction;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.enums.PaymentType;

import java.util.ArrayList;
import java.util.List;


public class TransactionHistorySpecification {

    public static Specification<Transactions> getProperties( String forceNumber, List<PaymentType> paymentTypes,
                                                            Long currencyId, Long referenceId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (forceNumber != null && !forceNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("forceNumber")), "%" + forceNumber.toLowerCase() + "%"));
            }

            if (paymentTypes != null && !paymentTypes.isEmpty()) {
                predicates.add(root.get("paymentType").in(paymentTypes));
            }
            if (referenceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("referenceId"), referenceId));
            }
            if (currencyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("currencyId"),currencyId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
