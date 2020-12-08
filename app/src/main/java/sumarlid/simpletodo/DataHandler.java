package sumarlid.simpletodo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class handles modifying data in the Firebase Firestore data store connected to this app.
 *
 * @author Dustin Sumarli
 * @version 2/10/2020
 */
public class DataHandler {

    /**
     * Deletes an item from the Cloud data store that matches the given item ID.
     *
     * @param id the ID of the item to delete
     */
    void delete(int id) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("accounts").document(user).collection("descriptions").document(String.valueOf(id)).delete();
    }

    /**
     * Adds a new item to the Cloud data store. If another item exists with the same item ID,
     * it will be replaced with this one.
     *
     * @param todoItem the item to add to the data store
     */
    void add(TodoItem todoItem) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("accounts").document(user).collection("descriptions").document(String.valueOf(todoItem.getId())).set(todoItem);
    }
}