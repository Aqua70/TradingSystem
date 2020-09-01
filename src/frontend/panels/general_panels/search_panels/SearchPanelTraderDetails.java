package frontend.panels.general_panels.search_panels;

import java.util.List;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.UserQuery;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Deals with displaying the search results
 */
public class SearchPanelTraderDetails implements ActionListener {

    private final UserQuery userQuery = new UserQuery();
    private final String traderId;
    private final Font regular;
    private final Font italic;

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(75, 75, 75);
    private final Color gray2 = new Color(196, 196, 196);

    /**
     * Deals with displaying search results
     *
     * @param traderId The id of the trader whose details need to be shown
     * @param regular  Regular font
     * @param italic   Italic font
     * @throws IOException if the database files could not be found
     */
    public SearchPanelTraderDetails(String traderId, Font regular, Font italic) throws IOException {
        this.traderId = traderId;
        this.regular = regular;
        this.italic = italic;
    }

    /**
     * Creates a trader detail screen for use in the search panel
     *
     * @param e the ActionEvent object
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JDialog traderDetailsModal = createTraderDetailsModal();

        JPanel traderDetailsPanel = createTraderDetailsPanel();

        JLabel traderNameTitle = createBasicLabel("Trader Username:", italic);

        JLabel traderNameLabel = createBasicLabel("", regular);
        try {
            traderNameLabel.setText("<html><pre>" + userQuery.getUsername(traderId) + "</pre></html>");
        } catch (UserNotFoundException userNotFoundException) {
            userNotFoundException.printStackTrace();
        }

        JLabel traderIdTitle = createBasicLabel("Trader ID:", italic);

        JLabel traderIdLabel = createBasicLabel("<html><pre>#" + traderId.substring(traderId.length() - 12) + "</pre></html>", regular);

        JLabel traderCityTitle = createBasicLabel("City:", italic);

        JLabel traderCityLabel = createBasicLabel("", regular);
        try {
            traderCityLabel.setText("<html><pre>" + userQuery.getCity(traderId) + "</pre></html>");
        } catch (UserNotFoundException | AuthorizationException e1) {
            e1.printStackTrace();
        }

        JLabel traderNumTradesTitle = createBasicLabel("Trade Items Ratio:", italic);

        JLabel traderNumTradesLabel = createBasicLabel("", regular);
        try {
            traderNumTradesLabel.setText("<html><pre>" + userQuery.getTotalItemsBorrowed(traderId) +
                    " borrowed / " + userQuery.getTotalItemsLent(traderId) + " lent </pre></html>");
        } catch (UserNotFoundException | AuthorizationException e1) {
            e1.printStackTrace();
        }

        JLabel traderReviewsTitle = createReviewsByOtherTradersLabel();

        JScrollPane traderReviewScrollPane = createTraderReviewScrollPane();

        createTraderReviewsPanel(traderReviewScrollPane);

        traderDetailsPanel.add(traderNameTitle);
        traderDetailsPanel.add(traderNameLabel);
        traderDetailsPanel.add(traderIdTitle);
        traderDetailsPanel.add(traderIdLabel);
        traderDetailsPanel.add(traderCityTitle);
        traderDetailsPanel.add(traderCityLabel);
        traderDetailsPanel.add(traderNumTradesTitle);
        traderDetailsPanel.add(traderNumTradesLabel);
        traderDetailsPanel.add(traderReviewsTitle);
        traderDetailsPanel.add(traderReviewScrollPane);

        traderDetailsModal.add(traderDetailsPanel);
        traderDetailsModal.setModal(true);
        traderDetailsModal.setVisible(true);

    }

    private JScrollPane createTraderReviewScrollPane() {
        JScrollPane traderReviewScrollPane = new JScrollPane();
        traderReviewScrollPane.setPreferredSize(new Dimension(580, 250));
        traderReviewScrollPane.setBorder(null);
        traderReviewScrollPane.setBackground(gray);
        return traderReviewScrollPane;
    }

    private JPanel createTraderDetailsPanel() {
        JPanel traderDetailsPanel = new JPanel();
        traderDetailsPanel.setPreferredSize(new Dimension(600, 600));
        traderDetailsPanel.setBackground(bg);
        return traderDetailsPanel;
    }

    private JDialog createTraderDetailsModal() {
        JDialog traderDetailsModal = new JDialog();
        traderDetailsModal.setTitle("Trader Details");
        traderDetailsModal.setSize(600, 600);
        traderDetailsModal.setResizable(false);
        traderDetailsModal.setLocationRelativeTo(null);
        return traderDetailsModal;
    }

    private void createTraderReviewsPanel(JScrollPane traderReviewScrollPane) {
        List<String[]> reviews = new ArrayList<>();
        try {
            reviews = userQuery.getReviews(traderId);
        } catch (UserNotFoundException | AuthorizationException e1) {
            e1.printStackTrace();
        }
        int numberOfRows = reviews.size();
        if (numberOfRows < 4) numberOfRows = 4;

        JPanel traderReviews = new JPanel(new GridLayout(numberOfRows, 1));
        traderReviews.setBackground(gray2);
        traderReviews.setPreferredSize(new Dimension(580, 250));
        reviews.forEach(review -> createTraderReviewRowPanel(traderReviews, review));

        traderReviewScrollPane.setViewportView(traderReviews);
    }

    private void createTraderReviewRowPanel(JPanel traderReviews, String[] review) {

        JPanel traderReview = new JPanel(new GridLayout(1, 1));
        traderReview.setBackground(gray2);
        traderReview.setPreferredSize(new Dimension(500, 50));
        traderReview.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, bg));

        JLabel text = new JLabel("DUMMY TEXT");
        try {
            text.setText(userQuery.getUsername(review[0]) + ": " + (review[3] + "   ->  ") + review[2]);
        } catch (UserNotFoundException ex) {
            ex.printStackTrace();
        }
        text.setFont(regular.deriveFont(20f));
        text.setForeground(Color.BLACK);
        text.setHorizontalAlignment(JLabel.LEFT);
        text.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        text.setOpaque(false);

        traderReview.add(text);
        traderReviews.add(traderReview);
    }

    private JLabel createBasicLabel(String display, Font font) {
        JLabel newLabel = new JLabel(display);
        newLabel.setFont(font.deriveFont(20f));
        newLabel.setPreferredSize(new Dimension(290, 50));
        newLabel.setOpaque(false);
        newLabel.setForeground(Color.WHITE);
        return newLabel;
    }


    private JLabel createReviewsByOtherTradersLabel() {
        JLabel newLabel = new JLabel("Reviews by other Traders:");
        newLabel.setFont(italic.deriveFont(20f));
        newLabel.setPreferredSize(new Dimension(580, 50));
        newLabel.setOpaque(false);
        newLabel.setForeground(Color.WHITE);
        return newLabel;
    }

}
