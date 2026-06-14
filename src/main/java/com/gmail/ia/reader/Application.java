package com.gmail.ia.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");
		SpringApplication.run(Application.class, args);
	}

}
