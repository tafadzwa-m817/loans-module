package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.subscription.dto.PaymentBulkUploadResponse;
import zw.co.afrosoft.zdf.transaction.Transactions;
import zw.co.afrosoft.zdf.transaction.dto.TransactionHistory;

import java.time.LocalDate;

@Slf4j
@Aspect
@Service
@RequiredArgsConstructor
public class PaymentTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.payment.PaymentsService.makePayment(..))")
    public void paymentMake() {}
    @Pointcut("execution(* zw.co.afrosoft.zdf.payment.PaymentsService.paymentReversal(..))")
    public void paymentReversal() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.payment.PaymentsService.uploadPaymentExcel(..))")
    public void paymentBulkUpload() {}

    @AfterReturning(value = "paymentReversal()", returning = "response")
    public void afterPaymentReversal(Transactions response) {
        log.info("Tracking payment reversal made: {}", response.getPaymentType());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PAYMENT_MADE.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PAYMENT_MADE))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PAYMENT_MADE.formatMessage(
                                response.getPaymentType(),
                                response.getAmount(),
                                response.getForceNumber(),
                                response.getPaymentType()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "paymentMake()", returning = "response")
    public void afterPaymentMake(Transactions response) {
        log.info("Tracking payment made: {}", response.getPaymentType());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PAYMENT_MADE.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PAYMENT_MADE))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PAYMENT_MADE.formatMessage(
                                response.getPaymentType(),
                                response.getAmount(),
                                response.getForceNumber(),
                                response.getPaymentType()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "paymentBulkUpload()", returning = "response")
    public void afterPaymentBulkUpload(PaymentBulkUploadResponse response) {
        log.info("Tracking bulk payment upload: {}", response.action());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PAYMENT_BULK_UPLOAD.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PAYMENT_BULK_UPLOAD))
                        .username(response.audit().getCreatedBy())
                        .ipAddress(response.audit().getIpAddress())
                        .description(UserAction.PAYMENT_BULK_UPLOAD.formatMessage(
                                response.uploads().size(),
                                response.action(),
                                LocalDate.now()
                        ))
                        .build()
        );
    }
}
