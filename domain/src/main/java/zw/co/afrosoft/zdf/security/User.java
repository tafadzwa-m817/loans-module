package zw.co.afrosoft.zdf.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {

    private Long id;

    private String username;
    @Column
    private String fullName;
    private String password;
    private String location;
    private String phoneNumber;
    private String gender;
    private boolean otpEnabled;
    private Integer loginAttempts;
    private String status;

    private String department;


    private final Set<Permission> authorities = new HashSet<>();

    private final Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(authorities::add);
        return authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
