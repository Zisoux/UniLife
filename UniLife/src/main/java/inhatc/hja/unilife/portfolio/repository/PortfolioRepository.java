package inhatc.hja.unilife.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long>{
	// 사용자 ID별 포트폴리오 조회
    List<Portfolio> findByUserIdOrderByCreatedAtDesc(Long userId);
}
