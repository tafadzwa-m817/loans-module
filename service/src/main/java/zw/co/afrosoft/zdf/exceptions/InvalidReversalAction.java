package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception thrown when an invalid reversal action is encountered.
 * This typically indicates that the reversal action provided is not supported
 * or does not match the expected logic for the given transaction context.
 *
 * <p>Returns HTTP 400 (Bad Request) when thrown.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidReversalAction extends RuntimeException {

    /**
     * Constructs a new InvalidReversalAction exception with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidReversalAction(String message) {
        super(message);
    }
}

