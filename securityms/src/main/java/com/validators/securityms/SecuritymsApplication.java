package com.validators.securityms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SecuritymsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuritymsApplication.class, args);
	}

	@Bean
	@LoadBalanced // Enables service discovery for RestTemplate
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
