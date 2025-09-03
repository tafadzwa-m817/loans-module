package zw.co.afrosoft.zdf.loans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.ProjectStatus;
import zw.co.afrosoft.zdf.project.Project;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_status_update_logs")
public class LoanStatusLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    private String comment;

    @Embedded
    private Audit audit;
    // Custom getter method to expose loanId
    @JsonProperty("projectId")
    public Long getLoanId() {
        return loan.getId();
    }
}
