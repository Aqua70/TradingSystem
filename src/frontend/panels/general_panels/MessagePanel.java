package frontend.panels.general_panels;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.MessageManager;
import backend.tradesystem.general_managers.ReportManager;
import backend.tradesystem.queries.UserQuery;
import frontend.components.TraderComboBoxItem;


/**
 * Represents messaging and receiving messaging part of the window
 */
public class MessagePanel extends JPanel {

    private final ReportManager reportManager = new ReportManager();
    private final MessageManager messageManager = new MessageManager();
    private final UserQuery userQuery = new UserQuery();

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(75, 75, 75);
    private final Color gray2 = new Color(196, 196, 196);
    private final Color red = new Color(219, 58, 52);
    private final Color gray3 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);

    private final Dimension titleBarDimension = new Dimension(1200, 75);
    private final Dimension messagesDimension = new Dimension(1200, 400);

    private JPanel messageTitleContainer, messagesListContainer;
    private JScrollPane messagesScrollPane;

    private final Font regular, bold, italic, boldItalic;

    private final String userId;

    /**
     * Making a new messaging panel
     * @param userId the user
     * @param regular regular font
     * @param bold bold font
     * @param italic italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with getting database file
     * @throws UserNotFoundException if the user isn't found
     */
    public MessagePanel(String userId, Font regular, Font bold, Font italic, Font boldItalic)
            throws IOException, UserNotFoundException {

        this.userId = userId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setPreferredSize(new Dimension(1200, 475));
        this.setBackground(bg);

        setMessageTitleContainer();
        setMessagesScrollPane();

        this.add(messageTitleContainer);
        this.add(messagesScrollPane);

    }

    /**
     * Changing colour scheme to black
     */
    public void changeToAdminColorScheme() {
        this.setBackground(Color.BLACK);
        for (Component c : messageTitleContainer.getComponents()) {
            c.setBackground(Color.BLACK);
        }
        messagesListContainer.setBackground(bg);

        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        messagesScrollPane.setPreferredSize(new Dimension(1200, 700));
        if(messagesListContainer.getLayout() instanceof GridLayout) {
            int numRows = ((GridLayout) messagesListContainer.getLayout()).getRows();
            if (numRows < 7)
                numRows = 7;
            messagesListContainer.setLayout(new GridLayout(numRows, 1));
            for (Component c : messagesListContainer.getComponents()) {
                c.setPreferredSize(new Dimension(1200, 75));
            }
        }
    }

    private void setMessagesScrollPane() throws UserNotFoundException {
        messagesScrollPane = new JScrollPane();
        messagesListContainer = new JPanel();
        messagesScrollPane.setPreferredSize(messagesDimension);
        messagesScrollPane.setBorder(null);
        messagesScrollPane.setBackground(gray3);
        getMessages();
        if (!userId.equals("") && userQuery.getType(userId).equals(UserTypes.ADMIN))
            getReports();
        messagesScrollPane.setViewportView(messagesListContainer);
    }

    private void setMessageTitleContainer() throws UserNotFoundException {
        messageTitleContainer = new JPanel(new GridLayout(1, 3));
        messageTitleContainer.setPreferredSize(titleBarDimension);

        JLabel messagesTitle = new JLabel("Messages");
        if (!userId.equals("") && userQuery.getType(userId).equals(UserTypes.ADMIN))
            messagesTitle.setText("Messages and Reports");
        messagesTitle.setBackground(bg);
        messagesTitle.setForeground(Color.WHITE);
        messagesTitle.setOpaque(true);
        messagesTitle.setFont(regular.deriveFont(30f));
        messagesTitle.setHorizontalAlignment(JButton.CENTER);

        messageTitleContainer.add(getNewMessageButton());
        messageTitleContainer.add(messagesTitle);
        messageTitleContainer.add(getClearAllmessagesButton());
    }

    private JButton getClearAllmessagesButton() {
        JButton clearAllMessagesButton = new JButton("Clear All Messages");
        clearAllMessagesButton.setBackground(bg);
        clearAllMessagesButton.setForeground(Color.CYAN);
        clearAllMessagesButton.setFont(boldItalic.deriveFont(25f));
        clearAllMessagesButton.setOpaque(true);
        clearAllMessagesButton.setBorderPainted(false);
        clearAllMessagesButton.setHorizontalAlignment(JButton.RIGHT);
        clearAllMessagesButton.addActionListener(e -> {
            if (userId.equals("") || messagesListContainer.getLayout() instanceof BorderLayout)
                return;
            try {
                messageManager.clearMessages(userId);
                if (userQuery.getType(userId).equals(UserTypes.ADMIN))
                    reportManager.clearReports();
            } catch (UserNotFoundException e1) {
                e1.printStackTrace();
            }
            setNoMessagesFound();
        });

        return clearAllMessagesButton;
    }

    private void setNoMessagesFound() {
        messagesListContainer.removeAll();
        messagesListContainer.setLayout(new BorderLayout());
        messagesListContainer.setBackground(gray3);
        JLabel noMessagesFound = new JLabel("<html><pre>No Messages Found</pre></html>");
        noMessagesFound.setFont(regular.deriveFont(30f));
        noMessagesFound.setPreferredSize(new Dimension(1000, 375));
        noMessagesFound.setHorizontalAlignment(JLabel.CENTER);
        noMessagesFound.setVerticalAlignment(JLabel.CENTER);
        noMessagesFound.setForeground(Color.WHITE);
        messagesListContainer.add(noMessagesFound);
        messagesScrollPane.revalidate();
        messagesScrollPane.repaint();
    }

    private JButton getNewMessageButton() {
        JButton addNewMessageButton = new JButton("New Message");
        addNewMessageButton.setBackground(bg);
        addNewMessageButton.setForeground(Color.CYAN);
        addNewMessageButton.setFont(boldItalic.deriveFont(25f));
        addNewMessageButton.setOpaque(true);
        addNewMessageButton.setBorderPainted(false);
        addNewMessageButton.setHorizontalAlignment(JButton.LEFT);
        addNewMessageButton.addActionListener(e -> {
            if (userId.equals(""))
                return;
            JDialog messageDetailsModal = new JDialog();
            messageDetailsModal.setTitle("Compose New Message");
            messageDetailsModal.setSize(600, 500);
            messageDetailsModal.setResizable(false);
            messageDetailsModal.setLocationRelativeTo(null);

            JPanel messageDetailsPanel = new JPanel();
            messageDetailsPanel.setPreferredSize(new Dimension(600, 500));
            messageDetailsPanel.setBackground(bg);

            JLabel userNameTitle = new JLabel("Sender Username:");
            userNameTitle.setFont(italic.deriveFont(20f));
            userNameTitle.setPreferredSize(new Dimension(500, 50));
            userNameTitle.setOpaque(false);
            userNameTitle.setForeground(Color.WHITE);

            JComboBox<TraderComboBoxItem> users = new JComboBox<>();
            users.setPreferredSize(new Dimension(500, 50));
            users.setFont(regular.deriveFont(20f));
            users.setBackground(gray2);
            users.setForeground(Color.BLACK);
            users.setOpaque(true);
            messageManager.getAllUsers().forEach(id -> {
                if (!id.equals(userId)) {
                    try {
                        users.addItem(new TraderComboBoxItem(id));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }  
            });

            JLabel messageBodyTitle = new JLabel("Full Message:");
            messageBodyTitle.setFont(italic.deriveFont(20f));
            messageBodyTitle.setPreferredSize(new Dimension(500, 50));
            messageBodyTitle.setOpaque(false);
            messageBodyTitle.setForeground(Color.WHITE);

            JTextArea fullMessageBody = new JTextArea();
            fullMessageBody.setFont(regular.deriveFont(20f));
            fullMessageBody.setBackground(gray2);
            fullMessageBody.setForeground(Color.BLACK);
            fullMessageBody.setPreferredSize(new Dimension(500, 200));
            fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            fullMessageBody.setLineWrap(true);

            JButton sendMessageButton = new JButton("Send");
            sendMessageButton.setFont(bold.deriveFont(25f));
            sendMessageButton.setBackground(green);
            sendMessageButton.setOpaque(true);
            sendMessageButton.setForeground(Color.WHITE);
            sendMessageButton.setPreferredSize(new Dimension(225, 75));
            sendMessageButton.setBorder(BorderFactory.createMatteBorder(10, 50, 10, 50, bg));
            sendMessageButton.addActionListener(e1 -> {
                try {
                    if (fullMessageBody.getText().trim().length() != 0) {
                        messageManager.sendMessage(userId, ((TraderComboBoxItem) users.getSelectedItem()).getId(),
                                fullMessageBody.getText().trim());
                        messageDetailsModal.dispose();
                    }
                } catch (UserNotFoundException | AuthorizationException e2) {
                    e2.printStackTrace();
                }
            });

            messageDetailsPanel.add(userNameTitle);
            messageDetailsPanel.add(users);
            messageDetailsPanel.add(messageBodyTitle);
            messageDetailsPanel.add(fullMessageBody);
            messageDetailsModal.add(messageDetailsPanel);
            messageDetailsModal.add(sendMessageButton, BorderLayout.SOUTH);
            messageDetailsModal.setModal(true);
            messageDetailsModal.setVisible(true);
        });
        return addNewMessageButton;
    }

    private void getMessages() {
        try {
            HashMap<String, List<String>> messages = userId.equals("") ? new HashMap<>()
                    : messageManager.getMessages(userId);
            if (messages.size() == 0) {
                messagesListContainer = new JPanel();
                setNoMessagesFound();
                return;
            }
            int numRows = messages.keySet().size();
            if (numRows < 4)
                numRows = 4;
            messagesListContainer = new JPanel(new GridLayout(numRows, 1));
            messagesListContainer.setPreferredSize(messagesDimension); // fix
            messagesListContainer.setBackground(gray3);
            for (String fromUserId : messages.keySet()) {
                JPanel messagePanel = new JPanel(new GridLayout(1, 5));
                messagePanel.setPreferredSize(new Dimension(1000, 75));
                messagePanel.setBackground(gray2);
                messagePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg));

                try {
                    JLabel userName = new JLabel(userQuery.getUsername(fromUserId));
                    if (userQuery.getType(fromUserId).equals(UserTypes.TRADER))
                        userName.setFont(regular.deriveFont(20f));
                    else
                        userName.setFont(bold.deriveFont(20f));
                    userName.setForeground(Color.BLACK);
                    userName.setHorizontalAlignment(JLabel.LEFT);
                    userName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                    JButton detailsButton = new JButton("View Conversation");
                    detailsButton.setFont(bold.deriveFont(20f));
                    detailsButton.setForeground(Color.WHITE);
                    detailsButton.setBackground(gray3);
                    detailsButton.setOpaque(true);
                    detailsButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    detailsButton.addActionListener(e -> {
                        JDialog messageDetailsModal = new JDialog();
                        messageDetailsModal.setTitle("Message Details");
                        messageDetailsModal.setSize(600, 400);
                        messageDetailsModal.setResizable(false);
                        messageDetailsModal.setLocationRelativeTo(null);

                        JPanel messageDetailsPanel = new JPanel();
                        messageDetailsPanel.setPreferredSize(new Dimension(600, 400));
                        messageDetailsPanel.setBackground(bg);

                        JLabel userNameTitle = new JLabel("Sender Username:");
                        userNameTitle.setFont(italic.deriveFont(20f));
                        userNameTitle.setPreferredSize(new Dimension(550, 50));
                        userNameTitle.setOpaque(false);
                        userNameTitle.setForeground(Color.WHITE);

                        JLabel userNameCopy = new JLabel();
                        try {
                            userNameCopy = new JLabel(userQuery.getUsername(fromUserId));
                        } catch (UserNotFoundException userNotFoundException) {
                            userNotFoundException.printStackTrace();
                        }
                        try {
                            if (userQuery.getType(fromUserId).equals(UserTypes.TRADER))
                                userNameCopy.setFont(regular.deriveFont(20f));
                            else
                                userNameCopy.setFont(bold.deriveFont(20f));
                        } catch (UserNotFoundException userNotFoundException) {
                            userNotFoundException.printStackTrace();
                        }
                        userNameCopy.setForeground(Color.WHITE);
                        userNameCopy.setHorizontalAlignment(JLabel.LEFT);
                        userNameCopy.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                        JLabel messageBodyTitle = new JLabel("Full Message:");
                        messageBodyTitle.setFont(italic.deriveFont(20f));
                        messageBodyTitle.setPreferredSize(new Dimension(550, 50));
                        messageBodyTitle.setOpaque(false);
                        messageBodyTitle.setForeground(Color.WHITE);

                        StringBuilder fullMessageString = new StringBuilder();
                        messages.get(fromUserId).forEach(msg -> {
                            fullMessageString.append("-> ").append(msg).append("\n");
                        });
                        JTextArea fullMessageBody = new JTextArea(fullMessageString.toString());
                        fullMessageBody.setFont(regular.deriveFont(20f));
                        fullMessageBody.setBackground(gray);
                        fullMessageBody.setForeground(Color.WHITE);
                        fullMessageBody.setPreferredSize(new Dimension(550, 200));
                        fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        fullMessageBody.setLineWrap(true);
                        fullMessageBody.setWrapStyleWord(true);
                        fullMessageBody.setEditable(false);

                        messageDetailsPanel.add(userNameTitle);
                        messageDetailsPanel.add(userNameCopy);
                        messageDetailsPanel.add(messageBodyTitle);
                        messageDetailsPanel.add(fullMessageBody);

                        messageDetailsModal.add(messageDetailsPanel);
                        messageDetailsModal.setModal(true);
                        messageDetailsModal.setVisible(true);

                    });

                    JButton clearButton = new JButton("Clear");
                    clearButton.setFont(bold.deriveFont(20f));
                    clearButton.setForeground(Color.WHITE);
                    clearButton.setBackground(red);
                    clearButton.setOpaque(true);
                    clearButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    clearButton.addActionListener(e -> {
                        try {
                            messageManager.clearMessagesFromUser(userId, fromUserId);
                            messagesListContainer.remove(messagePanel);
                            messagesListContainer.revalidate();
                            messagesListContainer.repaint();
                        } catch (UserNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    });

                    JButton replyButton = new JButton("Reply");
                    replyButton.setFont(bold.deriveFont(20f));
                    replyButton.setForeground(Color.WHITE);
                    replyButton.setBackground(green);
                    replyButton.setOpaque(true);
                    replyButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    replyButton.addActionListener(e -> {
                        JDialog messageReplyModal = new JDialog();
                        messageReplyModal.setTitle("Message Details");
                        messageReplyModal.setSize(600, 400);
                        messageReplyModal.setResizable(false);
                        messageReplyModal.setLocationRelativeTo(null);

                        JPanel messageReplyPanel = new JPanel();
                        messageReplyPanel.setPreferredSize(new Dimension(600, 400));
                        messageReplyPanel.setBackground(bg);

                        JLabel userNameTitle = new JLabel("Sender Username:");
                        userNameTitle.setFont(italic.deriveFont(20f));
                        userNameTitle.setPreferredSize(new Dimension(550, 50));
                        userNameTitle.setOpaque(false);
                        userNameTitle.setForeground(Color.WHITE);

                        JLabel userNameCopy = new JLabel();
                        try {
                            userNameCopy = new JLabel(userQuery.getUsername(fromUserId));
                        } catch (UserNotFoundException userNotFoundException) {
                            userNotFoundException.printStackTrace();
                        }
                        try {
                            if (userQuery.getType(fromUserId).equals(UserTypes.TRADER))
                                userNameCopy.setFont(regular.deriveFont(20f));
                            else
                                userNameCopy.setFont(bold.deriveFont(20f));
                        } catch (UserNotFoundException userNotFoundException) {
                            userNotFoundException.printStackTrace();
                        }
                        userNameCopy.setForeground(Color.WHITE);
                        userNameCopy.setHorizontalAlignment(JLabel.LEFT);
                        userNameCopy.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                        JLabel messageBodyTitle = new JLabel("Enter Message:");
                        messageBodyTitle.setFont(italic.deriveFont(20f));
                        messageBodyTitle.setPreferredSize(new Dimension(550, 50));
                        messageBodyTitle.setOpaque(false);
                        messageBodyTitle.setForeground(Color.WHITE);

                        JTextArea fullMessageBody = new JTextArea();
                        fullMessageBody.setFont(regular.deriveFont(20f));
                        fullMessageBody.setPreferredSize(new Dimension(550, 150));
                        fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        fullMessageBody.setLineWrap(true);

                        JButton submitButton = new JButton("Submit");
                        submitButton.setFont(bold.deriveFont(20f));
                        submitButton.setBackground(green);
                        submitButton.setForeground(Color.WHITE);
                        submitButton.setPreferredSize(new Dimension(325, 50));
                        submitButton.addActionListener(e1 -> {
                            if (fullMessageBody.getText().trim().length() > 0) {
                                try {
                                    messageManager.sendMessage(userId, fromUserId, fullMessageBody.getText());
                                    messageManager.clearMessagesFromUser(userId, fromUserId);
                                    messageReplyModal.dispose();
                                    messagesListContainer.remove(messagePanel);
                                    messagesListContainer.revalidate();
                                    messagesListContainer.repaint();
                                } catch (UserNotFoundException | AuthorizationException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        });

                        messageReplyPanel.add(userNameTitle);
                        messageReplyPanel.add(userNameCopy);
                        messageReplyPanel.add(messageBodyTitle);
                        messageReplyPanel.add(fullMessageBody);

                        messageReplyModal.add(messageReplyPanel);
                        messageReplyModal.add(submitButton, BorderLayout.SOUTH);
                        messageReplyModal.setModal(true);
                        messageReplyModal.setVisible(true);

                    });

                    messagePanel.add(userName);
                    messagePanel.add(detailsButton);
                    messagePanel.add(clearButton);
                    messagePanel.add(replyButton);
                    messagesListContainer.add(messagePanel);

                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getReports() throws UserNotFoundException {

        List<String[]> reports = reportManager.getReports();
        if (reports.size() == 0) {
            return;
        }
        int numRows = reports.size();
        if(messagesListContainer.getLayout() instanceof GridLayout) {
            numRows += ((GridLayout) messagesListContainer.getLayout()).getRows();
        }
        if(numRows == 0) {
            messagesListContainer = new JPanel();
            setNoMessagesFound();
            return;
        }
        if (numRows < 7)
            numRows = 7;
        if (messagesListContainer.getLayout() instanceof BorderLayout) {
            messagesListContainer = new JPanel(new GridLayout(numRows, 1));
        } else {
            messagesListContainer.setLayout(new GridLayout(numRows, 1));
        }
                
        for (String[] report : reports) {
            JPanel reportPanel = new JPanel(new GridLayout(1, 4));
            reportPanel.setPreferredSize(new Dimension(1000, 75));
            reportPanel.setBackground(gray3);
            reportPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg));

            JLabel fromUsername = new JLabel(userQuery.getUsername(report[0]));
            fromUsername.setForeground(Color.BLACK);
            fromUsername.setHorizontalAlignment(JLabel.LEFT);
            fromUsername.setFont(bold.deriveFont(20f));
            fromUsername.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JLabel toUsername = new JLabel(userQuery.getUsername(report[1]));
            toUsername.setForeground(Color.BLACK);
            toUsername.setHorizontalAlignment(JLabel.LEFT);
            toUsername.setFont(bold.deriveFont(20f));
            toUsername.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JButton detailsButton = new JButton("View Full Report");
            detailsButton.setFont(bold.deriveFont(20f));
            detailsButton.setForeground(Color.WHITE);
            detailsButton.setBackground(bg);
            detailsButton.setOpaque(true);
            detailsButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray3));
            detailsButton.addActionListener(e -> {
                JDialog reportDetailsModal = new JDialog();
                reportDetailsModal.setTitle("Report Details");
                reportDetailsModal.setSize(600, 400);
                reportDetailsModal.setResizable(false);
                reportDetailsModal.setLocationRelativeTo(null);

                JPanel reportDetailsPanel = new JPanel();
                reportDetailsPanel.setPreferredSize(new Dimension(600, 400));
                reportDetailsPanel.setBackground(bg);

                JLabel fromUsernametitle = new JLabel("Report Sender:");
                fromUsernametitle.setFont(italic.deriveFont(20f));
                fromUsernametitle.setPreferredSize(new Dimension(275, 50));
                fromUsernametitle.setOpaque(false);
                fromUsernametitle.setForeground(Color.WHITE);

                JLabel fromUsernameLabel = new JLabel(fromUsername.getText());
                fromUsernameLabel.setHorizontalAlignment(JLabel.LEFT);
                fromUsernameLabel.setFont(regular.deriveFont(20f));
                fromUsernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
                fromUsernameLabel.setForeground(Color.WHITE);
                fromUsernameLabel.setPreferredSize(new Dimension(275, 50));
                
                JLabel toUsernametitle = new JLabel("Report Reciever:");
                toUsernametitle.setFont(italic.deriveFont(20f));
                toUsernametitle.setPreferredSize(new Dimension(275, 50));
                toUsernametitle.setOpaque(false);
                toUsernametitle.setForeground(Color.WHITE);

                JLabel toUsernameLabel = new JLabel(toUsername.getText());
                toUsernameLabel.setHorizontalAlignment(JLabel.LEFT);
                toUsernameLabel.setFont(bold.deriveFont(20f));
                toUsernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
                toUsernameLabel.setForeground(Color.WHITE);
                toUsernameLabel.setPreferredSize(new Dimension(275, 50));

                JLabel messageBodyTitle = new JLabel("Report Message:");
                messageBodyTitle.setFont(italic.deriveFont(20f));
                messageBodyTitle.setPreferredSize(new Dimension(550, 50));
                messageBodyTitle.setOpaque(false);
                messageBodyTitle.setForeground(Color.WHITE);

                JTextArea fullMessageBody = new JTextArea(report[2]);
                fullMessageBody.setFont(regular.deriveFont(20f));
                fullMessageBody.setBackground(gray);
                fullMessageBody.setForeground(Color.WHITE);
                fullMessageBody.setPreferredSize(new Dimension(550, 200));
                fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                fullMessageBody.setLineWrap(true);
                fullMessageBody.setEditable(false);
                fullMessageBody.setWrapStyleWord(true);


                reportDetailsPanel.add(fromUsernametitle);
                reportDetailsPanel.add(fromUsernameLabel);
                reportDetailsPanel.add(toUsernametitle);
                reportDetailsPanel.add(toUsernameLabel);
                reportDetailsPanel.add(messageBodyTitle);
                reportDetailsPanel.add(fullMessageBody);

                reportDetailsModal.add(reportDetailsPanel);
                reportDetailsModal.setModal(true);
                reportDetailsModal.setVisible(true);

            }); 

            JButton clearButton = new JButton("Clear");
            clearButton.setFont(bold.deriveFont(20f));
            clearButton.setForeground(Color.WHITE);
            clearButton.setBackground(red);
            clearButton.setOpaque(true);
            clearButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray3));
            clearButton.addActionListener(e -> {
                reportManager.clearReport(report[3]);
                messagesListContainer.remove(reportPanel);
                messagesListContainer.revalidate();
                messagesListContainer.repaint();
            });

            reportPanel.add(fromUsername);
            reportPanel.add(toUsername);
            reportPanel.add(detailsButton);
            reportPanel.add(clearButton);
            messagesListContainer.add(reportPanel);
        }
    }
}
