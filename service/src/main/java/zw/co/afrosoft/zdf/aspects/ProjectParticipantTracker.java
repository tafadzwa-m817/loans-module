package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.audit.UserAction;
import zw.co.afrosoft.zdf.project.ProjectParticipant;
import zw.co.afrosoft.zdf.subscription.dto.PaymentBulkUploadResponse;

@Slf4j
@Aspect
@Service
@RequiredArgsConstructor
public class ProjectParticipantTracker {

    private final AspectCommons auditCommons;

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectParticipantService.addParticipant(..))")
    public void participantAdd() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectParticipantService.updateParticipant(..))")
    public void participantUpdate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectParticipantService.updateStatus(..))")
    public void participantStatusUpdate() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectParticipantService.uploadParticipantsExcel(..))")
    public void participantBulkUpload() {}

    @Pointcut("execution(* zw.co.afrosoft.zdf.project.ProjectParticipantService.removeProjectParticipant(..))")
    public void participantRemove() {}

    @AfterReturning(value = "participantAdd()", returning = "response")
    public void afterParticipantAdd(ProjectParticipant response) {
        log.info("Tracking beneficiary addition: {} to project {}", response.getMember().getForceNumber(), response.getProject().getSerialNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_BENEFICIARY_ADDED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_BENEFICIARY_ADDED))
                        .username(response.getAudit().getCreatedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_BENEFICIARY_ADDED.formatMessage(
                                response.getProject().getProjectName(),
                                response.getProject().getSerialNumber()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "participantRemove()", returning = "response")
    public void afterParticipantRemove(ProjectParticipant response) {
        log.info("Tracking beneficiary removal: {} from project {}", response.getMember().getForceNumber(), response.getProject().getSerialNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_BENEFICIARY_REMOVED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_BENEFICIARY_REMOVED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_BENEFICIARY_REMOVED.formatMessage(
                                response.getMember().getForceNumber(),
                                response.getProject().getSerialNumber()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "participantUpdate()", returning = "response")
    public void afterParticipantUpdate(ProjectParticipant response) {
        log.info("Tracking beneficiary update: {} in project {}", response.getMember().getForceNumber(), response.getProject().getSerialNumber());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_BENEFICIARY_UPDATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_BENEFICIARY_UPDATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_BENEFICIARY_UPDATED.formatMessage(
                                response.getMember().getForceNumber(),
                                response.getProject().getSerialNumber()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "participantStatusUpdate()", returning = "response")
    public void afterParticipantStatusUpdate(ProjectParticipant response) {
        log.info("Tracking beneficiary status update: {} to {}", response.getMember().getForceNumber(), response.getStatus());
        auditCommons.createAudit(
                AuditTrailRequest.builder()
                        .userAction(UserAction.PROJECT_BENEFICIARY_STATUS_UPDATED.name())
                        .isLoans(UserAction.isLoansAction(UserAction.PROJECT_BENEFICIARY_STATUS_UPDATED))
                        .username(response.getAudit().getModifiedBy())
                        .ipAddress(response.getAudit().getIpAddress())
                        .description(UserAction.PROJECT_BENEFICIARY_STATUS_UPDATED.formatMessage(
                                response.getMember().getForceNumber(),
                                response.getStatus()
                        ))
                        .build()
        );
    }

    @AfterReturning(value = "participantBulkUpload()", returning = "response")
    public void afterParticipantBulkUpload(PaymentBulkUploadResponse response) {
        if (!response.uploads().isEmpty()) {
            log.info("Tracking bulk beneficiary upload: {} records", response.uploads().size());
            auditCommons.createAudit(
                    AuditTrailRequest.builder()
                            .userAction(UserAction.PROJECT_BENEFICIARY_BULK_UPLOAD.name())
                            .isLoans(UserAction.isLoansAction(UserAction.PROJECT_BENEFICIARY_BULK_UPLOAD))
                            .username(response.audit().getCreatedBy())
                            .ipAddress(response.audit().getIpAddress())
                            .description(UserAction.PROJECT_BENEFICIARY_BULK_UPLOAD.formatMessage(
                                    response.action(),
                                    response.uploads().size()
                            ))
                            .build()
            );
        }
    }
}
