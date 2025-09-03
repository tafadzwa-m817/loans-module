package zw.co.afrosoft.zdf.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/13/25
 */

@Schema(description = "Dashboard response summarizing loan balances by currency")
public record LoansDashboardResponse(

        @Schema(description = "ID of the currency", example = "1")
        Long currency,

        @Schema(description = "Total active loan balance", example = "125000.50")
        Double active,

        @Schema(description = "Total defaulted loan balance", example = "3400.75")
        Double defaulted

) {
}

