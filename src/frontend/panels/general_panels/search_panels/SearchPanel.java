package frontend.panels.general_panels.search_panels;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.List;
import java.io.IOException;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TraderManager;
import backend.tradesystem.trader_managers.TradingInfoManager;
import frontend.WindowManager;

/**
 * Represents the search panel
 */
public class SearchPanel extends JPanel {

    private JLabel userSearchTitle, tradableItemSearchTitle;
    private JPanel userListContainer, userSearchBarContainer, tradableItemSearchBarContainer, tradableItemListContainer;
    private JTextField userSearchTextField, tradableItemSearchTextField;
    private JButton userSearchButton, tradableItemSearchButton;
    private JScrollPane userListScrollPane, tradableItemListScrollPane;

    private final TraderManager traderManager;
    private final TradingInfoManager infoManager;
    private final String user;

    private final UserQuery userQuery;
    private final ItemQuery itemQuery;

    private final Font regular, bold, italic, boldItalic;

    private final Color bg = new Color(51, 51, 51);
    private final Color current = new Color(159, 159, 159);
    private final Color gray = new Color(75, 75, 75);
    private final Color gray2 = new Color(196, 196, 196);
    private final Color detailsButton = new Color(142, 142, 142);
    private final Color red = new Color(219, 58, 52);

    /**
     * Creates a new search panel
     *
     * @param user       the user id
     * @param regular    the regular font
     * @param bold       the bold font
     * @param italic     italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with getting the database files
     */
    public SearchPanel(String user, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.user = user;
        traderManager = new TraderManager();
        infoManager = new TradingInfoManager();

        userQuery = new UserQuery();
        itemQuery = new ItemQuery();

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.setBackground(bg);

        // Starting user search:
        createrUserSearchTitle(regular);

        createUserSearchBarContainer();

        createUserSearchTextField(regular);

        createUserSearchButton(boldItalic);

        userSearchBarContainer.add(userSearchTextField);
        userSearchBarContainer.add(userSearchButton);

        createUserListContainer();

        createUserListScrollPane();

        // Starting tradable items search:
        createTradableItemSearchTitle(regular);

        createTradableItemSearchBarContainer();

        createTradableItemSearchTextField(regular);

        createTradableItemSearchButton(boldItalic);

        tradableItemSearchBarContainer.add(tradableItemSearchTextField);
        tradableItemSearchBarContainer.add(tradableItemSearchButton);

        createTradableItemListContainer();

        createTradableItemListScrollPane();

        // Add all elements to this
        this.add(userSearchTitle);
        this.add(userSearchBarContainer);
        this.add(userListScrollPane);
        this.add(tradableItemSearchTitle);
        this.add(tradableItemSearchBarContainer);
        this.add(tradableItemListScrollPane);
    }

    private boolean checkFrozen() throws UserNotFoundException {
        if (userQuery.getType(user).equals(UserTypes.ADMIN))
            return false;
        return userQuery.isFrozen(user);
    }

    private void createTradableItemListScrollPane() {
        tradableItemListScrollPane = new JScrollPane();
        tradableItemListScrollPane.setPreferredSize(new Dimension(1200, 230));
        tradableItemListScrollPane.setViewportView(userListContainer);
    }

    private void createTradableItemListContainer() {
        tradableItemListContainer = new JPanel();
        tradableItemListContainer.setBackground(gray2);
        tradableItemListContainer.setBorder(null);
    }

    private void createTradableItemSearchTextField(Font regular) {
        tradableItemSearchTextField = new JTextField();
        tradableItemSearchTextField.setBackground(gray);
        tradableItemSearchTextField.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        tradableItemSearchTextField.setFont(regular.deriveFont(25f));
        tradableItemSearchTextField.setCaretColor(Color.WHITE);
        tradableItemSearchTextField.setForeground(Color.WHITE);
    }

    private void createTradableItemSearchBarContainer() {
        tradableItemSearchBarContainer = new JPanel();
        tradableItemSearchBarContainer.setLayout(new BoxLayout(this.tradableItemSearchBarContainer, BoxLayout.X_AXIS));
        tradableItemSearchBarContainer.setPreferredSize(new Dimension(1200, 75));
        tradableItemSearchBarContainer.setBackground(bg);
    }

    private void createTradableItemSearchTitle(Font regular) {
        tradableItemSearchTitle = new JLabel("Tradable Item Search");
        tradableItemSearchTitle.setPreferredSize(new Dimension(1200, 75));
        tradableItemSearchTitle.setBackground(bg);
        tradableItemSearchTitle.setForeground(Color.WHITE);
        tradableItemSearchTitle.setFont(regular.deriveFont(30f));
    }

