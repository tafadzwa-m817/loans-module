package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.member.Member;

@Aspect
@Service
@RequiredArgsConstructor
public class MemberTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.member.MemberService.registerMember(..))")
    public void memberRegister() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.member.MemberService.activateMember(..))")
    public void memberActivate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.member.MemberService.update(..))")
    public void memberUpdate() {}

    @AfterReturning(value = "memberRegister()", returning = "response")
    public void afterMemberRegister(Member response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.MEMBER_REGISTERED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.MEMBER_REGISTERED))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.MEMBER_REGISTERED.formatMessage(
                                response.getForceNumber(),
                                response.getPersonalDetails().getFirstName(),
                                response.getPersonalDetails().getLastName()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "memberActivate()", returning = "response")
    public void afterMemberActivate(Member response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.MEMBER_ACTIVATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.MEMBER_ACTIVATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.MEMBER_ACTIVATED.formatMessage(
                                response.getForceNumber()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "memberUpdate()", returning = "response")
    public void afterMemberUpdate(zw.co.afrosoft.zdf.member.MemberResponseDto response) {
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.MEMBER_UPDATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.MEMBER_UPDATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.MEMBER_UPDATED.formatMessage(
                                response.getForceNumber()
                        ))
                        .build()
        );
    }
}
