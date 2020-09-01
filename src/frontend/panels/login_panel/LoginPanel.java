package frontend.panels.login_panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import backend.exceptions.BadPasswordException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import frontend.WindowManager;

/**
 * This represents the login screen
 */
public class LoginPanel extends JPanel implements ActionListener {
    private final JLabel loginNotification = new JLabel();
    protected JTextField usernameInput;
    protected JPasswordField passwordInput;
    protected JButton loginButton, registerButton, demoButton;
    private final LoginManager loginManager = new LoginManager();

    /**
     * New login panel
     *
     * @param regular    font for regular
     * @param bold       font for bold
     * @param italic     font for italics
     * @param boldItalic font for bold italics
     * @throws IOException if logging in causes issues
     */
    public LoginPanel(Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        // Colours
        final Color red = new Color(219, 58, 52);
        final Color input = new Color(156, 156, 156);

        // Overall panel settings
        this.setSize(480, 720);
        this.setBorder(BorderFactory.createEmptyBorder(60, 50, 20, 50));
        this.setLayout(new GridLayout(4, 1));
        this.setOpaque(false);

        // For managing spacing
        GridBagConstraints gbc = new GridBagConstraints();

        // Content of the login screen
        JLabel title = getTitleLabel(boldItalic, "TradeSystem", 60f, JLabel.CENTER);
        JPanel inputs = getInputsPanel();
        manageUsernameTitleLabel(italic, gbc, inputs);
        manageUsernameInputField(regular, input, gbc, inputs);
        managePasswordTitle(italic, gbc, inputs);
        managePasswordField(regular, input, gbc, inputs);

     //    usernameInput = new JTextField("admin1");
//       usernameInput = new JTextField("trader5");
//       passwordInput = new JPasswordField("userPassword1");

        JPanel buttonContainer = manageButtonPanel(bold, gbc);
        manageRegisterButton(bold, gbc, buttonContainer);
        manageDemoButton(bold, gbc, buttonContainer);
        JPanel info = manageInfoPanel();
        manageLoginNotification(boldItalic, red, info);
        manageCopyrightLabel(regular, info);
        this.add(title);
        this.add(inputs);
        this.add(buttonContainer);
        this.add(info);

    }

    /**
     * Used for displaying some message in the login screen
     *
     * @param msg the message being displayed
     */
    public void notifyLogin(String msg) {
        loginNotification.setText(msg);
        loginNotification.setVisible(true);
    }

