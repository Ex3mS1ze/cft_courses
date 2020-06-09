package org.cft.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "users_details", schema = "public")
public class UserDetailsImpl implements UserDetails {
    private static final int YEAR_OF_DEATH = 3_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    private String password;
    private boolean enabled = true;
    private boolean locked = false;
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_details_to_role")
    private Set<RoleImpl> roles;

    public void addRole(RoleImpl role) {
        roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getRegistrationDate().getYear() < YEAR_OF_DEATH;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getRegistrationDate().getYear() < YEAR_OF_DEATH;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
