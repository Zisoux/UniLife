package inhatc.hja.unilife.user.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println("[DEBUG] login requested for: " + userId);

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 🔍 이 부분 추가!
        try {
            System.out.println("[DEBUG] password_hash from DB: " + user.getPasswordHash());
        } catch (Exception e) {
            System.out.println("[ERROR] getPasswordHash() failed: " + e.getMessage());
            e.printStackTrace();
        }

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUserId())
            .password(user.getPasswordHash())
            .authorities("ROLE_USER")
            .build();
    }
    
    @Bean
    CommandLineRunner runner() {
        return args -> {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String hash = encoder.encode("1234");
            System.out.println("[TEST] Bcrypt hash of 1234: " + hash);
        };
    }

}