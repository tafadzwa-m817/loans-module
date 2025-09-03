package zw.co.afrosoft.zdf.dashboard.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/28/25
 */

@Builder
@Schema(description = "Response object containing a list of security details for the dashboard")
public record SecuritiesDashboardResponse(

        @Schema(description = "List of security details grouped by currency or type")
        List<SecurityDetails> details

) {
}

