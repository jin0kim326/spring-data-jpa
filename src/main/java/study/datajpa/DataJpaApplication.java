package study.datajpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@Slf4j
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") //스프링부트 생략가능
public class DataJpaApplication {
	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
		log.info("✅ Server Started... ");
	}
}
