package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import DAO_IMP.UserDaoImpl;
import Model.User;

public class gestion_profil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextLastName, editTextFirstName, editTextPhone;
    private ProgressBar progressBar;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_profil); // Make sure this matches your actual layout resource file
        EdgeToEdge.enable(this);

        // Initialize UI elements
        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> finish());

        editTextLastName = findViewById(R.id.nom);
        editTextFirstName = findViewById(R.id.prenom);
        editTextPhone = findViewById(R.id.tel);
        progressBar = findViewById(R.id.progress_bar);
        updateButton = findViewById(R.id.BttRegister);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Get user ID from Firebase Auth
            String uid = user.getUid();
            if (uid != null) {
                // Fetch data from Firestore
                db.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve data from document
                            String firstName = document.getString("first_Name");
                            String lastName = document.getString("last_Name");
                            String telephone = document.getString("phone_Number");

                            // Update UI elements with retrieved data
                            editTextLastName.setText(lastName);
                            editTextFirstName.setText(firstName);
                            editTextPhone.setText(telephone);
                        }
                    } else {
                        // Handle the error (e.g., show a Toast message)
                        Toast.makeText(gestion_profil.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    }
                    // Hide ProgressBar
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                // Hide ProgressBar if UID is null
                progressBar.setVisibility(View.GONE);
            }
        } else {
            // Hide ProgressBar if user is null
            progressBar.setVisibility(View.GONE);
        }

        // Set up the update button click listener
        updateButton.setOnClickListener(v -> {
            if (user != null) {
                String uid = user.getUid();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String telephone = editTextPhone.getText().toString();

                User updatedUser = new User( firstName, lastName, telephone,uid);
                UserDaoImpl userDAO = new UserDaoImpl();

                // Show the progress bar while updating
                progressBar.setVisibility(View.VISIBLE);

                userDAO.updateUser(updatedUser, uid).thenRun(() -> {
                    runOnUiThread(() -> {
                        // Hide the progress bar after update
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(gestion_profil.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }).exceptionally(ex -> {
                    runOnUiThread(() -> {
                        // Hide the progress bar if there was an error
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(gestion_profil.this, "Error updating profile: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return null;
                });
            }
        });
    }
}
