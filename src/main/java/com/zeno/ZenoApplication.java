package com.zeno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZenoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZenoApplication.class, args);
    }
}
