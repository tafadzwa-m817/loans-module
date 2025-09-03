package zw.co.afrosoft.zdf.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipientRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
}
