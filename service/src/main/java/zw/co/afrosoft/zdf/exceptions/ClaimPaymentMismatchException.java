package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when there is a mismatch between claim and payment details.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClaimPaymentMismatchException extends RuntimeException {

    /**
     * Constructs a new ClaimPaymentMismatchException with the specified detail message.
     *
     * @param message detailed error message
     */
    public ClaimPaymentMismatchException(String message) {
        super(message);
    }
}

