package com.example.server.Security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.server.Filter.JwtFilter;
import com.example.server.User.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Autowired
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider DAP = new DaoAuthenticationProvider();
        DAP.setUserDetailsService(userService);
        DAP.setPasswordEncoder(passwordEncoder());
        return DAP;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            .authorizeHttpRequests(
                config -> config
                        .requestMatchers(HttpMethod.GET, EndPointsConfig.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, EndPointsConfig.PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, EndPointsConfig.ADMIN_GET_ENDPOINTS).hasAuthority("ADMIN")
                        .anyRequest().authenticated()
            )
            .cors(
                cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration corsConfig = new CorsConfiguration();
                        corsConfig.addAllowedOrigin(EndPointsConfig.front_end_host);
                        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                        corsConfig.addAllowedHeader("*");
                        return corsConfig;
                    });
                }
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
