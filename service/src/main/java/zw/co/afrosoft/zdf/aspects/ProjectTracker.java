package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.project.Project;

@Slf4j
@Aspect
@Service
@RequiredArgsConstructor
public class ProjectTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectService.create(..))")
    public void projectCreate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectService.update(..))")
    public void projectUpdate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectService.updateStatus(..))")
    public void projectStatusUpdate() {}

    @AfterReturning(value = "projectCreate()", returning = "response")
    public void afterProjectCreate(Project response) {
        log.info("Tracking project creation: {}", response.getSerialNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_CREATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_CREATED))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_CREATED.formatMessage(
                                response.getSerialNumber(),
                                response.getProjectName()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "projectUpdate()", returning = "response")
    public void afterProjectUpdate(Project response) {
        log.info("Tracking project update: {}", response.getSerialNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_UPDATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_UPDATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_UPDATED.formatMessage(
                                response.getSerialNumber(),
                                response.getProjectName()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "projectStatusUpdate()", returning = "response")
    public void afterProjectStatusUpdate(Project response) {
        log.info("Tracking project status update: {}", response.getSerialNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_STATUS_UPDATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_STATUS_UPDATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_STATUS_UPDATED.formatMessage(
                                response.getSerialNumber(),
                                response.getStatus()
                        ))
                        .build()
        );
    }
}
