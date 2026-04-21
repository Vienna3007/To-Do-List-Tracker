package com.example.todolisttracker;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
        private static final String PREFS_NAME = "task_prefs";
        private static final String TASKS_KEY = "task_list";

        public static void saveTasks(Context context, List<Task> tasks) {
            Gson gson = new Gson();
            String json = gson.toJson(tasks);
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(TASKS_KEY, json).apply();
        }

        public static List<Task> loadTasks(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(TASKS_KEY, null);
            Type type = new TypeToken<List<Task>>() {}.getType();
            return json != null ? new Gson().fromJson(json, type) : new ArrayList<>();
        }
    }
