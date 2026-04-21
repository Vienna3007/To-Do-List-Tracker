package com.example.todolisttracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        String title = getIntent().getStringExtra("task_title");
        String category = getIntent().getStringExtra("task_category");
        String reminder = getIntent().getStringExtra("task_reminder");
        String subtasks = getIntent().getStringExtra("task_subtasks");
        String priority = getIntent().getStringExtra("task_priority");

        TextView textTitle = findViewById(R.id.textTitle);
        TextView textCategory = findViewById(R.id.textCategory);
        TextView textReminder = findViewById(R.id.textReminder);
        TextView textSubtasks = findViewById(R.id.textSubtasks);
        TextView textPriority = findViewById(R.id.textPriority);

        textTitle.setText(title);
        textCategory.setText("Category: " + category);
        textReminder.setText("Reminder: " + (reminder != null ? reminder : "None"));
        textSubtasks.setText("Subtasks: " + (subtasks != null ? subtasks : "None"));
        textPriority.setText("Priority: " + (priority != null ? priority : "None"));
    }
}

