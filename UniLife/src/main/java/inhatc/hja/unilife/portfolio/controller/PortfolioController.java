package inhatc.hja.unilife.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inhatc.hja.unilife.portfolio.repository.dto.PortfolioDTO;
import inhatc.hja.unilife.portfolio.service.PortfolioService;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/portfolios")
public class PortfolioController {

	@Autowired
	private PortfolioService portfolioService;

	/** 포트폴리오 목록 조회 */
	@GetMapping
	public String listPortfolios(Model model, @RequestParam(name = "sortBy", defaultValue = "date") String sortBy,
			@RequestParam(name = "searchKeyword", required = false) String searchKeyword) {

		// 날짜 가져오기
		LocalDate today = LocalDate.now();
		String date = today.toString(); // yyyy-MM-dd

		// 요일 가져오기
		String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

		model.addAttribute("todayDate", date);
		model.addAttribute("dayOfWeek", dayOfWeek);

		// 현재 로그인한 사용자의 ID 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(auth.getName());

		List<PortfolioDTO> portfolios = portfolioService.searchPortfolios(sortBy, searchKeyword);

		model.addAttribute("userId", userId);
		model.addAttribute("portfolios", portfolios);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("searchKeyword", searchKeyword);

		return "portfolio/list";
	}

	/** 포트폴리오 업로드 페이지 */
	@GetMapping("/upload")
	public String showUploadForm(Model model) {
		// 날짜 가져오기
		LocalDate today = LocalDate.now();
		String date = today.toString(); // yyyy-MM-dd

		// 요일 가져오기
		String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

		model.addAttribute("todayDate", date);
		model.addAttribute("dayOfWeek", dayOfWeek);

		PortfolioDTO portfolioDTO = new PortfolioDTO();
		
		// 현재 로그인한 사용자의 ID 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(auth.getName());
		portfolioDTO.setUserId(userId);

		model.addAttribute("portfolioDTO", portfolioDTO);
		return "portfolio/upload";
	}

	/** 포트폴리오 업로드 처리 */
	@PostMapping("/upload")
	public String uploadPortfolio(@ModelAttribute("portfolioDTO") PortfolioDTO portfolioDTO,
			@RequestParam(name = "file") MultipartFile file, RedirectAttributes redirectAttributes) {

		// 현재 로그인한 사용자의 ID 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(auth.getName());
		portfolioDTO.setUserId(userId);

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "파일을 선택해주세요.");
			return "redirect:/portfolios/upload";
		}

		String fileName = file.getOriginalFilename();
		if (fileName == null || !portfolioService.isValidFileType(fileName)) {
			redirectAttributes.addFlashAttribute("errorMessage", "지원하지 않는 파일 형식입니다.");
			return "redirect:/portfolios/upload";
		}

		portfolioService.savePortfolio(portfolioDTO, file, userId);
		redirectAttributes.addFlashAttribute("successMessage", "포트폴리오가 성공적으로 업로드되었습니다.");

		return "redirect:/portfolios";
	}

	/** 포트폴리오 상세 조회 */
	@GetMapping("/{id}")
	public String viewPortfolio(@PathVariable("id") Long id, Model model) {
		// 날짜 가져오기
		LocalDate today = LocalDate.now();
		String date = today.toString(); // yyyy-MM-dd

		// 요일 가져오기
		String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

		model.addAttribute("todayDate", date);
		model.addAttribute("dayOfWeek", dayOfWeek);

		PortfolioDTO portfolioDTO = portfolioService.getPortfolioById(id);
		model.addAttribute("portfolio", portfolioDTO);
		return "portfolio/view";
	}

	/** 포트폴리오 삭제 */
	@GetMapping("/{id}/delete")
	public String deletePortfolio(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		// 현재 로그인한 사용자의 ID 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(auth.getName());

		portfolioService.deletePortfolio(id);
		redirectAttributes.addFlashAttribute("successMessage", "포트폴리오가 삭제되었습니다.");

		return "redirect:/portfolios";
	}
}