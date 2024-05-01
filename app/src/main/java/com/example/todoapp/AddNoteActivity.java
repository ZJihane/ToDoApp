package com.example.todoapp;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import DAO_IMP.NoteDaoImpl;
import Model.Note;
import java.util.UUID;


public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonAddNote;
    private ImageButton backButton;
    private ImageView uploadImageButton;
    private ImageView uploadedImageView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private NoteDaoImpl noteDao;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private StorageReference storageReference;

    FirebaseUser user_mAuth ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.editTextTextMultiLine);
        buttonAddNote = findViewById(R.id.button_add_note);
        backButton = findViewById(R.id.back_button);
        uploadImageButton = findViewById(R.id.upload_image);
        uploadedImageView = findViewById(R.id.uploaded_image_view);
        progressBar = findViewById(R.id.prog_bar);
        progressBar.setVisibility(View.INVISIBLE);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user_mAuth = mAuth.getCurrentUser();

        noteDao = new NoteDaoImpl();
        storageReference = FirebaseStorage.getInstance().getReference();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();
                if (title.isEmpty()) {
                    editTextTitle.setError("Title required");
                    editTextTitle.requestFocus();
                    return;
                }
                if (content.isEmpty()) {
                    editTextContent.setError("Content required");
                    editTextContent.requestFocus();
                    return;
                }
                if (imageUri != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImageToStorage(title, content);
                } else {
                    addNoteToFirestore(title, content, null);
                }
            }
        });
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data.getData();
                            uploadedImageView.setImageURI(imageUri);
                        }
                    }
                }
        );

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    private void uploadImageToStorage(final String title, final String content) {
        if (imageUri != null) {
            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child("images/" + imageName);
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            addNoteToFirestore(title, content, uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNoteActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNoteActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addNoteToFirestore(String title, String content, String imageUrl) {
        Note note = new Note(title, content, imageUrl , user_mAuth.getUid());
        noteDao.addNote(db, note,
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddNoteActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddNoteActivity.this, "Failed to add note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
