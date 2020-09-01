package frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import backend.exceptions.AuthorizationException;
import backend.exceptions.CannotTradeException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingManager;

/**
 * For showing the dialog that adds a new trade
 */
public class AddNewTradeModal extends JDialog implements ActionListener {

    private final String trader;
    private final String[] suggested;

    private final Font regular, bold, italic, boldItalic;

    private JCheckBox tradeWithinCityButton;
    private JComboBox<String> traders;
    private JComboBox<String> traderItems;
    private JComboBox<String> otherTraderItems;
    private JTextField meetingLocationInput, messageInput;
    private JCheckBox isTemporaryButton;
    private JPanel firstMeetingDate;
    private JPanel secondMeetingDate;
    private JLabel error;

    private final Color bg = new Color(51, 51, 51);
    private final Color gray2 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);
    private final Color red = new Color(219, 58, 52);

    private final UserQuery userQuery = new UserQuery();
    private final ItemQuery itemQuery = new ItemQuery();

    private final TradingManager tradeManager = new TradingManager();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", new Locale("en", "US"));

    /**
     * For making a new dialog that shows adding a new trade
     * @param trader the trader id
     * @param suggested the suggestion for the trade
     * @param regular regular font
     * @param bold bold font
     * @param italic italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with getting database files
     * @throws UserNotFoundException if the user isn't found
     * @throws TradableItemNotFoundException if the item isn't found
     */
    public AddNewTradeModal(String trader, String[] suggested, Font regular, Font bold, Font italic, Font boldItalic)
            throws IOException, UserNotFoundException, TradableItemNotFoundException {

        this.trader = trader;
        this.suggested = suggested;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setTitle("Add New Trade");
        this.setSize(600, 600);
        this.setResizable(true);
        this.setLocationRelativeTo(null);

        boolean isSuggested = suggested != null;

        JScrollPane addNewTradeScrollPane = setAddNewScrollPane(isSuggested);

        JButton tradeSubmitButton = setTradeSubmitButton();

        this.add(addNewTradeScrollPane);
        this.add(tradeSubmitButton, BorderLayout.SOUTH);
        this.setModal(true);
    }

    private JScrollPane setAddNewScrollPane(boolean isSuggested)
            throws UserNotFoundException, TradableItemNotFoundException {
        JScrollPane addNewTradeScrollPane = new JScrollPane();
        addNewTradeScrollPane.setPreferredSize(new Dimension(600,600));
        addNewTradeScrollPane.setBackground(bg);
        addNewTradeScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel addNewTradePanel = setAddNewTradePanel(isSuggested);

        addNewTradeScrollPane.setViewportView(addNewTradePanel);

        return addNewTradeScrollPane;
    }

    private JButton setTradeSubmitButton() {
        JButton tradeSubmitButton = new JButton("Submit");
        tradeSubmitButton.setFont(bold.deriveFont(25f));
        tradeSubmitButton.setBackground(green);
        tradeSubmitButton.setOpaque(true);
        tradeSubmitButton.setForeground(Color.WHITE);
        tradeSubmitButton.setPreferredSize(new Dimension(225, 75));
        tradeSubmitButton.setBorder(BorderFactory.createLineBorder(bg, 15));
        tradeSubmitButton.addActionListener(this);

        return tradeSubmitButton;
    }

    private JPanel setTradeWithinCityPanel() {
        JPanel tradeWithinCityPanel = new JPanel();
        tradeWithinCityPanel.setBackground(bg);
        tradeWithinCityPanel.setPreferredSize(new Dimension(500, 50));

        JLabel tradeWithinCityTitle = new JLabel("Trade Within City?");
        tradeWithinCityTitle.setFont(italic.deriveFont(20f));
        tradeWithinCityTitle.setPreferredSize(new Dimension(425, 50));
        tradeWithinCityTitle.setOpaque(false);
        tradeWithinCityTitle.setForeground(Color.WHITE);

        tradeWithinCityButton = new JCheckBox();
        tradeWithinCityButton.setPreferredSize(new Dimension(25, 25));
        tradeWithinCityButton.setSelected(false);
        tradeWithinCityButton.setForeground(Color.WHITE);
        tradeWithinCityButton.setBackground(bg);

        tradeWithinCityPanel.add(tradeWithinCityTitle);
        tradeWithinCityPanel.add(tradeWithinCityButton);

        return tradeWithinCityPanel;
    }

    private JPanel setOtherTradersPanel() {

        List<String> allTraders = userQuery.getAllTraders();

        JPanel otherTradersPanel = new JPanel();
        otherTradersPanel.setBackground(bg);
        otherTradersPanel.setPreferredSize(new Dimension(500, 125));

        JLabel otherTraderNameTitle = new JLabel("Trader Username:");
        otherTraderNameTitle.setFont(italic.deriveFont(20f));
        otherTraderNameTitle.setPreferredSize(new Dimension(450, 50));
        otherTraderNameTitle.setOpaque(false);
        otherTraderNameTitle.setForeground(Color.WHITE);

        traders = new JComboBox<>();
        traders.setPreferredSize(new Dimension(450, 50));
        traders.setFont(regular.deriveFont(20f));
        traders.setBackground(gray2);
        traders.setForeground(Color.BLACK);
        traders.setOpaque(true);
        traders.addItem(null);
        allTraders.forEach(traderId -> {
            if (!traderId.equals(trader)) {
                try {
                    traders.addItem(userQuery.getUsername(traderId));
                } catch (UserNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        });

        traders.addItemListener(ev -> {
            try {

                if (ev.getStateChange() == ItemEvent.SELECTED) {
                    if (traders.getItemAt(0) == null)
                        traders.removeItemAt(0);
                    otherTraderItems.setEnabled(false);
                    otherTraderItems.setVisible(false);
                    otherTraderItems.removeAllItems();
                    otherTraderItems.addItem(null);
                    for (String itemId : userQuery
                            .getAvailableItems(userQuery.getUserByUsername((String) traders.getSelectedItem()))) {
                        otherTraderItems.addItem(itemQuery.getName(itemId));
                    }
                    otherTraderItems.setVisible(true);
                    otherTraderItems.setEnabled(true);
                }
            } catch (TradableItemNotFoundException | UserNotFoundException | AuthorizationException e1) {
                e1.printStackTrace();
            }
        });

        tradeWithinCityButton.addItemListener(ex -> {
            traders.setVisible(false);
            traders.removeAllItems();
            allTraders.clear();
            if (tradeWithinCityButton.isSelected()) {
                try {
                    allTraders.addAll(userQuery.getAllTradersInCity(userQuery.getCity(trader)));
                } catch (UserNotFoundException | AuthorizationException e2) {
                    e2.printStackTrace();
                }
            } else {
                allTraders.addAll(userQuery.getAllTraders());
            }
            traders.addItem(null);
            allTraders.forEach(traderId -> {
                if (!traderId.equals(trader)) {
                    try {
                        traders.addItem(userQuery.getUsername(traderId));
                    } catch (UserNotFoundException e2) {
                        e2.printStackTrace();
                    }
                }
            });
            otherTraderItems.removeAllItems();
            otherTraderItems.setEnabled(false);
            traders.setVisible(true);
            traders.revalidate();
            traders.repaint();
        });

        otherTradersPanel.add(otherTraderNameTitle);
        otherTradersPanel.add(traders);
        return otherTradersPanel;
    }

    private JPanel setTraderItemsPanel()  {

        JPanel traderItemsPanel = new JPanel();
        traderItemsPanel.setBackground(bg);
        traderItemsPanel.setPreferredSize(new Dimension(500, 125));

        JLabel traderItemTitle = new JLabel("Item from your Inventory:");
        traderItemTitle.setFont(italic.deriveFont(20f));
        traderItemTitle.setPreferredSize(new Dimension(450, 50));
        traderItemTitle.setOpaque(false);
        traderItemTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        traderItemTitle.setForeground(Color.WHITE);

        traderItems = new JComboBox<>();
        traderItems.setFont(regular.deriveFont(20f));
        traderItems.setBackground(gray2);
        traderItems.setForeground(Color.BLACK);
        traderItems.setOpaque(true);
        traderItems.setPreferredSize(new Dimension(450, 50));
        try {
            traderItems.addItem(null);
            for (String itemId : userQuery.getAvailableItems(trader)) {
                traderItems.addItem(itemQuery.getName(itemId));
            }
        } catch (AuthorizationException | UserNotFoundException | TradableItemNotFoundException exception) {
            exception.printStackTrace();
        }

        traderItemsPanel.add(traderItemTitle);
        traderItemsPanel.add(traderItems);

        return traderItemsPanel;
    }

    private JPanel setOtherTraderItemsPanel() {

        JPanel otherTraderItemsPanel = new JPanel();
        otherTraderItemsPanel.setBackground(bg);
        otherTraderItemsPanel.setPreferredSize(new Dimension(500, 125));

        JLabel otherTraderItemsTitle = new JLabel("Item from their Inventory:");
        otherTraderItemsTitle.setFont(italic.deriveFont(20f));
        otherTraderItemsTitle.setPreferredSize(new Dimension(450, 50));
        otherTraderItemsTitle.setOpaque(false);
        otherTraderItemsTitle.setForeground(Color.WHITE);

        otherTraderItems = new JComboBox<>();
        otherTraderItems.setPreferredSize(new Dimension(450, 50));
        otherTraderItems.setFont(regular.deriveFont(20f));
        otherTraderItems.setBackground(gray2);
        otherTraderItems.setForeground(Color.BLACK);
        otherTraderItems.setOpaque(true);
        otherTraderItems.setEnabled(false);

        otherTraderItemsPanel.add(otherTraderItemsTitle);
        otherTraderItemsPanel.add(otherTraderItems);

        return otherTraderItemsPanel;
    }

    private JPanel setMeetingLocationPanel() {
        JPanel meetingLocationPanel = new JPanel();
        meetingLocationPanel.setBackground(bg);
        meetingLocationPanel.setPreferredSize(new Dimension(500, 125));

        JLabel meetingLocationTitle = new JLabel("Meeting Location");
        meetingLocationTitle.setFont(italic.deriveFont(20f));
        meetingLocationTitle.setPreferredSize(new Dimension(450, 50));
        meetingLocationTitle.setOpaque(false);
        meetingLocationTitle.setForeground(Color.WHITE);

        meetingLocationInput = new JTextField();
        meetingLocationInput.setFont(regular.deriveFont(20f));
        meetingLocationInput.setBackground(gray2);
        meetingLocationInput.setForeground(Color.BLACK);
        meetingLocationInput.setPreferredSize(new Dimension(450, 50));
        meetingLocationInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        meetingLocationPanel.add(meetingLocationTitle);
        meetingLocationPanel.add(meetingLocationInput);

        return meetingLocationPanel;
    }

    private JPanel setIsTemporaryPanel() {
        JPanel isTemporaryPanel = new JPanel();
        isTemporaryPanel.setBackground(bg);
        isTemporaryPanel.setPreferredSize(new Dimension(500, 50));

        JLabel isTemporaryTitle = new JLabel("Is this trade temporary?");
        isTemporaryTitle.setFont(italic.deriveFont(20f));
        isTemporaryTitle.setPreferredSize(new Dimension(425, 50));
        isTemporaryTitle.setOpaque(false);
        isTemporaryTitle.setForeground(Color.WHITE);

        isTemporaryButton = new JCheckBox();
        isTemporaryButton.setPreferredSize(new Dimension(25, 25));
        isTemporaryButton.setSelected(true);
        isTemporaryButton.setForeground(Color.WHITE);
        isTemporaryButton.setBackground(bg);

        isTemporaryPanel.add(isTemporaryTitle);
        isTemporaryPanel.add(isTemporaryButton);

        return isTemporaryPanel;
    }

    private JPanel setMeetingsPanel() {
        JPanel meetingsPanel = new JPanel();
        meetingsPanel.setBackground(bg);
        meetingsPanel.setPreferredSize(new Dimension(450, 250));

        JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
        firstMeetingDateTitle.setPreferredSize(new Dimension(450, 50));
        firstMeetingDateTitle.setFont(italic.deriveFont(20f));
        firstMeetingDateTitle.setOpaque(false);
        firstMeetingDateTitle.setForeground(Color.WHITE);

        firstMeetingDate = dateInput();

        JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
        secondMeetingDateTitle.setPreferredSize(new Dimension(450, 50));
        secondMeetingDateTitle.setFont(italic.deriveFont(20f));
        secondMeetingDateTitle.setOpaque(false);
        secondMeetingDateTitle.setForeground(Color.WHITE);

        secondMeetingDate = dateInput();

        isTemporaryButton.addItemListener(ex -> {
            if (isTemporaryButton.isSelected()) {
                secondMeetingDateTitle.setVisible(true);
                secondMeetingDate.setVisible(true);
            } else {
                secondMeetingDateTitle.setVisible(false);
                secondMeetingDate.setVisible(false);
            }
        });

        meetingsPanel.add(firstMeetingDateTitle);
        meetingsPanel.add(firstMeetingDate);
        meetingsPanel.add(secondMeetingDateTitle);
        meetingsPanel.add(secondMeetingDate);

        return meetingsPanel;
    }

    private JPanel setMessagePanel() {
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(bg);
        messagePanel.setPreferredSize(new Dimension(500, 125));

        JLabel messageTitle = new JLabel("Attach a message with this trade: (Optional)");
        messageTitle.setFont(italic.deriveFont(20f));
        messageTitle.setPreferredSize(new Dimension(450, 50));
        messageTitle.setOpaque(false);
        messageTitle.setForeground(Color.WHITE);

        messageInput = new JTextField();
        messageInput.setFont(regular.deriveFont(20f));
        messageInput.setBackground(gray2);
        messageInput.setForeground(Color.BLACK);
        messageInput.setPreferredSize(new Dimension(450, 50));
        messageInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        messagePanel.add(messageTitle);
        messagePanel.add(messageInput);

        return messagePanel;
    }

    private JPanel setAddNewTradePanel(boolean isSuggested)
            throws UserNotFoundException, TradableItemNotFoundException {
        JPanel addNewTradePanel = new JPanel();
        addNewTradePanel.setPreferredSize(new Dimension(500, 1100));
        addNewTradePanel.setBackground(bg);

        JPanel tradeWithinCityPanel = setTradeWithinCityPanel();
        JPanel otherTradersPanel = setOtherTradersPanel();
        JPanel traderItemsPanel = setTraderItemsPanel();
        JPanel otherTraderItemsPanel = setOtherTraderItemsPanel();
        JPanel meetingLocationPanel = setMeetingLocationPanel();
        JPanel isTemporaryPanel = setIsTemporaryPanel();
        JPanel meetingsPanel = setMeetingsPanel();
        JPanel messagePanel = setMessagePanel();

        error = new JLabel();
        error.setPreferredSize(new Dimension(500, 50));
        error.setForeground(red);
        error.setFont(boldItalic.deriveFont(20f));
        error.setHorizontalAlignment(JLabel.CENTER);
        error.setVisible(false);

        if (!isSuggested)
            addNewTradePanel.add(tradeWithinCityPanel);
        addNewTradePanel.add(otherTradersPanel);
        addNewTradePanel.add(traderItemsPanel);
        addNewTradePanel.add(otherTraderItemsPanel);
        addNewTradePanel.add(meetingLocationPanel);
        addNewTradePanel.add(isTemporaryPanel);
        addNewTradePanel.add(meetingsPanel);
        addNewTradePanel.add(messagePanel);
        addNewTradePanel.add(error);

        if (isSuggested) {
            traders.setSelectedItem(userQuery.getUsername(suggested[1]));
            traderItems.setSelectedItem(itemQuery.getName(suggested[2]));

            if (suggested.length == 4) {
                otherTraderItems.setSelectedItem(itemQuery.getName(suggested[3]));
            }
        }

        return addNewTradePanel;
    }

    /**
     * Handling any events
     * @param e events
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(meetingLocationInput.getText().trim().equals("")) {
            error.setText("Enter a meeting location.");
            error.setVisible(true);
            return;
        }
        error.setVisible(false);
        if (otherTraderItems.isEnabled() && (!meetingLocationInput.getText().trim().equals(""))
                && ((traderItems.getSelectedItem() != null ^ otherTraderItems.getSelectedItem() != null)
                        || (traderItems.getSelectedItem() != null && otherTraderItems.getSelectedItem() != null))) {
            String firstMeetingString = "";
            String secondMeetingString = "";
            for (int i = 0; i < 5; i++) {
                Component c = firstMeetingDate.getComponent(i);
                if (c instanceof JComboBox<?>) {
                    if (((String) ((JComboBox<?>) c).getSelectedItem().toString()).length() == 1) {
                        firstMeetingString += "0" + ((JComboBox<?>) c).getSelectedItem();
                    } else {
                        firstMeetingString += ((JComboBox<?>) c).getSelectedItem();
                    }
                    if (i == 3) {
                        firstMeetingString += ":";
                    } else {
                        firstMeetingString += " ";
                    }
                }
            }
            if (isTemporaryButton.isSelected()) {
                for (int i = 0; i < 5; i++) {
                    Component c = secondMeetingDate.getComponent(i);
                    if (c instanceof JComboBox<?>) {
                        if (((String) ((JComboBox<?>) c).getSelectedItem().toString()).length() == 1) {
                            secondMeetingString += "0" + ((JComboBox<?>) c).getSelectedItem();
                        } else {
                            secondMeetingString += ((JComboBox<?>) c).getSelectedItem();
                        }
                        if (i == 3) {
                            secondMeetingString += ":";
                        } else {
                            secondMeetingString += " ";
                        }
                    }
                }

            }
            try {
                Date firstMeeting = dateFormat.parse(firstMeetingString);
                Date secondMeeting = secondMeetingString.equals("") ? null : dateFormat.parse(secondMeetingString);
                String firstTraderOffer = "";
                String otherTraderOffer = "";

                if (traderItems.getSelectedItem() != null) {
                    firstTraderOffer = userQuery.getAvailableItems(trader).get(traderItems.getSelectedIndex() -1);
                }

                if (otherTraderItems.getSelectedItem() != null) {
                    otherTraderOffer = userQuery
                            .getAvailableItems(userQuery.getUserByUsername((String) traders.getSelectedItem()))
                            .get(otherTraderItems.getSelectedIndex() - 1);
                }
                String message = messageInput.getText();

                tradeManager.requestTrade(trader, userQuery.getUserByUsername((String) traders.getSelectedItem()),
                        firstMeeting, secondMeeting, meetingLocationInput.getText(), firstTraderOffer, otherTraderOffer,
                        3, message);
                this.dispose();
            } catch (ParseException | UserNotFoundException | AuthorizationException | CannotTradeException e2) {
                error.setText(e2.getMessage());
                error.setVisible(true);
            }
        }
    }

    private JPanel dateInput() {
        JPanel meetingInput = new JPanel();
        meetingInput.setBackground(bg);
        meetingInput.setPreferredSize(new Dimension(450, 50));

        String[] monthList = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        JComboBox<String> months = new JComboBox<>(monthList);
        months.setPreferredSize(new Dimension(100, 50));
        months.setFont(regular.deriveFont(20f));

        JComboBox<Integer> days = new JComboBox<>();
        days.setFont(regular.deriveFont(20f));
        days.setPreferredSize(new Dimension(60, 50));
        for (int i = 1; i < 32; i++)
            days.addItem(i);

        months.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int numDays;
                if (e.getItem().equals("Apr") || e.getItem().equals("Jun") || e.getItem().equals("Sep")
                        || e.getItem().equals("Nov"))
                    numDays = 30;
                else if (e.getItem().equals("Feb"))
                    numDays = 28;
                else
                    numDays = 31;
                days.removeAllItems();
                for (int i = 1; i <= numDays; i++)
                    days.addItem(i);
            }
        });

        JComboBox<Integer> years = new JComboBox<>();
        years.setPreferredSize(new Dimension(100, 50));
        years.setFont(regular.deriveFont(20f));
        for (int i = 2020; i < 2026; i++)
            years.addItem(i);

        JComboBox<Integer> hours = new JComboBox<>();
        hours.setPreferredSize(new Dimension(50, 50));
        hours.setFont(regular.deriveFont(20f));
        for (int i = 0; i < 24; i++)
            hours.addItem(i);

        JComboBox<Integer> minutes = new JComboBox<>();
        minutes.setPreferredSize(new Dimension(50, 50));
        minutes.setFont(regular.deriveFont(20f));
        for (int i = 0; i < 60; i++)
            minutes.addItem(i);

        meetingInput.add(months);
        meetingInput.add(days);
        meetingInput.add(years);
        meetingInput.add(hours);
        meetingInput.add(minutes);
        return meetingInput;
    }
}