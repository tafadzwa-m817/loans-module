package zw.co.afrosoft.zdf.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import zw.co.afrosoft.zdf.audit.AuditTrailRequest;
import zw.co.afrosoft.zdf.feign.clients.InvestmentsModuleClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AspectCommons {

    private final InvestmentsModuleClient auditService;

    public void createAudit(AuditTrailRequest request) {
        try {
            log.info("Creating audit trail: {}", request.getUserAction());
            auditService.createAuditTrail(request);
        } catch (Exception e) {
            log.error("Error creating audit trail for {}: {}", request.getUserAction(), e.getMessage());
        }
    }
}
