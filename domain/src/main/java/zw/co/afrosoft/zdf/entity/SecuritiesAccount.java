package zw.co.afrosoft.zdf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.loans.AccountType;
import zw.co.afrosoft.zdf.loans.Loan;

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
public class SecuritiesAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String securitiesAccountNumber;
    private String forceNumber;
    private String membershipNumber;
    private Long currencyId;
    private LocalDateTime dateCreated;
    @OneToMany
    @JoinColumn(name = "securities_account_id")
    private List<Loan> loans = new ArrayList<>();
    @Embedded
    private Audit audit;
}
