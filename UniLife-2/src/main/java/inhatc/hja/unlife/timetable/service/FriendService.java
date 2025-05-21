package inhatc.hja.unlife.timetable.service;

import inhatc.hja.unlife.timetable.entity.Friend;
import inhatc.hja.unlife.timetable.entity.User;
import inhatc.hja.unlife.timetable.repository.FriendRepository;
import inhatc.hja.unlife.timetable.repository.UserRepository;
import inhatc.hja.unlife.timetable.dto.FriendWithUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 친구 목록 (이름 포함)
    public List<FriendWithUser> getFriends(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, Friend.Status.accepted)
                .stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getFriendId()).orElse(null);
                    if (user != null) {
                        return new FriendWithUser(friend.getFriendId(), user.getUsername(), user.getEmail());
                    } else {
                        return new FriendWithUser(friend.getFriendId(), "(탈퇴한 사용자)", null);
                    }
                })
                .toList();
    }

    // 검색
    public List<FriendWithUser> searchFriends(Long userId, String keyword) {
        return friendRepository.findByUserIdAndStatus(userId, Friend.Status.accepted)
                .stream()
                .filter(friend -> {
                    User user = userRepository.findById(friend.getFriendId()).orElse(null);
                    return user != null && user.getUsername().contains(keyword);
                })
                .map(friend -> {
                    User user = userRepository.findById(friend.getFriendId()).orElse(null);
                    return new FriendWithUser(friend.getFriendId(), user.getUsername(), user.getEmail());
                })
                .toList();
    }

 // FriendService.java

    public void addFriend(Long userId, Long friendId) {
        // A -> B 추가
        Friend friend1 = new Friend();
        friend1.setUserId(userId);
        friend1.setFriendId(friendId);
        friend1.setStatus(Friend.Status.accepted);
        friendRepository.save(friend1);

        // B -> A 추가
        Friend friend2 = new Friend();
        friend2.setUserId(friendId);
        friend2.setFriendId(userId);
        friend2.setStatus(Friend.Status.accepted);
        friendRepository.save(friend2);
    }

    public void deleteFriend(Long userId, Long friendId) {
        // A -> B 삭제
        Friend friend1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend1 != null) {
            friendRepository.delete(friend1);
        }

        // B -> A 삭제
        Friend friend2 = friendRepository.findByUserIdAndFriendId(friendId, userId);
        if (friend2 != null) {
            friendRepository.delete(friend2);
        }
    }


   
}
