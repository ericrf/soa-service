package br.ufpr.sept.soa.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"br.ufpr.sept.soa.components"})
@EntityScan(value = "br.ufpr.sept.soa.domain")
@EnableJpaRepositories({"br.ufpr.sept.soa.components"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}