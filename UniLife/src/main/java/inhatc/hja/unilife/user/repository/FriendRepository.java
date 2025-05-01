package inhatc.hja.unilife.user.repository;

import inhatc.hja.unilife.user.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // userId를 기준으로 상태가 'accepted'인 친구 목록 찾기
    List<Friend> findByUserIdAndStatus(Long userId, String status);

    // 친구 id를 기준으로 상태가 'accepted'인 친구 목록 찾기
    List<Friend> findByFriendIdAndStatus(Long friendId, String status);

    // 친구 관계(유저, 친구) 기준으로 상태가 'accepted'인 친구 찾기
    List<Friend> findByUserIdAndFriendIdAndStatus(Long userId, Long friendId, String status);
}