package inhatc.hja.unilife.timetable.service;

import inhatc.hja.unilife.timetable.dto.FriendWithUser;
import inhatc.hja.unilife.timetable.entity.Friend;
import inhatc.hja.unilife.user.repository.entity.User;
import inhatc.hja.unilife.timetable.repository.FriendRepository;
//✅ 올바른 import
import inhatc.hja.unilife.user.repository.UserRepository;
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
        // A -> B 관계 모두 삭제
        List<Friend> directRelations = friendRepository.findByUserIdAndFriendId(userId, friendId);
        for (Friend f : directRelations) {
            friendRepository.delete(f);
        }

        // B -> A 관계 모두 삭제
        List<Friend> reverseRelations = friendRepository.findByUserIdAndFriendId(friendId, userId);
        for (Friend f : reverseRelations) {
            friendRepository.delete(f);
        }
    }



   
}
