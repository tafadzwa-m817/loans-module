package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when a claim is rejected due to validation or business rules.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClaimRejectedException extends RuntimeException {

    /**
     * Constructs a new ClaimRejectedException with the specified detail message.
     *
     * @param message detailed error message explaining why the claim was rejected
     */
    public ClaimRejectedException(String message) {
        super(message);
    }
}

