package com.example.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import DAO_IMP.TaskDaoImpl;
import Model.Task;

public class TaskDetailsActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dueDateEditText;
    private Spinner statusSpinner;
    private Spinner prioritySpinner;
    private Button updateButton;
    private FirebaseFirestore db;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize views
        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        dueDateEditText = findViewById(R.id.edit_text_due_date);
        statusSpinner = findViewById(R.id.spinner_status);
        prioritySpinner = findViewById(R.id.spinner_priority);
        updateButton = findViewById(R.id.buttonUpdateTask);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set click listener for update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });

        // Get task details from intent
        Task task = getIntent().getParcelableExtra("task");

        // Populate fields with task details
        if (task != null) {
            titleEditText.setText(task.getTitle());
            descriptionEditText.setText(task.getDescription());

            // Format the due date to display only the date part
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDueDate = dateFormat.format(task.getDueDate());
            dueDateEditText.setText(formattedDueDate);

            // For status spinner
            ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                    TaskDetailsActivity.this, R.array.status_array, android.R.layout.simple_spinner_item);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(statusAdapter);
            int statusPosition = statusAdapter.getPosition(task.getStatus().toString());
            statusSpinner.setSelection(statusPosition);

            // For priority spinner
            ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                    TaskDetailsActivity.this, R.array.priority_array, android.R.layout.simple_spinner_item);
            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            prioritySpinner.setAdapter(priorityAdapter);
            int priorityPosition = priorityAdapter.getPosition(task.getPriority().toString());
            prioritySpinner.setSelection(priorityPosition);
        }

        // Set click listener for due date field to open date picker dialog
        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Initialize date picker listener
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dueDateEditText.setText(dateFormat.format(calendar.getTime()));
            }
        };
    }

    private void updateTask() {
        // Retrieve values from EditText and Spinners
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String dueDateString = dueDateEditText.getText().toString();
        String status = statusSpinner.getSelectedItem().toString();
        String priority = prioritySpinner.getSelectedItem().toString();

        Date dueDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            dueDate = dateFormat.parse(dueDateString);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Get the original task from intent
        Task originalTask = getIntent().getParcelableExtra("task");


        // Create Task object with updated values and original ID
        Task updatedTask = new Task(title, description, dueDate,
                Task.TaskStatus.valueOf(status), Task.TaskPriority.valueOf(priority));

        // Call DAO method to update task in Firestore
        TaskDaoImpl taskDao = new TaskDaoImpl();
        taskDao.updateTask(db, updatedTask, originalTask.getTaskId(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(TaskDetailsActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after successful update
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TaskDetailsActivity.this, "Failed to update task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskDetailsActivity.this,
                dateSetListener, year, month, dayOfMonth);
        datePickerDialog.show();
    }
}
