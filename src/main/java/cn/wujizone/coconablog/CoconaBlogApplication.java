package cn.wujizone.coconablog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"cn.wujizone.coconablog.mapper"})
public class CoconaBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoconaBlogApplication.class, args);
    }

}
