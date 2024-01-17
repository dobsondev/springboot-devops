package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.example.repository.TaskRepository;
import com.example.model.Task;

@SpringBootApplication
public class SpringBootApp {

    @Autowired private TaskRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApp.class, args);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        this.repository.save(new Task("Task #1", "Description for task 1.", true));
        this.repository.save(new Task("Task #2", "Description for task 2.", true));
        this.repository.save(new Task("Task #3", "Description for task 3.", true));
    }

}