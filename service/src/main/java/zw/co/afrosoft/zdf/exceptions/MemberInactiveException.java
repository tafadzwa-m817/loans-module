package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when an operation is attempted on an inactive member.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MemberInactiveException extends RuntimeException {

    /**
     * Constructs a new MemberInactiveException with the specified detail message.
     *
     * @param message detailed error message explaining that the member is inactive
     */
    public MemberInactiveException(String message) {
        super(message);
    }
}
