package backend.models.users;



import backend.models.Idable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Represents a typical account
 */
public abstract class User implements Serializable, Idable {
    private String username;
    private String password;
    private boolean isFrozen;
    private boolean isUnfrozenRequested;
    private HashMap<String, List<String>> messages; // User id to list of messages
    private final String id = UUID.randomUUID().toString();

    /**
     * Constructs a user with a given username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        this.messages = new HashMap<>();
    }

    /**
     * this user's username
     *
     * @return this user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * to be set to this user
     *
     * @param username to be set to this user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * this user's password
     *
     * @return this user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * new password
     *
     * @param password to be set to this user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * current frozen status
     *
     * @return if this user is frozen
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * new frozen status
     *
     * @param frozen if this user is now frozen
     */
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }


    /**
     * if this user requested to be unfrozen
     *
     * @return if this user requested to be unfrozen
     */
    public boolean isUnfrozenRequested() {
        return isUnfrozenRequested;
    }

    /**
     * if this user requested to be unfrozen
     *
     * @param unfrozenRequested if this user requested to be unfrozen
     */
    public void setUnfrozenRequested(boolean unfrozenRequested) {
        isUnfrozenRequested = unfrozenRequested;
    }

    /**
     * Clears out any past messages sent
     */
    public void clearMessages() {
        this.messages = new HashMap<>();
    }

    /**
     * Adds a new message sent to this user by another user
     * @param userId the user that sent the message
     * @param message the message
     */
    public void addMessage(String userId, String message) {
        if (!this.messages.containsKey(userId)) this.messages.put(userId, new ArrayList<>());
        this.messages.get(userId).add(message);
    }

    /**
     * All messages that got sent to this user
     * @return all messages that got sent to this user
     */
    public HashMap<String, List<String>> getMessages(){
        return this.messages;
    }

    /**
     * Name of the user
     * @return name of the user
     */
    @Override
    public String toString(){
        return username;
    }

    /**
     * Gets the id
     * @return gets the id
     */
    @Override
    public String getId(){
        return id;
    }
}
