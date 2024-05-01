package DAO_IMP;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import DAO.NoteDAO;
import Model.Note;
import Model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteDaoImpl implements NoteDAO {
    private Map<String, Note> notes;

    public NoteDaoImpl() {
        this.notes = new HashMap<>();
    }

    @Override
    public void addNote(FirebaseFirestore db, Note note, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        db.collection("Notes")
                .add(note)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    @Override
    public void updateNote(FirebaseFirestore db, Note note, String noteId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("Notes")
                .whereEqualTo("noteID", noteId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Update the found document with the provided note object
                        db.collection("Notes")
                                .document(document.getId()) // Use the document ID to update
                                .set(note)
                                .addOnSuccessListener(aVoid -> {
                                    // Invoke the success listener once the update is successful
                                    successListener.onSuccess(null);
                                })
                                .addOnFailureListener(failureListener);
                        return; // Exit the loop after updating the first matching document
                    }
                    // If no matching document is found, handle the failure
                    failureListener.onFailure(new Exception("No document found with noteId: " + noteId));
                })
                .addOnFailureListener(failureListener);
    }

    @Override
    public void deleteNote(FirebaseFirestore db, String noteID, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("Notes")
                .whereEqualTo("noteID", noteID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete(); // Delete each document found
                    }
                    successListener.onSuccess(null); // Invoke success listener once all documents are deleted
                })
                .addOnFailureListener(failureListener);
    }



    @Override
    public void getNoteById(FirebaseFirestore db, String noteId, OnSuccessListener<Note> successListener, OnFailureListener failureListener) {
        if (noteId == null) {
            failureListener.onFailure(new NullPointerException("Note ID cannot be null"));
            return;
        }
        db.collection("Notes")
                .whereEqualTo("noteID", noteId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            if (note != null) {
                                successListener.onSuccess(note);
                                return;
                            }
                        }
                        failureListener.onFailure(new NullPointerException("Note object not found"));
                    } else {
                        failureListener.onFailure(new NullPointerException("Note does not exist"));
                    }
                })
                .addOnFailureListener(e -> failureListener.onFailure(e));
    }


    @Override
    public void getNotesByUid(FirebaseFirestore db, String currentUserUid, OnSuccessListener<List<Note>> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference notesRef = db.collection("Notes");
        Query query = notesRef.whereEqualTo("uid", currentUserUid);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Note> notes = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Note note = document.toObject(Note.class);
                    notes.add(note);
                }
                onSuccessListener.onSuccess(notes);
            }
        }).addOnFailureListener(onFailureListener);
    }
}
