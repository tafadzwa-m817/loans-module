package zw.co.afrosoft.zdf.dashboard.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/14/25
 */

@Builder
@Schema(description = "Summary of loan statistics, including active and unpaid loans")
public record TotalLoans(

        @Schema(description = "Total number of active loans", example = "125")
        Long total_active_loans,

        @Schema(description = "Number of loans unpaid for more than 60 days", example = "8")
        Long loans_unpaid_60_days

) {
}

