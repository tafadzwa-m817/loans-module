package zw.co.afrosoft.zdf.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.Category;
import zw.co.afrosoft.zdf.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    private String serialNumber;
    private String projectName;
    private Long provinceId;
    private Long townId;
    private String farm;
    private String developer;
    private Integer numberOfStands;
    private LocalDate dateOfPurchase;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    private BigDecimal projectCostPrice;
    private Double totalAreaInSqm;
    private Double allocatedAreaInSqm;
    private BigDecimal costPricePerSqm;
    @Enumerated(EnumType.STRING)
    private Category categoryPerSqm;
    private boolean isDeleted;
    private String comment;
    private Long currencyId;
    @Embedded
    private Audit audit;
}
