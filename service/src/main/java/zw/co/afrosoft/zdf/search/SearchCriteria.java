package zw.co.afrosoft.zdf.search;

import lombok.Getter;
import lombok.ToString;




/**
 * Represents a search criterion with a key, an operation, and a value.
 */
@Getter
@ToString
public class SearchCriteria {
    private final String key;
    private final String operation;
    private final Object value;

    private SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    /**
     * Factory method to create a SearchCriteria instance.
     *
     * @param key       the field name to search on
     * @param operation the operation to apply (e.g. '=', '>', '<', etc.)
     * @param value     the value to compare against
     * @return a new SearchCriteria instance
     */
    public static SearchCriteria of(String key, String operation, Object value) {
        return new SearchCriteria(key, operation, value);
    }
}
