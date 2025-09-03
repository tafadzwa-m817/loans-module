package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.interest.InterestResponse;

@Aspect
@Service
@RequiredArgsConstructor
public class InterestTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.interest.InterestApplicationService.applyInterest(..))")
    public void interestApply() {}

    @AfterReturning(value = "interestApply()", returning = "response")
    public void afterInterestApply(InterestResponse response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.INTEREST_APPLIED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.INTEREST_APPLIED))
                        .username(response.getInterestRunBy())
                        .description(UserAction.INTEREST_APPLIED.formatMessage(
                                response.getNumberOfLoansAffected(),
                                response.getNumberOfProjectsAffected(),
                                response.getNumberOfSubscriptionsAffected(),
                                response.getInterestRunBy(),
                                response.getInterestRunAt().toString()
                        ))
                        .build()
        );
    }
}
