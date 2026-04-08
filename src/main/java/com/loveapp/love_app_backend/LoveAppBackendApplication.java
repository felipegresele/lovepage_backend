package com.loveapp.love_app_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoveAppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoveAppBackendApplication.class, args);
	}

}
