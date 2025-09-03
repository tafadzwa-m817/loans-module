package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the provided execution date is invalid or not allowed.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidExecutionDateException extends RuntimeException {

    /**
     * Constructs a new InvalidExecutionDateException with the specified detail message.
     *
     * @param message detailed error message explaining why the execution date is invalid
     */
    public InvalidExecutionDateException(String message) {
        super(message);
    }
}

