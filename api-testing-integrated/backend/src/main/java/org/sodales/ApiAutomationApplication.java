package org.sodales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.sodales", "reader", "runner", "model"})
public class ApiAutomationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiAutomationApplication.class, args);
    }
}
