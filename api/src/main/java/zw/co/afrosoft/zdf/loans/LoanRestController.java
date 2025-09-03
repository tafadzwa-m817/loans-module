package zw.co.afrosoft.zdf.loans;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.dto.LoanRequestDto;
import zw.co.afrosoft.zdf.dto.LoanResponseDto;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.exceptions.MemberInactiveException;
import zw.co.afrosoft.zdf.exceptions.OutStandingLoanException;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.utils.constants.Constants;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toLoanResponseDtoList;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toLoanResponseDto;



@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/loans")
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Zdf Loans", description = "REST APIs to CREATE, UPDATE, FETCH AND CLOSE Loan")
public class LoanRestController {

    private final LoanService loanService;

    @Operation(
            summary = "Create Loan REST API",
            description = "REST API to create a new Loan"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400", description = "Outstanding loan exists",
                    content = @Content(schema = @Schema(implementation = OutStandingLoanException.class))),
            @ApiResponse(responseCode = "400", description = "Member is inactive",
                    content = @Content(schema = @Schema(implementation = MemberInactiveException.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(@Valid @RequestBody LoanRequestDto loanRequestDto) {
        return ResponseEntity.status(CREATED)
                .body(toLoanResponseDto(loanService.createLoan(loanRequestDto)));
    }

    @Operation(
            summary = "Fetch Loan REST API",
            description = "Fetch loan details by loan ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "400", description = "Loan not found",
                    content = @Content(schema = @Schema(implementation = RecordNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> findLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(toLoanResponseDto(loanService.findLoanById(id)));
    }

    @Operation(
            summary = "Close Paid Loan REST API",
            description = "Close a loan when loan status is PAID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan closed successfully"),
            @ApiResponse(responseCode = "400", description = "Expectation Failed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/close")
    public ResponseEntity<LoanResponseDto> closeLoan(@RequestParam String loanNumber) {
        return ResponseEntity.ok(toLoanResponseDto(loanService.closeLoan(loanNumber)));
    }

    @Operation(
            summary = "Update Loan REST API",
            description = "Update loan details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Expectation Failed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/update")
    public ResponseEntity<LoanResponseDto> updateLoan(@RequestParam Long id,
                                                     @Valid @RequestBody LoanRequestDto loanRequestDto) {
        return ResponseEntity.ok(toLoanResponseDto(loanService.updateLoan(id, loanRequestDto)));
    }

    @Operation(
            summary = "Update Loan Status REST API",
            description = "Update the status of a loan"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Expectation Failed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/update-status")
    public ResponseEntity<LoanResponseDto> updateLoanStatus(@Valid @RequestBody LoanUpdateStatusRequest request) {
        return ResponseEntity.ok(toLoanResponseDto(loanService.updateLoanStatus(request)));
    }

    @Operation(
            summary = "Fetch All Loans REST API",
            description = "Fetch all loans with optional filters"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<LoanResponseDto>> getAllLoans(
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Page number must be zero or positive") Integer pageNumber,
            @RequestParam(defaultValue = "10")
            @Positive(message = "Page size must be positive") Integer pageSize,
            @RequestParam(value = "forceNumber", required = false) String forceNumber,
            @RequestParam(value = "loanNumber", required = false) String loanNumber,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "loanStatus", required = false) LoanStatus loanStatus,
            @RequestParam(value = "loanType", required = false) LoanType loanType) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Loan> loanPage = loanService.getLoans(forceNumber, loanNumber, firstName, lastName, loanStatus, loanType, pageable);

        return ResponseEntity.ok(new PageImpl<>(
                toLoanResponseDtoList(loanPage.getContent()),
                loanPage.getPageable(),
                loanPage.getTotalElements()));
    }

    @Operation(
            summary = "Bulk Upload Loans REST API",
            description = "Upload loan details using CSV or Excel file"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loans uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "500", description = "Server error while processing file",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping(value = "/bulk-upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> bulkUploadLoans(@RequestParam("file") MultipartFile file) {
        loanService.bulkUploadLoans(file);
        return ResponseEntity.ok("Loans uploaded successfully");
    }

    @Operation(
            summary = "Loan Status Logs API",
            description = "Fetch historical updates of loan statuses"
    )
    @GetMapping("/status-updates")
    public ResponseEntity<Page<LoanStatusLogs>> showLoanStatusLogs(
            @RequestParam(required = false) Long loanId,
            @RequestParam(required = false) LoanStatus loanStatus,
            @Parameter(description = Constants.PAGE_NUMBER_DESCRIPTION) @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION) @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(loanService.showLoanStatusLogs(loanId, loanStatus, pageable));
    }
}
