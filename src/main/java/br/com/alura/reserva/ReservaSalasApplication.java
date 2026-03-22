package br.com.alura.reserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"controller", "service", "exception", "repository"})
@EntityScan(basePackages = "model")
@EnableJpaRepositories(basePackages = "repository")
public class ReservaSalasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservaSalasApplication.class, args);
    }
}
