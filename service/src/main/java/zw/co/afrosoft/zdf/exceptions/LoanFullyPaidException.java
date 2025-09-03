package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;




/**
 * Exception thrown when an operation is attempted on a loan that is already fully paid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoanFullyPaidException extends RuntimeException {

    /**
     * Constructs a new LoanFullyPaidException with the specified detail message.
     *
     * @param message detailed error message explaining that the loan is fully paid
     */
    public LoanFullyPaidException(String message) {
        super(message);
    }
}

