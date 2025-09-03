package zw.co.afrosoft.zdf.subscription;

import lombok.Data;
import zw.co.afrosoft.zdf.member.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "Request object for filtering member details")
public class MemberDetailsRequest {

    @Schema(description = "First name of the member", example = "John")
    private String name;

    @Schema(description = "Surname of the member", example = "Doe")
    private String surname;

    @Schema(description = "Service type of the member (e.g. ZNA, AFZ)", example = "ZNA")
    private ServiceType serviceType;

    @Schema(description = "Account status of the member (e.g. ACTIVE, CLOSED)", example = "ACTIVE")
    private AccountStatus accountStatus;
}
