package frontend.panels.admin_panel.admin_subpanels;

import backend.exceptions.*;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingManager;
import frontend.components.TraderComboBoxItem;
import frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals.TradeDetailsModal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is used to show the different settings that an admin can configure
 */
public class ControlPanel extends JPanel implements ActionListener {

    private final LoginManager loginManager = new LoginManager();
    private final JLabel errorMessage = new JLabel();
    private final JComboBox<Integer> minLendChoice, tradeLimitChoice, incompleteLimitChoice;
    private final JButton submitSettings = new JButton("Submit"), submitAdmin = new JButton("Submit");
    private final JTextField usernameInput = new JTextField();
    private final JPasswordField passwordInput = new JPasswordField();

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(196, 196, 196);
    private final Color gray2 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);
    private final Color red = new Color(219, 58, 52);

    private final TradeQuery tradeQuery = new TradeQuery();
    private final UserQuery userQuery = new UserQuery();
    private String trader = "";
    private JComboBox<TraderComboBoxItem> traders;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", new Locale("en", "US"));
    private final TradingManager tradingManager = new TradingManager();
    private JScrollPane ongoingTradesScrollPane;

    /**
     * Makes a new control panel
     *
     * @param userId     the admin id
     * @param regular    regular font
     * @param bold       bold font
     * @param italic     italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with accessing database
     */
    public ControlPanel(String userId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.setSize(1200, 900);
        this.setBackground(Color.BLACK);
        this.setBorder(new EmptyBorder(50, 50, 100, 50));

        JPanel titles = new JPanel(new GridLayout(1, 2, 100, 0));
        titles.setOpaque(false);
        titles.setPreferredSize(new Dimension(1200, 50));

        createLabel(regular, titles, "Trade Settings", 30f, JLabel.LEFT);
        createLabel(regular, titles, "Create New Admin", 30f, JLabel.LEFT);

        JPanel splitContainer = new JPanel(new GridLayout(1, 2, 100, 0));
        splitContainer.setOpaque(false);
        splitContainer.setPreferredSize(new Dimension(1200, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel tradeSettings = createTradeSettings(bg, splitContainer);

        JPanel info = createInfoPanel(bg, gbc, tradeSettings);

        createLabel(regular, info, "Minimum to Borrow:", 22f, JLabel.RIGHT);

        Integer[] minLendChoices = new Integer[100];
        for (int i = 1; i < 101; i++) {
            minLendChoices[i - 1] = i;
        }
        minLendChoice = new JComboBox<>(minLendChoices);

        handleInfoSubpanel(bg, info, minLendChoice, TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW);

        createLabel(regular, info, "Default Trade Limit:", 22f, JLabel.RIGHT);

        Integer[] tradeLimitChoices = new Integer[100];
        for (int i = 1; i < 101; i++) {
            tradeLimitChoices[i - 1] = i;
        }
        tradeLimitChoice = new JComboBox<>(tradeLimitChoices);
        handleInfoSubpanel(bg, info, tradeLimitChoice, TraderProperties.TRADE_LIMIT);

        createLabel(regular, info, "Incomplete Trade Limit:", 22f, JLabel.RIGHT);

        Integer[] incompleteLimitChoices = new Integer[100];
        for (int i = 1; i < 101; i++) {
            incompleteLimitChoices[i - 1] = i;
        }
        incompleteLimitChoice = new JComboBox<>(incompleteLimitChoices);
        handleInfoSubpanel(bg, info, incompleteLimitChoice, TraderProperties.INCOMPLETE_TRADE_LIM);

        createSubmitSettings(bold, bg, gbc, tradeSettings);
        JPanel newAdmin = createNewAdmin(bg, splitContainer);
        gbc = new GridBagConstraints();

        JPanel input = createNewInputForAdmin(bg, gbc, newAdmin);
        createLabel(regular, input, "Username:", 25f, JLabel.CENTER);
        createAccountInputs(regular, regular, bg, input);
        createMessageWrapper(regular, bg, gbc, newAdmin);
        handleSubmitAdmin(bold, bg, gbc, newAdmin);

        JPanel ongoingTradesHeader = setOngoingTradesHeader(regular);


        // JLabel ah = new JLabel("<html><b><i>Lorem ipsum dolor sit amet, consectetur
        // adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna
        // aliqua. <br>Ut enim ad minim veniam, quis nostrud exercitation ullamco
        // laboris nisi ut aliquip ex ea commodo consequat. <br>Duis aute irure dolor in
        // reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
        // <br>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia
        // deserunt mollit anim id est laborum.</i></b></html>");
        // ah.setBackground(Color.black);
        // ah.setForeground(Color.white);
        // ah.setFont(ah.getFont().deriveFont(20f));
        // ah.setBorder(BorderFactory.createMatteBorder(80, 0, 0, 0, Color.black));

        this.add(titles);
        this.add(splitContainer);
        this.add(setUndoTradeButtonPanel(userId, regular, bold, italic, boldItalic));
        this.add(ongoingTradesHeader);
        this.add(setOngoingTradesScrollPane());

    }

    private JPanel setOngoingTradesHeader(Font regular) {
        JPanel ongoingTradesHeader = new JPanel(new GridLayout(1, 5, 25, 0));
        ongoingTradesHeader.setPreferredSize(new Dimension(1200, 25));
        ongoingTradesHeader.setBackground(bg);
        ongoingTradesHeader.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 80));

        JLabel name = new JLabel("Name");
        name.setFont(regular.deriveFont(20f));
        name.setForeground(Color.white);
        name.setHorizontalAlignment(JLabel.LEFT);

        JLabel location = new JLabel("Location");
        location.setFont(regular.deriveFont(20f));
        location.setForeground(Color.white);
        location.setHorizontalAlignment(JLabel.CENTER);

        JLabel meetingTime = new JLabel("           Meeting Time");
        meetingTime.setFont(regular.deriveFont(20f));
        meetingTime.setForeground(Color.white);
        meetingTime.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty2 = new JLabel("");
        JLabel empty1 = new JLabel("");

        ongoingTradesHeader.add(name);
        ongoingTradesHeader.add(location);
        ongoingTradesHeader.add(meetingTime);

        ongoingTradesHeader.add(empty1);
        ongoingTradesHeader.add(empty2);

        return ongoingTradesHeader;
    }

    private JScrollPane setOngoingTradesScrollPane() {
        ongoingTradesScrollPane = new JScrollPane();

        JPanel ongoingTradesContainer = new JPanel();
        ongoingTradesContainer.setBackground(bg);

        ongoingTradesScrollPane.setPreferredSize(new Dimension(1200, 280));
        ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
        ongoingTradesScrollPane.setBorder(null);

        return ongoingTradesScrollPane;
    }

    private JPanel setOngoingTradesContainer(Font regular, Font bold, Font italic, Font boldItalic) throws UserNotFoundException, AuthorizationException,
            TradeNotFoundException {
        JPanel ongoingTradesContainer = new JPanel();

        List<String> acceptedTrades = trader.equals("") ? new ArrayList<>() : userQuery.getAcceptedTrades(trader);

        if(acceptedTrades.isEmpty())
            return createNoTradesFoundPanel(bold);

        int numRows = acceptedTrades.size();
        numRows = Math.max(4, numRows);

        ongoingTradesContainer.setLayout(new GridLayout(numRows, 1));
        ongoingTradesContainer.setBackground(bg);
        ongoingTradesContainer.setBorder(null);

        for (String tradeID : acceptedTrades) {
            JPanel ongoingTradePanel = createOngoingTradePanel(tradeID, regular, bold, italic, boldItalic);
            ongoingTradesContainer.add(ongoingTradePanel);
        }

        return ongoingTradesContainer;
    }

    private JPanel createNoTradesFoundPanel(Font bold) {
        JPanel noTradesFoundPanel = new JPanel();
        noTradesFoundPanel.setBackground(bg);
        JLabel noTradesFound = new JLabel();
        noTradesFound.setFont(bold.deriveFont(30f));
        noTradesFound.setForeground(Color.WHITE);
        noTradesFoundPanel.add(noTradesFound, BorderLayout.CENTER);
        return noTradesFoundPanel;
    }

    private JPanel createOngoingTradePanel(String tradeID, Font regular, Font bold, Font italic, Font boldItalic) throws TradeNotFoundException, UserNotFoundException {
        JPanel ongoingTradePanel = new JPanel(new GridLayout(1, 5, 10, 0));
        ongoingTradePanel.setPreferredSize(new Dimension(1000, 75));
        ongoingTradePanel.setBorder(BorderFactory.createLineBorder(bg));
        ongoingTradePanel.setBackground(gray);

        boolean isTraderFirstUser = tradeQuery.getFirstUserId(tradeID).equals(trader);

        JLabel otherTraderName = new JLabel();

        if(isTraderFirstUser)
            otherTraderName.setText(userQuery.getUsername(tradeQuery.getSecondUserId(tradeID)));
        else
            otherTraderName.setText(userQuery.getUsername(tradeQuery.getFirstUserId(tradeID)));

        otherTraderName.setFont(regular.deriveFont(20f));
        otherTraderName.setForeground(Color.BLACK);
        otherTraderName.setHorizontalAlignment(JLabel.LEFT);
        otherTraderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        JLabel tradeLocation = new JLabel(tradeQuery.getMeetingLocation(tradeID));
        tradeLocation.setFont(regular.deriveFont(20f));
        tradeLocation.setForeground(Color.BLACK);
        tradeLocation.setHorizontalAlignment(JLabel.CENTER);

        JLabel tradeMeetingTime = new JLabel();

        if (tradeQuery.isFirstUserConfirmed1(tradeID) && tradeQuery.isSecondUserConfirmed1(tradeID)) {
            tradeMeetingTime.setText(dateFormat.format(tradeQuery.getSecondMeetingTime(tradeID)));
        } else {
            tradeMeetingTime.setText(dateFormat.format(tradeQuery.getMeetingTime(tradeID)));
        }

        tradeMeetingTime.setFont(regular.deriveFont(20f));
        tradeMeetingTime.setForeground(Color.BLACK);
        tradeMeetingTime.setHorizontalAlignment(JLabel.CENTER);

        JButton tradeDetailsButton = new JButton("Details");
        tradeDetailsButton.setFont(bold.deriveFont(20f));
        tradeDetailsButton.setForeground(Color.WHITE);
        tradeDetailsButton.setBackground(gray2);
        tradeDetailsButton.setOpaque(true);
        tradeDetailsButton.setBorder(BorderFactory.createLineBorder(gray, 15));

        tradeDetailsButton.addActionListener(e -> {
            try {
                new TradeDetailsModal(tradeID, false, isTraderFirstUser, regular, bold, italic, boldItalic);
            } catch (IOException | TradeNotFoundException | UserNotFoundException
                    | TradableItemNotFoundException exception) {
                exception.printStackTrace();
            }
        });

        JButton tradeUndoButton = setUndoButton(tradeID, regular, bold, italic, boldItalic);

        ongoingTradePanel.add(otherTraderName);
        ongoingTradePanel.add(tradeLocation);
        ongoingTradePanel.add(tradeMeetingTime);
        ongoingTradePanel.add(tradeDetailsButton);
        ongoingTradePanel.add(tradeUndoButton);

        return ongoingTradePanel;
    }

    private JButton setUndoButton(String tradeID, Font regular, Font bold, Font italic, Font boldItalic) {
        JButton tradeUndoButton = new JButton();

        tradeUndoButton.setText("Undo");
        tradeUndoButton.setFont(bold.deriveFont(20f));
        tradeUndoButton.setForeground(Color.WHITE);
        tradeUndoButton.setBackground(Color.red);
        tradeUndoButton.setBorder(BorderFactory.createLineBorder(gray, 15));

        tradeUndoButton.addActionListener(e -> {
            try {
                tradingManager.rescindOngoingTrade(tradeID);
                JPanel ongoingTradesContainer = setOngoingTradesContainer(regular, bold, italic, boldItalic);
                ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
            } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException | CannotTradeException exception) {
                exception.printStackTrace();
            }
        });

        return tradeUndoButton;
    }

    private JPanel setUndoTradeButtonPanel(String userId, Font regular, Font bold, Font italic, Font boldItalic) {
        JPanel undoTradeButtonPanel = new JPanel(new GridLayout(1, 3));
        undoTradeButtonPanel.setPreferredSize(new Dimension(1200, 130));
        undoTradeButtonPanel.setBackground(bg);
        undoTradeButtonPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 10, 0, Color.BLACK));

        JLabel undoTradeLabel = new JLabel("Undo Trade");
        undoTradeLabel.setFont(bold.deriveFont(25f));
        undoTradeLabel.setForeground(Color.WHITE);
        undoTradeLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        undoTradeLabel.setOpaque(false);

        traders = new JComboBox<>();
        traders.setFont(regular.deriveFont(20f));
        traders.setBorder(BorderFactory.createMatteBorder(20, 25, 20, 50, bg));
        traders.setBackground(gray2);
        traders.setForeground(Color.BLACK);
        traders.setOpaque(true);
        traders.addItem(null);
        userQuery.getAllTraders().forEach(id -> {
            try {
                traders.addItem(new TraderComboBoxItem(id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // JButton undoTradeButton = new JButton("View Ongoing Trades");
        JButton undoTradeButton = new JButton("Select a Trader");
        undoTradeButton.setFont(boldItalic.deriveFont(20f));
        undoTradeButton.setBackground(bg);
        undoTradeButton.setForeground(Color.WHITE);
        undoTradeButton.setBorder(BorderFactory.createMatteBorder(20, 50, 20, 25, bg));
        undoTradeButton.setEnabled(false);
        undoTradeButton.addActionListener(e -> {
            trader = traders.getItemAt(traders.getSelectedIndex()).getId();
            try {
                JPanel ongoingTradesContainer = setOngoingTradesContainer(regular, bold, italic, boldItalic);
                ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
            } catch (UserNotFoundException | AuthorizationException | TradeNotFoundException exception) {
                exception.printStackTrace();
            }
        });

        traders.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (traders.getItemAt(0) == null)
                    traders.removeItemAt(0);
                undoTradeButton.setText("View Ongoing Trades");
                undoTradeButton.setFont(bold.deriveFont(20f));
                undoTradeButton.setBackground(red);
                undoTradeButton.setEnabled(true);
            }
        });

        undoTradeButtonPanel.add(undoTradeLabel);
        undoTradeButtonPanel.add(traders);
        undoTradeButtonPanel.add(undoTradeButton);
        return undoTradeButtonPanel;
    }

    private void handleSubmitAdmin(Font bold, Color bg, GridBagConstraints gbc, JPanel newAdmin) {
        submitAdmin.setBorder(BorderFactory.createMatteBorder(0, 180, 10, 180, bg));
        submitAdmin.setBackground(green);
        submitAdmin.setForeground(Color.WHITE);
        submitAdmin.setFont(bold.deriveFont(25f));
        submitAdmin.addActionListener(this);
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        newAdmin.add(submitAdmin, gbc);
    }

    private void createMessageWrapper(Font regular, Color bg, GridBagConstraints gbc, JPanel newAdmin) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setOpaque(false);
        messageWrapper.setPreferredSize(new Dimension(450, 50));
        errorMessage.setForeground(Color.red);
        errorMessage.setBackground(bg);
        errorMessage.setFont(regular.deriveFont(15f));
        errorMessage.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        messageWrapper.add(errorMessage);
        newAdmin.add(messageWrapper, gbc);
    }

    private void createAccountInputs(Font regular, Font boldItalic, Color bg, JPanel input) {
        usernameInput.setBorder(BorderFactory.createMatteBorder(25, 0, 25, 30, bg));
        usernameInput.setFont(regular.deriveFont(25f));
        input.add(usernameInput);

        createLabel(boldItalic, input, "Password:", 25f, JLabel.CENTER);

        passwordInput.setBorder(BorderFactory.createMatteBorder(25, 0, 25, 30, bg));
        passwordInput.setFont(regular.deriveFont(25f));
        input.add(passwordInput);
    }

    private JPanel createNewInputForAdmin(Color bg, GridBagConstraints gbc, JPanel newAdmin) {
        JPanel input = new JPanel(new GridLayout(2, 2, 70, 0));
        input.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, bg));
        input.setBackground(bg);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.6;
        newAdmin.add(input, gbc);
        return input;
    }

    private JPanel createNewAdmin(Color bg, JPanel splitContainer) {
        JPanel newAdmin = new JPanel(new GridBagLayout());
        newAdmin.setPreferredSize(new Dimension(450, 250));
        newAdmin.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, bg));
        newAdmin.setBackground(bg);
        splitContainer.add(newAdmin);
        return newAdmin;
    }

    private void createSubmitSettings(Font bold, Color bg, GridBagConstraints gbc, JPanel tradeSettings) {
        submitSettings.setBorder(BorderFactory.createMatteBorder(10, 160, 10, 160, bg));
        submitSettings.setBackground(green);
        submitSettings.setForeground(Color.WHITE);
        submitSettings.setFont(bold.deriveFont(25f));
        submitSettings.addActionListener(this);
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.insets = new Insets(30, 0, 0, 0);
        tradeSettings.add(submitSettings, gbc);
    }

    private void handleInfoSubpanel(Color bg, JPanel info, JComboBox<Integer> minLendChoice,
            TraderProperties minimumAmountNeededToBorrow) throws IOException {
        minLendChoice.setSelectedIndex(loginManager.getProperty(minimumAmountNeededToBorrow) - 1);
        minLendChoice.setBorder(BorderFactory.createMatteBorder(0, 75, 0, 75, bg));
        info.add(minLendChoice);
    }

    private JPanel createInfoPanel(Color bg, GridBagConstraints gbc, JPanel tradeSettings) {
        JPanel info = new JPanel(new GridLayout(3, 2, 20, 35));
        info.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, bg));
        info.setBackground(bg);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.9;
        tradeSettings.add(info, gbc);
        return info;
    }

    private JPanel createTradeSettings(Color bg, JPanel splitContainer) {
        JPanel tradeSettings = new JPanel(new GridBagLayout());
        tradeSettings.setBorder(BorderFactory.createMatteBorder(30, 20, 20, 20, bg));
        tradeSettings.setPreferredSize(new Dimension(450, 250));
        tradeSettings.setBackground(bg);
        splitContainer.add(tradeSettings);
        return tradeSettings;
    }

    private void createLabel(Font boldItalic, JPanel titles, String s, float v, int left) {
        JLabel tradeSettingsTitle = new JLabel(s);
        tradeSettingsTitle.setForeground(Color.white);
        tradeSettingsTitle.setFont(boldItalic.deriveFont(v));
        tradeSettingsTitle.setHorizontalAlignment(left);
        titles.add(tradeSettingsTitle);
    }

    /**
     * Used for displaying some message in the login screen
     *
     * @param msg the message being displayed
     */
    public void notifyLogin(String msg) {
        errorMessage.setText(msg);
        errorMessage.setVisible(true);
    }

    /**
     * Used to handle events
     *
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitAdmin) {
            try {
                loginManager.registerUser(usernameInput.getText(),
                        String.valueOf(passwordInput.getPassword()), UserTypes.ADMIN);
            } catch (BadPasswordException ex) {
                notifyLogin("<html><b><i>Invalid Password: " + ex.getMessage() + "</i></b></html>");
            } catch (UserAlreadyExistsException ignored) {
                notifyLogin("<html><b><i>The username '" + usernameInput.getText() + "' is taken.</i></b></html>");
            } catch (IOException ignored) {
                notifyLogin("<html><b><i>Could not create the account at this time.</i></b></html>");
            }
        } else if (e.getSource() == submitSettings) {
            try {
                loginManager.setProperty(TraderProperties.INCOMPLETE_TRADE_LIM, incompleteLimitChoice.getItemAt(incompleteLimitChoice.getSelectedIndex()));
                loginManager.setProperty(TraderProperties.TRADE_LIMIT, tradeLimitChoice.getItemAt(tradeLimitChoice.getSelectedIndex()));
                loginManager.setProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW, minLendChoice.getItemAt(minLendChoice.getSelectedIndex()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

