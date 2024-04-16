package DAO_IMP;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import DAO.TaskDAO;
import Model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDaoImpl implements TaskDAO {
    private Map<String, Task> tasks;

    public TaskDaoImpl() {
        this.tasks = new HashMap<>();
    }

    @Override
    public void addTask(FirebaseFirestore db, Task task, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        // Add the task to the Firestore database
        db.collection("Tasks")
                .add(task)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    @Override
    public void updateTask(FirebaseFirestore db, Task task, String TaskId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("Tasks")
                .whereEqualTo("taskId", TaskId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Update the found document with the provided task object
                        db.collection("Tasks")
                                .document(document.getId()) // Use the document ID to update
                                .set(task)
                                .addOnSuccessListener(aVoid -> {
                                    // Invoke the success listener once the update is successful
                                    successListener.onSuccess(null);
                                })
                                .addOnFailureListener(failureListener);
                        return; // Exit the loop after updating the first matching document
                    }
                    // If no matching document is found, handle the failure
                    failureListener.onFailure(new Exception("No document found with taskId: " + task.getTaskId()));
                })
                .addOnFailureListener(failureListener);
    }




    @Override
    public void deleteTask(String taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void getAllTasks(FirebaseFirestore db, OnSuccessListener<List<Task>> successListener, OnFailureListener failureListener) {
        db.collection("Tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> tasks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Task task = document.toObject(Task.class);
                        tasks.add(task);
                    }
                    successListener.onSuccess(tasks);
                })
                .addOnFailureListener(failureListener);
    }

    @Override
    public void getTaskById(FirebaseFirestore db, String taskId, OnSuccessListener<Task> successListener, OnFailureListener failureListener) {
        if (taskId == null) {
            failureListener.onFailure(new NullPointerException("Task ID cannot be null"));
            return;
        }

        db.collection("Tasks")
                .whereEqualTo("taskId", taskId) // Ajout de la condition taskId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Task task = documentSnapshot.toObject(Task.class);
                            if (task != null) {
                                successListener.onSuccess(task);
                                return; // Sortie de la boucle après avoir trouvé la tâche correspondante
                            }
                        }
                        failureListener.onFailure(new NullPointerException("Task object not found"));
                    } else {
                        failureListener.onFailure(new NullPointerException("Task does not exist"));
                    }
                })
                .addOnFailureListener(e -> failureListener.onFailure(e));
    }




}
