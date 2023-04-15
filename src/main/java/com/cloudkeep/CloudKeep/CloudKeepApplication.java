package com.cloudkeep.CloudKeep;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudKeepApplication {

	@Value("${cloudinary.url}")
	private static String CLOUDINARY_URL;

	public static void main(String[] args) {
		SpringApplication.run(CloudKeepApplication.class, args);
	}

}
