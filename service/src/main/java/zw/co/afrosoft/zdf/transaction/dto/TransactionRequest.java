package zw.co.afrosoft.zdf.transaction.dto;

import lombok.Data;

import java.time.LocalDateTime;



public record TransactionRequest(
        LocalDateTime transactionDate,
        Long TransactionTypeId
) {
}
