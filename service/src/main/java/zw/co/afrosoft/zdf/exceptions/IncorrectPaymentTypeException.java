package zw.co.afrosoft.zdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Exception thrown when an incorrect payment type is provided during a transaction.
 * <p>
 * This exception results in an HTTP 400 (Bad Request) response.
 * </p>
 *
 * <p>Typical usage example:</p>
 * <pre>
 * if (!isValidPaymentType(paymentType)) {
 *     throw new IncorrectPaymentTypeException("Invalid payment type: " + paymentType);
 * }
 * </pre>
 *
 * @author [Your Name]
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectPaymentTypeException extends RuntimeException {

    /**
     * Constructs a new IncorrectPaymentTypeException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public IncorrectPaymentTypeException(String message) {
        super(message);
    }
}

