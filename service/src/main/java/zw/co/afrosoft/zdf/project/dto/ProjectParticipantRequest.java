package zw.co.afrosoft.zdf.project.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;



@Builder
@Schema(name = "ProjectParticipantRequest", description = "Request payload for creating or updating a project participant")
public record ProjectParticipantRequest(

        @Schema(description = "ID of the project", example = "123")
        Long projectId,

        @Schema(description = "ID of the currency", example = "1")
        Long currencyId,

        @Schema(description = "Force number of the participant", example = "F123456")
        String forceNumber,

        @Schema(description = "Stand number related to the project participant", example = "ST-789")
        String standNumber,

        @Schema(description = "Area in square meters", example = "250.5")
        Double squareMeters,

        @Schema(description = "Cost price of the participation", example = "15000.00")
        BigDecimal costPrice,

        @Schema(description = "Interest charged on the participation", example = "5.5")
        Double interestCharged,

        @Schema(description = "Fund cost price", example = "12000.00")
        BigDecimal fundCostPrice
) {}
