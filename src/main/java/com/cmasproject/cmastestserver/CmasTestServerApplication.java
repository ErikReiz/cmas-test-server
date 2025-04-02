package com.cmasproject.cmastestserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Arrays;

@SpringBootApplication
@EnableWebSecurity
public class CmasTestServerApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(CmasTestServerApplication.class, args);
    }

}
