package frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals;

import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.*;

/**
 * Showing the details of a trade
 */
public class TradeDetailsModal extends JDialog {

	private final Color bg = new Color(51, 51, 51);
	private final Color gray = new Color(196, 196, 196);

	private final String tradeID;
	private final String traderName;
	private final String otherTraderName;
	private final boolean showAvailableEdits;
	private final boolean isTraderFirstUser;

	private final Font regular;
	private final Font bold;
	private final Font italic;

	private final ItemQuery itemQuery = new ItemQuery();
	private final TradeQuery tradeQuery = new TradeQuery();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", new Locale("en", "US"));

	/**
	 * Making a new dialog showing details of a trade
	 * @param tradeID the trade id
	 * @param showAvailableEdits the number of edits the trade can have
	 * @param isTraderFirstUser whether or not if this trader is the first user of the trade
	 * @param regular regular font
	 * @param bold bold font
	 * @param italic italics font
	 * @param boldItalic bold italics font
	 * @throws IOException if database files aren't found
	 * @throws TradeNotFoundException if the trade isn't found
	 * @throws UserNotFoundException if the user doesn't exist
	 * @throws TradableItemNotFoundException if the item doesn't exist
	 */
	public TradeDetailsModal(String tradeID, boolean showAvailableEdits, boolean isTraderFirstUser, Font regular,
			Font bold, Font italic, Font boldItalic) throws IOException, TradeNotFoundException, UserNotFoundException,
			TradableItemNotFoundException {
		this.tradeID = tradeID;
		this.showAvailableEdits = showAvailableEdits;
		this.isTraderFirstUser = isTraderFirstUser;
		this.regular = regular;
		this.bold = bold;
		this.italic = italic;
		UserQuery userQuery = new UserQuery();
		if (isTraderFirstUser) {
			traderName = userQuery.getUsername(tradeQuery.getFirstUserId(tradeID));
			otherTraderName = userQuery.getUsername(tradeQuery.getSecondUserId(tradeID));
		} else {
			traderName = userQuery.getUsername(tradeQuery.getSecondUserId(tradeID));
			otherTraderName = userQuery.getUsername(tradeQuery.getFirstUserId(tradeID));
		}

		this.setTitle("Trade Details");
		this.setSize(new Dimension(800, showAvailableEdits ? 650 : 600));
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		JPanel tradeDetailsPanel = setTradeDetailsPanel();

		this.add(tradeDetailsPanel);
		this.setModal(true);
		this.setVisible(true);
	}

	private JPanel setTradeDetailsPanel() throws TradeNotFoundException, TradableItemNotFoundException {
		JPanel tradeDetailsPanel = new JPanel();
		tradeDetailsPanel.setPreferredSize(new Dimension(800, showAvailableEdits ? 550 : 500));
		tradeDetailsPanel.setBackground(bg);

		tradeDetailsPanel.add(setTraderItemTitle());
		tradeDetailsPanel.add(setTraderItemRequestName());
		tradeDetailsPanel.add(setOtherTraderItemTitle());
		tradeDetailsPanel.add(setOtherTraderItemRequestName());
		tradeDetailsPanel.add(setMeetingLocationTitle());
		tradeDetailsPanel.add(setMeetingLocationName());
		tradeDetailsPanel.add(setFirstMeetingDateTitle());
		tradeDetailsPanel.add(setFirstMeetingDate());
		tradeDetailsPanel.add(setSecondMeetingDateTitle());
		tradeDetailsPanel.add(setSecondMeetingDate());
		if(showAvailableEdits) {
			tradeDetailsPanel.add(setAvailableEditsTitle());
			tradeDetailsPanel.add(setAvailableEdits());
		}
		tradeDetailsPanel.add(setMessageTitle());
		tradeDetailsPanel.add(setMessageBody());

		return tradeDetailsPanel;
	}

	private JLabel setTraderItemTitle() {
		JLabel traderItemTitle = new JLabel("Item from " + traderName + ":");
		traderItemTitle.setFont(italic.deriveFont(20f));
		traderItemTitle.setPreferredSize(new Dimension(290, 50));
		traderItemTitle.setOpaque(false);
		traderItemTitle.setForeground(Color.WHITE);

		return traderItemTitle;
	}

	private JLabel setOtherTraderItemTitle() {
		JLabel otherTraderItemTitle = new JLabel("Item from " + otherTraderName + ":");
		otherTraderItemTitle.setFont(italic.deriveFont(20f));
		otherTraderItemTitle.setPreferredSize(new Dimension(290, 50));
		otherTraderItemTitle.setOpaque(false);
		otherTraderItemTitle.setForeground(Color.WHITE);

		return otherTraderItemTitle;
	}

