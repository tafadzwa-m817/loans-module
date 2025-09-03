package zw.co.afrosoft.zdf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.enums.SecuritiesCategory;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;

import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Securities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String securitiesAccountNumber;
    private String forceNumber;
    private String loanNumber;
    private Long currencyId;
    private Double securitiesAmountPaid;
    private Double securitiesAmount;
    private Double balance;
    private Double overdraftAmount;
    private LocalDateTime transactionDate;
    private Boolean isPaid;
    @Enumerated(EnumType.STRING)
    private SecuritiesStatus securitiesStatus;
    @Enumerated(EnumType.STRING)
    private SecuritiesCategory securitiesCategory;
    @Embedded
    private Audit audit;
}
