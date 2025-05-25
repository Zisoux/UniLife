package inhatc.hja.unilife.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.user.dto.SimpleUserDto;
import inhatc.hja.unilife.user.entity.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt 암호화

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean register(SimpleUserDto dto) {
        if (userRepository.existsByUserId(dto.getUserId())) {
            return false; // 이미 존재하는 ID
        }

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash())); // BCrypt 암호화

        userRepository.save(user);
        return true;
    }
}
