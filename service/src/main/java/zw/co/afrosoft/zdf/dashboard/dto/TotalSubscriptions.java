package zw.co.afrosoft.zdf.dashboard.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Author Terrance Nyamfukudza
 * Date: 4/14/25
 */

@Builder
@Schema(description = "Summary of subscription statistics, including active and unpaid subscriptions")
public record TotalSubscriptions(

        @Schema(description = "Total number of active subscriptions", example = "350")
        Long total_active_subscriptions,

        @Schema(description = "Number of subscriptions unpaid for more than 60 days", example = "15")
        Long subscriptions_unpaid_60_days

) {
}

