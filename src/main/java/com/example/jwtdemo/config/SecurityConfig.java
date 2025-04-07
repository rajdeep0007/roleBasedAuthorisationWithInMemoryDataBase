package com.example.jwtdemo.config;

import com.example.jwtdemo.handler.CustomAccessDeniedHandler;
import com.example.jwtdemo.handler.CustomAuthEntryPoint;
import com.example.jwtdemo.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler,
                                                   CustomAuthEntryPoint authEntryPoint) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                )
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/public/healthCheck", "/error","/admin/addUser").permitAll()
                                .requestMatchers("/admin/healthCheck").hasRole("ADMIN")
                                .requestMatchers("/user/healthCheck").hasRole("USER")
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails adminUser = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin123"))
//                .roles("ADMIN")
//                .build();
//        UserDetails normalUser = User.builder()
//                .username("user")
//                .password(passwordEncoder().encode("user123"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(adminUser, normalUser);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //return NoOpPasswordEncoder.getInstance(); //  for testing only
    }

}
