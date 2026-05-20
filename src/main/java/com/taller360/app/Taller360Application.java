package com.taller360.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Taller360Application {

	public static void main(String[] args) {
		SpringApplication.run(Taller360Application.class, args);
	}

}
