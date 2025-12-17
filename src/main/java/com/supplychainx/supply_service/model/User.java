package com.supplychainx.supply_service.model;

import com.supplychainx.supply_service.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor // by default and since there is no final attribute, or @NonNull, this annotation provides the @NoArgsConstructor
@ToString
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring doesn't understand Enums. It needs "Authorities" (Strings).
        // We convert your Enum (e.g., ADMIN) into a standard authority (e.g., "ROLE_ADMIN")
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword(){
        return password; //telling spring that this is the password
    }

    @Override
    public String getUsername(){
        return email; //telling spring that this is the username which is the email in our entity
    }

    // 4. Account Status Methods
    // For a real app, you might have boolean fields in your DB like 'isActive'.
    // For now, we return 'true' to say "This user is always allowed to login"

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }


}