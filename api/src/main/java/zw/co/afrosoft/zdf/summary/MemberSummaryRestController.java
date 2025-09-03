package zw.co.afrosoft.zdf.summary;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/summary")
@SecurityRequirement(name = "authorization")
@Tag(name = "Member Summary Rest API", description = "API to retrieve member summary information including loans, " +
        "projects, subscriptions, and securities")
public class MemberSummaryRestController {

    private final MemberSummaryService memberSummaryService;

    @Operation(
            summary = "Get member summary by force number",
            description = "Fetches a summary of the member's financial data such as loans, projects, subscriptions, and securities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved member summary"),
            @ApiResponse(responseCode = "404", description = "Member not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping
    public ResponseEntity<MemberSummaryResponse> memberSummary(
            @Parameter(description = "Unique force number of the member", example = "ZNA123456", required = true)
            @RequestParam String forceNumber) {

        return ResponseEntity.ok(memberSummaryService.getMemberSummary(forceNumber));
    }
}

