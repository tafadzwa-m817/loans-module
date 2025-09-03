package zw.co.afrosoft.zdf.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_action", nullable = false)
    private UserAction userAction;

    @Column(name = "description", length = 500)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    public AuditTrail() {
        this.createdDate = LocalDateTime.now();
    }
}

