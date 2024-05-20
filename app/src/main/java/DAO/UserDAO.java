package DAO;

import java.util.concurrent.CompletableFuture;
import Model.User;

public interface UserDAO {
    CompletableFuture<Void> addUser(User user);
    CompletableFuture<Void> updateUser(User user, String uid);
}
