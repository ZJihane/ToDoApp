package DAO_IMP;
import Model.Note;
import DAO.NoteDAO;
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
    public void addNote(Note note) {
        notes.put(note.getNoteId(), note);
    }

    @Override
    public void updateNote(Note note) {
        notes.put(note.getNoteId(), note);
    }

    @Override
    public void deleteNote(String noteId) {
        notes.remove(noteId);
    }

    @Override
    public void getAllNotes(DataCallback<List<Note>> callback) {
        List<Note> noteList = new ArrayList<>(notes.values());
        callback.onSuccess(noteList);
    }
}

