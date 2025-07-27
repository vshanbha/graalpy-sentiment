package com.example;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;
    
    private final Map<String, UsernamePasswordAuthenticationToken> authCache = new ConcurrentHashMap<>();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
            .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        // ✅ Custom AuthenticationProvider with cache logic
        AuthenticationProvider provider = new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) {
                String name = authentication.getName();
                String rawPassword = authentication.getCredentials().toString();

                String cacheKey = name + ":" + rawPassword;

                // ✅ Check cache first
                UsernamePasswordAuthenticationToken cachedAuth = authCache.get(cacheKey);
                if (cachedAuth != null) {
                    return cachedAuth;
                }

                // ✅ Validate credentials with bcrypt on first attempt
                if (username.equals(name) && passwordEncoder.matches(rawPassword, password)) {
                    UsernamePasswordAuthenticationToken authResult =
                            new UsernamePasswordAuthenticationToken(name, rawPassword, authentication.getAuthorities());

                    // ✅ Cache result
                    authCache.put(cacheKey, authResult);
                    return authResult;
                }

                throw new BadCredentialsException("Invalid username or password");
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Define available encoders
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        // Add additional encoders if needed

        // Default encoder ID
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }

}
