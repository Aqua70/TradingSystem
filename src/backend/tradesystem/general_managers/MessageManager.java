package backend.tradesystem.general_managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.User;
import backend.tradesystem.Manager;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Used for managing notifications and messages
 */
public class MessageManager extends Manager {


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public MessageManager() throws IOException {
        super();
    }


    /**
     * Send a message
     *
     * @param userId   the user sending the message
     * @param toUserId the user receiving the message
     * @param message  the message
     * @throws UserNotFoundException  if one of the users don't exist
     * @throws AuthorizationException if not allowed to send the message
     */
    public void sendMessage(String userId, String toUserId, String message) throws UserNotFoundException, AuthorizationException {
        if (userId.equals(toUserId)) throw new AuthorizationException("Cannot send a message to self");
        if (getUser(userId).isFrozen()) throw new AuthorizationException("You are frozen and cant send a message.");
        getUser(userId); // Ensures the user exists
        User toUser = getUser(toUserId);
        toUser.addMessage(userId, message);
        updateUserDatabase(toUser);
    }

    /**
     * Empty out messages that were received
     *
     * @param userId the user being checked
     * @throws UserNotFoundException if the user isn't found
     */
    public void clearMessages(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        user.clearMessages();
        updateUserDatabase(user);
    }

    /**
     * Empty out messages that were received from a single user
     *
     * @param userId      the user being checked
     * @param clearUserId the messages received from this user that will get cleared
     * @throws UserNotFoundException if the user isn't found
     */
    public void clearMessagesFromUser(String userId, String clearUserId) throws UserNotFoundException {
        User user = getUser(userId);
        user.getMessages().remove(clearUserId);
        updateUserDatabase(user);
    }

    /**
     * Get all messages received by a user
     *
     * @param userId the user being checked for
     * @return a hashmap from user to messages
     * @throws UserNotFoundException if the user isn't found
     */
    public HashMap<String, List<String>> getMessages(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        return user.getMessages();
    }
}
