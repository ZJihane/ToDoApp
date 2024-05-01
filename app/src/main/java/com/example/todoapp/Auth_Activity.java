package com.example.todoapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import DAO.UserDAO;
import DAO_IMP.UserDaoImpl;
import Model.User;

public class Auth_Activity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout espace1;
    LinearLayout espace2;
    LinearLayout espace3;

    EditText nom;
    EditText prenom;
    EditText email;
    EditText tel;
    EditText login;
    EditText password;
    EditText password_signup; // New EditText for password during signup
    TextView messageAuth;
    Button button_login;
    Button button_register;
    Button button_signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();

        espace1 = findViewById(R.id.espace1);
        espace2 = findViewById(R.id.espace2);
        espace3 = findViewById(R.id.espace3);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        tel = findViewById(R.id.tel);
        password = findViewById(R.id.password);
        password_signup = findViewById(R.id.register_password); // Initialize the new EditText
        //messageAuth = findViewById(R.id.messageAuth);

        button_login = findViewById(R.id.BttLogin);
        button_register = findViewById(R.id.BttRegister);
        button_signup = findViewById(R.id.BttsignUp);
        button_login.setOnClickListener(this);
        button_register.setOnClickListener(this);
        button_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.BttLogin) {
            signin(login.getText().toString(), password.getText().toString());
        } else if (view.getId() == R.id.BttsignUp) {
            espace2.setVisibility(View.VISIBLE);
            espace3.setVisibility(View.GONE);
            espace1.setVisibility(View.GONE);
        } else if (view.getId() == R.id.BttRegister) {
            String emailValue = email.getText().toString();
            String passwordValue = password_signup.getText().toString(); // Retrieve password from new EditText
            String nomValue = nom.getText().toString();
            String prenomValue = prenom.getText().toString();
            String telValue = tel.getText().toString();

            if (emailValue.isEmpty()) {
                Log.d("Signup", "Email field is empty");
            }
            if (passwordValue.isEmpty()) {
                Log.d("Signup", "Password field is empty");
            }
            if (nomValue.isEmpty()) {
                Log.d("Signup", "Nom field is empty");
            }
            if (prenomValue.isEmpty()) {
                Log.d("Signup", "Prenom field is empty");
            }
            if (telValue.isEmpty()) {
                Log.d("Signup", "Tel field is empty");
            }

            if (emailValue.isEmpty() || passwordValue.isEmpty() || nomValue.isEmpty() || prenomValue.isEmpty() || telValue.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                signup(emailValue, passwordValue, nomValue, prenomValue, telValue);
            }
        }
    }

    private void signup(String email, String password, String nom, String prenom, String tel) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                User user = new User(nom, prenom, tel, uid); // Create a User object with the provided data

                                // Add the user to Firestore database
                                UserDAO userDao = new UserDaoImpl();
                                userDao.addUser(user)
                                        .thenAccept(result -> {
                                            // User added successfully
                                            updateUI(firebaseUser);
                                        })
                                        .exceptionally(e -> {
                                            // Handle error
                                            Log.e(TAG, "Error adding user to Firestore", e);
                                            Toast.makeText(Auth_Activity.this, "Failed to add user to Firestore", Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                            return null;
                                        });
                            } else {
                                // Firebase user is null
                                Toast.makeText(Auth_Activity.this, "Failed to get current user", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        } else {
                            // Sign up failed
                            Toast.makeText(Auth_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            Toast.makeText(Auth_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser!=null){
            Intent MyIntent= new Intent(this, first_screen.class);
            startActivity(MyIntent);

        } else {
            espace2.setVisibility(View.GONE);
            espace3.setVisibility(View.GONE);
            espace1.setVisibility(View.VISIBLE);
        }
    }
}
