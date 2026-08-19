package inhatc.hja.unilife.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // userId로 유저 조회
    Optional<User> findByUserId(String userId);

    // 유저 이름으로 유저 조회 (정확히 일치)
    Optional<User> findByUsername(String username);

    // userId 중복 확인
    boolean existsByUserId(String userId);

    // ✅ 이름 또는 학번으로 부분 일치 검색 (검색용)
    List<User> findByUserIdContainingOrUsernameContaining(String userIdPart, String usernamePart);
}
