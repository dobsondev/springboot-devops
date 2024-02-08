package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.controller.TaskController;
import com.example.model.Task;
import com.example.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskControllerTests {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTasks() {
        // Mock data
        List<Task> mockTasks = new ArrayList<>();
        mockTasks.add(new Task("Task 1", "Description 1", false));
        mockTasks.add(new Task("Task 2", "Description 2", false));

        when(taskRepository.findAll()).thenReturn(mockTasks);

        // Test the endpoint
        ResponseEntity<List<Task>> response = taskController.getAllTasks(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTasks, response.getBody());
    }

    @Test
    public void testGetTaskById() {
        // Mock data
        Task mockTask = new Task("Task 1", "Description 1", false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Test the endpoint
        ResponseEntity<Task> response = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTask, response.getBody());
    }

    @Test
    public void testCreateTask() {
        // Mock data
        Task inputTask = new Task("Task 1", "Description 1", false);
        Task savedTask = new Task("Task 1", "Description 1", false);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Test the endpoint
        ResponseEntity<Task> response = taskController.createTask(inputTask);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedTask, response.getBody());
    }

    @Test
    public void testUpdateTask() {
        // Mock data
        Task existingTask = new Task("Task 1", "Description 1", false);
        Task updatedTask = new Task("Updated Task", "Updated Description", true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Test the endpoint
        ResponseEntity<Task> response = taskController.updateTask(1L, updatedTask);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTask, response.getBody());
    }

    @Test
    public void testDeleteTask() {
        // Test the endpoint
        ResponseEntity<HttpStatus> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteAllTasks() {
        // Test the endpoint
        ResponseEntity<HttpStatus> response = taskController.deleteAllTasks();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskRepository, times(1)).deleteAll();
    }

    @Test
    public void testFindByCompleted() {
        // Mock data
        List<Task> mockTasks = new ArrayList<>();
        mockTasks.add(new Task("Task 1", "Description 1", true));
        mockTasks.add(new Task("Task 2", "Description 2", true));

        when(taskRepository.findByCompleted(true)).thenReturn(mockTasks);

        // Test the endpoint
        ResponseEntity<List<Task>> response = taskController.findByCompleted();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTasks, response.getBody());
    }

}
