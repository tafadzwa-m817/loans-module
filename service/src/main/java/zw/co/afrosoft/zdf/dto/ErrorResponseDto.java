package zw.co.afrosoft.zdf.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;



@Data
@RequiredArgsConstructor
@Schema(
        name = "ErrorResponseDto",
        description = "Standard schema for handling error responses returned by the API"
)
public class ErrorResponseDto {

    @Schema(
            description = "HTTP status code of the error response",
            example = "400"
    )
    private final int status;

    @Schema(
            description = "Detailed error message",
            example = "Invalid request: missing required field 'forceNumber'"
    )
    private final String error;
}
