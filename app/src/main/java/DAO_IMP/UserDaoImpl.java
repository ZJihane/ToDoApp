package DAO_IMP;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;

import DAO.UserDAO;
import Model.User;

public class UserDaoImpl implements UserDAO {

    private final CollectionReference usersCollection;

    public UserDaoImpl() {
        this.usersCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    @Override
    public CompletableFuture<Void> addUser(User user) {
        return CompletableFuture.runAsync(() -> {
            usersCollection.document(user.getUID()).set(user)
                    .addOnSuccessListener(aVoid -> System.out.println("User added successfully"))
                    .addOnFailureListener(e -> System.err.println("Error adding user: " + e.getMessage()));
        });
    }

    @Override
    public CompletableFuture<Void> updateUser(User user, String uid) {
        return CompletableFuture.runAsync(() -> {
            usersCollection.document(uid).update(
                            "first_Name", user.getFirst_Name(),
                            "last_Name", user.getLast_Name(),
                            "phone_Number", user.getPhone_Number()
                    ).addOnSuccessListener(aVoid -> System.out.println("User updated successfully in Firestore"))
                    .addOnFailureListener(e -> System.err.println("Error updating user in Firestore: " + e.getMessage()));
        });
    }
}
