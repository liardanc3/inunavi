package com.maru.inunavi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class InunaviApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(InunaviApplication.class, args);
	}

}
