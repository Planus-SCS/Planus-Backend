package scs.planus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlanusApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanusApplication.class, args);
	}

}
