package frontend;

import backend.DatabaseFilePaths;
import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import frontend.panels.admin_panel.AdminPanel;
import frontend.panels.trader_panel.TraderPanel;
import frontend.panels.login_panel.LoginPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is used to manage settings in the overall window itself Code inspired
 * from
 * https://stackoverflow.com/questions/54815226/how-can-i-detect-if-a-file-has-been-modified-using-lastmodified
 */
public class WindowManager extends JFrame {
    protected Font regular, bold, italic, boldItalic;
    private final LoginPanel loginPanel;
    private JPanel userPanel = null;
    private final BufferedImage loginBg = ImageIO.read(new File("./src/frontend/images/LoginPanelBg.jpg")),
            adminBg = ImageIO.read(new File("./src/frontend/images/IconAdmin.jpg")),
            traderBg = ImageIO.read(new File("./src/frontend/images/IconTrader.jpg"));
    private final LoginManager loginManager = new LoginManager();
    private boolean infiltraded;

    private String userId = "bad";

    private String currentPanel = "";

    /**
     * This is where initial settings that affects the entire window is at
     *
     * @throws IOException         if logging causes issues
     * @throws FontFormatException if the font is bad
     */
    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT, new File("./src/frontend/fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, new File("./src/frontend/fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, new File("./src/frontend/fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT, new File("./src/frontend/fonts/IBMPlexSans-BoldItalic.ttf"));
        loginPanel = new LoginPanel(regular, bold, italic, boldItalic);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new ImagePanel(loginBg));
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
        this.setLocationRelativeTo(null);
        this.setResizable(false);

    }

    /**
     * Gets the current userId (trader or admin)
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Changes from login screen to the actual dashboard
     *
     * @param loggedInUserId the user id that is logged in
     * @throws IOException            if login causes issues
     * @throws TradeNotFoundException trade doesn't exist
     */
    public void login(String loggedInUserId) throws IOException, TradeNotFoundException {
        try {
            this.userId = loggedInUserId;
            if (userId.equals("bad"))
                return;
            if (loggedInUserId.equals("") || loginManager.getType(loggedInUserId).equals(UserTypes.TRADER)) {
                userPanel = new TraderPanel(loggedInUserId, regular, bold, italic, boldItalic, infiltraded);
                if (!this.currentPanel.equals(""))
                    ((TraderPanel) userPanel).setCurrentPanel(this.currentPanel);
                this.setContentPane(new ImagePanel(traderBg));
                this.setContentPane(new ImagePanel(traderBg));
            } else {
                userPanel = new AdminPanel(loggedInUserId, regular, bold, italic, boldItalic);
                if (!this.currentPanel.equals(""))
                    ((AdminPanel) userPanel).setCurrentPanel(this.currentPanel);
                this.setContentPane(new ImagePanel(adminBg));
            }
            this.add(userPanel, BorderLayout.CENTER);
            this.setSize(userPanel.getSize());
        } catch (UserNotFoundException | AuthorizationException | TradableItemNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Puts the window back on the login screen
     */
    public void logout() {
        privateLogout();
        this.userId = "bad";
    }

    private void privateLogout() {
        this.currentPanel = userPanel instanceof TraderPanel ? ((TraderPanel) userPanel).getCurrentPanel()
                : ((AdminPanel) userPanel).getCurrentPanel();
        if (userPanel != null)
            this.remove(userPanel);
        this.setContentPane(new ImagePanel(loginBg));
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
    }

    /**
     * Sets the window to visible and refreshes the JFrame if needed
     */
    public void run() {
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<File> file = new ArrayList<>();
        int i = 0;
        for (DatabaseFilePaths path : DatabaseFilePaths.values()) {
            if (path.isConfig())
                continue;
            file.add(new File(path.getFilePath()));
            times.add(file.get(i).lastModified());
            i++;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (userId.equals("bad"))
                    return;
                for (int i = 0; i < times.size(); i++) {
                    if (times.get(i) != file.get(i).lastModified()) {
                        times.set(i, file.get(i).lastModified());
                        try {
                            if (!userId.equals("")) {
                                privateLogout();
                                login(userId);
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Date(), 500);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ignored) {
                timer.cancel();
            }
        });
        this.setVisible(true);
    }

    private static class ImagePanel extends JComponent {
        private final Image image;

        public ImagePanel(Image image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }
    }

    /**
     * Sets infiltraded to true (this WindowManager was created by an admin to
     * infiltrade)
     */
    public void setInfiltraded() {
        infiltraded = true;
    }
}
