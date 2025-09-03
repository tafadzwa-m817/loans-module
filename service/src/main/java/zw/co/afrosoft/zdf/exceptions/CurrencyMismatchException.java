package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when there is a mismatch between expected and actual currency.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CurrencyMismatchException extends RuntimeException {

    /**
     * Constructs a new CurrencyMismatchException with the specified detail message.
     *
     * @param message detailed error message describing the currency mismatch
     */
    public CurrencyMismatchException(String message) {
        super(message);
    }
}

