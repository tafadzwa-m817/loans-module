package zw.co.afrosoft.zdf.payments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.enums.PaymentType;
import zw.co.afrosoft.zdf.exceptions.ClaimPaymentMismatchException;
import zw.co.afrosoft.zdf.exceptions.CurrencyMismatchException;
import zw.co.afrosoft.zdf.payment.PaymentRequest;
import zw.co.afrosoft.zdf.payment.PaymentReversalRequest;
import zw.co.afrosoft.zdf.payment.PaymentsService;
import zw.co.afrosoft.zdf.payment.ReversalAction;
import zw.co.afrosoft.zdf.subscription.dto.PaymentBulkUploadResponse;
import zw.co.afrosoft.zdf.transaction.TransactionService;
import zw.co.afrosoft.zdf.transaction.dto.TransactionHistory;
import zw.co.afrosoft.zdf.utils.constants.Constants;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.CREATED;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toTransactionHistory;
import static zw.co.afrosoft.zdf.mapper.EntityDtoMapper.toTransactionHistoryList;



@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/payments")
@SecurityRequirement(name = "authorization")
@Tag(
        name = "REST APIs for Zdf Payments And Transaction History",
        description = "REST APIs to make and track member payments"
)
public class PaymentsRestController {

    private final PaymentsService paymentsService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Make Payment REST API",
            description = "REST API to make a payment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment made successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment amount does not match the claim amount",
                    content = @Content(schema = @Schema(implementation = ClaimPaymentMismatchException.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<TransactionHistory> makePayment(
            @RequestParam("paymentType") PaymentType paymentType,
            @Valid @RequestBody PaymentRequest paymentRequest,
            @RequestParam("transactionTypeId") Long transactionTypeId,
            @RequestParam("transactionDate") @DateTimeFormat(iso = DATE) LocalDate transactionDate) {

        var transaction = paymentsService.makePayment(paymentType, paymentRequest, transactionTypeId, transactionDate);
        return ResponseEntity.status(CREATED).body(toTransactionHistory(transaction));
    }

    @Operation(
            summary = "Reverse Payment REST API",
            description = "REST API to reverse a payment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment reversed successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Currency on reversal does not match the currency of the payment to be reversed",
                    content = @Content(schema = @Schema(implementation = CurrencyMismatchException.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/reversal")
    public ResponseEntity<TransactionHistory> paymentReversal(
            @RequestParam("paymentType") PaymentType paymentType,
            @Valid @RequestBody PaymentReversalRequest paymentReversalRequest,
            @RequestParam("transactionTypeId") Long transactionTypeId,
            @RequestParam("reversalAction") ReversalAction reversalAction,
            @RequestParam("transactionDate") @DateTimeFormat(iso = DATE) LocalDate transactionDate){
        var transaction = paymentsService.paymentReversal(paymentType, paymentReversalRequest,
                transactionTypeId, transactionDate, reversalAction);
        return ResponseEntity.ok(toTransactionHistory(transaction));
    }
    @Operation(
            summary = "Transaction History REST API",
            description = "Retrieve member payment transaction history"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<TransactionHistory>> getTransactionHistory(
            @RequestParam(value = "forceNumber", required = false) String forceNumber,
            @RequestParam(value = "currencyId", required = false) Long currencyId,
            @RequestParam(value = "paymentTypes", required = false) List<PaymentType> paymentTypes,
            @RequestParam(value = "referenceId", required = false) Long referenceId,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = Constants.PAGE_SIZE_DESCRIPTION)
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        List<PaymentType> types = (paymentTypes == null || paymentTypes.isEmpty()) ? null : paymentTypes;

        var pagedTransactions = transactionService.transactions(forceNumber, types, currencyId, referenceId, pageable);
        return ResponseEntity.ok(new PageImpl<>(
                toTransactionHistoryList(pagedTransactions.getContent()),
                pagedTransactions.getPageable(),
                pagedTransactions.getTotalElements()));
    }

    @Operation(
            summary = "Upload Payment Excel File REST API",
            description = "Upload an Excel file for bulk payments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments uploaded successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PaymentBulkUploadResponse> uploadSubscriptionExcel(
            @RequestPart("file") MultipartFile file,
            @RequestParam("transactionDate") @DateTimeFormat(iso = DATE) LocalDate transactionDate,
            @RequestParam("transactionTypeId") Long transactionTypeId,
            @RequestParam("paymentType") PaymentType paymentType,
            @RequestParam("currencyId") Long currencyId) {

        var response = paymentsService.uploadPaymentExcel(file, transactionTypeId, transactionDate, paymentType, currencyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all payment types", description = "Returns all available payment types")
    @GetMapping("/payment-types")
    public ResponseEntity<List<PaymentType>> getAllPaymentTypes() {
        return ResponseEntity.ok(List.of(PaymentType.values()));
    }
}
