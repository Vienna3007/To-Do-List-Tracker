package com.example.todolisttracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.util.Log;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private EditText editTextTask;
    private Spinner categorySpinner;
    private Spinner recurrenceSpinner;
    private Button buttonAddTask, buttonTime, buttonTheme;
    private Calendar reminderCalendar;
    private boolean isDarkMode;
    private RecyclerView recyclerViewTasks;
    private int editingTaskPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        editTextTask = findViewById(R.id.editTextTask);
        categorySpinner = findViewById(R.id.spinnerCategory);
        recurrenceSpinner = findViewById(R.id.spinnerRecurrence);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonTime = findViewById(R.id.buttonTime);
        buttonTheme = findViewById(R.id.buttonTheme);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> recurrenceAdapter = ArrayAdapter.createFromResource(
                this, R.array.recurrence_options, android.R.layout.simple_spinner_item
        );
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurrenceSpinner.setAdapter(recurrenceAdapter);

        Spinner prioritySpinner = findViewById(R.id.spinnerPriority);
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_options, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        taskList = TaskStorage.loadTasks(this);

        taskAdapter = new TaskAdapter(taskList, position -> {
            Task task = taskList.get(position);
            editTextTask.setText(task.getTitle());
            categorySpinner.setSelection(getCategoryIndex(task.getCategory()));
            recurrenceSpinner.setSelection(getRecurrenceIndex(task.getRecurrence()));
            reminderCalendar = task.getReminderTime();
            editingTaskPosition = position;
            Toast.makeText(this, "Edit mode: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        buttonAddTask.setOnClickListener(view -> {
            String taskTitle = editTextTask.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String recurrence = recurrenceSpinner.getSelectedItem().toString();
            String priority = prioritySpinner.getSelectedItem().toString();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }

            if (taskTitle.isEmpty()) {
                Toast.makeText(this, "Please enter a task!", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task = new Task(taskTitle, false, category, reminderCalendar, recurrence, priority);

            if (editingTaskPosition >= 0) {
                taskList.set(editingTaskPosition, task);
                taskAdapter.notifyItemChanged(editingTaskPosition);
                editingTaskPosition = -1;
                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
            } else {
                taskList.add(task);
                taskAdapter.notifyItemInserted(taskList.size() - 1);
                Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
            }

            TaskStorage.saveTasks(this, taskList);
            taskAdapter.notifyDataSetChanged();  // Important to refresh list

            if (reminderCalendar != null) {
                long triggerTime = reminderCalendar.getTimeInMillis();
                scheduleNotification(taskTitle, triggerTime);
                Log.d("MainActivity", "Reminder set for: " + triggerTime);
            } else {
                Log.d("MainActivity", "No reminder set");
            }

            editTextTask.setText("");
            reminderCalendar = null;
            recurrenceSpinner.setSelection(0);
        });

        buttonTime.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (timePicker, hourOfDay, minute1) -> {
                reminderCalendar = Calendar.getInstance();
                reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                reminderCalendar.set(Calendar.MINUTE, minute1);
                reminderCalendar.set(Calendar.SECOND, 0);
                reminderCalendar.set(Calendar.MILLISECOND, 0);
                Toast.makeText(this, "Reminder set: " + hourOfDay + ":" + String.format("%02d", minute1), Toast.LENGTH_SHORT).show();
            }, hour, minute, true).show();
        });

        buttonTheme.setOnClickListener(view -> {
            isDarkMode = !isDarkMode;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isDarkMode);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task deletedTask = taskList.get(position);
                taskList.remove(position);
                taskAdapter.notifyItemRemoved(position);
                TaskStorage.saveTasks(MainActivity.this, taskList);

                Snackbar.make(recyclerViewTasks, "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> {
                            taskList.add(position, deletedTask);
                            taskAdapter.notifyItemInserted(position);
                            TaskStorage.saveTasks(MainActivity.this, taskList);
                        }).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewTasks);
    }

    private void scheduleNotification(String taskTitle, long triggerTime) {
        Intent notificationIntent = new Intent(this, ReminderBroadcast.class);
        notificationIntent.putExtra("taskTitle", taskTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Log.d("MainActivity", "Notification scheduled for: " + triggerTime);
        }
    }

    private int getCategoryIndex(String category) {
        for (int i = 0; i < categorySpinner.getCount(); i++) {
            if (categorySpinner.getItemAtPosition(i).toString().equalsIgnoreCase(category)) {
                return i;
            }
        }
        return 0;
    }

    private int getRecurrenceIndex(String recurrence) {
        for (int i = 0; i < recurrenceSpinner.getCount(); i++) {
            if (recurrenceSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(recurrence)) {
                return i;
            }
        }
        return 0;
    }
}
