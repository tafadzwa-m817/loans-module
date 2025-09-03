package zw.co.afrosoft.zdf.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String forceNumber;
    private LocalDateTime datePosted;
    private LocalDate transactionDate;
    private String transactionCode;
    private String transactor;
    private BigDecimal amount;
    private Long referenceId;
    private Long reversedTransactionId;
    @Builder.Default
    private Boolean isReversed = false;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private Long currencyId;
    @Embedded
    private Audit audit;
}
