package com.example.todoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import DAO_IMP.NoteDaoImpl;
import Model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class All_Notes
        extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;

    private FloatingActionButton fabAddNote;
    private List<Note> allNotes;
    private EditText searchEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);

        ImageButton backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recycler_notes);
        searchEditText = findViewById(R.id.edit_text_search);
        noteAdapter = new NoteAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(noteAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allNotes = new ArrayList<>();
        fabAddNote = findViewById(R.id.fab_add_note);

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(All_Notes.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        getNotes();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading.......");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void getNotes() {
        showProgressDialog();
        NoteDaoImpl noteDao = new NoteDaoImpl();
        FirebaseApp.initializeApp(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUid = mAuth.getCurrentUser().getUid();

        noteDao.getNotesByUid(db, currentUserUid, new OnSuccessListener<List<Note>>() {
            @Override
            public void onSuccess(List<Note> data) {
                hideProgressDialog();
                allNotes = data;
                noteAdapter.setData(allNotes);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                hideProgressDialog();
                // Handle failure
            }
        });
    }

    private void filterNotes(String query) {
        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : allNotes) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredNotes.add(note);
            }
        }
        noteAdapter.setData(filteredNotes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
    }
}
