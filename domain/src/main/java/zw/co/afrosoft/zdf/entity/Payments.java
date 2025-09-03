package zw.co.afrosoft.zdf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;



@SuppressWarnings("ALL")
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private Long id;
    private String forceNumber;
    private String loanNumber;
    private Double amountPaid;
    private LocalDateTime paymentDate;
    @Embedded
    private Audit audit;
}
