package com.example.todoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import DAO_IMP.TaskDaoImpl;
import Model.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class All_Tasks extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    private FloatingActionButton fabAddTask;
    private List<Task> allTasks;
    private EditText searchEditText;



    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        ImageButton backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recycler_tasks);
        searchEditText = findViewById(R.id.edit_text_search);
        myAdapter = new MyAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allTasks = new ArrayList<>();
        fabAddTask = findViewById(R.id.fab_add_task);
        // Corrected to use findViewById(R.id.logoutIcon)

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(All_Tasks.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        getTasks();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set OnClickListener for logoutIcon

    }

    // Method to show the ProgressDialog
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading.......");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    // Method to hide the ProgressDialog
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    // Method to get tasks
    public void getTasks() {
        showProgressDialog();
        TaskDaoImpl taskDao = new TaskDaoImpl();
        FirebaseApp.initializeApp(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUid = mAuth.getCurrentUser().getUid();

        // Call the method to fetch tasks filtered by UID
        taskDao.getTasksByUid(db, currentUserUid, new OnSuccessListener<List<Task>>() {
            @Override
            public void onSuccess(List<Task> data) {
                hideProgressDialog();
                allTasks = data;
                myAdapter.setData(allTasks);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                hideProgressDialog();
                // Handle failure
            }
        });
    }



    private void filterTasks(String query) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredTasks.add(task);
            }
        }
        myAdapter.setData(filteredTasks);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getTasks();
    }


}