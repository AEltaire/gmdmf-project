package net.zjitc;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKnife4j
@EnableScheduling
@SpringBootApplication
public class GmdmfServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmdmfServiceApplication.class, args);
    }
}
