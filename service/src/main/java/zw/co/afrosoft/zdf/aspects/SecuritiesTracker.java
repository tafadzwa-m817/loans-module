package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.entity.Securities;

@Slf4j
@Aspect
@Service
@RequiredArgsConstructor
public class SecuritiesTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.securities.SecuritiesService.paySecurity(..))")
    public void securityPayment() {}

    @AfterReturning(value = "securityPayment()", returning = "response")
    public void afterSecurityPayment(Securities response) {
        log.info("Tracking security payment for loan: {}", response.getLoanNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.SECURITY_PAYMENT_MADE.name())
                        .isLoans(UserAction.isLoansAction(UserAction.SECURITY_PAYMENT_MADE))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.SECURITY_PAYMENT_MADE.formatMessage(
                                response.getLoanNumber()
                        ))
                        .build()
        );
    }
}
