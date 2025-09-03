package zw.co.afrosoft.zdf.security;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Embeddable
@Data
public class Role {
    @Column(name = "role_id")
    private Long id;
    @Column(name = "role_name")
    private String name;
    @Transient
    private Set<Permission> permissions = new HashSet<>();
}
