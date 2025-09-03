package zw.co.afrosoft.zdf.loans;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.subscription.AccountStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String loanAccountNumber;
    private String forceNumber;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private String membershipNumber;
    private AccountType accountType;
    private LocalDateTime dateCreated;
    @OneToMany
    @JoinColumn(name = "loan_account_id")
    private List<Loan> loans = new ArrayList<>();
    @Embedded
    private Audit audit;
}
