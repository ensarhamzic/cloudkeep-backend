package com.cloudkeep.CloudKeep;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@SpringBootApplication
public class CloudKeepApplication {

	@Value("${cloudinary.url}")
	private static String CLOUDINARY_URL;

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		return new MultipartConfigElement("");
	}

	@Bean(name = "multipartResolver")
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public ServletContextInitializer initializer() {
		return servletContext -> servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", "1000000");
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudKeepApplication.class, args);
	}

}
