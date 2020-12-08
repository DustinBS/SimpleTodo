package sumarlid.simpletodo;

import java.util.Objects;

/**
 * Represents each item in a to-do list.
 *
 * @author Dustin Sumarli
 * @version 2/10/2020
 */
public class TodoItem {

    private int id = 10000000; // shows up in order in Firebase up to 99999999 items
    private String description;

    /**
     * Default constructor for Firebase's toObject method.
     */
    public TodoItem() {
    }

    /**
     * A public constructor which requires the item to initialize its contents and unique id.
     *
     * @param description initial text of item
     * @param id          initial value of id
     */
    public TodoItem(String description, int id) {
        this.description = description;
        this.id = id;
    }

    /**
     * Accesses the unique id of the item.
     *
     * @return integer id
     */
    public int getId() {
        return id;
    }

    /**
     * Allows the id to be changed. (Could be useful to reset if items > 99999999)
     *
     * @param id replacement integer id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Accesses the text of the item
     *
     * @return the String value of the the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Allows the text to be changed.
     *
     * @param description replacement String text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Displays the text of the item.
     *
     * @return the text of the item
     */
    @Override
    public String toString() {
        return description;
    }

    /**
     * To-do items are only equals if their ids are the same.
     *
     * @param o the other to-do item
     * @return true(equal) or false(not equal)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return id == todoItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
