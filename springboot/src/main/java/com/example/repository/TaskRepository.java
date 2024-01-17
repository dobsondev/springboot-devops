package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByReminder(boolean reminder);

  List<Task> findByTitleContaining(String title);
}