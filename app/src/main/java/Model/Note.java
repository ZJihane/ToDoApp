package Model;
import java.util.Date;

public class Note {
    private String noteId;
    private String title;
    private String content;
    private Date createdAt;
    private Date lastModified;

    public Note() {
        // Constructeur par défaut requis par Firebase
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = new Date(); // Date de création actuelle
        this.lastModified = new Date(); // Date de dernière modification actuelle
    }

    // Getters et setters
    // Notez que Firebase utilise des getters/setters pour lire/écrire les données
    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}

