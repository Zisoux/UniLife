package inhatc.hja.unilife.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;

@Repository
public interface PortforlioRepository extends JpaRepository<Portfolio, Long>{

}
