package inhatc.hja.unilife.calendar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import inhatc.hja.unilife.user.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

	@Autowired
	private CustomLoginSuccessHandler customLoginSuccessHandler;

    private final CustomUserDetailsService customUserDetailsService;

    public AppSecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector).servletPath("/");

        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // X-Frame-Options 헤더 비활성화
            .authenticationProvider(daoAuthenticationProvider()) // 인증 제공자 설정
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            	    .requestMatchers(mvc.pattern("/login"), mvc.pattern("/signup")).permitAll()
            	    .requestMatchers(mvc.pattern("/api/signup")).permitAll()
            	    .requestMatchers(mvc.pattern("/h2-console/**")).permitAll()
            	    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // 로그인 페이지 경로 설정
                .loginProcessingUrl("/login") // 로그인 처리 URL
                .successHandler(customLoginSuccessHandler)
                .defaultSuccessUrl("/calendar", true) // 로그인 성공 후 리다이렉트할 경로
                .failureUrl("/login?error") // 로그인 실패 시 리다이렉트할 경로
                .permitAll() // 로그인 페이지는 모든 사용자에게 허용
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃 URL
                .logoutSuccessUrl("/login?logout") // 로그아웃 성공 후 리다이렉트할 경로
                .permitAll() // 로그아웃 URL은 모든 사용자에게 허용
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder()); // ⚠️ 반드시 있어야 함
        return provider;
    }
    
 
}
