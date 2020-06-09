package org.cft.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "roles", schema = "public")
public class RoleImpl implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.REMOVE)
    private Set<UserDetailsImpl> userDetailsList;

    @Override
    public String getAuthority() {
        return name;
    }
}
