package com.example.todolisttracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskEditListener editListener;
    private Context context;

    public TaskAdapter(List<Task> taskList, OnTaskEditListener editListener) {
        this.taskList = taskList;
        this.editListener = editListener;
    }

    public interface OnTaskEditListener {
        void onTaskEdit(int position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        String priority = task.getPriority();
        if (priority != null) {
            switch (priority.toLowerCase()) {
                case "high":
                    holder.textPriority.setText("🔴 High");
                    holder.textPriority.setTextColor(Color.RED);
                    break;
                case "medium":
                    holder.textPriority.setText("🟡 Medium");
                    holder.textPriority.setTextColor(Color.parseColor("#FFA500")); // Orange
                    break;
                case "low":
                    holder.textPriority.setText("🟢 Low");
                    holder.textPriority.setTextColor(Color.parseColor("#4CAF50")); // Green
                    break;
                default:
                    holder.textPriority.setText("");
            }
        } else {
            holder.textPriority.setText(""); // Fallback if null
        }

        holder.taskText.setText(task.getTitle());
        holder.checkBox.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (task.getReminderTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            holder.textReminder.setText("⏰ " + sdf.format(task.getReminderTime().getTime()));
            holder.textReminder.setVisibility(View.VISIBLE);
        } else {
            holder.textReminder.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskDetailsActivity.class);
            intent.putExtra("task_title", task.getTitle());
            intent.putExtra("task_category", task.getCategory());
            intent.putExtra("task_reminder", task.getFormattedReminderTime());
            intent.putExtra("task_subtasks", task.getSubtasks());
            intent.putExtra("task_priority", task.getPriority());
            context.startActivity(intent);
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
        });

        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onTaskEdit(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskText;
        TextView textPriority;
        TextView textReminder;
        CheckBox checkBox;
        ImageButton editButton;

        TaskViewHolder(View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
            textPriority = itemView.findViewById(R.id.text_priority);
            textReminder = itemView.findViewById(R.id.text_reminder);
            checkBox = itemView.findViewById(R.id.checkbox);
            editButton = itemView.findViewById(R.id.buttonEdit);
        }
    }

}


