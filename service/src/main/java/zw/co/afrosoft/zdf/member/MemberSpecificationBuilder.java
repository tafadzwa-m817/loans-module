package zw.co.afrosoft.zdf.member;

import org.springframework.data.jpa.domain.Specification;
import zw.co.afrosoft.zdf.search.SearchCriteria;

import java.util.ArrayList;
import java.util.List;



public class MemberSpecificationBuilder {

    private final List<SearchCriteria> params = new ArrayList<>();

    private MemberSpecificationBuilder() {
    }

    public static MemberSpecificationBuilder create() {
        return new MemberSpecificationBuilder();
    }

    /**
     * Add a search criterion with a key and value (operation is null)
     *
     * @param key   the field name to search on
     * @param value the value to filter by
     * @return this builder instance
     */
    public MemberSpecificationBuilder with(String key, Object value) {
        params.add(SearchCriteria.of(key, null, value));  // Use updated factory method 'of'
        return this;
    }

    /**
     * Build the combined Specification<Member> from the accumulated criteria.
     *
     * @return combined Specification or null if no criteria present
     */
    public Specification<Member> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<Member> result = MemberSpecification.of(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            Specification<Member> spec = MemberSpecification.of(params.get(i));
            result = Specification.where(result).and(spec);
        }

        return result;
    }
}

