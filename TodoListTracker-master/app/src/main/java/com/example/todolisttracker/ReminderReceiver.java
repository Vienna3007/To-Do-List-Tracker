package com.example.todolisttracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.showNotification(context, "To-Do Reminder", "Don't forget to check your tasks today!");
    }
}
