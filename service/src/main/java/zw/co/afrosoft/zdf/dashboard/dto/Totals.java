package zw.co.afrosoft.zdf.dashboard.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/9/25
 */

@Builder
@Schema(description = "Represents aggregated totals for a specific currency")
public record Totals(

        @Schema(description = "Total number of items (e.g., loans, securities)", example = "42")
        Long total,

        @Schema(description = "ID of the currency", example = "1")
        Long currency,

        @Schema(description = "Monetary value of the total in the specified currency", example = "10500.75")
        BigDecimal value

) {
}

