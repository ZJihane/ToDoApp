package DAO;
import Model.Note;

import java.util.List;

public interface NoteDAO {
    void addNote(Note note);
    void updateNote(Note note);
    void deleteNote(String noteId);
    void getAllNotes(DataCallback<List<Note>> callback);

    interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
}
