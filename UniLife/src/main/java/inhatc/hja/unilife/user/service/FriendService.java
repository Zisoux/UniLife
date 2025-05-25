package inhatc.hja.unilife.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inhatc.hja.unilife.user.entity.Friend;
import inhatc.hja.unilife.user.repository.FriendRepository;
import inhatc.hja.unilife.user.dto.SimpleUserDto;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 현재 로그인한 유저의 친구 목록을 가져오기
    public List<SimpleUserDto> getFriendList(Long userId) {
        // 1. userId로 유저 찾기
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. user와 관련된 'accepted' 친구 목록 찾기
        List<Friend> friends = friendRepository.findByUserIdAndStatus(user.getId(), "accepted");

        // 3. Friend -> SimpleUserDto로 변환하여 반환
        return friends.stream()
                .map(friend -> {
                    User friendUser = friend.getFriend();  // Friend 엔티티에 friend(User) 연관관계
                    return new SimpleUserDto(friendUser.getId(), friendUser.getUsername(), 
                                              friendUser.getUserId(), friendUser.getEmail(), 
                                              friendUser.getPasswordHash());
                })
                .collect(Collectors.toList());
    }
}