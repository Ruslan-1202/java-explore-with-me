package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ru.practicum.stats.client", "ru.practicum.main"})
public class MainApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApp.class, args);
//        StatClient statClient = context.getBean(StatClient.class);
//        StatsCreateDTO statsCreateDTO = new StatsCreateDTO("dsd", "wdsd", "dsdsds", LocalDateTime.now());
//        statClient.addHit(statsCreateDTO);
    }
}
