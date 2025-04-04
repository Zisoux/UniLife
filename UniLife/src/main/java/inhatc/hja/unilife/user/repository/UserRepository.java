package inhatc.hja.unilife.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.user.repository.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
