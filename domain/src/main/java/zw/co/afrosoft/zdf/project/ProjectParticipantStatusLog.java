package zw.co.afrosoft.zdf.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.ProjectParticipantStatus;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProjectParticipantStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "project_participant_id", nullable = false)
    private ProjectParticipant projectParticipant;

    @Enumerated(EnumType.STRING)
    private ProjectParticipantStatus status;

    private String comment;

    @Embedded
    private Audit audit;
    // Custom getter method to expose projectId
    @JsonProperty("projectParticipantId")
    public Long getProjectParticipantId() {
        return projectParticipant.getId();
    }
}
