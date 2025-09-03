package zw.co.afrosoft.zdf.member;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.search.SearchCriteria;

import java.util.Arrays;




public class MemberSpecification implements Specification<Member> {

    private final SearchCriteria searchCriteria;

    private MemberSpecification(SearchCriteria criteria) {
        this.searchCriteria = criteria;
    }

    public static MemberSpecification of(SearchCriteria criteria) {
        return new MemberSpecification(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var key = SearchCriteriaKey.fromValue(searchCriteria.getKey());
        var value = searchCriteria.getValue();

        return switch (key) {
            case FIRST_NAME -> cb.like(root.get("personalDetails").get("firstName"), "%" + value + "%");
            case LAST_NAME -> cb.like(root.get("personalDetails").get("lastName"), "%" + value + "%");
            case FORCE_NUMBER -> cb.like(root.get("forceNumber"), "%" + value + "%");
            case MEMBERSHIP_NUMBER -> cb.like(root.get("membershipNumber"), "%" + value + "%");
            case MEMBER_STATUS -> cb.equal(root.get("memberStatus"), value);
        };
    }

    @Getter
    public enum SearchCriteriaKey {
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        FORCE_NUMBER("forceNumber"),
        MEMBERSHIP_NUMBER("membershipNumber"),
        MEMBER_STATUS("memberStatus");

        private final String value;

        SearchCriteriaKey(String value) {
            this.value = value;
        }

        public static SearchCriteriaKey fromValue(String keyValue) {
            return Arrays.stream(values())
                    .filter(key -> key.value.equalsIgnoreCase(keyValue))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid search key: " + keyValue));
        }
    }
}

