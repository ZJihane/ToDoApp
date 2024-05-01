package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class first_screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView logoutIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        EdgeToEdge.enable(this);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        logoutIcon = findViewById(R.id.logoutIcon);

        // Get the TextView
        TextView textView = findViewById(R.id.textView);

        // Fetch user's first name and last name from Firestore
        String uid = mAuth.getUid();
        if (uid != null) {
            db.collection("Users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String firstName = document.getString("first_Name");
                                String lastName = document.getString("last_Name");

                                // Set the text to the TextView
                                textView.setText("Hello, " + firstName + " " + lastName );
                            } else {
                                // Document does not exist
                                textView.setText("Hello,");
                            }
                        } else {
                            // Task failed with an exception
                            textView.setText("Hello,");
                        }
                    });
        } else {
            // User is not authenticated
            textView.setText("Hello,");
        }

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the "Tasks" button
        findViewById(R.id.button2).setOnClickListener(view -> {
            // Start the All_Tasks activity
            startActivity(new Intent(first_screen.this, All_Tasks.class));
        });

        findViewById(R.id.button3).setOnClickListener(view -> {

            startActivity(new Intent(first_screen.this, All_Notes.class));
        });

        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


    }
    private void logout() {
        mAuth.signOut(); // Sign out the current user
        Intent intent = new Intent(first_screen.this, Auth_Activity.class);
        startActivity(intent);
        finish();
    }
}
