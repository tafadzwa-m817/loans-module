package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


 * created by  Romeo Jerenyama
 * created on  16/4/2025 at 16:16
 */

/**
 * Exception thrown when an invalid interest category is provided.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInterestCategoryException extends RuntimeException {

    /**
     * Constructs a new InvalidInterestCategoryException with the specified detail message.
     *
     * @param message detailed error message explaining the invalid interest category
     */
    public InvalidInterestCategoryException(String message) {
        super(message);
    }
}

