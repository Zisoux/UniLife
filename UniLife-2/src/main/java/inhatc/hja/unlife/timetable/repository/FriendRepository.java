package inhatc.hja.unlife.timetable.repository;

import inhatc.hja.unlife.timetable.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByUserIdAndStatus(Long userId, Friend.Status status);

    Friend findByUserIdAndFriendId(Long userId, Long friendId);
}
