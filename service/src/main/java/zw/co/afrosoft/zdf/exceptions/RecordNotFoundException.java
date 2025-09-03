package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;




/**
 * Exception thrown when a requested record is not found in the system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {

    /**
     * Constructs a new RecordNotFoundException with the specified detail message.
     *
     * @param message detailed error message indicating what record was not found
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
}

