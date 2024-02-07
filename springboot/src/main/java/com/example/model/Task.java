package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "reminder")
	private boolean reminder;

	public Task() {
		// Silence is golden...
	}

	public Task(String title, String description, boolean reminder) {
		this.title = title;
		this.description = description;
		this.reminder = reminder;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isReminder() {
		return reminder;
	}

	public void setReminder(boolean isReminder) {
		this.reminder = isReminder;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", desc=" + description + ", reminder=" + reminder + "]";
	}
}