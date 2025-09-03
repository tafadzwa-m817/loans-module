package zw.co.afrosoft.zdf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;




@Data
@Embeddable
@Schema(description = "Audit metadata for entity lifecycle events")
public class Audit {

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp when the entity was created", example = "2025-05-01 08:30:00")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp when the entity was last modified", example = "2025-05-15 10:45:00")
    private LocalDateTime modifiedDate;

    @CreatedBy
    @Schema(description = "Username of the user who created the entity", example = "admin")
    private String createdBy;

    @LastModifiedBy
    @Schema(description = "Username of the user who last modified the entity", example = "supervisor")
    private String modifiedBy;

    @Schema(description = "IP address from which the request originated", example = "192.168.1.10")
    private String ipAddress;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        setIpFromRequest();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
        setIpFromRequest();
    }

    private void setIpFromRequest() {
        try {
            RequestUtil requestUtil = StaticContextAccessor.getBean(RequestUtil.class);
            this.ipAddress = requestUtil.getClientIp();
        } catch (Exception e) {
            this.ipAddress = "N/A";
        }
    }
}
