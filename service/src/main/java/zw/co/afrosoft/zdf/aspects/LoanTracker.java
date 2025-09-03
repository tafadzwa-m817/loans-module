package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.dto.LoanResponseDto;
import zw.co.afrosoft.zdf.loans.Loan;

@Aspect
@Service
@RequiredArgsConstructor
public class LoanTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.loans.LoanService.createLoan(..))")
    public void loanCreate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.loans.LoanService.updateLoan(..))")
    public void loanUpdate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.loans.LoanService.closeLoan(..))")
    public void loanClose() {}

    @AfterReturning(value = "loanCreate()", returning = "response")
    public void afterLoanCreate(Loan response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.LOAN_CREATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.LOAN_CREATED))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.LOAN_CREATED.formatMessage(
                                response.getLoanNumber(),
                                response.getForceNumber(),
                                response.getLoanAdvance(),
                                response.getDuration()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "loanUpdate()", returning = "response")
    public void afterLoanUpdate(Loan response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.LOAN_UPDATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.LOAN_UPDATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.LOAN_UPDATED.formatMessage(
                                response.getLoanAdvance(),
                                response.getDuration()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "loanClose()", returning = "response")
    public void afterLoanClose(Loan response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.LOAN_CLOSED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.LOAN_CLOSED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.LOAN_CLOSED.formatMessage(
                                response.getLoanStatus()
                        ))
                        .build()
        );
    }
}
