package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	@Bean
        public RestTemplate restTemplate() {
                return new RestTemplate();
        }
        
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
