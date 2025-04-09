package inhatc.hja.unilife.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inhatc.hja.unilife.portfolio.repository.dto.PortfolioDTO;
import inhatc.hja.unilife.portfolio.service.PortfolioService;

import java.util.List;

@Controller
@RequestMapping("/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    private static final Long FIXED_USER_ID = 999L;  // 고정된 userId

    /** 포트폴리오 목록 조회 */
    @GetMapping
    public String listPortfolios(
            Model model,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {

        List<PortfolioDTO> portfolios;

        // userId가 없으면 고정된 userId를 사용 **테스트용입니다!!**
        if (userId == null) {
            userId = FIXED_USER_ID;
        }

        // 포트폴리오 목록을 정렬 기준에 맞게 가져오기
        portfolios = portfolioService.searchPortfolios(sortBy, searchKeyword);  // 키워드도 추가된 메서드 호출

        model.addAttribute("userId", userId);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("sortBy", sortBy);  // 선택된 정렬 기준을 전달
        model.addAttribute("searchKeyword", searchKeyword);  // 입력된 키워드를 다시 폼에 전달

        return "portfolio/list";
    }

    /** 포트폴리오 업로드 페이지 */
    @GetMapping("/upload")
    public String showUploadForm(Model model, @RequestParam(name = "userId", required = false) Long userId) {
        PortfolioDTO portfolioDTO = new PortfolioDTO();

        // userId가 없으면 고정된 userId를 설정 **테스트용입니다!!**
        if (userId == null) {
            portfolioDTO.setUserId(FIXED_USER_ID);
        } else {
            portfolioDTO.setUserId(userId);
        }

        model.addAttribute("portfolioDTO", portfolioDTO);
        return "portfolio/upload";
    }

    /** 포트폴리오 업로드 처리 */
    @PostMapping("/upload")
    public String uploadPortfolio(
            @ModelAttribute PortfolioDTO portfolioDTO,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "userId", required = false) Long userId) {

        // userId가 없으면 고정된 userId를 사용 **테스트용입니다!!**
        if (userId == null) {
            portfolioDTO.setUserId(FIXED_USER_ID);
        } else {
            portfolioDTO.setUserId(userId);
        }

        // 파일 유효성 검사
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "파일을 선택해주세요.");
            return "redirect:/portfolios/upload" + (portfolioDTO.getUserId() != null ? "?userId=" + portfolioDTO.getUserId() : "");
        }

        // 파일 형식 검사
        String fileName = file.getOriginalFilename();
        if (fileName == null || !portfolioService.isValidFileType(fileName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "지원하지 않는 파일 형식입니다.");
            return "redirect:/portfolios/upload" + (portfolioDTO.getUserId() != null ? "?userId=" + portfolioDTO.getUserId() : "");
        }

        portfolioService.savePortfolio(portfolioDTO, file, userId); // userId를 함께 전달
        redirectAttributes.addFlashAttribute("successMessage", "포트폴리오가 성공적으로 업로드되었습니다.");

        return "redirect:/portfolios?userId=" + portfolioDTO.getUserId();
    }

    /** 포트폴리오 상세 조회 */
    @GetMapping("/{id}")
    public String viewPortfolio(@PathVariable("id") Long id, Model model) {
        PortfolioDTO portfolioDTO = portfolioService.getPortfolioById(id);
        model.addAttribute("portfolio", portfolioDTO);
        return "portfolio/view";
    }

    /** 포트폴리오 삭제 */
    @GetMapping("/{id}/delete")
    public String deletePortfolio(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "userId", required = false) Long userId) {

        // userId가 없으면 고정된 userId를 사용 **테스트용입니다!!**
        if (userId == null) {
            userId = FIXED_USER_ID;
        }

        portfolioService.deletePortfolio(id);
        redirectAttributes.addFlashAttribute("successMessage", "포트폴리오가 삭제되었습니다.");

        return "redirect:/portfolios?userId=" + userId;
    }

    // 파일 다운로드
    @GetMapping("/download/{portfolioId}")
    public ResponseEntity<?> downloadPortfolioFile(@PathVariable Long portfolioId) {
        return portfolioService.downloadFile(portfolioId);
    }
}
