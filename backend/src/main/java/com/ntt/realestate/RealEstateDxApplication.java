package com.ntt.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class RealEstateDxApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealEstateDxApplication.class, args);
	}
}
