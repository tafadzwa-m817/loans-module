package zw.co.afrosoft.zdf.member;

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
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(STRING)
    private ServiceType serviceType;
    private String membershipNumber;
    private String forceNumber;
	private String prevForceNumber;
	private Long rankID;
	private Long unitID;
	private LocalDate  dateOfAttestation;
	private LocalDate membershipDate;
    @Enumerated(STRING)
	private MemberStatus memberStatus;
	private Double grossSalary;
	private Double netSalary;
    private Double tax;
    @Embedded
    private PersonalDetails personalDetails;
    @Embedded
    private Audit audit;

}
