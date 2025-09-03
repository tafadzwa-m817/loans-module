package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when the provided duration value is invalid or out of allowed range.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDurationException extends RuntimeException {

    /**
     * Constructs a new InvalidDurationException with the specified detail message.
     *
     * @param message detailed error message explaining why the duration is invalid
     */
    public InvalidDurationException(String message) {
        super(message);
    }
}

