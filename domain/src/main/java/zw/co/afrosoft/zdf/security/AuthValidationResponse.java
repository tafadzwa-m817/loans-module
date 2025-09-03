package zw.co.afrosoft.zdf.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthValidationResponse {
    private boolean isTokenValid;
    private String username;
    private User userDetails;
}
