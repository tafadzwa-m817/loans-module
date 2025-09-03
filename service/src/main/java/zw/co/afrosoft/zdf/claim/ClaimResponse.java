package zw.co.afrosoft.zdf.claim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * created by  Romeo Jerenyama
 * created on  27/5/2025 at 11:45
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing claim summary information")
public class ClaimResponse {

    @Schema(description = "Total number of claims", example = "5")
    private int totalClaims;

    @Schema(description = "Current status of the claim")
    private ClaimStatus claimStatus;
}

