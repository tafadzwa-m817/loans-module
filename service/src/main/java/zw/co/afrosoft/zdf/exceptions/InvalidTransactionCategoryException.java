package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when an invalid transaction category is encountered.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTransactionCategoryException extends RuntimeException {

    /**
     * Constructs a new InvalidTransactionCategoryException with the specified detail message.
     *
     * @param message detailed error message explaining the invalid transaction category
     */
    public InvalidTransactionCategoryException(String message) {
        super(message);
    }
}

