package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;




/**
 * Exception thrown when a new operation is not permitted due to an existing outstanding loan.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutStandingLoanException extends RuntimeException {

    /**
     * Constructs a new OutStandingLoanException with the specified detail message.
     *
     * @param message detailed error message indicating an outstanding loan exists
     */
    public OutStandingLoanException(String message) {
        super(message);
    }
}

