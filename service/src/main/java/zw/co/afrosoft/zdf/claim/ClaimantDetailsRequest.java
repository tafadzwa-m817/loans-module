package zw.co.afrosoft.zdf.claim;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Email;

/*
  created by  Romeo Jerenyama
  created on  21/5/2025 at 10:55
 */

@Data
@Schema(description = "Details of the person submitting the claim")
public class ClaimantDetailsRequest {

    @Schema(description = "First name of the claimant", example = "John")
    private String claimantName;

    @Schema(description = "Surname of the claimant", example = "Doe")
    private String claimantSurname;

    @Schema(description = "National ID number of the claimant", example = "63-1234567Z00")
    private String claimantIdNumber;

    @Schema(description = "Phone number of the claimant", example = "(+263/0)771234567")
    private String claimantPhone;

    @Schema(description = "Physical address of the claimant", example = "123 Samora Machel Ave, Harare")
    private String claimantAddress;

    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the claimant", example = "john.doe@example.com")
    private String claimantEmail;
}
