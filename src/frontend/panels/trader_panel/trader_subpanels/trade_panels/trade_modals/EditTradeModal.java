package frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import backend.exceptions.AuthorizationException;
import backend.exceptions.CannotTradeException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingManager;

/**
 * For showing the dialog that edits a trade
 */
public class EditTradeModal extends JDialog implements ActionListener {
	
	private final String tradeID, trader;
	private final boolean isTraderFirstUser;

	private JLabel availableEdits, error;
	private JPanel firstMeetingDate, secondMeetingDate;
	private JTextField messageInput;
	private JCheckBox isTemporaryButton;
	private JTextField meetingLocationInput;
	private JComboBox<String> otherTraderItems;
	private JComboBox<String> traderItems;

	private final Font regular, bold, italic, boldItalic;

	private boolean returnValue;

	private final Color bg = new Color(51, 51, 51);
	private final Color gray2 = new Color(142, 142, 142);
	private final Color green = new Color(27, 158, 36);
	private final Color red = new Color(219, 58, 52);

	private final UserQuery userQuery = new UserQuery();
	private final TradeQuery tradeQuery = new TradeQuery();
	private final ItemQuery itemQuery = new ItemQuery();

	private final TradingManager tradeManager = new TradingManager();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", new Locale("en", "US"));

	/**
	 * Making a dialog to edit a trade
	 * @param tradeID the trade id
	 * @param trader the trader id
	 * @param isTraderFirstUser if the trader is the user that started the trade
	 * @param regular regular font
	 * @param bold bold font
	 * @param italic italics font
	 * @param boldItalic bold italics font
	 * @throws IOException issues with getting database files
	 * @throws TradeNotFoundException if the trade doesn't exist
	 */
	public EditTradeModal(String tradeID, String trader, boolean isTraderFirstUser, Font regular, Font bold,
			Font italic, Font boldItalic) throws IOException, TradeNotFoundException {

		this.tradeID = tradeID;		
		this.trader = trader;
		this.isTraderFirstUser = isTraderFirstUser;
		this.regular = regular;
		this.bold = bold;
		this.italic = italic;
		this.boldItalic = boldItalic;

		this.setTitle("Trade Edit");
		this.setSize(700, 700);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setModal(true);

		JPanel tradeEditsPanel = setTradeEditsPanel();

		JButton submitButton = setSubmitButton();

		this.add(tradeEditsPanel);
		this.add(submitButton, BorderLayout.SOUTH);
	}

	/**
	 * Shows this dialog to the user (used in TradePanel)
	 * @return returnValue, true if the user has submitted an edit false otherwise
	 */
	public boolean showDialog() {
		this.setVisible(true);
		return returnValue;
	}

	private JButton setSubmitButton() {
		JButton submitButton = new JButton("Submit");
		submitButton.setFont(bold.deriveFont(20f));
		submitButton.setBackground(green);
		submitButton.setForeground(Color.WHITE);
		submitButton.setPreferredSize(new Dimension(325, 50));
		submitButton.addActionListener(this);
		return submitButton;
	}

