package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/product/**").hasRole("USER") // Solo usuarios pueden ver productos
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Solo administradores pueden gestionar productos
                        .requestMatchers("/auth/**", "/", "/img/**").permitAll() // Cualquiera puede ver login y registro
                        .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                )
                .formLogin(login -> login
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true) // Ahora redirige correctamente
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login") // Redirige a login después de cerrar sesión
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
