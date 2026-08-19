package inhatc.hja.unilife.user.repository;

import inhatc.hja.unilife.user.entity.Friend;
import inhatc.hja.unilife.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // ✔ 수락된 친구 목록
	List<Friend> findByUserIdAndStatus(Long userId, String status);
	List<Friend> findByUserAndFriend(User user, User friend);

    // ✔ 특정 관계 + 상태로 친구 조회
    List<Friend> findByUserIdAndFriendIdAndStatus(Long userId, Long friendId, String status);

    // ❌ 현재 사용되지 않음 → 제거 가능
    // List<Friend> findByUserAndFriend(User user, User friend);

    // ✅ 친구 관계 존재 여부 확인용
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    // ✅ 친구 삭제용
    void deleteByUserIdAndFriendId(Long userId, Long friendId);

    // ✅ 상태 관계 없이 유저-친구 관계 조회
    List<Friend> findByUserIdAndFriendId(Long userId, Long friendId);
}
