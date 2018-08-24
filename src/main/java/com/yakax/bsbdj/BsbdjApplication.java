package com.yakax.bsbdj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yakax.bsbdj.mapper")
public class BsbdjApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsbdjApplication.class, args);
    }
}