    /**
     * For actions of the button
     *
     * @param e the event that occurred
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Login":
                try {
                    if (usernameInput.getText().trim().equals("") || String.valueOf(passwordInput.getPassword()).trim().equals("")) {
                        notifyLogin("<html><b><i>Empty Username and/or Password.</i></b></html>");
                        return;
                    }
                    String loggedInUser = loginManager.login(usernameInput.getText(), String.valueOf(passwordInput.getPassword()));
                    ((WindowManager) SwingUtilities.getWindowAncestor(this)).login(loggedInUser);
                } catch (UserNotFoundException ignored) {
                    notifyLogin("<html><b><i>Username or Password is incorrect.</i></b></html>");
                } catch (IOException | TradeNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Register":
                try {
                    String loggedInUser = loginManager.registerUser(usernameInput.getText(), String.valueOf(passwordInput.getPassword()), UserTypes.TRADER);
                    ((WindowManager) SwingUtilities.getWindowAncestor(this)).login(loggedInUser);
                } catch (BadPasswordException ex) {
                    notifyLogin("<html><b><i>Invalid Password: " + ex.getMessage() + "</i></b></html>");
                } catch (UserAlreadyExistsException ignored) {
                    notifyLogin("<html><b><i>The username '" + usernameInput.getText() + "' is taken.</i></b></html>");
                } catch (IOException | TradeNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Demo":
                try {
                    ((WindowManager) SwingUtilities.getWindowAncestor(this)).login("");
                } catch (TradeNotFoundException | IOException ignored) {
                    notifyLogin("<html><b><i>Error with logging in.</i></b></html>");
                }
                break;
        }
    }

    private void manageCopyrightLabel(Font regular, JPanel info) {
        JLabel copyright = new JLabel("Copyright © 2020 group_56. All rights reserved.");
        copyright.setFont(regular.deriveFont(10f));
        copyright.setForeground(new Color(169, 169, 169));
        copyright.setHorizontalAlignment(JLabel.CENTER);
        info.add(copyright);
    }

    private void manageLoginNotification(Font boldItalic, Color red, JPanel info) {
        loginNotification.setFont(boldItalic.deriveFont(20f));
        loginNotification.setForeground(red);
        loginNotification.setHorizontalAlignment(JLabel.CENTER);
        loginNotification.setVisible(false);
        info.add(loginNotification);
    }

    private JPanel manageInfoPanel() {
        JPanel info = new JPanel();
        info.setLayout(new GridLayout(2, 0));
        info.setOpaque(false);
        return info;
    }

    private void manageDemoButton(Font bold, GridBagConstraints gbc, JPanel buttonContainer) {
        demoButton = new JButton("Demo");
        demoButton.setFont(bold.deriveFont(20f));
        demoButton.setForeground(Color.WHITE);
        demoButton.setOpaque(false);
        demoButton.setContentAreaFilled(false);
        demoButton.setBorderPainted(false);
        demoButton.addActionListener(this);
        gbc.gridy = 2;
        buttonContainer.add(demoButton, gbc);
    }

    private void manageRegisterButton(Font bold, GridBagConstraints gbc, JPanel buttonContainer) {
        registerButton = new JButton("Register");
        registerButton.setFont(bold.deriveFont(20f));
        registerButton.setForeground(Color.RED);
        registerButton.setOpaque(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(this);
        gbc.gridy = 1;
        buttonContainer.add(registerButton, gbc);
    }

    private JPanel manageButtonPanel(Font bold, GridBagConstraints gbc) {
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridBagLayout());
        buttonContainer.setOpaque(false);
        gbc.insets = new Insets(0, 80, 0, 80);
        loginButton = new JButton("Login");
        loginButton.setForeground(new Color(98, 123, 255));
        loginButton.setFont(bold.deriveFont(20f));
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonContainer.add(loginButton, gbc);
        return buttonContainer;
    }

    private void managePasswordField(Font regular, Color input, GridBagConstraints gbc, JPanel inputs) {
        passwordInput = new JPasswordField();
        passwordInput.setFont(regular.deriveFont(20f));
        passwordInput.setBackground(input);
        passwordInput.setForeground(Color.BLACK);
        passwordInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputs.add(passwordInput, gbc);
    }

    private void managePasswordTitle(Font italic, GridBagConstraints gbc, JPanel inputs) {
        JLabel passwordTitle = getTitleLabel(italic, "Password:", 20f, JLabel.LEFT);
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        inputs.add(passwordTitle, gbc);
    }

    private void manageUsernameInputField(Font regular, Color input, GridBagConstraints gbc, JPanel inputs) {
        usernameInput = new JTextField();
        usernameInput.setFont(regular.deriveFont(20f));
        usernameInput.setBackground(input);
        usernameInput.setForeground(Color.BLACK);
        usernameInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputs.add(usernameInput, gbc);
    }

    private void manageUsernameTitleLabel(Font italic, GridBagConstraints gbc, JPanel inputs) {
        JLabel usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(italic.deriveFont(20f));
        usernameTitle.setForeground(Color.WHITE);
        gbc.insets = new Insets(0, 0, 40, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputs.add(usernameTitle, gbc);
    }

    private JPanel getInputsPanel() {
        JPanel inputs = new JPanel();
        inputs.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        inputs.setLayout(new GridBagLayout());
        inputs.setOpaque(false);
        return inputs;
    }

    private JLabel getTitleLabel(Font boldItalic, String tradeSystem, float v, int center) {
        JLabel title = new JLabel(tradeSystem);
        title.setFont(boldItalic.deriveFont(v));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(center);
        return title;
    }


}