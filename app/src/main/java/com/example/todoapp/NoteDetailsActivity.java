package com.example.todoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

import DAO_IMP.NoteDaoImpl;
import Model.Note;

public class NoteDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText titleEditText;
    private EditText contentEditText;
    private ImageView updateImage;
    private Button updateNoteButton;

    private ImageButton back_btn ;

    private Uri imageUri;

    private ProgressDialog progressDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        // Initialize views
        imageView = findViewById(R.id.uploaded_image_view);
        titleEditText = findViewById(R.id.edit_text_title);
        contentEditText = findViewById(R.id.editTextTextMultiLine);
        updateImage = findViewById(R.id.update_image);
        updateNoteButton = findViewById(R.id.button_update_note);
        back_btn = findViewById(R.id.back_button) ;
        progressDialog = new ProgressDialog(this);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Get note details from intent
        Note note = getIntent().getParcelableExtra("note");

        // Populate fields with note details
        if (note != null) {
            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
            // Load image using Glide
            Glide.with(this)
                    .load(note.getPicture())
                    .into(imageView);
        }

        // Set click listener for update image button
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set click listener for update note button
        updateNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void updateNote() {
        final String title = titleEditText.getText().toString().trim();
        final String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating Note...");
        progressDialog.show();

        // Upload new image if selected
        if (imageUri != null) {
            final StorageReference fileReference = storage.getReference()
                    .child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    updateNoteInFirestore(title, content, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NoteDetailsActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No new image selected, update note without changing image
            updateNoteInFirestore(title, content, null);
        }
    }

    private void updateNoteInFirestore(final String title, final String content, final String imageUrl) {
        // Get the original note from intent
        final Note originalNote = getIntent().getParcelableExtra("note");

        String img = originalNote.getPicture() ;
        if (imageUrl != null) {
            img=imageUrl; // Update picture if new image URL is provided
        }


        Note updatedNote = new Note(title,content,img,originalNote.getUID());
        NoteDaoImpl noteDao = new NoteDaoImpl();

        noteDao.updateNote(db, updatedNote, originalNote.getNoteID(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(NoteDetailsActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after successful update
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(NoteDetailsActivity.this, "Failed to update note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getFileExtension(Uri uri) {
        return Objects.requireNonNull(getContentResolver().getType(uri)).split("/")[1];
    }
}
