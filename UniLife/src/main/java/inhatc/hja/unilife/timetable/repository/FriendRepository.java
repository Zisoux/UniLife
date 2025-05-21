package inhatc.hja.unilife.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import inhatc.hja.unilife.timetable.entity.Friend;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByUserIdAndStatus(Long userId, Friend.Status status);

    List<Friend> findByUserIdAndFriendId(Long userId, Long friendId);
}
