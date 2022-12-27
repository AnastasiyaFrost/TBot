package pro.sky.tbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TBotApplication.class, args);
    }

}