	private JLabel setOtherTraderItemRequestName() throws TradeNotFoundException, TradableItemNotFoundException {
		String otherTraderItemName = isTraderFirstUser ? itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)) : itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID));

		JLabel otherTraderItemRequestName = new JLabel("<html><pre>" + otherTraderItemName + "</pre></html>");
		otherTraderItemRequestName.setFont(regular.deriveFont(20f));
		otherTraderItemRequestName.setPreferredSize(new Dimension(400, 50));
		otherTraderItemRequestName.setOpaque(false);
		otherTraderItemRequestName.setForeground(Color.WHITE);

		return otherTraderItemRequestName;
	}

	private JLabel setTraderItemRequestName() throws TradeNotFoundException, TradableItemNotFoundException {
		String traderItemName = !isTraderFirstUser ? itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)) : itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID));

		JLabel traderItemRequestName = new JLabel(
				"<html><pre>" + traderItemName + "</pre></html>");
		traderItemRequestName.setFont(regular.deriveFont(20f));
		traderItemRequestName.setPreferredSize(new Dimension(400, 50));
		traderItemRequestName.setOpaque(false);
		traderItemRequestName.setForeground(Color.WHITE);

		return traderItemRequestName;
	}

	private JLabel setMeetingLocationTitle() {
		JLabel meetingLocationTitle = new JLabel("Meeting Location:");
		meetingLocationTitle.setFont(italic.deriveFont(20f));
		meetingLocationTitle.setPreferredSize(new Dimension(290, 50));
		meetingLocationTitle.setOpaque(false);
		meetingLocationTitle.setForeground(Color.WHITE);

		return meetingLocationTitle;
	}

	private JLabel setMeetingLocationName() {
		JLabel meetingLocationName = new JLabel();
		try {
			meetingLocationName = new JLabel(
					"<html><pre>" + tradeQuery.getMeetingLocation(tradeID) + "</pre></html>");
		} catch (TradeNotFoundException tradeNotFoundException) {
			tradeNotFoundException.printStackTrace();
		}

		meetingLocationName.setFont(italic.deriveFont(20f));
		meetingLocationName.setPreferredSize(new Dimension(400, 50));
		meetingLocationName.setOpaque(false);
		meetingLocationName.setForeground(Color.WHITE);

		return meetingLocationName;
	}

	private JLabel setFirstMeetingDateTitle() {
		JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
		firstMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
		firstMeetingDateTitle.setFont(italic.deriveFont(20f));
		firstMeetingDateTitle.setOpaque(false);
		firstMeetingDateTitle.setForeground(Color.WHITE);

		return firstMeetingDateTitle;
	}

	private JLabel setFirstMeetingDate() {
		JLabel firstMeetingDate = new JLabel();
		try {
			firstMeetingDate = new JLabel("<html><pre>"
					+ dateFormat.format(tradeQuery.getMeetingTime(tradeID)) + "</pre></html>");
		} catch (TradeNotFoundException tradeNotFoundException) {
			tradeNotFoundException.printStackTrace();
		}

		firstMeetingDate.setFont(italic.deriveFont(20f));
		firstMeetingDate.setPreferredSize(new Dimension(400, 50));
		firstMeetingDate.setOpaque(false);
		firstMeetingDate.setForeground(Color.WHITE);

		return firstMeetingDate;
	}

	private JLabel setSecondMeetingDateTitle() {
		JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
		secondMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
		secondMeetingDateTitle.setFont(italic.deriveFont(20f));
		secondMeetingDateTitle.setOpaque(false);
		secondMeetingDateTitle.setForeground(Color.WHITE);

		return secondMeetingDateTitle;
	}

	private JLabel setSecondMeetingDate() {
		JLabel secondMeetingDate = new JLabel();
		secondMeetingDate.setFont(bold.deriveFont(20f));
		secondMeetingDate.setPreferredSize(new Dimension(400, 50));
		secondMeetingDate.setOpaque(false);
		secondMeetingDate.setForeground(Color.WHITE);

		try {
			if (tradeQuery.getSecondMeetingTime(tradeID) != null) {
				secondMeetingDate.setText(
						"<html><pre>" + dateFormat.format(tradeQuery.getSecondMeetingTime(tradeID))
								+ "</pre></html>");
			} else {
				secondMeetingDate.setText("N/A");
			}
		} catch (TradeNotFoundException tradeNotFoundException) {
			tradeNotFoundException.printStackTrace();
		}

		return secondMeetingDate;
	}

	private JLabel setMessageTitle() {
		JLabel messageTitle = new JLabel("Optional Attached Messsage:");
		messageTitle.setFont(italic.deriveFont(20f));
		messageTitle.setPreferredSize(new Dimension(690, 50));
		messageTitle.setOpaque(false);
		messageTitle.setForeground(Color.WHITE);

		return messageTitle;
	}

	private JTextArea setMessageBody() {
		JTextArea messageBody = new JTextArea();
		messageBody.setFont(regular.deriveFont(20f));
		messageBody.setPreferredSize(new Dimension(690, 150));
		messageBody.setOpaque(true);
		messageBody.setEditable(false);
		messageBody.setBackground(gray);
		messageBody.setForeground(Color.BLACK);
		messageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		messageBody.setLineWrap(true);
		messageBody.setWrapStyleWord(true);

		try {
			messageBody.setText(tradeQuery.getMessage(tradeID));
		} catch (TradeNotFoundException e1) {
			e1.printStackTrace();
		}

		return messageBody;
	}

	private JLabel setAvailableEditsTitle() {
		JLabel availableEditsTitle = new JLabel("Available Edits Left:");
		availableEditsTitle.setPreferredSize(new Dimension(290, 50));
		availableEditsTitle.setFont(bold.deriveFont(20f));
		availableEditsTitle.setOpaque(false);
		availableEditsTitle.setForeground(Color.WHITE);

		return availableEditsTitle;
	}

	private JLabel setAvailableEdits() {
		JLabel availableEdits = new JLabel();
		try {
			availableEdits = new JLabel("<html><pre>"
					+ tradeQuery.getEditAmountLeft(tradeID)
					+ "</pre></html>");
		} catch (TradeNotFoundException tradeNotFoundException) {
			tradeNotFoundException.printStackTrace();
		}

		availableEdits.setFont(italic.deriveFont(20f));
		availableEdits.setPreferredSize(new Dimension(400, 50));
		availableEdits.setOpaque(false);
		availableEdits.setForeground(Color.WHITE);

		return availableEdits;
	}
}