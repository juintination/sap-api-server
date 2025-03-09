package com.jay.sapapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SapApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SapApiApplication.class, args);
	}

}
