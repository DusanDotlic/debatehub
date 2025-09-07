package com.debatehub.backend.config;

import com.debatehub.backend.security.CustomUserDetailsService;
import com.debatehub.backend.security.JwtAuthenticationFilter;
import com.debatehub.backend.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public DaoAuthenticationProvider authProvider(CustomUserDetailsService uds, PasswordEncoder enc) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtUtil jwt, CustomUserDetailsService uds) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"success\":false,\"message\":\"Forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/health/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Authenticated reads for "mine/*"
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/debates/mine/**").authenticated()

                        // Public reads by slug/list, etc.
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/debates/**").permitAll()

                        // Writes: must be authenticated
                        .requestMatchers(org.springframework.http.HttpMethod.POST,   "/api/debates/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT,    "/api/debates/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/debates/**").authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )


                .httpBasic(h -> h.disable())
                .formLogin(f -> f.disable());


        http.addFilterBefore(new JwtAuthenticationFilter(jwt, uds), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
