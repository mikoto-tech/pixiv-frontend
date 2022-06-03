package net.mikoto.pixiv.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author mikoto
 */
@SpringBootApplication
@ComponentScan("net.mikoto.pixiv")
public class PixivFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixivFrontendApplication.class, args);
    }

}
