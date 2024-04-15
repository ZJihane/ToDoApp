package com.example.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Model.Task;

public class TaskDetailsActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dueDateEditText;
    private Spinner statusSpinner;
    private Spinner prioritySpinner;
    private Calendar dueDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize EditTexts and Spinners
        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        dueDateEditText = findViewById(R.id.edit_text_due_date);
        statusSpinner = findViewById(R.id.spinner_status);
        prioritySpinner = findViewById(R.id.spinner_priority);

        // Get task details from intent
        Task task = getIntent().getParcelableExtra("task");

        // Set task values in EditTexts and Spinners
        if (task != null) {
            titleEditText.setText(task.getTitle());
            descriptionEditText.setText(task.getDescription());

            // Initialize dueDateCalendar with task's due date
            dueDateCalendar = Calendar.getInstance();
            dueDateCalendar.setTime(task.getDueDate());

            // Update dueDateEditText with task's due date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            dueDateEditText.setText(dateFormat.format(dueDateCalendar.getTime()));

            // Set status spinner selection
            ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                    this, R.array.status_array, android.R.layout.simple_spinner_item);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(statusAdapter);
            if (task.getStatus() != null) {
                int statusPosition = statusAdapter.getPosition(task.getStatus().toString());
                statusSpinner.setSelection(statusPosition);
            }

            // Set priority spinner selection
            ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                    this, R.array.priority_array, android.R.layout.simple_spinner_item);
            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            prioritySpinner.setAdapter(priorityAdapter);
            if (task.getPriority() != null) {
                int priorityPosition = priorityAdapter.getPosition(task.getPriority().toString());
                prioritySpinner.setSelection(priorityPosition);
            }
        }

        // Set click listener for dueDateEditText to show DatePickerDialog
        dueDateEditText.setOnClickListener(v -> showDatePickerDialog());
    }

    // Method to display DatePickerDialog
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dueDateCalendar.set(Calendar.YEAR, year);
                        dueDateCalendar.set(Calendar.MONTH, monthOfYear);
                        dueDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Update dueDateEditText with selected date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dueDateEditText.setText(dateFormat.format(dueDateCalendar.getTime()));
                    }
                },
                dueDateCalendar.get(Calendar.YEAR),
                dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
