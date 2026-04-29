package com.example.ayan.Chat.Application.Config;

import com.example.ayan.Chat.Application.Filter.JwtFilter;
import com.example.ayan.Chat.Application.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService customService;

       @Autowired
        private JwtFilter jwtFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


            return http
                        .csrf(customizer -> customizer.disable())
                        .cors(Customizer.withDefaults())

                    .authorizeHttpRequests(requests -> requests
                            .requestMatchers("/public/**").permitAll()
                            .requestMatchers("/api/**").permitAll()
                            .requestMatchers("/user/auth/api/**").hasRole("USER")
                            .requestMatchers("/admin/auth/**").hasRole("ADMIN")
                            .requestMatchers("/friends/**").hasAnyRole("ADMIN", "USER")
                            .requestMatchers("/group/api/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/group-message/**").permitAll()
                            .requestMatchers("/chat/**").permitAll()
                            .requestMatchers("/private/**").hasAnyRole("USER", "ADMIN")







                            .anyRequest().authenticated())

                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                    .build();
        }



        @Bean
        public AuthenticationProvider authenticationProvider(){

            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

            provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
            provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
            provider.setUserDetailsService(customService);

            return provider;
        }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customService).passwordEncoder(passwordEncoder());
//    }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
            return config.getAuthenticationManager();
        }



    }



