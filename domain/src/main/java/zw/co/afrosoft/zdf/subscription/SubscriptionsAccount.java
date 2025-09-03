package zw.co.afrosoft.zdf.subscription;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.member.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.*;



@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionsAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true, nullable = false)
	private String accountNumber;
	private String name;
	private String surname;
	private String forceNumber;
	private LocalDate membershipDate;
    private ServiceType serviceType;
    private String rank;
	private Long currencyId;
	private BigDecimal currentBalance;
    private BigDecimal currentArrears;
	private LocalDateTime startDate;
	@Enumerated(STRING)
	private AccountStatus accountStatus;
	private BigDecimal balanceBForward;
	private Double interestToDate;
	@Embedded
	private Audit audit;

}
