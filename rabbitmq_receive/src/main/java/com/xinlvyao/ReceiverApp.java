package com.xinlvyao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xinlvyao.mapper")
public class ReceiverApp {
    public static void main(String[] args) {
        SpringApplication.run(ReceiverApp.class);
    }
}
