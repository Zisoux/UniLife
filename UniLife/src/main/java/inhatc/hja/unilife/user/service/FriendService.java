package inhatc.hja.unilife.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inhatc.hja.unilife.user.entity.Friend;
import inhatc.hja.unilife.user.repository.FriendRepository;
import inhatc.hja.unilife.user.dto.FriendWithUser;
import inhatc.hja.unilife.user.dto.SimpleUserDto;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
                    User friendUser = friend.getFriend(); // Friend 엔티티에 friend(User) 연관관계
                    return new SimpleUserDto(friendUser.getId(), friendUser.getUsername(),
                            friendUser.getUserId(), friendUser.getEmail(),
                            friendUser.getPasswordHash());
                })
                .collect(Collectors.toList());
    }

    // 친구 목록 (이름 포함)
    public List<FriendWithUser> getFriends(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, "accepted") // 문자열로 직접 비교
                .stream()
                .map(friend -> {
                    User friendUser = friend.getFriend();
                    if (friendUser != null) {
                        return new FriendWithUser(friendUser.getId(), friendUser.getUsername(), friendUser.getEmail());
                    } else {
                        return new FriendWithUser(null, "(탈퇴한 사용자)", null);
                    }
                })
                .toList();
    }

    // 검색
    public List<FriendWithUser> searchFriends(Long userId, String keyword) {
        return friendRepository.findByUserIdAndStatus(userId, "accepted")
                .stream()
                .filter(friend -> {
                    User user = friend.getFriend();
                    return user != null && user.getUsername().contains(keyword);
                })
                .map(friend -> {
                    User user = friend.getFriend();
                    return new FriendWithUser(user.getId(), user.getUsername(), user.getEmail());
                })
                .toList();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 없음"));

        // A -> B 추가
        Friend friend1 = new Friend();
        friend1.setUser(user);
        friend1.setFriend(friendUser);
        friend1.setStatus("accepted");
        friendRepository.save(friend1);

        // B -> A 추가
        Friend friend2 = new Friend();
        friend2.setUser(friendUser);
        friend2.setFriend(user);
        friend2.setStatus("accepted");
        friendRepository.save(friend2);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 없음"));

        // A -> B 관계 삭제
        List<Friend> directRelations = friendRepository.findByUserAndFriend(user, friendUser);
        for (Friend f : directRelations) {
            friendRepository.delete(f);
        }

        // B -> A 관계 삭제
        List<Friend> reverseRelations = friendRepository.findByUserAndFriend(friendUser, user);
        for (Friend f : reverseRelations) {
            friendRepository.delete(f);
        }
    }

    public boolean isAlreadyFriend(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId).size() > 0;
    }

}