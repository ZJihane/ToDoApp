package com.example.todoapp;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import Model.Task;
import DAO.TaskDAO;
import DAO_IMP.TaskDaoImpl;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDueDate;
    private Spinner spinnerStatus;
    private Spinner spinnerPriority;
    private Button buttonAddTask;
    private TextView textViewError;
    private Calendar calendar;

    private FirebaseFirestore db;
    private TaskDAO taskDAO;

    FirebaseUser user_mAuth ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ImageButton backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_mAuth = mAuth.getCurrentUser();


        taskDAO = new TaskDaoImpl();
        calendar = Calendar.getInstance();

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDueDate = findViewById(R.id.edit_text_due_date);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerPriority = findViewById(R.id.spinner_priority);
        buttonAddTask = findViewById(R.id.button_add_task);
        textViewError = findViewById(R.id.text_view_error);

        // Populate status spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Populate priority spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this, R.array.priority_options, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        editTextDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(user_mAuth.getUid());
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDueDateEditText();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDueDateEditText() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextDueDate.setText(sdf.format(calendar.getTime()));
    }

    private void addTask(String UID) {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dueDateString = editTextDueDate.getText().toString().trim();
        Date dueDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            dueDate = dateFormat.parse(dueDateString);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String status = spinnerStatus.getSelectedItem().toString();
        String priority = spinnerPriority.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || dueDateString.isEmpty()) {
            textViewError.setText("Please fill in all fields");
            return;
        }

        // Create a new Task object with UID
        Task task = new Task(title, description, dueDate, Task.TaskStatus.valueOf(status.toUpperCase()), Task.TaskPriority.valueOf(priority.toUpperCase()), UID);

        // Call the addTask method from TaskDaoImpl
        taskDAO.addTask(db, task,
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddTaskActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after adding the task
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTaskActivity.this, "Failed to add task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
