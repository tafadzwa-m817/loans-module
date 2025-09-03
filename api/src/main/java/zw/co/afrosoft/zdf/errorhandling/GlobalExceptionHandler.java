package zw.co.afrosoft.zdf.errorhandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.exceptions.*;

import java.util.Map;
import java.util.stream.Collectors;



@SuppressWarnings("ALL")
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return ResponseEntity.badRequest().body(validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @ExceptionHandler({
            ClaimRejectedException.class,
            OutStandingLoanException.class,
            MemberInactiveException.class,
            LoanAlreadyOpenException.class,
            PostTransactionException.class,
            InvalidPaymentTypeException.class,
            InvalidExecutionDateException.class,
            InvalidTransactionCategoryException.class,
            InvalidDurationException.class,
            RecordNotFoundException.class,
            AccountClosedException.class,
            ClaimPaymentMismatchException.class,
            CurrencyMismatchException.class,
            IncorrectTransactionTypeException.class,
            InvalidReversalAction.class,
            IncorrectPaymentTypeException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TransactionTypeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(TransactionTypeNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(new ErrorResponseDto(status.value(), message), status);
    }
}
