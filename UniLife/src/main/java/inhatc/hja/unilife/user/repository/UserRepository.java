package inhatc.hja.unilife.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // userId로 유저 조회
    Optional<User> findByUserId(String userId);

    // 유저 이름으로 유저 조회
    Optional<User> findByUsername(String username);

    boolean existsByUserId(String userId);
}