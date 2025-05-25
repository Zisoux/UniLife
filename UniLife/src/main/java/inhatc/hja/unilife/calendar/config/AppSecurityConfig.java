package inhatc.hja.unilife.calendar.config;

import inhatc.hja.unilife.user.service.CustomUserDetailsService;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@Order(0)
public class AppSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public AppSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authenticationProvider(daoAuthenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스 허용
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()

                // 로그인 & 회원가입 허용 (GET & POST 모두)
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login", "POST")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup", "POST")).permitAll()

                // API 허용
                .requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/**", "POST")).permitAll()

                // H2 콘솔 허용
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                // 나머지 요청은 인증 필요
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customLoginSuccessHandler)
                .defaultSuccessUrl("/calendar", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