    private void createUserListScrollPane() {
        userListScrollPane = new JScrollPane();
        userListScrollPane.setPreferredSize(new Dimension(1200, 230));
        userListScrollPane.setViewportView(userListContainer);
    }

    private void createUserListContainer() {
        userListContainer = new JPanel();
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);
    }

    private void createTradableItemSearchButton(Font boldItalic) {
        tradableItemSearchButton = new JButton("Search");
        tradableItemSearchButton.setBackground(current);
        tradableItemSearchButton.setForeground(Color.WHITE);
        tradableItemSearchButton.setFont(boldItalic.deriveFont(30f));
        tradableItemSearchButton.setOpaque(true);
        tradableItemSearchButton.setBorder(BorderFactory.createLineBorder(current, 20));
        tradableItemSearchButton.setPreferredSize(new Dimension(200, 75));
        tradableItemSearchButton.addActionListener(e -> {
            if (tradableItemSearchTextField.getText().trim().length() > 0) {
                findItems(tradableItemSearchTextField.getText().trim());
            }
        });
    }

    private void createUserSearchButton(Font boldItalic) {
        userSearchButton = new JButton("Search");
        userSearchButton.setBackground(current);
        userSearchButton.setForeground(Color.WHITE);
        userSearchButton.setFont(boldItalic.deriveFont(30f));
        userSearchButton.setOpaque(true);
        userSearchButton.setBorder(BorderFactory.createLineBorder(current, 20));
        userSearchButton.setPreferredSize(new Dimension(200, 75));
        userSearchButton.addActionListener(e -> {
            if (userSearchTextField.getText().trim().length() > 0) {
                findUsers(userSearchTextField.getText().trim());
            }
        });
    }

    private void createUserSearchTextField(Font regular) {
        userSearchTextField = new JTextField();
        userSearchTextField.setBackground(gray);
        userSearchTextField.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        userSearchTextField.setFont(regular.deriveFont(25f));
        userSearchTextField.setCaretColor(Color.WHITE);
        userSearchTextField.setForeground(Color.WHITE);
    }

    private void createUserSearchBarContainer() {
        userSearchBarContainer = new JPanel();
        userSearchBarContainer.setLayout(new BoxLayout(this.userSearchBarContainer, BoxLayout.X_AXIS));
        userSearchBarContainer.setPreferredSize(new Dimension(1200, 75));
        userSearchBarContainer.setBackground(bg);
    }

    private void createrUserSearchTitle(Font regular) {
        userSearchTitle = new JLabel("Trader Search");
        userSearchTitle.setPreferredSize(new Dimension(1200, 75));
        userSearchTitle.setBackground(bg);
        userSearchTitle.setForeground(Color.WHITE);
        userSearchTitle.setFont(regular.deriveFont(30f));
    }

    private void findUsers(String username) {
        List<String> matches = infoManager.searchTrader(username);
        int numRows = matches.size();
        if (numRows < 3)
            numRows = 3;
        userListContainer = new JPanel(new GridLayout(numRows, 1));
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);
        for (String t : matches) {
            createTraderDetailRow(t);
        }
        userListScrollPane.setViewportView(userListContainer);
    }

    private void createTraderDetailRow(String t) {
        // for(String userId : userQuery) {
        JPanel trader = new JPanel(new GridLayout(1, 3));
        trader.setPreferredSize(new Dimension(1000, 75));
        trader.setBackground(gray2);
        trader.setBorder(BorderFactory.createLineBorder(bg));

        JLabel traderName = new JLabel();
        try {
            traderName = new JLabel(userQuery.getUsername(t));
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        traderName.setFont(regular.deriveFont(20f));
        traderName.setForeground(Color.BLACK);
        traderName.setHorizontalAlignment(JLabel.LEFT);
        traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        JLabel traderId = new JLabel("<html><pre>#" + t.substring(t.length() - 12) + "</pre></html>");
        traderId.setFont(regular.deriveFont(20f));
        traderId.setForeground(Color.BLACK);
        traderId.setHorizontalAlignment(JLabel.CENTER);

        JButton traderDetailsButton = new JButton("Details");
        traderDetailsButton.setFont(bold.deriveFont(20f));
        traderDetailsButton.setForeground(Color.WHITE);
        traderDetailsButton.setBackground(detailsButton);
        traderDetailsButton.setOpaque(true);
        traderDetailsButton.setBorder(BorderFactory.createLineBorder(gray2, 15));
        try {
            if (!user.equals("") && userQuery.getType(user).equals(UserTypes.TRADER)) {
                traderDetailsButton.addActionListener(new SearchPanelTraderDetails(t, regular, italic));
            } else if (!user.equals("")) {
                traderDetailsButton.setText("Infiltrade");
                traderDetailsButton.setForeground(Color.WHITE);
                traderDetailsButton.setBackground(red);
                traderDetailsButton.setFont(boldItalic.deriveFont(20f));
                traderDetailsButton.addActionListener(e -> {
                    try {
                        SwingUtilities.getWindowAncestor(this).setVisible(false);
                        WindowManager traderFrame = new WindowManager();
                        traderFrame.run();
                        traderFrame.setInfiltraded();
                        traderFrame.login(t);
                        traderFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                        Window window = SwingUtilities.getWindowAncestor(this);
                        traderFrame.addWindowListener(new WindowAdapter() {
                            public void windowClosing(java.awt.event.WindowEvent e) {
                                window.setVisible(true);
                                e.getWindow().dispose();
                            }
                        });
                    } catch (IOException | FontFormatException | TradeNotFoundException ioException) {
                        ioException.printStackTrace();
                    }
                });
            }
        } catch (IOException | UserNotFoundException e) {
            e.printStackTrace();
        }

        trader.add(traderName);
        trader.add(traderId);
        trader.add(traderDetailsButton);
        userListContainer.add(trader);
    }

    private void findItems(String itemNameSearchString) {
        List<String> matches = infoManager.getTradableItemsWithName(itemNameSearchString);
        int numRows = matches.size();
        // int numRows = itemNameSearchString.length();
        if (numRows < 3)
            numRows = 3;
        tradableItemListContainer = new JPanel(new GridLayout(numRows, 1));
        // tradableItemListContainer = new JPanel(new GridLayout(matches.size(), 1));
        tradableItemListContainer.setBackground(gray2);
        tradableItemListContainer.setBorder(null);
        for (String t : matches) {
            try {
                createTradableItemRow(t);
            } catch (TradableItemNotFoundException | UserNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        tradableItemListScrollPane.setViewportView(tradableItemListContainer);
    }

    private void createTradableItemRow(String t) throws TradableItemNotFoundException, UserNotFoundException {
        String owner = userQuery.getTraderThatHasTradableItemId(t);

        JPanel item = new JPanel(new GridLayout(1, 4));
        item.setPreferredSize(new Dimension(1000, 75));
        item.setBackground(gray2);
        item.setBorder(BorderFactory.createLineBorder(bg));

        JLabel itemName = new JLabel(itemQuery.getName(t));
        itemName.setFont(regular.deriveFont(20f));
        itemName.setForeground(Color.BLACK);
        itemName.setHorizontalAlignment(JLabel.LEFT);
        itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        JLabel itemDesc = new JLabel(itemQuery.getDesc(t));
        itemDesc.setFont(regular.deriveFont(20f));
        itemDesc.setForeground(Color.BLACK);
        itemDesc.setHorizontalAlignment(JLabel.CENTER);

        JLabel itemOwnerName = new JLabel(userQuery.getUsername(owner));
        itemOwnerName.setFont(regular.deriveFont(20f));
        itemOwnerName.setForeground(Color.BLACK);
        itemOwnerName.setHorizontalAlignment(JLabel.CENTER);

        JButton addToWishlistButton = new JButton("Add To Wishlist");
        addToWishlistButton.setFont(bold.deriveFont(20f));
        addToWishlistButton.setForeground(Color.WHITE);
        addToWishlistButton.setBackground(detailsButton);
        addToWishlistButton.setOpaque(true);
        addToWishlistButton.setBorder(BorderFactory.createLineBorder(gray2, 15));
        addToWishlistButton.addActionListener(e -> {
            try {
                traderManager.addToWishList(user, t);
            } catch (UserNotFoundException | TradableItemNotFoundException | AuthorizationException e1) {
                e1.printStackTrace();
            }

        });


        item.add(itemName);
        item.add(itemDesc);
        item.add(itemOwnerName);
        if (!user.equals("") && userQuery.getType(user).equals(UserTypes.TRADER) && !checkFrozen())
            item.add(addToWishlistButton);
        tradableItemListContainer.add(item);
    }


}