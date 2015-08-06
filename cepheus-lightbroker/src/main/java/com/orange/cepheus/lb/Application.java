package com.orange.cepheus.lb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by pborscia on 06/08/2015.
 */

@SpringBootApplication
@ComponentScan("com.orange")
public class Application {

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(Application.class);
        application.setShowBanner(false);
        application.run(args);
    }
}
