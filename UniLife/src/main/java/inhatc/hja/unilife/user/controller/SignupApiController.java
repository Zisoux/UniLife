package inhatc.hja.unilife.user.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inhatc.hja.unilife.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignupApiController {

    private final UserRepository userRepository;

    @GetMapping("/check-id")
    public Map<String, Boolean> checkId(@RequestParam(name = "userId") String userId) {
        boolean exists = userRepository.existsByUserId(userId);
        return Collections.singletonMap("duplicate", exists);
    }
}
