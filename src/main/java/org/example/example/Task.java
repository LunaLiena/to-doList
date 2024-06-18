package org.example.example;

import java.util.Date;

public class Task {
    private Long id;
    private String description;
    private Date dueDate;
    private int priority;

    // Конструкторы, геттеры, сеттеры и другие методы

    public Task(Long id,String description, Date dueDate, int priority) {
        this.id = id;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public java.sql.Date getDueDate() {
        return (java.sql.Date) dueDate;
    }



    // Другие методы, если необходимо
}
