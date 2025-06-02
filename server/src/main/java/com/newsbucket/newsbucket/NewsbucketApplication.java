package com.newsbucket.newsbucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.newsbucket")
public class NewsbucketApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsbucketApplication.class, args);
	}

}