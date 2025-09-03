package zw.co.afrosoft.zdf.claim;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;



@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String forceNumber;
    private String accountHolderName;
    private String accountHolderSurname;
    @Embedded
    private ClaimantDetails claimantDetails;
    private LocalDate claimDate;
    private LocalDate retirementDate;
    private LocalDate approvedDate;
    private Double loanSecuritiesUsdBalance;
    private Double loanSecuritiesLocalBalance;
    private Double projectSecuritiesUsdBalance;
    private Double projectSecuritiesLocalBalance;
    private Double loanProjectSecuritiesBalance;
    private Double loanUsdBalance;
    private Double loanLocalBalance;
    private Double projectUsdBalance;
    private Double projectLocalBalance;
    private Double totalLoanProjectBalance;
    private Double totalOwing;
    private Double subscriptionUSDBalance;
    private Double subscriptionLocalCurrencyBalance;
    private Double subscriptionTotalBalance;
    private Double claimAmount;
    private Long currencyId;
    private Double rate;
    @Enumerated(STRING)
    private ClaimStatus claimStatus;
    @Enumerated(STRING)
    private ClaimType claimType;
    @Embedded
    private Audit audit;
}
