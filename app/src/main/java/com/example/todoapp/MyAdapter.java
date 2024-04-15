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
import DAO_IMP.TaskDaoImpl;
import Model.Task;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Task> taskList;
    private Context context;

    public MyAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    public void setData(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.txtTitle.setText(task.getTitle());
        holder.txtDescription.setText(task.getDescription());

        // Gestion du long clic pour afficher le menu contextuel
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, task);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.text_title);
            txtDescription = itemView.findViewById(R.id.text_description);
        }
    }

    private void showPopupMenu(View view, Task task) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.task_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.item1) {
                    TaskDaoImpl taskDao = new TaskDaoImpl();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    taskDao.getTaskById(db, task.getTaskId(), new OnSuccessListener<Task>() {
                        @Override
                        public void onSuccess(Task retrievedTask) {
                            if (retrievedTask != null) {
                                // Log the task ID if it exists
                                Log.d("TaskID", "Retrieved Task ID: " + retrievedTask.getTaskId());
                                showTaskDetails(retrievedTask);
                            } else {
                                Toast.makeText(context, "La tâche n'a pas été trouvée", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, "Erreur lors de la récupération de la tâche: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TaskID", "Retrieved Task ID: none" );
                        }
                    });
                    return true;



        } else if (itemId == R.id.item2) {
                    Toast.makeText(context, "Supprimer la tâche: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.item3) {
                    Toast.makeText(context, "Partager la tâche: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    /*
    private void showTaskDetails(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Task Details");

        // Build message string with all task fields
        StringBuilder message = new StringBuilder();
        message.append("Titre: ").append(task.getTitle()).append("\n");
        message.append("Description: ").append(task.getDescription()).append("\n");
        message.append("Priority: ").append(task.getPriority()).append("\n");
        message.append("Status: ").append(task.getStatus()).append("\n");
        // Include other task fields as needed

        builder.setMessage(message.toString());

        builder.setPositiveButton("OK", (dialogInterface, which) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    } */

    private void showTaskDetails(Task task) {
        // Start TaskDetailsActivity and pass the selected task
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra("task",task);
        context.startActivity(intent);
    }



}
