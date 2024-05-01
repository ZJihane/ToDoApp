package com.example.todoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import DAO_IMP.NoteDaoImpl;
import Model.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> noteList;
    private Context context;

    public NoteAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }

    public void setData(List<Note> noteList) {
        this.noteList = noteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.txtTitle.setText(note.getTitle());
        holder.txtContent.setText(note.getContent());

        // Handling long click to show the context menu
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, note);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.text_title);
            txtContent = itemView.findViewById(R.id.text_content);
        }
    }

    private void showPopupMenu(View view, Note note) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.task_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.item1) {
                    // Handle option 1 (view note details)
                    showNoteDetails(note);
                    return true;

                } else if (itemId == R.id.item2) {
                    // Handle option 2 (delete note)
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete note");
                    builder.setMessage("Do you really want to delete this note?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteNote(note);
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                    return true;
                } else if (itemId == R.id.item3) {
                    // Handle option 3 (share note)
                    shareNote(note);
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void showNoteDetails(Note note) {
        // Start NoteDetailsActivity and pass the selected note
        Intent intent = new Intent(context, NoteDetailsActivity.class);
        intent.putExtra("note", note);
        context.startActivity(intent);
    }

    private void deleteNote(Note note) {
        NoteDaoImpl noteDao = new NoteDaoImpl();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Use the document ID of the note instead of note.getNoteId()

        noteDao.deleteNote(db, note.getNoteID(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Note deleted successfully, update the UI
                Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                noteList.remove(note);
                notifyDataSetChanged();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to delete note
                Toast.makeText(context, "Failed to delete note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void shareNote(Note note) {
        // Create a string with the note details to share
        StringBuilder shareText = new StringBuilder();
        shareText.append("Title: ").append(note.getTitle()).append("\n");
        shareText.append("Content: ").append(note.getContent()).append("\n");

        // Create an intent to share the note details
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Note Details");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        // Start the activity to share the note details
        context.startActivity(Intent.createChooser(shareIntent, "Share Note Details"));
    }
}
