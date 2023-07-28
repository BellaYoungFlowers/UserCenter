package com.example.myusercenterback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.myusercenterback.mapper")
@EnableScheduling
public class findfriendsBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(findfriendsBackApplication.class, args);
	}

}