	private JPanel setTradeEditsPanel() throws TradeNotFoundException {
		JPanel tradeEditsPanel = new JPanel();
		tradeEditsPanel.setPreferredSize(new Dimension(700, 700));
		tradeEditsPanel.setBackground(bg);

		JPanel traderItemsPanel = setTraderItemsPanel();
		JPanel otherTraderItemsPanel = setOtherTraderItemsPanel();
		JPanel meetingLocationPanel = setMeetingLocationPanel();
		JPanel isTemporaryPanel = setIsTemporaryPanel();
		JPanel firstMeetingDatePanel = setFirstMeetingDatePanel();
		JPanel secondMeetingDatePanel = setSecondMeetingDatePanel();
		JPanel messagePanel = setMessagePanel();
		JPanel availableEditsPanel = setAvailableEditsPanel();

		addExistingTradeData();

		error = new JLabel();
		error.setPreferredSize(new Dimension(650, 50));
		error.setForeground(red);
		error.setFont(boldItalic.deriveFont(20f));
		error.setHorizontalAlignment(JLabel.CENTER);
		error.setVisible(false);

		isTemporaryButton.addItemListener(ex -> secondMeetingDatePanel.setVisible(isTemporaryButton.isSelected()));

		tradeEditsPanel.add(traderItemsPanel);
		tradeEditsPanel.add(otherTraderItemsPanel);
		tradeEditsPanel.add(meetingLocationPanel);
		tradeEditsPanel.add(isTemporaryPanel);
		tradeEditsPanel.add(firstMeetingDatePanel);
		tradeEditsPanel.add(secondMeetingDatePanel);
		tradeEditsPanel.add(messagePanel);
		tradeEditsPanel.add(availableEditsPanel);
		tradeEditsPanel.add(error);

		return tradeEditsPanel;
	}
	
	
	private void addExistingTradeData() {
		try {
			String otherTraderId = isTraderFirstUser ? tradeQuery.getSecondUserId(tradeID) : tradeQuery.getFirstUserId(tradeID);
			for (String itemId : (userQuery.getAvailableItems(otherTraderId))) {
				otherTraderItems.addItem(itemQuery.getName(itemId));
			}

			String traderItemId = isTraderFirstUser ? tradeQuery.getFirstUserOffer(tradeID) : tradeQuery.getSecondUserOffer(tradeID);
			String otherTraderItemId = isTraderFirstUser ? tradeQuery.getSecondUserOffer(tradeID) : tradeQuery.getFirstUserOffer(tradeID);
			traderItems.setSelectedItem(traderItemId.equals("") ? null : itemQuery.getName(traderItemId));
			otherTraderItems.setSelectedItem(otherTraderItemId.equals("") ? null : itemQuery.getName(otherTraderItemId));

			meetingLocationInput.setText(tradeQuery.getMeetingLocation(tradeID));
			availableEdits.setText("<html><pre>" + tradeQuery.getEditAmountLeft(tradeID) + " Edit(s) Remaining</pre></html>");

		} catch (TradeNotFoundException | UserNotFoundException | TradableItemNotFoundException
				| AuthorizationException e) {
			e.printStackTrace();
		}
	}

	private JPanel setAvailableEditsPanel() {
		JPanel availableEditsPanel = new JPanel();
		availableEditsPanel.setBackground(bg);
		availableEditsPanel.setPreferredSize(new Dimension(700,50)); 
		

		JLabel availableEditsTitle = new JLabel("Available Edits Left:");
		availableEditsTitle.setPreferredSize(new Dimension(325, 50));
		availableEditsTitle.setFont(bold.deriveFont(20f));
		availableEditsTitle.setOpaque(false);
		availableEditsTitle.setForeground(Color.WHITE);

		availableEdits =  new JLabel();
		availableEdits.setFont(italic.deriveFont(20f));
		availableEdits.setPreferredSize(new Dimension(325, 50));
		availableEdits.setHorizontalAlignment(JLabel.CENTER);
		availableEdits.setOpaque(false);
		availableEdits.setForeground(Color.WHITE);

		availableEditsPanel.add(availableEditsTitle);
		availableEditsPanel.add(availableEdits);

		return availableEditsPanel;
	}

	private JPanel setMessagePanel() {
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(bg);
		messagePanel.setPreferredSize(new Dimension(700, 100));

		JLabel messageTitle = new JLabel("Attach a counter-message: (Optional)");
		messageTitle.setFont(italic.deriveFont(20f));
		messageTitle.setPreferredSize(new Dimension(650, 50));
		messageTitle.setOpaque(false);
		messageTitle.setForeground(Color.WHITE);

		messageInput = new JTextField();
		messageInput.setFont(regular.deriveFont(20f));
		messageInput.setBackground(gray2);
		messageInput.setForeground(Color.BLACK);
		messageInput.setPreferredSize(new Dimension(650, 50));
		messageInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		messagePanel.add(messageTitle);
		messagePanel.add(messageInput);

		return messagePanel;
	}

