package DAO;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.Note;

import java.util.List;

public interface NoteDAO {
    void addNote(FirebaseFirestore db, Note note, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener);
    public void updateNote(FirebaseFirestore db, Note note,String taskId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) ;
    void deleteNote(FirebaseFirestore db ,String noteId , OnSuccessListener<Void> successListener, OnFailureListener failureListener );
    public void getNotesByUid(FirebaseFirestore db, String currentUserUid, OnSuccessListener<List<Note>> onSuccessListener, OnFailureListener onFailureListener);
    public void getNoteById(FirebaseFirestore db, String noteId, OnSuccessListener<Note> successListener , OnFailureListener failureListener);



    // Define the DataCallback interface within the TaskDAO interface
    interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}
