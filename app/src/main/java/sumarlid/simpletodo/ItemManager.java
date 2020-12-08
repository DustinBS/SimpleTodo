package sumarlid.simpletodo;

import java.util.ArrayList;

/**
 * This singleton class maintains a local copy of data needed for this app and
 * communicates with the DataHandler in order to update the cloud data store.
 * <p>
 * References:
 * Linda Zuvich Panopto Recordings
 *
 * @author Dustin Sumarli
 * @version 2/11/2020
 */
public class ItemManager {

    private static ItemManager im;

    private DataHandler dh;
    private ArrayList<TodoItem> todoItems = new ArrayList<>();

    /**
     * A private no-arg constructor to set up the connection to the data handler and
     * prevent instantiation of the ItemManager outside this class.
     */
    private ItemManager() {
        dh = new DataHandler();
    }

    /**
     * The singleton method that provides access to the one instance of ItemManager.
     *
     * @return the instance of ItemManager
     */
    public static ItemManager getItemManager() {
        if (im == null) {
            im = new ItemManager();
        }
        return im;
    }

    /**
     * Provides stored list of todoItems
     *
     * @return the ArrayList of todoItems instances stored in ItemManager
     */
    public ArrayList<TodoItem> getTodoItems() {
        return todoItems;
    }

    /**
     * Allows the entire ArrayList of todoItems in ItemManager to be replaced.
     *
     * @param todoItems the ArrayList of Item instances
     */
    public void setTodoItems(ArrayList<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    /**
     * Removes the given TodoItem instance from the ItemManager ArrayList and the cloud data store.
     *
     * @param todoItem the item to delete
     */
    public void deleteItem(TodoItem todoItem) {
        todoItems.remove(todoItem);
        dh.delete(todoItem.getId());
    }

    /**
     * Adds the new item to the local ArrayList and the cloud data store.
     *
     * @param todoItem the new todoItem instance to be added
     */
    public void addItem(TodoItem todoItem) {
        todoItems.add(todoItem);
        dh.add(todoItem);
    }

    /**
     * Searches through the list of todoItems to find the largest id.
     *
     * @return the max id in the list.
     */
    public int maxId() {
        int max = 1000000;
        for (TodoItem todoItem : todoItems) {
            if (todoItem.getId() > max) {
                max = todoItem.getId();
            }
        }
        return max;
    }
}
