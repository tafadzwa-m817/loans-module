package zw.co.afrosoft.zdf.audit;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuditTrailRequest {
    private String ipAddress;
    private String userAction;
    private String description;
    private String username;
    private boolean isLoans;
}
