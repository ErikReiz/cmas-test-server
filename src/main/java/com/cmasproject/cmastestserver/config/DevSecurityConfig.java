package com.cmasproject.cmastestserver.config;

import com.cmasproject.cmastestserver.entities.enums.Role;
import com.cmasproject.cmastestserver.exceptions.CustomAccessDeniedHandler;
import com.cmasproject.cmastestserver.exceptions.CustomBasicAuthenticationEntryPoint;
import com.cmasproject.cmastestserver.security.UsernamePasswordAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.cmasproject.cmastestserver.security.JWTTokenValidatorFilter;

@Configuration
@Profile({"integration-testing", "deployment"})
public class DevSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Order
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .requiresChannel(rcc -> rcc.anyRequest().requiresSecure())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/auth/signup/patient").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/signup/doctor").hasRole(Role.ADMIN.name())
                        .requestMatchers("/api/doctor/**").hasRole(Role.DOCTOR.name())
                        .requestMatchers("/api/patient/**").hasRole(Role.PATIENT.name())
                        .requestMatchers("/api/test/**").hasRole(Role.PATIENT.name())
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UsernamePasswordAuthenticationProvider authenticationProvider = new UsernamePasswordAuthenticationProvider(userDetailsService, passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }
}
