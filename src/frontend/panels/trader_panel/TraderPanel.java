package frontend.panels.trader_panel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalButtonUI;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.UserQuery;
import frontend.WindowManager;
import frontend.panels.general_panels.search_panels.SearchPanel;
import frontend.panels.trader_panel.trader_subpanels.*;
import frontend.panels.trader_panel.trader_subpanels.settings_panels.FrozenSettingsPanel;
import frontend.panels.trader_panel.trader_subpanels.settings_panels.SettingsPanel;
import frontend.panels.trader_panel.trader_subpanels.trade_panels.TradePanel;

/**
 * This is used to show what a trader account sees
 */
public class TraderPanel extends JPanel implements ActionListener {
    private JButton logoutButton;
    private JPanel menuContainer;
    private final JPanel menuPanelContainer;

    private GridBagConstraints gbc;
    private final CardLayout cardLayout;

    private final UserQuery userQuery = new UserQuery();

    private final Color CURRENT = new Color(159, 159, 159);
    private final Color GRAY = new Color(75, 75, 75);
    private final Color RED = new Color(219, 58, 52);

    private String currentPanel;

    /**
     * Making a new trader panel
     *
     * @param traderId    the trader id
     * @param regular     regular font
     * @param bold        bold font
     * @param italic      italics font
     * @param boldItalic  bold italics font
     * @param infiltraded if this is true, it means an admin is accessing this panel
     * @throws IOException                   if accessing database has issues
     * @throws UserNotFoundException         if the user id is bad
     * @throws AuthorizationException        if this user isn't allowed to access
     *                                       this panel
     * @throws TradeNotFoundException        if trade doesn't exist
     * @throws TradableItemNotFoundException if item doesn't exist
     */
    public TraderPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic, boolean infiltraded)
            throws IOException, UserNotFoundException, AuthorizationException, TradeNotFoundException,
            TradableItemNotFoundException {
        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        JPanel searchPanel = new SearchPanel(traderId, regular, bold, italic, boldItalic);
        JPanel tradePanel = new TradePanel(traderId, regular, bold, italic, boldItalic);
        JPanel itemsPanel = new ItemsPanel(traderId, regular, bold, italic, boldItalic);
        JPanel notificationsPanel = new NotificationsPanel(traderId, regular, bold, italic, boldItalic);
        JPanel settingsPanel = new SettingsPanel(traderId, regular, bold, italic, boldItalic);

        menuPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        menuPanelContainer.setLayout(cardLayout);
        Color bg = new Color(214, 214, 214);
        menuPanelContainer.setBackground(bg);

        createMenuContainer();
        createIcon(traderId, boldItalic);
        createUsernameTitle(traderId, regular);
        createUserIdTitle(traderId, regular);

        boolean isDemo = traderId.equals("");
        boolean isFrozen = !isDemo && checkFrozenTrader(traderId);
        boolean isIdle = !isDemo && checkIdleTrader(traderId);
        String[] menuTitles = getMenuTitles(isFrozen, isIdle);

        for (int i = 0; i < menuTitles.length; i++)
            createPanelButton(menuTitles[i], i + 3, regular);

        createLogoutButton(boldItalic, infiltraded);

        if (isFrozen) {
            menuPanelContainer.add(new FrozenSettingsPanel(traderId, regular, bold, italic, boldItalic),
                    "Frozen Settings");
            menuPanelContainer.add(searchPanel, "Search");
        } else if (isIdle) {
            menuPanelContainer.add(itemsPanel, "Items");
            menuPanelContainer.add(notificationsPanel, "Notifications");
            menuPanelContainer.add(searchPanel, "Search");
            menuPanelContainer.add(settingsPanel, "Settings");
        } else {
            menuPanelContainer.add(tradePanel, "Trades");
            menuPanelContainer.add(itemsPanel, "Items");
            menuPanelContainer.add(notificationsPanel, "Notifications");
            menuPanelContainer.add(searchPanel, "Search");
            menuPanelContainer.add(settingsPanel, "Settings");
        }

        this.add(menuContainer, BorderLayout.WEST);
        this.add(menuPanelContainer, BorderLayout.CENTER);

        this.currentPanel = menuTitles[0];

    }

    /**
     * Gets the current Panel that the trader is in
     * 
     * @return the current panel
     */
    public String getCurrentPanel() {
        return this.currentPanel;
    }

    /**
     * Sets the currentPanel given the name of the panel
     * 
     * @param panelName name of the panel
     */
    public void setCurrentPanel(String panelName) {
        this.currentPanel = panelName;
        cardLayout.show(menuPanelContainer, panelName);
        for (Component button : menuContainer.getComponents()) {
            if (button instanceof JButton) {
                if (!button.equals(logoutButton)) {
                    button.setEnabled(true);
                    ((JButton) button).setOpaque(false);
                }
                if (button.getName().equals(panelName)) {
                    button.setEnabled(false);
                    ((JButton) button).setOpaque(true);
                    ((JButton) button).setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.BLACK;
                        }
                    });
                }
            }
        }
    }

    private String[] getMenuTitles(boolean isFrozen, boolean isIdle) {
        if (isFrozen)
            return new String[] { "Frozen Settings", "Search", "", "", "" };
        if (isIdle)
            return new String[] { "Items", "Notifications", "Search", "Settings", "" };
        return new String[] { "Trades", "Items", "Notifications", "Search", "Settings" };
    }

    private boolean checkIdleTrader(String traderId) {
        try {
            return userQuery.isIdle(traderId);
        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkFrozenTrader(String traderId) {
        try {
            return userQuery.isFrozen(traderId);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createMenuContainer() {
        menuContainer = new JPanel(new GridBagLayout());
        menuContainer.setPreferredSize(new Dimension(250, this.getHeight()));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        menuContainer.setOpaque(false);
    }

    private void createLogoutButton(Font boldItalic, boolean infiltraded) {
        logoutButton = new JButton();
        if (!infiltraded) {
            logoutButton.setText("Logout");
            logoutButton.setFont(boldItalic.deriveFont(25f));
            logoutButton.setForeground(Color.WHITE);
            logoutButton.setBackground(RED);
            logoutButton.setOpaque(true);
            logoutButton.addActionListener(e -> ((WindowManager) SwingUtilities.getWindowAncestor(this)).logout());
        } else {
            logoutButton.setOpaque(false);
            logoutButton.setBackground(CURRENT);
        }
        logoutButton.setName("Logout");
        logoutButton.setBorderPainted(false);
        gbc.gridy = 8;
        menuContainer.add(logoutButton, gbc);
    }

    private void createPanelButton(String title, int gridy, Font regular) {
        JButton panelButton = new JButton(title);
        panelButton.setHorizontalAlignment(SwingConstants.LEFT);
        panelButton.setName(title);
        panelButton.setFont(regular.deriveFont(30f));
        panelButton.setForeground(Color.BLACK);
        panelButton.setBackground(CURRENT);
        panelButton.setOpaque(title.equals("Trades") || title.equals("Frozen Settings"));
        panelButton.setEnabled(!title.equals(""));
        panelButton.setBorderPainted(false);
        panelButton.addActionListener(this);
        gbc.gridy = gridy;
        gbc.weighty = 0.14;
        gbc.insets = new Insets(0, 0, 0, 0);

        menuContainer.add(panelButton, gbc);
    }

    private void createUserIdTitle(String traderId, Font regular) {
        JLabel userIdTitle = new JLabel("<html><pre>ID: #"
                + (traderId.equals("") ? "DemoID" : traderId.substring(traderId.length() - 12)) + "</pre></html>");
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(GRAY);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.weighty = 0.01;
        gbc.insets = new Insets(0, 0, 10, 0);
        menuContainer.add(userIdTitle, gbc);
    }

    private void createUsernameTitle(String traderId, Font regular) throws UserNotFoundException {
        String text = traderId.equals("") ? "Demo"
                : (userQuery.getUsername(traderId).length() > 12
                        ? userQuery.getUsername(traderId).substring(0, 12) + "..."
                        : userQuery.getUsername(traderId));
        JLabel usernameTitle = new JLabel(text);
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.BLACK);
        usernameTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        usernameTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.01;
        menuContainer.add(usernameTitle, gbc);
    }

    private void createIcon(String traderId, Font boldItalic) throws UserNotFoundException {
        String text = traderId.equals("") ? "D" : userQuery.getUsername(traderId).toUpperCase().substring(0, 1);
        JLabel iconText = new JLabel(text);
        iconText.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        iconText.setFont(boldItalic.deriveFont(55f));
        iconText.setForeground(Color.BLACK);
        iconText.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.weighty = 0.16;
        menuContainer.add(iconText, gbc);
    }

    /**
     * Runs when an action was performed
     *
     * @param e The event object
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        setCurrentPanel(e.getActionCommand());
        menuContainer.repaint();
    }
}