	private JPanel setSecondMeetingDatePanel() {

		JPanel meetingDatePanel = new JPanel();
		meetingDatePanel.setBackground(bg);
		meetingDatePanel.setPreferredSize(new Dimension(700, 65)); 

		JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
		secondMeetingDateTitle.setPreferredSize(new Dimension(200, 50));
		secondMeetingDateTitle.setFont(italic.deriveFont(20f));
		secondMeetingDateTitle.setOpaque(false);
		secondMeetingDateTitle.setForeground(Color.WHITE);

		if(isTemporaryButton.isSelected()) {
			String month2 = null;
			int day2 = 0, year2 = 0, hour2 = 0, min2 = 0;
			try {
				month2 = tradeQuery.getSecondMeetingTime(tradeID).toString().substring(4, 7);
				day2 = Integer.parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(8, 10));
				year2 = Integer.parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(24, 28));
				hour2 = Integer.parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(11, 13));
				min2 = Integer.parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(14, 16));
			} catch (TradeNotFoundException tradeNotFoundException) {
				tradeNotFoundException.printStackTrace();
			}

			secondMeetingDate = setDateInput(month2, day2, year2, hour2, min2);
		} else {
			secondMeetingDate = setDateInput("Jan", 1, 2020, 0, 0);
		}

		

		meetingDatePanel.add(secondMeetingDateTitle);
		meetingDatePanel.add(secondMeetingDate);

		meetingDatePanel.setVisible(isTemporaryButton.isSelected());

		return meetingDatePanel;
	}

	private JPanel setFirstMeetingDatePanel() {
		JPanel meetingDatePanel = new JPanel();
		meetingDatePanel.setBackground(bg);
		meetingDatePanel.setPreferredSize(new Dimension(700,65)); // fix

		JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
		firstMeetingDateTitle.setPreferredSize(new Dimension(200, 50));
		firstMeetingDateTitle.setFont(italic.deriveFont(20f));
		firstMeetingDateTitle.setOpaque(false);
		firstMeetingDateTitle.setForeground(Color.WHITE);

		String month = null;
		int day = 0, year = 0, hour = 0, min = 0;
		try {
			month = tradeQuery.getMeetingTime(tradeID).toString().substring(4, 7);
			day = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(8, 10));
			year = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(24, 28));
			hour = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(11, 13));
			min = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(14, 16));
		} catch (TradeNotFoundException tradeNotFoundException) {
			tradeNotFoundException.printStackTrace();
		}

		firstMeetingDate = setDateInput(month, day, year, hour, min);

		meetingDatePanel.add(firstMeetingDateTitle);
		meetingDatePanel.add(firstMeetingDate);

		return meetingDatePanel;
	}

	private JPanel setIsTemporaryPanel() throws TradeNotFoundException {
		JPanel isTemporaryPanel = new JPanel();
		isTemporaryPanel.setBackground(bg);
		isTemporaryPanel.setPreferredSize(new Dimension(700,50));

		JLabel isTemporaryTitle = new JLabel("Is this trade temporary?");
		isTemporaryTitle.setFont(italic.deriveFont(20f));
		isTemporaryTitle.setPreferredSize(new Dimension(625, 50));
		isTemporaryTitle.setOpaque(false);
		isTemporaryTitle.setForeground(Color.WHITE);

		boolean isTemporary = tradeQuery.getSecondMeetingTime(tradeID) != null;

		isTemporaryButton = new JCheckBox();
		isTemporaryButton.setPreferredSize(new Dimension(25, 25));
		isTemporaryButton.setSelected(isTemporary);
		isTemporaryButton.setForeground(Color.WHITE);
		isTemporaryButton.setBackground(bg);


		isTemporaryPanel.add(isTemporaryTitle);
		isTemporaryPanel.add(isTemporaryButton);

		return isTemporaryPanel;
	}

	private JPanel setMeetingLocationPanel() {
		JPanel meetingLocationPanel = new JPanel();
		meetingLocationPanel.setBackground(bg);
		meetingLocationPanel.setPreferredSize(new Dimension(700, 50));

		JLabel meetingLocationTitle = new JLabel("Meeting Location:");
		meetingLocationTitle.setFont(italic.deriveFont(20f));
		meetingLocationTitle.setPreferredSize(new Dimension(325, 50));
		meetingLocationTitle.setOpaque(false);
		meetingLocationTitle.setForeground(Color.WHITE);

		meetingLocationInput = new JTextField();
		meetingLocationInput.setPreferredSize(new Dimension(325, 50));
		meetingLocationInput.setFont(regular.deriveFont(20f));

		meetingLocationPanel.add(meetingLocationTitle);
		meetingLocationPanel.add(meetingLocationInput);

		return meetingLocationPanel;
	}

	private JPanel setOtherTraderItemsPanel() {
		JPanel otherTraderItemsPanel = new JPanel();
		otherTraderItemsPanel.setBackground(bg);
		otherTraderItemsPanel.setPreferredSize(new Dimension(700, 50));

		JLabel otherTraderItemTitle = new JLabel("Item from their Inventory:");
		otherTraderItemTitle.setFont(italic.deriveFont(20f));
		otherTraderItemTitle.setPreferredSize(new Dimension(325, 50));
		otherTraderItemTitle.setOpaque(false);
		otherTraderItemTitle.setForeground(Color.WHITE);

		otherTraderItems = new JComboBox<>();
		otherTraderItems.setFont(regular.deriveFont(20f));
		otherTraderItems.setBackground(gray2);
		otherTraderItems.setForeground(Color.BLACK);
		otherTraderItems.setOpaque(true);
		otherTraderItems.setPreferredSize(new Dimension(325, 50));
		otherTraderItems.addItem(null);

		otherTraderItemsPanel.add(otherTraderItemTitle);
		otherTraderItemsPanel.add(otherTraderItems);
		return otherTraderItemsPanel;
	}

	private JPanel setTraderItemsPanel() {
		JPanel traderItemsPanel = new JPanel();
		traderItemsPanel.setBackground(bg);
		traderItemsPanel.setPreferredSize(new Dimension(700, 50));


		JLabel traderItemTitle = new JLabel("Item from your Inventory:");
		traderItemTitle.setFont(italic.deriveFont(20f));
		traderItemTitle.setPreferredSize(new Dimension(325, 50));
		traderItemTitle.setOpaque(false);
		traderItemTitle.setForeground(Color.WHITE);

		traderItems = new JComboBox<>();
		traderItems.setFont(regular.deriveFont(20f));
		traderItems.setBackground(gray2);
		traderItems.setForeground(Color.BLACK);
		traderItems.setOpaque(true);
		traderItems.setPreferredSize(new Dimension(325, 50));
		traderItems.addItem(null);
		try {
			for (String itemId : userQuery.getAvailableItems(trader)) {
				traderItems.addItem(itemQuery.getName(itemId));
			}
		} catch (UserNotFoundException | AuthorizationException | TradableItemNotFoundException e1) {
			e1.printStackTrace();
		}

		traderItemsPanel.add(traderItemTitle);
		traderItemsPanel.add(traderItems);

		return traderItemsPanel;
	}

	private JPanel setDateInput(String month, int day, int year, int hour, int min) {
		JPanel meetingInput = new JPanel();
		meetingInput.setBackground(bg);
		meetingInput.setPreferredSize(new Dimension(450, 50));

		String[] monthList = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		JComboBox<String> months = new JComboBox<>(monthList);
		months.setPreferredSize(new Dimension(100, 50));
		months.setFont(regular.deriveFont(20f));

		JComboBox<Integer> days = new JComboBox<>();
		days.setFont(regular.deriveFont(20f));
		days.setPreferredSize(new Dimension(60, 50));
		for (int i = 1; i < 32; i++)
			days.addItem(i);

		months.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				int numDays;
				if (e.getItem().equals("Apr") || e.getItem().equals("Jun") || e.getItem().equals("Sep")
						|| e.getItem().equals("Nov"))
					numDays = 30;
				else if (e.getItem().equals("Feb"))
					numDays = 28;
				else
					numDays = 31;
				days.removeAllItems();
				for (int i = 1; i <= numDays; i++)
					days.addItem(i);
			}
		});

		JComboBox<Integer> years = new JComboBox<>();
		years.setPreferredSize(new Dimension(100, 50));
		years.setFont(regular.deriveFont(20f));
		for (int i = 2020; i < 2026; i++)
			years.addItem(i);

		JComboBox<Integer> hours = new JComboBox<>();
		hours.setPreferredSize(new Dimension(50, 50));
		hours.setFont(regular.deriveFont(20f));
		for (int i = 0; i < 24; i++)
			hours.addItem(i);

		JComboBox<Integer> minutes = new JComboBox<>();
		minutes.setPreferredSize(new Dimension(50, 50));
		minutes.setFont(regular.deriveFont(20f));
		for (int i = 0; i < 60; i++)
			minutes.addItem(i);

		months.setSelectedItem(month);
		days.setSelectedItem(day);
		years.setSelectedItem(year);
		hours.setSelectedItem(hour);
		minutes.setSelectedItem(min);

		meetingInput.add(months);
		meetingInput.add(days);
		meetingInput.add(years);
		meetingInput.add(hours);
		meetingInput.add(minutes);
		return meetingInput;
	}

	/**
	 * This is the actionListener for the SubmitButton
	 * @param e the actionEvent of the button press
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!meetingLocationInput.getText().equals("")) {
			String firstMeetingString = "";
			String secondMeetingString = "";
			for (int i = 0; i < 5; i++) {
				Component c = firstMeetingDate.getComponent(i);
				if (c instanceof JComboBox<?>) {
					if (((String) ((JComboBox<?>) c).getSelectedItem().toString()).length() == 1) {
						firstMeetingString += "0" + ((JComboBox<?>) c).getSelectedItem();
					} else {
						firstMeetingString += ((JComboBox<?>) c).getSelectedItem();
					}
					if (i == 3) {
						firstMeetingString += ":";
					} else {
						firstMeetingString += " ";
					}
				}
			}
			if (isTemporaryButton.isSelected()) {
				for (int i = 0; i < 5; i++) {
					Component c = secondMeetingDate.getComponent(i);
					if (c instanceof JComboBox<?>) {
						if (((String) ((JComboBox<?>) c).getSelectedItem().toString()).length() == 1) {
							secondMeetingString += "0" + ((JComboBox<?>) c).getSelectedItem();
						} else {
							secondMeetingString += ((JComboBox<?>) c).getSelectedItem();
						}
						if (i == 3) {
							secondMeetingString += ":";
						} else {
							secondMeetingString += " ";
						}
					}
				}

			}
			try {
				Date firstMeeting = dateFormat.parse(firstMeetingString);
				Date secondMeeting = secondMeetingString.equals("") ? null : dateFormat.parse(secondMeetingString);

				String thisTraderOffer = "";

				if (traderItems.getSelectedItem() != null) {
					thisTraderOffer = userQuery.getAvailableItems(trader).get(traderItems.getSelectedIndex() - 1);
				}

				String thatTraderOffer = "";
				if (otherTraderItems.getSelectedItem() != null) {
					thatTraderOffer = userQuery.getAvailableItems(tradeQuery.getOtherUserId(tradeID, trader)).get(otherTraderItems.getSelectedIndex() - 1);
				}
				tradeManager.counterTradeOffer(trader, tradeID, firstMeeting, secondMeeting,
						meetingLocationInput.getText(), thisTraderOffer, thatTraderOffer, messageInput.getText());
				returnValue = true;
				this.dispose();
			} catch (ParseException | TradeNotFoundException | UserNotFoundException | CannotTradeException
					| AuthorizationException e2) {
				error.setText(e2.getMessage());
				error.setVisible(true);
			}

		} else {
			error.setText("Enter a meeting location.");
			error.setVisible(true);
		}

	}
}