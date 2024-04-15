package DAO;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import Model.Task;
import java.util.List;

public interface TaskDAO {
    void addTask(FirebaseFirestore db, Task task, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener);
    void updateTask(Task task);
    void deleteTask(String taskId);
    void getAllTasks(FirebaseFirestore db, OnSuccessListener<List<Task>> successListener, OnFailureListener failureListener);
    void getTaskById(FirebaseFirestore db, String taskId, OnSuccessListener<Task> successListener, OnFailureListener failureListener);



    // Define the DataCallback interface within the TaskDAO interface
    interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}
