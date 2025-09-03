package zw.co.afrosoft.zdf.member;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;



@Data
@Schema(description = "Request payload for registering a new member")
public class MemberRegistrationRequest {

    @Schema(description = "Type of military service", example = "ZIMBABWE_NATIONAL_ARMY")
    private ServiceType serviceType;

    @Schema(description = "Current force number of the member", example = "123456")
    private String forceNumber;

    @Schema(description = "Previous force number if any", example = "654321")
    private String prevForceNumber;

    @Schema(description = "ID of the member's rank", example = "3")
    private Long rankID;

    @Schema(description = "ID of the member's unit", example = "5")
    private Long unitID;

    @Schema(description = "Date the member was attested", example = "2010-05-20")
    private LocalDate dateOfAttestation;

    @Schema(description = "Date the member joined the scheme", example = "2011-01-01")
    private LocalDate membershipDate;

    @Schema(description = "Gross salary of the member", example = "1200.00")
    private Double grossSalary;

    @Schema(description = "Net salary of the member", example = "900.00")
    private Double netSalary;

    @Schema(description = "Monthly tax deductions for the member", example = "100.00")
    private Double tax;

    @Schema(description = "Personal details of the member")
    private PersonalDetails personalDetails;
}
