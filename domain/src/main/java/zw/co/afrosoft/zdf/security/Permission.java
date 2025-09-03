package zw.co.afrosoft.zdf.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class Permission implements GrantedAuthority {
    private String permission;

    @Override
    public String getAuthority() {
        return permission;
    }
}
