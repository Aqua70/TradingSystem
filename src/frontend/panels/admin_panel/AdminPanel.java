package frontend.panels.admin_panel;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.UserQuery;
import frontend.WindowManager;
import frontend.panels.admin_panel.admin_subpanels.ControlPanel;
import frontend.panels.admin_panel.admin_subpanels.OverviewPanel;
import frontend.panels.general_panels.MessagePanel;
import frontend.panels.general_panels.search_panels.SearchPanel;

/**
 * This is used to represent what an admin account sees
 */
public class AdminPanel extends JPanel implements ActionListener {

    private final JPanel menuContainer = new JPanel(new GridBagLayout()), menuPanelContainer = new JPanel();
    private final JButton logoutButton = new JButton("Logout");
    private final CardLayout cardLayout = new CardLayout();

    private String currentPanel = "";


    /**
     * For making a new admin panel
     *
     * @param admin      the admin user id
     * @param regular    regular font
     * @param bold       bold font
     * @param italic     italicized font
     * @param boldItalic bold italics font
     * @throws IOException           if access to database has issues
     * @throws UserNotFoundException if the admin id is bad
     */
    public AdminPanel(String admin, Font regular, Font bold, Font italic, Font boldItalic) throws IOException, UserNotFoundException {
        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        Color bg = new Color(51, 51, 51);
        Color current = new Color(32, 32, 32);
        Color gray = new Color(184, 184, 184);
        Color red = new Color(219, 58, 52);
        UserQuery userQuery = new UserQuery();
        JPanel overviewPanel = new OverviewPanel(admin, regular, bold, italic, boldItalic);
        JPanel searchPanel = new SearchPanel(admin, regular, bold, italic, boldItalic);
        JPanel controlPanel = new ControlPanel(admin, regular, bold, italic, boldItalic);
        MessagePanel messagePanel = new MessagePanel(admin, regular, bold, italic, boldItalic);

        messagePanel.changeToAdminColorScheme();

        searchPanel.setBackground(Color.BLACK);
        menuContainer.setPreferredSize(new Dimension(250, this.getHeight()));
        menuContainer.setOpaque(false);
        GridBagConstraints gbc = setupGbc();
        setupIconText(admin, boldItalic, userQuery, gbc);
        setupUsernameTitle(admin, regular, userQuery, gbc);
        setupUserIdTitle(admin, regular, gray, gbc);
        setupOverviewPanelButton(regular, current, gbc);
        setupPanelButton(regular, current, gbc, "Control Panel", 4);
        setupPanelButton(regular, current, gbc, "Messages", 5);
        setupPanelButton(regular, current, gbc, "Search", 6);

        setupLogoutButton(boldItalic, red, gbc);
        setupMenuPanelContainer(bg, overviewPanel, searchPanel, controlPanel, messagePanel);
        this.add(menuContainer, BorderLayout.WEST);
        this.add(menuPanelContainer, BorderLayout.CENTER);

    }

    /**
     * Gets the current Panel that the admin user is in
     * @return the current Panel
     */
    public String getCurrentPanel() {
        return this.currentPanel;
    }

    /**
     * Sets the current Panel, and changes the buttons to match
     * @param panelName name of the panel (eg. "Control Panel")
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
                            return Color.WHITE;
                        }
                    });
                }
            }
        }

    }

    private void setupMenuPanelContainer(Color bg, JPanel overviewPanel, JPanel searchPanel, JPanel controlPanel,
            JPanel messagePanel) {
        menuPanelContainer.setLayout(cardLayout);
        menuPanelContainer.setBackground(bg);
        menuPanelContainer.add(overviewPanel, "Overview");
        menuPanelContainer.add(controlPanel, "Control Panel");
        menuPanelContainer.add(messagePanel, "Messages");
        menuPanelContainer.add(searchPanel, "Search");
    }

    private void setupLogoutButton(Font boldItalic, Color red, GridBagConstraints gbc) {
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setName("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e ->
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).logout()
        );
        gbc.weighty = 0.1;
        gbc.gridy = 7;
        menuContainer.add(logoutButton, gbc);
    }

    private void setupPanelButton(Font regular, Color current, GridBagConstraints gbc, String s, int i) {
        JButton panelButton = new JButton(s);
        panelButton.setHorizontalAlignment(SwingConstants.LEFT);
        panelButton.setName(s);
        panelButton.setFont(regular.deriveFont(30f));
        panelButton.setForeground(Color.WHITE);
        panelButton.setBackground(current);
        panelButton.setOpaque(false);
        panelButton.setBorderPainted(false);
        panelButton.addActionListener(this);
        gbc.gridy = i;
        menuContainer.add(panelButton, gbc);
    }

    private void setupOverviewPanelButton(Font regular, Color current, GridBagConstraints gbc) {
        JButton overviewPanelButton = new JButton("Overview");
        overviewPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        overviewPanelButton.setName("Overview");
        overviewPanelButton.setFont(regular.deriveFont(30f));
        overviewPanelButton.setForeground(Color.WHITE);
        overviewPanelButton.setBackground(current);
        overviewPanelButton.setOpaque(true);
        overviewPanelButton.setBorderPainted(false);
        overviewPanelButton.addActionListener(this);
        gbc.weighty = 0.14;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        menuContainer.add(overviewPanelButton, gbc);
    }

    private void setupUserIdTitle(String admin, Font regular, Color gray, GridBagConstraints gbc) {
        JLabel userIdTitle = new JLabel("<html><pre>ID: #" + admin.substring(admin.length() - 12) + "</pre></html>");
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.weighty = 0.01;
        gbc.insets = new Insets(0, 0, 10, 0);
        menuContainer.add(userIdTitle, gbc);
    }

    private void setupUsernameTitle(String admin, Font regular, UserQuery userQuery, GridBagConstraints gbc) throws UserNotFoundException {
        JLabel usernameTitle = new JLabel((userQuery.getUsername(admin).length() > 12 ? userQuery.getUsername(admin).substring(0, 12) + "..."
                : userQuery.getUsername(admin)));
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        usernameTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.weighty = 0.01;
        gbc.gridy = 1;
        menuContainer.add(usernameTitle, gbc);
    }

    private void setupIconText(String admin, Font boldItalic, UserQuery userQuery, GridBagConstraints gbc) throws UserNotFoundException {
        JLabel iconText = new JLabel(userQuery.getUsername(admin).toUpperCase().substring(0, 1));
        iconText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        iconText.setFont(boldItalic.deriveFont(55f));
        iconText.setForeground(Color.WHITE);
        iconText.setHorizontalAlignment(SwingConstants.CENTER);
        menuContainer.add(iconText, gbc);
    }

    private GridBagConstraints setupGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        gbc.weighty = 0.16;
        return gbc;
    }

    /**
     * Used for handling events
     *
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        setCurrentPanel(e.getActionCommand());
        menuContainer.repaint();
    }
}