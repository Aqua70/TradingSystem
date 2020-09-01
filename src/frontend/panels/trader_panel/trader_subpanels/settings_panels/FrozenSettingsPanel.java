package frontend.panels.trader_panel.trader_subpanels.settings_panels;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.admin_managers.HandleFrozenManager;

/**
 * This panel represents the settings a frozen trader can interact with
 */
public class FrozenSettingsPanel extends SettingsPanel {

    private final HandleFrozenManager frozenManager = new HandleFrozenManager();

    private final String traderId;

    /**
     * Makes new a frozen settings panel
     *
     * @param traderId   the trader that is frozen
     * @param regular    regular font
     * @param bold       bold font
     * @param italic     italics font
     * @param boldItalic bold italics font
     * @throws IOException            if issues with getting database file
     * @throws UserNotFoundException  if the trader id is bad
     * @throws AuthorizationException if the user isn't a trader
     */
    public FrozenSettingsPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException, UserNotFoundException, AuthorizationException {
        super(traderId, regular, bold, italic, boldItalic);
        this.traderId = traderId;
        super.remove(0);
        super.remove(3);

        JLabel settingsTitleLabel = new JLabel("Frozen Trader Settings");
        settingsTitleLabel.setFont(regular.deriveFont(35f));
        settingsTitleLabel.setPreferredSize(new Dimension(1200, 75));
        settingsTitleLabel.setForeground(Color.WHITE);
        settingsTitleLabel.setOpaque(false);

        JTextArea prefaceText = getPrefaceText();
        JPanel requestUnfreezePanel = getRequestUnFreezePanel();

        this.add(settingsTitleLabel, 0);
        this.add(prefaceText, 1);
        this.add(requestUnfreezePanel, 2);
    }

    private boolean checkUnfreezeRequested() {
        try {
            return super.userQuery.isUnfrozenRequested(traderId);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JPanel getRequestUnFreezePanel() {

        boolean isUnfreezeRequested = checkUnfreezeRequested();

        JPanel unFreezePanel = new JPanel(new GridLayout(1, 3));
        unFreezePanel.setPreferredSize(new Dimension(1200, 150));
        unFreezePanel.setBackground(gray2);
        unFreezePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 50, 0, bg));

        JLabel unFreezeLabel = new JLabel("Request Un-Freeze");
        unFreezeLabel.setFont(bold.deriveFont(25f));
        unFreezeLabel.setForeground(Color.BLACK);
        unFreezeLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        unFreezeLabel.setOpaque(false);

        JLabel pad = new JLabel("");
        pad.setFont(italic.deriveFont(25f));
        pad.setForeground(Color.BLACK);
        pad.setOpaque(false);

        JButton unFreezeButton = new JButton(isUnfreezeRequested ? "Requested" : "Request");
        unFreezeButton.setFont((isUnfreezeRequested ? boldItalic : bold).deriveFont(20f));
        unFreezeButton.setBackground(isUnfreezeRequested ? bg : red);
        unFreezeButton.setForeground(isUnfreezeRequested ? gray : Color.WHITE);
        unFreezeButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 25, gray2));
        unFreezeButton.setEnabled(!isUnfreezeRequested);
        unFreezeButton.addActionListener(e -> {
            try {
                frozenManager.requestUnfreeze(traderId, true);
                unFreezeButton.setText("Requested");
                unFreezeButton.setFont(boldItalic.deriveFont(20f));
                unFreezeButton.setBackground(bg);
                unFreezeButton.setForeground(gray);
                unFreezeButton.setEnabled(false);

            } catch (UserNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        unFreezePanel.add(unFreezeLabel);
        unFreezePanel.add(pad);
        unFreezePanel.add(unFreezeButton);
        return unFreezePanel;
    }

    private JTextArea getPrefaceText() {
        JTextArea preface = new JTextArea("You are currently unable to trade or manage items. This may be caused due to trading beyond the ongoing trades limit.\nIn order to be un-frozen, you must request an un-freeze and an admin will process your request as soon as possible.");
        preface.setFont(super.italic.deriveFont(20f));
        preface.setPreferredSize(new Dimension(1200, 75));
        preface.setLineWrap(true);
        preface.setWrapStyleWord(true);
        preface.setEditable(false);
        preface.setForeground(gray);
        preface.setOpaque(false);

        return preface;
    }
}