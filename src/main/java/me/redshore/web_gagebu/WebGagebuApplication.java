package me.redshore.web_gagebu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WebGagebuApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebGagebuApplication.class, args);
	}

}
