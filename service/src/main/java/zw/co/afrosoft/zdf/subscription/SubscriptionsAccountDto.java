package zw.co.afrosoft.zdf.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.member.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a member's subscriptions account details")
public class SubscriptionsAccountDto {

        @Schema(description = "Unique identifier of the subscriptions account", example = "1001")
        private Long id;

        @Schema(description = "Account number assigned to the member", example = "SUB123456")
        private String accountNumber;

        @Schema(description = "Member's first name", example = "John")
        private String name;

        @Schema(description = "Member's last name", example = "Doe")
        private String surname;

        @Schema(description = "Member's force number", example = "ZNA987654")
        private String forceNumber;

        @Schema(description = "Date when the member joined", example = "2020-01-15")
        private LocalDate membershipDate;

        @Schema(description = "Type of service the member belongs to", example = "ZIMBABWE_NATIONAL_ARMY")
        private ServiceType serviceType;

        @Schema(description = "Member's military or service rank", example = "Captain")
        private String rank;

        @Schema(description = "Currency ID used for this account", example = "1")
        private Long currencyId;

        @Schema(description = "Current available balance in the account", example = "5000.00")
        private BigDecimal currentBalance;

        @Schema(description = "Current arrears amount in the account", example = "300.00")
        private BigDecimal currentArrears;

        @Schema(description = "Start date of active subscription", example = "2023-06-01T00:00:00")
        private LocalDateTime startDate;

        @Schema(description = "Current status of the member’s account", example = "ACTIVE")
        private AccountStatus accountStatus;

        @Schema(description = "Brought forward balance from previous cycles", example = "250.00")
        private BigDecimal balanceBForward;

        @Schema(description = "Total interest accrued to date", example = "120.75")
        private Double interestToDate;

        @Schema(description = "Audit metadata including created and modified timestamps")
        private Audit audit;
}

