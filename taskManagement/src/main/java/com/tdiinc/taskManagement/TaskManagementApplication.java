package com.tdiinc.taskManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tdiinc.taskManagement.model.TaskDB;
import com.tdiinc.taskManagement.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class TaskManagementApplication {

    private static final Logger log = LoggerFactory.getLogger(TaskManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }

    @Bean(name = "dateUtil")
    public DateUtil getDateUtil() {
        return new DateUtil();
    }

    @Bean(name = "jsonFilePath")
    public String getJSONFilePath() {
        String userHomeFolder = System.getProperty("user.home");
        return userHomeFolder + "/tasks" + "/" + "data.json";
    }

    @Bean(name = "taskDB")
    public TaskDB getTaskDB() {
        initDataFolder();

        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String content = new String(Files.readAllBytes(Paths.get(getJSONFilePath())));
            return (gson.fromJson(content, TaskDB.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void initDataFolder() {
        String userHomeFolder = System.getProperty("user.home");

        Path path = Paths.get(userHomeFolder + "/tasks");

        if (!Files.exists(path)) {

            try {
                Files.createDirectory(path);

                //init initialFile
                Path tasksFilePath = Paths.get(getJSONFilePath());
                try {
                    TaskDB newTaskDB = new TaskDB();

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Files.write(tasksFilePath, gson.toJson(newTaskDB).getBytes());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

            } catch (IOException e) {
                log.error(e.getMessage());
            }

        } else {
            Path tasksFilePath = Paths.get(getJSONFilePath());
            if (!Files.exists(tasksFilePath)) {
                try {
                    TaskDB newTaskDB = new TaskDB();

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Files.write(tasksFilePath, gson.toJson(newTaskDB).getBytes());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }

        }

    }
}
