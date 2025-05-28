package inhatc.hja.unilife.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.user.dto.UserDto;
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

    // public boolean register(UserDto userDto) {
    //     // 1. 중복 ID 체크
    //     if (userRepository.existsByUserId(userDto.getUserId())) {
    //         return false;
    //     }

    //     // 2. 비밀번호 일치 여부 확인 (안 맞으면 false 반환)
    //     if (!userDto.getPassword().equals(userDto.getPasswordck())) {
    //         return false;
    //     }

    //     // 3. 이메일 인증 여부 확인
    //     if (!"true".equalsIgnoreCase(userDto.getEmailVerified())) {
    //         return false;
    //     }

    //     // 4. User 엔티티 생성 및 저장
    //     User user = new User();
    //     user.setUserId(userDto.getUserId());
    //     user.setUsername(userDto.getUsername());
    //     user.setEmail(userDto.getEmail());
    //     user.setDepartment(userDto.getDepartment());
    //     user.setPasswordHash(passwordEncoder.encode(userDto.getPassword())); // 암호화 저장

    //     userRepository.save(user);
    //     return true;
    // }

    public boolean register(UserDto dto) {
        if (userRepository.existsByUserId(dto.getUserId())) {
            System.out.println("[DEBUG] 중복 아이디 발견: " + dto.getUserId());
            return false;
        }
        // 2. 비밀번호 일치 여부 확인 (안 맞으면 false 반환)
        if (!dto.getPassword().equals(dto.getPasswordck())) {
            return false;
        }

        // 3. 이메일 인증 여부 확인
        if (!"true".equalsIgnoreCase(dto.getEmailVerified())) {
            return false;
        }

        User user = new User();
        user.setId(Long.parseLong(dto.getUserId()));
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setDepartment(dto.getDepartment());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);
        System.out.println("[DEBUG] 저장된 유저 ID: " + saved.getId());

        return true;
    }

}
