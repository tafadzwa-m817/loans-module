package zw.co.afrosoft.zdf.member.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import zw.co.afrosoft.zdf.member.MemberStatus;



@NoArgsConstructor
@Data
@Schema(name = "MemberFilterRequest", description = "Request object for filtering members")
public class MemberFilterRequest {

    @Schema(description = "Member's first name", example = "John", nullable = true)
    private String firstName;

    @Schema(description = "Member's last name", example = "Doe", nullable = true)
    private String lastName;

    @Schema(description = "Member's force number", example = "F12345", nullable = true)
    private String forceNumber;

    @Schema(description = "Member's membership number", example = "M67890", nullable = true)
    private String membershipNumber;

    @Schema(description = "Status of the member", nullable = true)
    private MemberStatus memberStatus;
}

