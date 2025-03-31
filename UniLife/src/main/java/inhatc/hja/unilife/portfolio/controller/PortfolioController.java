package inhatc.hja.unilife.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inhatc.hja.unilife.portfolio.repository.PortforlioRepository;
import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
	@Autowired
	private PortforlioRepository portfolioRepository;
	
	@GetMapping
	public List<Portfolio> getAllPortfolios() {
		return portfolioRepository.findAll();
	}
}
