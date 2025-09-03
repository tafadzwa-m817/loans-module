package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when attempting to open a loan that is already open.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoanAlreadyOpenException extends RuntimeException {

    /**
     * Constructs a new LoanAlreadyOpenException with the specified detail message.
     *
     * @param message detailed error message explaining why the loan cannot be opened again
     */
    public LoanAlreadyOpenException(String message) {
        super(message);
    }
}

