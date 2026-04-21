package com.example.todolisttracker;

import java.util.Calendar;
import java.util.Locale;

public class Task {
    private String title;
    private boolean completed;
    private String category;
    private String priority;
    private String subtasks;
    private Calendar reminderTime;
    private String recurrence;

    public Task(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    public Task(String title, boolean completed, String category, Calendar reminderTime, String recurrence, String priority) {
        this.title = title;
        this.completed = completed;
        this.category = category;
        this.reminderTime = reminderTime;
        this.recurrence = recurrence;
        this.priority = priority;
    }

    public Task(String title, boolean completed, String category, Calendar reminderTime) {
        this.title = title;
        this.completed = completed;
        this.category = category;
        this.reminderTime = reminderTime;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getCategory() {
        return category;
    }

    public Calendar getReminderTime() {
        return reminderTime;
    }

    public String getPriority() {
        return priority;
    }

    public String getSubtasks() {
        return subtasks;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public String getFormattedReminderTime() {
        if (reminderTime == null) return null;
        int hour = reminderTime.get(Calendar.HOUR_OF_DAY);
        int minute = reminderTime.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setReminderTime(Calendar reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setSubtasks(String subtasks) {
        this.subtasks = subtasks;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
}




