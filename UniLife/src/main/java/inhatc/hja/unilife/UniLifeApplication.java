package inhatc.hja.unilife;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import inhatc.hja.unilife.portfolio.service.PortfolioService;

@SpringBootApplication // 기본적으로 현재 패키지(inhatc.hja.unilife) 이하를 자동 스캔
public class UniLifeApplication {
    
    @Bean
    CommandLineRunner init(PortfolioService portfolioService) {
        return args -> {
            // 애플리케이션 시작 시 업로드 디렉토리 초기화
            portfolioService.init();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(UniLifeApplication.class, args);
    }
}
