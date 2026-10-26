package vnx.sisterfix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vnx.sisterfix.security.RateLimitFilter;
import vnx.sisterfix.security.RateLimiter;

import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Solo en desarrollo: deja usar la consola H2 sin login ni CSRF. */
    @Bean
    @Order(1)
    @Profile("dev")
    SecurityFilterChain h2ConsoleChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/h2-console/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain appChain(
            HttpSecurity http,
            RateLimiter limitador,
            @Value("${sisterfix.seguridad.login-max-intentos:8}") int loginMaximo,
            @Value("${sisterfix.seguridad.login-ventana-minutos:10}") long loginMinutos,
            @Value("${sisterfix.seguridad.consulta-max-intentos:30}") int consultaMaximo,
            @Value("${sisterfix.seguridad.consulta-ventana-minutos:1}") long consultaMinutos) throws Exception {

        RateLimitFilter limite = new RateLimitFilter(limitador,
                loginMaximo, Duration.ofMinutes(loginMinutos),
                consultaMaximo, Duration.ofMinutes(consultaMinutos));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/sisterfix/**", "/css/**", "/js/**", "/login", "/error").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "TECNICO")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/ordenes", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .addFilterBefore(limite, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}