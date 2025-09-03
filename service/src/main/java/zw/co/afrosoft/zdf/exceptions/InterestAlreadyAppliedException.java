package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when interest has already been applied to the loan or account.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InterestAlreadyAppliedException extends RuntimeException {

    /**
     * Constructs a new InterestAlreadyAppliedException with the specified detail message.
     *
     * @param message detailed error message explaining the duplicate interest application
     */
    public InterestAlreadyAppliedException(String message) {
        super(message);
    }
}

