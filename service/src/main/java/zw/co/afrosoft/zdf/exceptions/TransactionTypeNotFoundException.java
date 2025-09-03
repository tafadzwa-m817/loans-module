package zw.co.afrosoft.zdf.exceptions;

/**
 * Exception thrown when a transaction type cannot be found
 */
/**
 * Exception thrown when a specified transaction type is not found in the system.
 */
public class TransactionTypeNotFoundException extends RuntimeException {

    /**
     * Constructs a new TransactionTypeNotFoundException with the specified detail message.
     *
     * @param message detailed error message indicating the missing transaction type
     */
    public TransactionTypeNotFoundException(String message) {
        super(message);
    }
}
