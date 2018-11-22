package com.tdiinc.taskManagement;

import com.tdiinc.taskManagement.utils.DateUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }

    @Bean(name = "dateUtil")
    public DateUtil getDateUtil() {
        return new DateUtil();
    }

}
