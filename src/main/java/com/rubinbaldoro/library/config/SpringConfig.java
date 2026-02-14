package com.rubinbaldoro.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringConfig {

    private final UserDetailsService userDetailsService;

    public SpringConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Allow Vaadin internal resources & Static assets
                        .requestMatchers(
                                "/VAADIN/**",
                                "/favicon.ico",
                                "/robots.txt",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/offline.html",
                                "/icons/**",
                                "/images/**",
                                "/styles/**",
                                "/frontend/**",
                                "/webjars/**",
                                "/line-awesome/**"
                        ).permitAll()

                        // 2. Allow Public Pages
                        .requestMatchers("/", "/login", "/registration", "/verify").permitAll()

                        // 3. Block everything else (Books, Loans, Students)
                        .anyRequest().authenticated()
                )

                // 4. Disable standard CSRF (Vaadin handles it)
                .csrf(csrf -> csrf.disable())

                // 5. Configure the login form
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true) // <--- FORCE redirect to Dashboard after login
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // outdated way below
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(this.userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

    // new way of authenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // NEW API: Pass UserDetailsService directly to constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}