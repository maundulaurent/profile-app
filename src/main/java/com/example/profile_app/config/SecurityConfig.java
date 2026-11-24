package com.example.profile_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        String passA = System.getenv("OWNER_A_PASSWORD");
        String passB = System.getenv("OWNER_B_PASSWORD");

        if (passA == null || passB == null) {
            throw new IllegalStateException("OWNER_A_PASSWORD and OWNER_B_PASSWORD must be set");
        }

        UserDetails ownerA = User.withUsername("ownerA")
                .password(encoder.encode(passA))
                .roles("OWNER")
                .build();

        UserDetails ownerB = User.withUsername("ownerB")
                .password(encoder.encode(passB))
                .roles("OWNER")
                .build();

        return new InMemoryUserDetailsManager(ownerA, ownerB);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/directory", "/profiles/**", "/projects/**", "/css/**").permitAll()
                .requestMatchers("/owner/**").hasRole("OWNER")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/owner/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }
}