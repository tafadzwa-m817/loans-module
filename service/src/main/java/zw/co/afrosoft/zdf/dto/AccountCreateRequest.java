package zw.co.afrosoft.zdf.dto;


import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;


@Builder
@Schema(name = "AccountCreateRequest", description = "Request body for creating an account")
public record AccountCreateRequest(

        @Schema(description = "Name of the account", example = "Development Fund")
        String name,

        @Schema(description = "Category of the account", example = "Project")
        String category,

        @Schema(description = "Currency ID associated with the account", example = "1")
        Long currencyId

) {
}

