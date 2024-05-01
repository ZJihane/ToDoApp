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
}

