package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;




/**
 * Exception thrown when an operation is attempted but no outstanding loan exists for the member.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoOutStandingLoanException extends RuntimeException {

    /**
     * Constructs a new NoOutStandingLoanException with the specified detail message.
     *
     * @param message detailed error message explaining that no outstanding loan was found
     */
    public NoOutStandingLoanException(String message) {
        super(message);
    }
}

