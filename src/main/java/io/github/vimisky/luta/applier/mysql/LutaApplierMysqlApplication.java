package io.github.vimisky.luta.applier.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class LutaApplierMysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(LutaApplierMysqlApplication.class, args);
    }

}
