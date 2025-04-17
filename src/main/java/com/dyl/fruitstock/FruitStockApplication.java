package com.dyl.fruitstock;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
@MapperScan("com.dyl.fruitstock.mapper")
public class FruitStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(FruitStockApplication.class, args);

    }

}
