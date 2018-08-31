package com.yakax.bsbdj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//扫描mapper 包 自动生成mapper 对应的实现类
@MapperScan("com.yakax.bsbdj.mapper")
//启用任务调度
@EnableScheduling
public class BsbdjApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsbdjApplication.class, args);
    }
}
