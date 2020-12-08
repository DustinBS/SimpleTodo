package sumarlid.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Launches the to-do-list application and also allows different users to have different
 * to-do lists through Firebase Authentication.
 *
 * @author Dustin Sumarli
 * @version 2/10/2020
 */
public class MainActivity extends AppCompatActivity {
    private ListView lstItems;
    private ItemManager im;
    private ArrayList<TodoItem> todoItems;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayAdapter<TodoItem> itemsAdapter;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    /**
     * Brings up AuthUI when user isn't signed in. Logging in gives access to the user's to-do list
     * whereas failing closes the app. The method populates the to-do list by fetching data from
     * Firestore in real-time.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lstItems = findViewById(R.id.lstItems);
        im = ItemManager.getItemManager();
        todoItems = new ArrayList<TodoItem>();

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    lstItems = findViewById(R.id.lstItems);

                    db.collection("accounts").document(auth.getCurrentUser().getUid())
                            .collection("descriptions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                todoItems.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    todoItems.add(document.toObject(TodoItem.class));
                                }
                                im.setTodoItems(todoItems); // set local copy
                                itemsAdapter = new ArrayAdapter<TodoItem>(MainActivity.this,
                                        android.R.layout.simple_list_item_1, todoItems);
                                lstItems.setAdapter(itemsAdapter);
                                setupListViewListener();
                            }
                        }
                    });
                } else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false).build(), 1);
                }
            }
        };
    }

    /**
     * Detaches the authentication listener when activity is not in focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (auth != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    /**
     * Reattaches the authentication listener when activity is brought back to focus.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (auth != null) {
            auth.addAuthStateListener(authStateListener);
        }
    }

    /**
     * The result when AuthUI finishes. Creates Toasts depending on the result of the login attempt.
     *
     * @param requestCode matches the correct code block to run depending on the requesting
     *                    activity.
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Creates the menu.
     *
     * @param menu menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Defines the actions in the menu. "Log Out" logs out the current user.
     *
     * @param item items in the menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            auth.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds the text from the adjacent EditText as an item to the to-do list when the button is
     * clicked.
     *
     * @param view the button view being clicked
     */
    public void onAddItem(View view) {
        EditText txtItem = findViewById(R.id.txtNewItem);
        TodoItem itemAdded = new TodoItem(txtItem.getText().toString(), im.maxId() + 1);
        im.addItem(itemAdded);
        txtItem.setText("");
    }

    /**
     * Sets up a long click listener for the to-do list. Items on the to-do list are deleted when
     * hold-clicked for a few seconds.
     */
    private void setupListViewListener() {
        lstItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem remove = (TodoItem) lstItems.getAdapter().getItem(position);
                im.deleteItem(remove);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}
