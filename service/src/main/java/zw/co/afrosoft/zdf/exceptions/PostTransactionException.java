package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when an error occurs during the posting of a transaction.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PostTransactionException extends RuntimeException {

    /**
     * Constructs a new PostTransactionException with the specified detail message.
     *
     * @param message detailed error message indicating the transaction post failure
     */
    public PostTransactionException(String message) {
        super(message);
    }
}

