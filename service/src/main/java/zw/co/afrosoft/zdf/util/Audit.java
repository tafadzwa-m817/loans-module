package zw.co.afrosoft.zdf.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Embeddable
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class Audit {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedDate;
    private String createdBy;
    private String modifiedBy;
    private String ipAddress;


    public Audit() {
        setCreatedDate( LocalDate.now());
        setModifiedDate(LocalDateTime.now());
    }

    public Audit(LocalDate createdDate, LocalDateTime modifiedDate) {
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}