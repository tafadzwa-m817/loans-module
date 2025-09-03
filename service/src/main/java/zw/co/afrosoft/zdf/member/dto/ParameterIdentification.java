package zw.co.afrosoft.zdf.member.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;



@Builder
@Schema(description = "Parameter identification record for member unit and rank")
public record ParameterIdentification(

        @Schema(description = "Identifier of the member's unit", example = "101")
        Long unitId,

        @Schema(description = "Identifier of the member's rank", example = "5")
        Long rankId
) {}

