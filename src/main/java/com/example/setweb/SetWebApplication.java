package com.example.setweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.setweb.mapper")

public class SetWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(SetWebApplication.class, args);
	}
}
