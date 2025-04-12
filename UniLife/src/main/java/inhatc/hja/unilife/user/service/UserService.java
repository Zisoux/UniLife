package inhatc.hja.unilife.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.user.repository.entity.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
