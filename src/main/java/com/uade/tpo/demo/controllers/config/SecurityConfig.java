package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        // Autenticación (Público)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Categorías: Lectura pública, gestión solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        // Productos: Lectura pública, gestión y descuentos solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                        // Carrito: Operaciones para usuarios autenticados (USER o ADMIN)
                        .requestMatchers("/cart/**").hasAnyRole("USER", "ADMIN")

                        // Órdenes:
                        // - Crear orden: Usuarios autenticados (USER o ADMIN)
                        // - Listar todas las órdenes: Solo ADMIN
                        // - Consultar orden por ID: ADMIN
                        // - Modificar estado o eliminar órdenes: Solo ADMIN
                        // - Crear Checkout: Usuarios autenticados (USER o ADMIN)
                        .requestMatchers(HttpMethod.POST, "/orders/checkout").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/orders/pay/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("ADMIN")           // Crear orden directa: solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")            // Listar todas: solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER", "ADMIN") // Ver una orden por ID: USER o ADMIN
                        .requestMatchers(HttpMethod.PUT, "/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")

                        // Rutas de administración general
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Rutas de imagenes de productos
                        .requestMatchers("/images/**").permitAll()

                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
