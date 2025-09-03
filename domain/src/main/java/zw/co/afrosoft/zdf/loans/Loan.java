package zw.co.afrosoft.zdf.loans;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.member.PersonalDetails;

import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "loan_id")
    private Long id;
    private String forceNumber;
    @Embedded
    private PersonalDetails personalDetails;
    private Long rankId;
    private Long unitId;
    private String unitAddress;
    private Double loanAdvance;
    private Double balance;
    private Double amountPaid;
    private Double overPaidAmount;
    private String loanNumber;
    private String comment;
    private Double interestToDate;
    private Double interestAmount;
    private Double totalInterestAmount;
    private int duration;
    private LocalDateTime dueDate;
    private LocalDateTime dateClosed;
    private boolean isDefaulted;
    private Long currencyId;
    @Enumerated(EnumType.STRING)
    private LoanType loanType;
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @Embedded
    private Audit audit;
}