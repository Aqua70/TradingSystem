package frontend.panels.trader_panel.trader_subpanels.trade_panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.suggestion_strategies.*;
import backend.tradesystem.trader_managers.TradingInfoManager;
import backend.tradesystem.trader_managers.TradingManager;
import frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals.AddNewTradeModal;
import frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals.TradeDetailsModal;

/**
 * For showing ongoing trades
 */
public class OngoingTradesPanel extends JPanel implements ActionListener {

    private final String trader;

    private final Font regular, bold, italic, boldItalic;

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(196, 196, 196);
    private final Color gray2 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);

    private final TradeQuery tradeQuery = new TradeQuery();
    private final UserQuery userQuery = new UserQuery();

    private final TradingManager tradeManager = new TradingManager();
    private final TradingInfoManager infoManager = new TradingInfoManager();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", new Locale("en", "US"));

    private final SuggestionStrategy normalSuggestionStrategy = new ExactWishlistSuggestion();
    private final SuggestionStrategy similarSuggestionStrategy = new SimilarWishlistSuggestion();

    /**
     * For making a panel to show ongoing trades
     * @param trader the trader id
     * @param regular regular font
     * @param bold bold font
     * @param italic italics font
     * @param boldItalic bold italics font
     * @throws IOException if database files aren't found
     * @throws UserNotFoundException if the trader isn't found
     * @throws AuthorizationException if the trader isn't allowed to access this
     * @throws TradeNotFoundException if a trade could not be found
     */
    public OngoingTradesPanel(String trader, Font regular, Font bold, Font italic, Font boldItalic)
            throws IOException, UserNotFoundException, AuthorizationException, TradeNotFoundException {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        JPanel ongoingTradesTitleHeader = setOngoingTradesTitleHeader();
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        this.add(ongoingTradesTitleHeader, gbc);

        JPanel ongoingTradesHeader = setOngoingTradesHeader();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        this.add(ongoingTradesHeader, gbc);

        JScrollPane ongoingTradesScrollPane = setOngoingTradesScrollPane();
        gbc.gridy = 2;
        gbc.weighty = 0.8;
        this.add(ongoingTradesScrollPane, gbc);
    }

    private JScrollPane setOngoingTradesScrollPane()
            throws UserNotFoundException, AuthorizationException, TradeNotFoundException {
        JScrollPane ongoingTradesScrollPane = new JScrollPane();

        JPanel ongoingTradesContainer = setOngoingTradesContainer();

        ongoingTradesScrollPane.setPreferredSize(new Dimension(1200, 325));
        ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
        ongoingTradesScrollPane.setBackground(gray);
        ongoingTradesScrollPane.setBorder(null);

        return ongoingTradesScrollPane;
    }

    private JPanel setOngoingTradesContainer()
            throws UserNotFoundException, AuthorizationException, TradeNotFoundException {
        JPanel ongoingTradesContainer = new JPanel();

        List<String> acceptedTrades = trader.equals("") ? new ArrayList<>() : userQuery.getAcceptedTrades(trader);

        if (acceptedTrades.isEmpty())
            return createNoTradesFoundPanel();

        int numRows = acceptedTrades.size();
        numRows = Math.max(numRows, 4);

        ongoingTradesContainer.setLayout(new GridLayout(numRows, 1));
        ongoingTradesContainer.setBackground(gray2);
        ongoingTradesContainer.setBorder(null);

        for (String tradeID : acceptedTrades) {
            JPanel ongoingTradePanel = createOngoingTradePanel(tradeID);
            ongoingTradesContainer.add(ongoingTradePanel);
        }

        return ongoingTradesContainer;
    }

    private JPanel createOngoingTradePanel(String tradeID) throws TradeNotFoundException, UserNotFoundException {
        JPanel ongoingTradePanel = new JPanel(new GridLayout(1, 5, 10, 0));
        ongoingTradePanel.setPreferredSize(new Dimension(1000, 75));
        ongoingTradePanel.setBorder(BorderFactory.createLineBorder(bg));
        ongoingTradePanel.setBackground(gray);

        boolean isTraderFirstUser = tradeQuery.getFirstUserId(tradeID).equals(trader);

        JLabel otherTraderName = new JLabel();

        if (isTraderFirstUser)
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

        JButton tradeConfirmButton = setConfirmTradeButton(tradeID, isTraderFirstUser);

        ongoingTradePanel.add(otherTraderName);
        ongoingTradePanel.add(tradeLocation);
        ongoingTradePanel.add(tradeMeetingTime);
        ongoingTradePanel.add(tradeDetailsButton);
        ongoingTradePanel.add(tradeConfirmButton);

        return ongoingTradePanel;
    }

    private JButton setConfirmTradeButton(String tradeID, boolean isTraderFirstUser) throws TradeNotFoundException {
        JButton tradeConfirmButton = new JButton();

        boolean hasSecondMeeting = tradeQuery.getSecondMeetingTime(tradeID) != null;
        boolean hasFirstMeetingCompleted = tradeQuery.isFirstUserConfirmed1(tradeID) && tradeQuery.isSecondUserConfirmed1(tradeID);
        boolean hasSecondMeetingCompleted = tradeQuery.isFirstUserConfirmed2(tradeID) && tradeQuery.isSecondUserConfirmed2(tradeID);
        boolean isTraderAbleToConfirm = true;
        if(hasFirstMeetingCompleted && (hasSecondMeeting && !hasSecondMeetingCompleted)) {
            isTraderAbleToConfirm = isTraderFirstUser ? !tradeQuery.isFirstUserConfirmed2(tradeID) : !tradeQuery.isSecondUserConfirmed2(tradeID); 
        } else if(!hasFirstMeetingCompleted) {
            isTraderAbleToConfirm = isTraderFirstUser ? !tradeQuery.isFirstUserConfirmed1(tradeID) : !tradeQuery.isSecondUserConfirmed1(tradeID);
        }

        tradeConfirmButton.setText(!isTraderAbleToConfirm ? "Confirmed" : "Confirm");
        tradeConfirmButton.setFont((!isTraderAbleToConfirm ? boldItalic : bold).deriveFont(20f));
        tradeConfirmButton.setForeground(Color.WHITE);
        tradeConfirmButton.setBackground(!isTraderAbleToConfirm ? bg : green);
        tradeConfirmButton.setEnabled(isTraderAbleToConfirm);
        tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));

        tradeConfirmButton.addActionListener(e -> {
            try {
                tradeManager.confirmMeetingGeneral(trader, tradeID);
                // System.out.println(tradeQuery.isFirstUserConfirmed1(tradeID));
                // System.out.println(tradeQuery.isSecondUserConfirmed1(tradeID));
                // System.out.println(tradeQuery.isFirstUserConfirmed2(tradeID));
                // System.out.println(tradeQuery.isSecondUserConfirmed2(tradeID));
                tradeConfirmButton.setBackground(bg);
                tradeConfirmButton.setText("Confirmed");
                tradeConfirmButton.setEnabled(false);
                tradeConfirmButton.setFont(boldItalic.deriveFont(20f));
                ((TradePanel) this.getParent()).refreshOngoingTradesPanel();
            } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException | IOException e1) {
                e1.printStackTrace();
            }
        });

        return tradeConfirmButton;
    }

    private JPanel createNoTradesFoundPanel() {
        JPanel noTradesFoundPanel = new JPanel();
        noTradesFoundPanel.setPreferredSize(new Dimension(1200, 300));
        noTradesFoundPanel.setBackground(gray2);
        JLabel noTradesFound = new JLabel("<html><pre>No Ongoing Trades Found</pre></html>");
        noTradesFound.setFont(bold.deriveFont(30f));
        noTradesFound.setForeground(Color.WHITE);
        noTradesFound.setHorizontalAlignment(JLabel.CENTER);
        noTradesFound.setPreferredSize(new Dimension(1200, 300));
        noTradesFoundPanel.add(noTradesFound, BorderLayout.CENTER);
        return noTradesFoundPanel;
    }

    private JPanel setOngoingTradesHeader() {
        JPanel ongoingTradesHeader = new JPanel(new GridLayout(1, 5, 25, 0));
        ongoingTradesHeader.setPreferredSize(new Dimension(1200, 25));
        ongoingTradesHeader.setBackground(gray);
        ongoingTradesHeader.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 80));

        JLabel name = new JLabel("Name");
        name.setFont(this.regular.deriveFont(20f));
        name.setForeground(Color.BLACK);
        name.setHorizontalAlignment(JLabel.LEFT);

        JLabel location = new JLabel("Location");
        location.setFont(this.regular.deriveFont(20f));
        location.setForeground(Color.BLACK);
        location.setHorizontalAlignment(JLabel.CENTER);

        JLabel meetingTime = new JLabel("           Meeting Time");
        meetingTime.setFont(this.regular.deriveFont(20f));
        meetingTime.setForeground(Color.BLACK);
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

    private JButton createTradeButton(String title) {
        JButton tradeButton = new JButton(title);
        tradeButton.setFont(regular.deriveFont(20f));
        tradeButton.setHorizontalAlignment(JButton.RIGHT);
        tradeButton.setForeground(Color.cyan);
        tradeButton.setBackground(bg);
        tradeButton.setOpaque(true);
        tradeButton.setBorderPainted(false);
        tradeButton.addActionListener(this);
        return tradeButton;
    }

    private JPanel setOngoingTradesTitleHeader() {
        JPanel ongoingTradesTitleContainer = new JPanel(new GridLayout(1, 4));
        ongoingTradesTitleContainer.setOpaque(false);
        ongoingTradesTitleContainer.setPreferredSize(new Dimension(1200, 50));

        JLabel ongoingTradesTitle = new JLabel("Ongoing Trades");
        ongoingTradesTitle.setFont(regular.deriveFont(30f));
        ongoingTradesTitle.setForeground(Color.WHITE);
        ongoingTradesTitle.setBackground(bg);
        ongoingTradesTitle.setHorizontalAlignment(JLabel.LEFT);
        ongoingTradesTitle.setOpaque(true);
        ongoingTradesTitleContainer.add(ongoingTradesTitle);

        JButton suggestLendButton = createTradeButton("<html><b><i><u>Suggest Lend</u></i></b></html>");
        JButton suggestTradeButton = createTradeButton("<html><b><i><u>Suggest Trade</u></i></b></html>");
        JButton addNewTradeButton = createTradeButton("<html><b><i><u>Add New Trade</u></i></b></html>");

        ongoingTradesTitleContainer.add(ongoingTradesTitle);
        ongoingTradesTitleContainer.add(suggestLendButton);
        ongoingTradesTitleContainer.add(suggestTradeButton);
        ongoingTradesTitleContainer.add(addNewTradeButton);

        return ongoingTradesTitleContainer;
    }

    /**
     * For responding to any events
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // same as actionPreformed in TradePanel
        if (trader.equals(""))
            return;

        boolean isSuggestedTrade = e.getActionCommand().equals("<html><b><i><u>Suggest Trade</u></i></b></html>");
        boolean isSuggestedLend = e.getActionCommand().equals("<html><b><i><u>Suggest Lend</u></i></b></html>");

        String[] suggested = null;
        try {
            if (isSuggestedLend) {
                suggested = infoManager.suggestLend(trader, true, normalSuggestionStrategy);
                if (suggested == null){
                    suggested = infoManager.suggestLend(trader, true, similarSuggestionStrategy);
                }
            }
            else if (isSuggestedTrade) {
                suggested = infoManager.suggestTrade(trader, true, normalSuggestionStrategy);
                if (suggested == null) {
                    suggested = infoManager.suggestTrade(trader, true, similarSuggestionStrategy);
                }
            }
        } catch (UserNotFoundException | AuthorizationException e1) {
            e1.printStackTrace();
        }

        if (suggested == null && (isSuggestedLend || isSuggestedTrade)) {
            JDialog noSuggestionsFoundModal = createNoSuggestsFoundModal();
            noSuggestionsFoundModal.setVisible(true);
        }

        try {
            JDialog newTradeModal = new AddNewTradeModal(trader, suggested, regular, bold, italic, boldItalic);
            newTradeModal.setVisible(true);
        } catch (IOException | UserNotFoundException | TradableItemNotFoundException e1) {
            e1.printStackTrace();
        }

    }

    private JDialog createNoSuggestsFoundModal() {
        JDialog noSuggestionsFound = new JDialog();
        noSuggestionsFound.setTitle("No Suggestions Found");
        noSuggestionsFound.setSize(500, 200);
        noSuggestionsFound.setResizable(false);
        noSuggestionsFound.setLocationRelativeTo(null);

        JTextArea noSuggestionsTitle = new JTextArea(
                "Unfortunately, we are not able to find a trade suggestion for you.\n\nClosing this pop-up will take you to the\n'Add New Trade' menu.");
        noSuggestionsTitle.setFont(regular.deriveFont(22f));
        noSuggestionsTitle.setBackground(bg);
        noSuggestionsTitle.setForeground(Color.WHITE);
        noSuggestionsTitle.setPreferredSize(new Dimension(500, 200));
        noSuggestionsTitle.setOpaque(true);
        noSuggestionsTitle.setEditable(false);
        noSuggestionsTitle.setLineWrap(true);
        noSuggestionsTitle.setWrapStyleWord(true);
        noSuggestionsTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        noSuggestionsFound.add(noSuggestionsTitle);
        noSuggestionsFound.setModal(true);

        return noSuggestionsFound;
    }
    
}