package com.gradgoals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {

    public static void main(String[] args) {
        // This line starts the whole Spring framework
        SpringApplication.run(Server.class, args);
    }
}