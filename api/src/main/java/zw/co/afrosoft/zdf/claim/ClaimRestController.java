package zw.co.afrosoft.zdf.claim;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zw.co.afrosoft.zdf.dto.ClaimDto;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.exceptions.ClaimRejectedException;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.mapper.EntityDtoMapper;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toClaimDto;



@Validated
@RestController
@RequestMapping("v1/claim")
@RequiredArgsConstructor
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Zdf Claim", description = "REST APIs to process and fetch member claims")
public class ClaimRestController {

    private final ClaimService claimService;

    @Operation(
            summary = "Process Member Claim",
            description = "Processes a claim for a member"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Claim created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ClaimRejectedException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ClaimDto> processClaim(
            @Valid @RequestBody ClaimantDetailsRequest request,
            @RequestParam @NotBlank(message = "Force number must be provided") String forceNumber,
            @RequestParam @NotNull(message = "Claim type must be specified") ClaimType claimType,
            @RequestParam(required = false) LocalDate retirementDate) {

        var claim = claimService.processClaim(request, forceNumber, claimType, retirementDate);
        return ResponseEntity.status(CREATED).body(toClaimDto(claim));
    }

    @Operation(
            summary = "Fetch Claim by ID",
            description = "Retrieves claim details by the given claim ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved claim details"),
            @ApiResponse(responseCode = "400", description = "Invalid claim ID provided",
                    content = @Content(schema = @Schema(implementation = RecordNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClaimDto> retrieveClaim(
            @PathVariable @NotNull(message = "Claim ID must be provided") Long id) {

        ClaimDto claimDto = EntityDtoMapper.toClaimDto(claimService.getMemberClaim(id));
        return ResponseEntity.ok(claimDto);
    }

    @Operation(
            summary = "Fetch All Claims",
            description = "Retrieves a paginated list of all claims, optionally filtered by force number and claim type"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = RecordNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ClaimDto>> retrieveClaims(
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Page number must be zero or positive") Integer pageNumber,
            @RequestParam(defaultValue = "10")
            @Positive(message = "Page size must be positive") Integer pageSize,
            @RequestParam(value = "forceNumber", required = false) String forceNumber,
            @RequestParam(value = "claimStatus", required = false) ClaimStatus claimStatus,
            @RequestParam(value = "claimType", required = false) ClaimType claimType
           ) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        var claimsPage = claimService.retrieveClaims(forceNumber, claimStatus,claimType, pageable);

        var claimDtos = EntityDtoMapper.toClaimDtoList(claimsPage.getContent());
        var resultPage = new PageImpl<>(claimDtos, pageable, claimsPage.getTotalElements());

        return ResponseEntity.ok(resultPage);
    }

    @Operation(
            summary = "Get total number of claims grouped by status",
            description = "Returns a list of total claim counts grouped by each ClaimStatus",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of claim counts by status"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @GetMapping("/status-summary")
    public ResponseEntity<List<ClaimResponse>> getClaimsCountByStatus() {
        return ResponseEntity.ok(claimService.getClaimCountsGroupedByStatus());
    }
}
