package inhatc.hja.unilife.user.service;

import inhatc.hja.unilife.user.dto.FriendWithUser;
import inhatc.hja.unilife.user.entity.Friend;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.FriendRepository;
import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.user.dto.SimpleUserDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }
    public List<SimpleUserDto> getFriendList(Long userId) {
        List<Friend> friends = friendRepository.findByUserIdAndStatus(userId, "accepted");
        List<SimpleUserDto> result = new ArrayList<>();

        for (Friend f : friends) {
            User friendUser = f.getFriend();
            result.add(new SimpleUserDto(
            	    friendUser.getId(),
            	    friendUser.getUsername(),
            	    friendUser.getUserId(),
            	    friendUser.getEmail()
            	));

        }

        return result;
    }

    // 학번 기준 친구 목록
    public List<FriendWithUser> getFriendsByUserId(String userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) return new ArrayList<>();
        return getFriends(userOpt.get().getId());
    }

    // 기존 ID 기반 친구 목록
    public List<FriendWithUser> getFriends(Long userId) {
        List<Friend> friends = friendRepository.findByUserIdAndStatus(userId, "accepted");
        List<FriendWithUser> result = new ArrayList<>();
        for (Friend f : friends) {
            User friendUser = f.getFriend();
            result.add(new FriendWithUser(f, friendUser));
        }
        return result;
    }

    // 학번 기준 친구 검색
 // 학번 기준 친구 검색 (이름 or 학번 포함)
    public List<FriendWithUser> searchFriendsByUserId(String userId, String keyword) {
        Optional<User> meOpt = userRepository.findByUserId(userId);
        List<FriendWithUser> result = new ArrayList<>();

        if (meOpt.isEmpty()) return result;

        User me = meOpt.get();

        // 이름 또는 학번에 keyword가 포함된 사용자 목록 (본인은 제외)
        List<User> candidates = userRepository.findByUserIdContainingOrUsernameContaining(keyword, keyword);
        for (User u : candidates) {
            if (!u.getId().equals(me.getId())) {
                result.add(new FriendWithUser(null, u)); // 아직 친구 상태는 없음
            }
        }

        return result;
    }


    // 학번 기준 친구 추가
    @Transactional
    public void addFriendByUserId(String userId, String friendUserId) {
        User me = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        User friend = userRepository.findByUserId(friendUserId)
                .orElseThrow(() -> new IllegalArgumentException("친구 없음"));

        // 자기 자신은 추가 불가
        if (me.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("자기 자신을 친구로 추가할 수 없습니다.");
        }

        // 이미 친구인 경우 방지
        if (friendRepository.findByUserAndFriend(me, friend).size() > 0) {
            throw new IllegalArgumentException("이미 친구로 등록된 사용자입니다.");
        }

        // 1. 정방향 저장
        Friend f1 = new Friend();
        f1.setUser(me);
        f1.setFriend(friend);
        f1.setStatus("accepted");

        // 2. 역방향 저장
        Friend f2 = new Friend();
        f2.setUser(friend);
        f2.setFriend(me);
        f2.setStatus("accepted");

        // 저장
        friendRepository.save(f1);
        friendRepository.save(f2);
    }


    // 학번 기준 친구 중복 확인
    public boolean isAlreadyFriendByUserId(String userId, String friendUserId) {
        Optional<User> me = userRepository.findByUserId(userId);
        Optional<User> friend = userRepository.findByUserId(friendUserId);

        if (me.isEmpty() || friend.isEmpty()) return false;
        return !friendRepository.findByUserAndFriend(me.get(), friend.get()).isEmpty();
    }

    // 학번 기준 친구 삭제
    @Transactional
    public void deleteFriend(String userId, String friendUserId) {
        Optional<User> me = userRepository.findByUserId(userId);
        Optional<User> friend = userRepository.findByUserId(friendUserId);

        if (me.isPresent() && friend.isPresent()) {
            List<Friend> connections = friendRepository.findByUserAndFriend(me.get(), friend.get());
            friendRepository.deleteAll(connections);
        }
    }
}
