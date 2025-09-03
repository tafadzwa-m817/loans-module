package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Thrown when an invalid or unsupported transaction type is encountered.
 *
 * <p>This exception results in an HTTP 400 (Bad Request) response.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectTransactionTypeException extends RuntimeException {

    /**
     * Constructs a new IncorrectTransactionTypeException with the specified detail message.
     *
     * @param message a description of the error
     */
    public IncorrectTransactionTypeException(String message) {super(message);}
}
