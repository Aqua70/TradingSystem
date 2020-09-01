package frontend;

import javax.swing.UIManager;

/**
 * This is where the program starts running
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Uncommenting, then running the program will reset all the database files to a fresh state
            // The line below should be left commented out while running the program
            // Running the line below only once is useful in resetting any database files
            // new TemporarySetup();

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            WindowManager windowManager = new WindowManager();
            windowManager.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
