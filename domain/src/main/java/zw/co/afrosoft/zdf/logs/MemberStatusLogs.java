package zw.co.afrosoft.zdf.logs;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.member.MemberStatus;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MemberStatusLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    private String comment;

    @Embedded
    private Audit audit;
}
