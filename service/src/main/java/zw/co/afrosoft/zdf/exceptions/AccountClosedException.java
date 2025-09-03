package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;




/**
 * Exception thrown when an operation is attempted on a closed account.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountClosedException extends RuntimeException {

    /**
     * Constructs a new AccountClosedException with the specified detail message.
     *
     * @param message detailed error message
     */
    public AccountClosedException(String message) {
        super(message);
    }
}

