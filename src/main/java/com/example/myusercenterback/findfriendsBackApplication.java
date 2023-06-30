package com.example.myusercenterback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.myusercenterback.mapper")
public class findfriendsBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(findfriendsBackApplication.class, args);
	}

}
