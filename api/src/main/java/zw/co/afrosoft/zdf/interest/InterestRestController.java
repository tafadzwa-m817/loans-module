package zw.co.afrosoft.zdf.interest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.utils.enums.InterestCategory;

import java.time.YearMonth;



@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/interest")
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Interest", description = "REST APIs to apply loan and project interest ")
public class InterestRestController {

    private final InterestApplicationService interestApplicationService;

    @Operation(
            summary = "Interest Application REST API",
            description = "REST API to apply interest"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Interest Successfully applied!"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping("/apply")
    public ResponseEntity<InterestResponse> applyInterest() {
        return ResponseEntity.ok(interestApplicationService.applyInterest());
    }

    @Operation(
            summary = "Retrieve Interest Next Execution Month REST API",
            description = "REST API to fetch interest next execution month"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Date Retrieved Successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/next-execution-month")
    public ResponseEntity<YearMonth> getNextExecutionMonth() {
        return ResponseEntity.ok(interestApplicationService.getNextExecutionMonth());
    }
}
