package zw.co.afrosoft.zdf.notification;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Recipient {
    private String name;
    private String phoneNumber;
    private String email;
    private String fcmToken;
}
