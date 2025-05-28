package inhatc.hja.unilife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UniLifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniLifeApplication.class, args);
	}

}
