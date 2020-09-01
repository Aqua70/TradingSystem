package frontend.panels.trader_panel.trader_subpanels.trade_panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import backend.exceptions.AuthorizationException;
import backend.exceptions.CannotTradeException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingManager;
import frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals.EditTradeModal;
import frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals.TradeDetailsModal;

/**
 * For showing trade requests
 */
public class TradeRequestsPanel extends JPanel {

    private final Font regular, bold, italic, boldItalic;
    private final String trader;

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(196, 196, 196);
    private final Color gray2 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);
    private final Color red = new Color(219, 58, 52);

    private final TradeQuery tradeQuery = new TradeQuery();
    private final UserQuery userQuery = new UserQuery();
    private final ItemQuery itemQuery = new ItemQuery();

    private final TradingManager tradeManager = new TradingManager();

    /**
     * For making a panel for showing trade requests
     * @param trader the trader id
     * @param regular regular font
     * @param bold bold font
     * @param italic italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with getting database file
     * @throws UserNotFoundException if the user wasn't found
     * @throws AuthorizationException if the user isn't allowed to do certain actions
     * @throws TradeNotFoundException if the trade isn't found
     * @throws TradableItemNotFoundException if the item isn't found
     */
    public TradeRequestsPanel(String trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException,
            UserNotFoundException, AuthorizationException, TradeNotFoundException, TradableItemNotFoundException {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        JLabel tradeRequestsTitle = setTradeRequestsTitle();
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        this.add(tradeRequestsTitle, gbc);

        JPanel tradeRequestsHeader = setTradeRequestsHeader();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        this.add(tradeRequestsHeader, gbc);

        JScrollPane tradeRequestsScrollPane = setTradeRequestsScrollPane();
        gbc.gridy = 2;
        gbc.weighty = 0.8;
        this.add(tradeRequestsScrollPane, gbc);
    }

    private JScrollPane setTradeRequestsScrollPane() throws UserNotFoundException, AuthorizationException,
            TradeNotFoundException, TradableItemNotFoundException {
        JScrollPane tradeRequestsScrollPane = new JScrollPane();

        JPanel tradeRequestsContainer = setTradeRequestsContainer();
        
        tradeRequestsScrollPane.setBackground(gray);
        tradeRequestsScrollPane.setPreferredSize(new Dimension(1200, 325));
        tradeRequestsScrollPane.setViewportView(tradeRequestsContainer);
        tradeRequestsScrollPane.setBorder(null);

        return tradeRequestsScrollPane;

    }

    private JPanel setTradeRequestsContainer() throws UserNotFoundException, AuthorizationException,
            TradeNotFoundException, TradableItemNotFoundException {
        JPanel tradeRequestsContainer = new JPanel();

        List<String> requestedTrades = trader.equals("") ? new ArrayList<>() : userQuery.getRequestedTrades(trader);

        if(requestedTrades.isEmpty())
            return createNoTradesFoundPanel();

        int numRows = requestedTrades.size();
        numRows = Math.max(numRows, 4);

        tradeRequestsContainer.setLayout(new GridLayout(numRows, 1));
        tradeRequestsContainer.setBackground(gray2);
        tradeRequestsContainer.setBorder(null);

        for (String tradeID : requestedTrades) {
            boolean isTraderAbleToEdit = tradeQuery.getUserTurnToEdit(tradeID).equals(trader);

            if(isTraderAbleToEdit) {
                JPanel tradeRequestPanel = createTradeRequestPanel(tradeID);
                tradeRequestsContainer.add(tradeRequestPanel);
            }
        }

        if (tradeRequestsContainer.getComponentCount() == 0)
            return createNoTradesFoundPanel();

        return tradeRequestsContainer;
    }

    private JPanel createTradeRequestPanel(String tradeID) throws TradeNotFoundException, UserNotFoundException,
            TradableItemNotFoundException {
        JPanel tradeRequestPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        tradeRequestPanel.setPreferredSize(new Dimension(1000, 75));
        tradeRequestPanel.setBackground(gray);
        tradeRequestPanel.setBorder(BorderFactory.createLineBorder(bg));

        JLabel otherTraderName = new JLabel();
        otherTraderName.setFont(regular.deriveFont(20f));
        otherTraderName.setForeground(Color.BLACK);
        otherTraderName.setHorizontalAlignment(JLabel.LEFT);
        otherTraderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        
        JLabel otherTraderItemName = new JLabel();
        otherTraderItemName.setFont(regular.deriveFont(20f));
        otherTraderItemName.setForeground(Color.BLACK);
        otherTraderItemName.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel traderItemName = new JLabel();
        traderItemName.setFont(regular.deriveFont(20f));
        traderItemName.setForeground(Color.BLACK);
        traderItemName.setHorizontalAlignment(JLabel.CENTER);
        
        if (tradeQuery.getFirstUserId(tradeID).equals(trader)) {
            otherTraderName.setText(userQuery.getUsername(tradeQuery.getSecondUserId(tradeID)));
            traderItemName.setText(tradeQuery.getFirstUserOffer(tradeID).equals("") ? "N/A" : itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID)));
            otherTraderItemName.setText(tradeQuery.getSecondUserOffer(tradeID).equals("") ? "N/A" : itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)));
        } else {
            otherTraderName.setText(userQuery.getUsername(tradeQuery.getFirstUserId(tradeID)));
            traderItemName.setText(tradeQuery.getSecondUserOffer(tradeID).equals("") ? "N/A" : itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)));
            otherTraderItemName.setText(tradeQuery.getFirstUserOffer(tradeID).equals("") ? "N/A" : itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID)));
        }

        JLabel tradeLocation = new JLabel(tradeQuery.getMeetingLocation(tradeID));
        tradeLocation.setFont(regular.deriveFont(20f));
        tradeLocation.setForeground(Color.BLACK);
        tradeLocation.setHorizontalAlignment(JLabel.CENTER);

        boolean isTraderFirstUser = tradeQuery.getFirstUserId(tradeID).equals(trader);

        JButton tradeDetailsButton = createTradeRequestButton("Details", gray2);
        tradeDetailsButton.addActionListener(e -> {
            try {
                new TradeDetailsModal(tradeID, true, isTraderFirstUser, regular, bold, italic, boldItalic);
            } catch (IOException | TradeNotFoundException | UserNotFoundException
                    | TradableItemNotFoundException exception) {
                exception.printStackTrace();
            }
        });

        JButton editTradeButton = createTradeRequestButton("Edit", Color.CYAN);
        editTradeButton.addActionListener(e -> {
            try {
                EditTradeModal editTradeModal = new EditTradeModal(tradeID, trader, isTraderFirstUser, regular, bold, italic, boldItalic);
                boolean result = editTradeModal.showDialog();
                if(result) {
                    ((TradePanel) this.getParent()).refreshTradeRequestsPanel();
                } 
            } catch (IOException | TradeNotFoundException | UserNotFoundException | TradableItemNotFoundException
                    | AuthorizationException e2) {
                e2.printStackTrace();
            }
        });

        JButton tradeConfirmButton = createTradeRequestButton("Accept", green);
        tradeConfirmButton.addActionListener(e -> {
            try {
                tradeManager.acceptRequest(trader, tradeID);
                ((TradePanel) this.getParent()).refreshOngoingTradesPanel();
                ((TradePanel) this.getParent()).refreshTradeRequestsPanel();
            } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException | CannotTradeException
                    | IOException | TradableItemNotFoundException e1) {
                e1.printStackTrace();
            }

        });
        
        JButton tradeRejectButton = createTradeRequestButton("Reject", red);
        tradeRejectButton.addActionListener(e -> {
            try {
                tradeManager.rescindTradeRequest(tradeID);
                ((TradePanel) this.getParent()).refreshTradeRequestsPanel();
            } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException
                    | TradableItemNotFoundException | IOException e1) {
                e1.printStackTrace();
            }
        });

        tradeRequestPanel.add(otherTraderName);
        tradeRequestPanel.add(tradeLocation);
        tradeRequestPanel.add(otherTraderItemName);
        tradeRequestPanel.add(traderItemName);
        tradeRequestPanel.add(tradeDetailsButton);
        tradeRequestPanel.add(editTradeButton);
        tradeRequestPanel.add(tradeConfirmButton);
        tradeRequestPanel.add(tradeRejectButton);
        
        return tradeRequestPanel;
    }

    private JButton createTradeRequestButton(String title, Color bg) {
        JButton tradeRequestButton = new JButton(title);
        tradeRequestButton.setFont(bold.deriveFont(20f));
        tradeRequestButton.setForeground(Color.WHITE);
        tradeRequestButton.setBackground(bg);
        tradeRequestButton.setOpaque(true);
        tradeRequestButton.setBorder(BorderFactory.createLineBorder(gray, 15));
        return tradeRequestButton;
    }

    private JPanel createNoTradesFoundPanel() {
        JPanel noTradesFoundPanel = new JPanel();
        noTradesFoundPanel.setPreferredSize(new Dimension(1200, 300));
        noTradesFoundPanel.setBackground(gray2);
        JLabel noTradesFound = new JLabel("<html><pre>No Trade Requests Found</pre></html>");
        noTradesFound.setFont(bold.deriveFont(30f));
        noTradesFound.setForeground(Color.WHITE);
        noTradesFound.setHorizontalAlignment(JLabel.CENTER);
        noTradesFound.setPreferredSize(new Dimension(1200, 300));
        noTradesFoundPanel.add(noTradesFound, BorderLayout.CENTER);
        return noTradesFoundPanel;
    }

    private JPanel setTradeRequestsHeader() {
        JPanel tradeRequestsHeader = new JPanel(new GridLayout(1, 8, 20, 0));
        tradeRequestsHeader.setPreferredSize(new Dimension(1200, 25));
        tradeRequestsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 120));
        tradeRequestsHeader.setBackground(gray);

        JLabel name = new JLabel("Name");
        name.setFont(this.regular.deriveFont(20f));
        name.setForeground(Color.BLACK);
        name.setHorizontalAlignment(JLabel.CENTER);

        JLabel location = new JLabel("Location");
        location.setFont(this.regular.deriveFont(20f));
        location.setForeground(Color.BLACK);
        location.setHorizontalAlignment(JLabel.CENTER);

        JLabel theirItem = new JLabel("Their Item");
        theirItem.setFont(this.regular.deriveFont(20f));
        theirItem.setForeground(Color.BLACK);
        theirItem.setHorizontalAlignment(JLabel.CENTER);

        JLabel yourItem = new JLabel("Your Item   ");
        yourItem.setFont(this.regular.deriveFont(20f));
        yourItem.setForeground(Color.BLACK);
        yourItem.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty1 = new JLabel("");
        JLabel empty2 = new JLabel("");
        JLabel empty3 = new JLabel("");

        tradeRequestsHeader.add(name);
        tradeRequestsHeader.add(location);
        tradeRequestsHeader.add(theirItem);
        tradeRequestsHeader.add(yourItem);
        tradeRequestsHeader.add(empty1);
        tradeRequestsHeader.add(empty2);
        tradeRequestsHeader.add(empty3);

        return tradeRequestsHeader;
    }

    private JLabel setTradeRequestsTitle() {

        JLabel tradeRequestsTitle = new JLabel("Trade Requests");
        tradeRequestsTitle.setFont(this.regular.deriveFont(30f));
        tradeRequestsTitle.setForeground(Color.WHITE);
        tradeRequestsTitle.setBackground(bg);
        tradeRequestsTitle.setOpaque(true);
        tradeRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        tradeRequestsTitle.setPreferredSize(new Dimension(1200, 50));

        return tradeRequestsTitle;
    }
}