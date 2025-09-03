package zw.co.afrosoft.zdf.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;
import zw.co.afrosoft.zdf.member.Member;

import java.math.BigDecimal;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProjectParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    private String serialNumber;
    @Enumerated(EnumType.STRING)
    private ProjectParticipantStatus status;
    private String standNumber;
    private Double squareMeters;
    private BigDecimal costPrice;
    private Double interestCharged;// interest charges per 5 years/60 months with option to change
    private BigDecimal fundCostPrice;
    private Long currencyId;
    private String comment;
    @ManyToOne
    private Member member;
    @ManyToOne
    private Project project;
    @Embedded
    private Audit audit;
}
