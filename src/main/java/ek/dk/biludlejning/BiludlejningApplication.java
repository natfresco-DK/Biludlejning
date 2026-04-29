package ek.dk.biludlejning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class})
public class BiludlejningApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiludlejningApplication.class, args);
    }

}
