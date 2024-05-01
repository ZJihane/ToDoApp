package Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Note implements Parcelable {
    private String title;
    private String content;
    private String picture;
    private Date createdAt;
    private Date lastModified;
    private String UID;

    private String NoteID ;

    // Required no-argument constructor for Firebase
    public Note() {
    }

    public Note(String title, String content, String picture , String UID) {
        this.title = title;
        this.content = content;
        this.picture = picture;
        this.createdAt = new Date();
        this.lastModified = new Date();
        this.UID = UID ;
        this.NoteID = generateNoteId() ;
    }

    public Note(String NoteID ,String title, String content, String picture , String UID) {
        this.title = title;
        this.content = content;
        this.picture = picture;
        this.createdAt = new Date();
        this.lastModified = new Date();
        this.UID = UID ;
        this.NoteID = NoteID ;
    }



    public String getNoteID(){
        return NoteID ;
    }

    public void setNoteID(String noteID) {
        NoteID = noteID;
    }

    // Getters and setters for UID
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    // Getters and setters (public for Firebase)
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    // Parcelable implementation
    protected Note(Parcel in) {
        NoteID = in.readString();
        title = in.readString();
        content = in.readString();
        picture = in.readString();
        createdAt = new Date(in.readLong());
        lastModified = new Date(in.readLong());
        UID = in.readString(); // Read UID from parcel
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NoteID);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(picture);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1L);
        dest.writeLong(lastModified != null ? lastModified.getTime() : -1L);
        dest.writeString(UID); // Write UID to parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private String generateNoteId() {
        String uniqueId = java.util.UUID.randomUUID().toString();
        String timestamp = String.valueOf(createdAt.getTime());
        return uniqueId + "_" + timestamp;
    }


}
