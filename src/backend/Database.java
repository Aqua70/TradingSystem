package backend;


import backend.exceptions.EntryNotFoundException;
import backend.models.Idable;

import java.io.*;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to store a list of items with methods provided to update entries of that list.
 * The list of items is stored using a Linked List in .ser files. Any object being stored must be serializable.
 * Code is partially taken from logging.zip, StudentManager.java from week 6 slides and codes
 *
 */
public class Database implements Serializable {
    private final String FILE_PATH;

    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static final Handler consoleHandler = new ConsoleHandler();

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public Database(String filePath) throws IOException {
        logger.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.WARNING);
        logger.addHandler(consoleHandler);
        this.FILE_PATH = filePath;
        new File(filePath).createNewFile();
    }

    /**
     * Updates the list with a new entry for the same id, if that entry exists
     *
     * @param newItem the item to replace to existing entry (if it exists)
     * @return the old item in the entry, if it doesn't exist then the new item is returned
     */
    public Idable update(Idable newItem) {
        HashMap<String, Idable> allItems = getItems();
        Idable oldItem = allItems.getOrDefault(newItem.getId(), newItem);
        try {
            allItems.put(newItem.getId(), newItem);
            save(allItems);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Input could not be read. Failed to update.", e);
        }
        return oldItem;

    }

    /**
     * Deletes an entry in the list of items if it exists
     *
     * @param id the entry id
     */
    public void delete(String id) {
        HashMap<String, Idable> allItems = getItems();
        if (allItems.containsKey(id)) {
            allItems.remove(id);
            try {
                save(allItems);
            } catch (FileNotFoundException e) {
                logger.log(Level.SEVERE, "Could not save when deleting.", e);
            }
        }
    }


    /**
     * Returns the object instance equivalent of the id given
     *
     * @param id the id of the object that is requested
     * @return the object instance of the id
     * @throws EntryNotFoundException if the id given does not exist in the list of items
     */
    public Idable populate(String id) throws EntryNotFoundException {
        HashMap<String, Idable> allItems = getItems();
        if (allItems.containsKey(id)) return allItems.get(id);
        throw new EntryNotFoundException("Could not find item " + id);
    }

    /**
     * Return true if the database contains the id
     *
     * @param id the id being checked
     * @return true if the database contains the id
     */
    public boolean contains(String id) {
        try {
            populate(id);
        } catch (EntryNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Return a hashmap of items in the database file
     *
     * @return a hashmap of items in the database file
     */
    public HashMap<String, Idable> getItems() {
        if (!new File(this.FILE_PATH).exists()) {
            logger.log(Level.SEVERE, "The file " + FILE_PATH + " doesn't exist.");
            return new HashMap<>();
        }
        try {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(this.FILE_PATH));
            ObjectInputStream input = new ObjectInputStream(buffer);
            Object tmp = input.readObject();
            input.close();
            if (tmp instanceof HashMap)
                return (HashMap<String, Idable>) tmp;
            return new HashMap<>();
        } catch (IOException e) {
            logger.log(Level.INFO, "Empty file was used.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Input could not be read.", e);
        }
        return new HashMap<>();

    }

    /**
     * Overwrites the file and saves a new hashmap to it
     *
     * @param items the items that are being saved to the file
     * @throws FileNotFoundException if the file doesn't exist
     */
    public void save(HashMap<String, Idable> items) throws FileNotFoundException {
        if (!new File(this.FILE_PATH).exists()) {
            logger.log(Level.SEVERE, "The file " + FILE_PATH + " doesn't exist.");
            throw new FileNotFoundException();
        }
        try {
            BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(FILE_PATH));
            ObjectOutputStream  output = new ObjectOutputStream(buffer);
            output.writeObject(items);
            output.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to save.", e);
        }
    }
}

