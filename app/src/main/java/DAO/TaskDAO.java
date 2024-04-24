package DAO;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import Model.Task;
import java.util.List;

public interface TaskDAO {
    void addTask(FirebaseFirestore db, Task task, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener);
    public void updateTask(FirebaseFirestore db, Task task,String taskId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) ;
    void deleteTask(FirebaseFirestore db ,String taskId , OnSuccessListener<Void> successListener, OnFailureListener failureListener );
    public void getTasksByUid(FirebaseFirestore db, String currentUserUid, OnSuccessListener<List<Task>> onSuccessListener, OnFailureListener onFailureListener);
    void getTaskById(FirebaseFirestore db, String taskId, OnSuccessListener<Task> successListener, OnFailureListener failureListener);



    // Define the DataCallback interface within the TaskDAO interface
    interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}
