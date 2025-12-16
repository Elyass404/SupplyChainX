package com.supplychainx.security;


import com.supplychainx.config.PasswordConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {



//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }

    //here iam creating an instance of the password config because i did it in other file before to hash the password
    //and because  spring knows only one bean, i cant define it here and there, that is why iam creating this instance here instead of making the one above this comment
    PasswordConfig passwordConfig = new PasswordConfig();

    //Here iam defining the fake users that I would store in the memory as users that can log in
    @Bean
    public UserDetailsService userDetailsService(){

        UserDetails admin = User.builder()
                .username("ilyass_admin")
                .password(passwordConfig.passwordEncoder().encode("1234"))
                .roles("ADMIN")
                .build();

        UserDetails supplyManager = User.builder()
                .username("soufiane_manager")
                .password(passwordConfig.passwordEncoder().encode("1234"))
                .roles("SUPPLY_MANAGER")
                .build();

        UserDetails purchasingManager = User.builder()
                .username("imran_manager")
                .password(passwordConfig.passwordEncoder().encode("1234"))
                .roles("PURCHASING_MANAGER")
                .build();

        return new InMemoryUserDetailsManager(
                admin,
                supplyManager,
                purchasingManager
        );

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())


                .httpBasic(Customizer.withDefaults())


                .authorizeHttpRequests(auth ->auth

                        .requestMatchers("/api/v1/raw-materials/**").hasRole("ADMIN")

                        .requestMatchers("/api/v1/suppliers/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")

                        .requestMatchers("/api/v1/supply-orders/**").hasAnyRole("ADMIN", "PURCHASING_MANAGER")

                        .anyRequest().authenticated()
                );

        return http.build();

    }


}
