package zw.co.afrosoft.zdf.transaction.dto;

import lombok.Data;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Data
@Schema(description = "Details of a transaction history record")
public class TransactionHistory {

    @Schema(description = "Unique identifier of the transaction", example = "12345")
    private Long id;

    @Schema(description = "Member's force number", example = "ZN123456")
    private String forceNumber;

    @Schema(description = "Timestamp when the transaction was posted", example = "2025-05-27T14:45:00")
    private LocalDateTime datePosted;

    @Schema(description = "Date of the transaction", example = "2025-05-27")
    private LocalDate transactionDate;

    @Schema(description = "Type Code of the transaction", example = "00LR_USD")
    private String transactionCode;

    @Schema(description = "Name of the person who initiated the transaction", example = "John Doe")
    private String transactor;

    @Schema(description = "Reference ID related to the transaction (e.g. loan ID)", example = "789")
    private Long referenceId;

    @Schema(description = "ID for the reversed transaction", example = "1")
    private Long reversedTransactionId;

    @Schema(description = "Flag to check is a transaction has been reversed", example = "true")
    private Boolean isReversed;

    @Schema(description = "Amount of money involved in the transaction", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "Payment type used in the transaction")
    private PaymentType paymentType;

    @Schema(description = "Currency ID used in the transaction", example = "1")
    private Long currencyId;

    @Schema(description = "Audit information such as created/modified by and dates")
    private Audit audit;
}
