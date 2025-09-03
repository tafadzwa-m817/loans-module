package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when an invalid payment type is provided.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPaymentTypeException extends RuntimeException {

    /**
     * Constructs a new InvalidPaymentTypeException with the specified detail message.
     *
     * @param message detailed error message explaining the invalid payment type
     */
    public InvalidPaymentTypeException(String message) {
        super(message);
    }
}

