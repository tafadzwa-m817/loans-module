package zw.co.afrosoft.zdf.claim;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;




@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of the person making a claim")
public class ClaimantDetails {

    @Schema(description = "First name of the claimant", example = "Jane")
    private String claimantName;

    @Schema(description = "Surname of the claimant", example = "Moyo")
    private String claimantSurname;

    @Schema(
            description = "Zimbabwean National ID in correct format",
            example = "12-3456789X00"
    )
    private String claimantIdNumber;

    @Schema(description = "Claimant's phone number", example = "0772123456")
    private String claimantPhone;

    @Schema(description = "Residential address of the claimant", example = "123 Borrowdale Road, Harare")
    private String claimantAddress;

    @Schema(description = "Email address of the claimant (optional)", example = "jane.moyo@example.com", nullable = true)
    private String claimantEmail;
}
