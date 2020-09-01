package frontend.panels.trader_panel.trader_subpanels;

import javax.swing.*;
import java.util.List;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TraderManager;
import frontend.components.*;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Shows the list of items and list of wishlist items
 */
public class ItemsPanel extends JPanel {

    private final String traderId;
    private final Font regular, bold;
    private JPanel inventoryHeader, wishListHeader;
    private JPanel inventoryItemsContainer, wishlistItemsContainer;
    private final JScrollPane wishlistItemsScrollPane;

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(196, 196, 196);
    private final Color gray2 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);
    private final Color red = new Color(219, 58, 52);


    private final TraderManager traderManager = new TraderManager();

    private final ItemQuery itemQuery = new ItemQuery();
    private final UserQuery userQuery = new UserQuery();

    /**
     * Makes a new panel that shows list of items and wishlist items
     *
     * @param traderId   the trader id
     * @param regular    regular font
     * @param bold       bold font
     * @param italic     italics font
     * @param boldItalic bold italics font
     * @throws IOException            if issues with accessing database files
     * @throws UserNotFoundException  trader id is bad
     * @throws AuthorizationException user isn't a trader or is frozen
     */
    public ItemsPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException, UserNotFoundException, AuthorizationException {

        this.traderId = traderId;
        this.regular = regular;
        this.bold = bold;

        this.setSize(1000, 900);
        this.setBackground(bg);

        JPanel inventoryItems = new JPanel(new GridBagLayout());
        JPanel wishListItems = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton addInventoryItemButton = addInventoryItemButton(traderId, regular, bold, italic, boldItalic);

        JPanel inventoryTitleContainer = new JPanel(new GridLayout(1, 2));
        inventoryTitleContainer.setPreferredSize(new Dimension(1200, 75));
        inventoryTitleContainer.setBackground(bg);

        JLabel inventoryTitle = new JLabel("Inventory");
        inventoryTitle.setForeground(Color.WHITE);
        inventoryTitle.setFont(regular.deriveFont(30f));


        JScrollPane inventoryItemsScrollPane = new JScrollPane();
        inventoryItemsScrollPane.setBorder(null);
        inventoryItemsScrollPane.setPreferredSize(new Dimension(1200, 325));
        getInventory();
        inventoryItemsScrollPane.setViewportView(inventoryItemsContainer);

        JPanel topInventoryItemsScrollHeaderPane = new JPanel(new GridLayout(1, 3));
        topInventoryItemsScrollHeaderPane.setPreferredSize(new Dimension(1200, 50));

        JPanel wishlistTitleContainer = new JPanel(new GridLayout(1, 2));
        wishlistTitleContainer.setPreferredSize(new Dimension(1200, 75));
        wishlistTitleContainer.setBackground(bg);

        JLabel wishlistTitle = new JLabel("Wishlist");
        wishlistTitle.setForeground(Color.WHITE);
        wishlistTitle.setFont(regular.deriveFont(30f));

        JButton addWishlistItemButton = addWishlistItemButton(traderId, regular, bold, italic, boldItalic);

        wishlistItemsScrollPane = new JScrollPane();
        wishlistItemsScrollPane.setBorder(null);
        wishlistItemsScrollPane.setPreferredSize(new Dimension(1200, 325));
        getWishlist();
        wishlistItemsScrollPane.setViewportView(wishlistItemsContainer);

        JPanel topWishlistItemsScrollHeaderPane = new JPanel(new GridLayout(1, 3));
        topWishlistItemsScrollHeaderPane.setPreferredSize(new Dimension(1200, 325));

        inventoryTitleContainer.add(inventoryTitle);
        inventoryTitleContainer.add(addInventoryItemButton);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        inventoryItems.add(inventoryTitleContainer, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        createInventoryHeader();
        inventoryItems.add(inventoryHeader, gbc);
                                                        
        gbc.gridy = 2;
        gbc.weighty = 0.8;
        inventoryItems.add(inventoryItemsScrollPane, gbc);

        wishlistTitleContainer.add(wishlistTitle);
        wishlistTitleContainer.add(addWishlistItemButton);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        wishListItems.add(wishlistTitleContainer, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        createWishListHeader();
        wishListItems.add(wishListHeader, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.8;
        wishListItems.add(wishlistItemsScrollPane, gbc);

        this.add(inventoryItems);
        this.add(wishListItems);

    }

    private void createInventoryHeader() {
        inventoryHeader = new JPanel(new GridLayout(1, 4));
        inventoryHeader.setPreferredSize(new Dimension(1200, 25));
        inventoryHeader.setBorder(BorderFactory.createEmptyBorder(0,0,0,120));
        inventoryHeader.setBackground(gray);

        JLabel itemName = new JLabel("Item Name");
        itemName.setFont(this.regular.deriveFont(20f));
        itemName.setForeground(Color.BLACK);
        itemName.setHorizontalAlignment(JLabel.CENTER);

        JLabel description = new JLabel("Description");
        description.setFont(this.regular.deriveFont(20f));
        description.setForeground(Color.BLACK);
        description.setHorizontalAlignment(JLabel.CENTER);

        JLabel itemId = new JLabel("Item ID");
        itemId.setFont(this.regular.deriveFont(20f));
        itemId.setForeground(Color.BLACK);
        itemId.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty = new JLabel("");

        inventoryHeader.add(itemName);
        inventoryHeader.add(description);
        inventoryHeader.add(itemId);
        inventoryHeader.add(empty);

    }

    private void createWishListHeader(){
        wishListHeader = new JPanel(new GridLayout(1, 5));
        wishListHeader.setPreferredSize(new Dimension(1200, 25));
        wishListHeader.setBorder(BorderFactory.createEmptyBorder(0,0,0,120));
        wishListHeader.setBackground(gray);

        JLabel itemName = new JLabel("Item Name");
        itemName.setFont(this.regular.deriveFont(20f));
        itemName.setForeground(Color.BLACK);
        itemName.setHorizontalAlignment(JLabel.CENTER);

        JLabel description = new JLabel("Description");
        description.setFont(this.regular.deriveFont(20f));
        description.setForeground(Color.BLACK);
        description.setHorizontalAlignment(JLabel.CENTER);

        JLabel itemId = new JLabel("Item ID");
        itemId.setFont(this.regular.deriveFont(20f));
        itemId.setForeground(Color.BLACK);
        itemId.setHorizontalAlignment(JLabel.CENTER);

        JLabel itemOwner = new JLabel("Item Owner");
        itemOwner.setFont(this.regular.deriveFont(20f));
        itemOwner.setForeground(Color.BLACK);
        itemOwner.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty = new JLabel("");

        wishListHeader.add(itemName);
        wishListHeader.add(description);
        wishListHeader.add(itemId);
        wishListHeader.add(itemOwner);
        wishListHeader.add(empty);
    }

    private JButton addWishlistItemButton(String traderId, Font regular, Font bold, Font italic, Font boldItalic) {
        JButton addWishlistItemButton = new JButton("Add Item to Wishlist");
        addWishlistItemButton.setForeground(Color.CYAN);
        addWishlistItemButton.setBackground(bg);
        addWishlistItemButton.setFont(boldItalic.deriveFont(25f));
        addWishlistItemButton.setHorizontalAlignment(JButton.RIGHT);
        addWishlistItemButton.setOpaque(true);
        addWishlistItemButton.setBorderPainted(false);
        addWishlistItemButton.addActionListener(event -> {
            if (traderId.equals("")) return;
            JDialog addNewItemModal = new JDialog();
            addNewItemModal.setTitle("Add Item to Wishlist");
            addNewItemModal.setSize(500, 500);
            addNewItemModal.setResizable(false);
            addNewItemModal.setLocationRelativeTo(null);

            JPanel addNewItemPanel = new JPanel();
            addNewItemPanel.setPreferredSize(new Dimension(500, 500));
            addNewItemPanel.setBackground(bg);

            JLabel itemNameTitle = new JLabel("Trader Username:");
            itemNameTitle.setFont(italic.deriveFont(20f));
            itemNameTitle.setPreferredSize(new Dimension(450, 50));
            itemNameTitle.setOpaque(false);
            itemNameTitle.setForeground(Color.WHITE);

            JComboBox<TraderComboBoxItem> traders = new JComboBox<>();
            traders.setPreferredSize(new Dimension(450, 50));
            traders.setFont(regular.deriveFont(20f));
            traders.setBackground(gray2);
            traders.setForeground(Color.BLACK);
            traders.setOpaque(true);
            traders.addItem(null);
            userQuery.getAllTraders().forEach(id -> {
                if (!id.equals(this.traderId)) {
                    try {
                        traders.addItem(new TraderComboBoxItem(id));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            JLabel inventoryItemTitle = new JLabel("Item from their Inventory:");
            inventoryItemTitle.setFont(italic.deriveFont(20f));
            inventoryItemTitle.setPreferredSize(new Dimension(450, 50));
            inventoryItemTitle.setOpaque(false);
            inventoryItemTitle.setForeground(Color.WHITE);

            JComboBox<InventoryComboBoxItem> inventoryItems = new JComboBox<>();
            inventoryItems.setPreferredSize(new Dimension(450, 50));
            inventoryItems.setFont(regular.deriveFont(20f));
            inventoryItems.setBackground(gray2);
            inventoryItems.setForeground(Color.BLACK);
            inventoryItems.setOpaque(true);
            inventoryItems.setEnabled(false);

            JButton itemSubmitButton = new JButton("Submit Request");
            itemSubmitButton.setFont(bold.deriveFont(25f));
            itemSubmitButton.setBackground(green);
            itemSubmitButton.setOpaque(true);
            itemSubmitButton.setForeground(Color.WHITE);
            itemSubmitButton.setPreferredSize(new Dimension(225, 75));
            itemSubmitButton.setBorder(BorderFactory.createLineBorder(bg, 15));
            itemSubmitButton.addActionListener(e -> {
                if (inventoryItems.getSelectedItem() != null) {
                    try {
                        traderManager.addToWishList(traderId, ((InventoryComboBoxItem) inventoryItems.getSelectedItem()).getId());
                        addNewItemModal.dispose();
                        getWishlist();
                        wishlistItemsContainer.revalidate();
                        wishlistItemsContainer.repaint();
                        wishlistItemsScrollPane.setViewportView(wishlistItemsContainer);
                    } catch (UserNotFoundException | TradableItemNotFoundException | AuthorizationException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            traders.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if(traders.getItemAt(0) == null) traders.removeItemAt(0);
                    inventoryItems.setVisible(false);
                    inventoryItems.removeAllItems();
                    inventoryItems.setEnabled(true);
                    try {
                        for (String itemId : userQuery.getAvailableItems(((TraderComboBoxItem) e.getItem()).getId())) {
                            inventoryItems.addItem(new InventoryComboBoxItem(itemId));
                        }
                    } catch (UserNotFoundException | AuthorizationException | IOException ex) {
                        ex.printStackTrace();
                    }
                    inventoryItems.setVisible(true);
                }
            });

            addNewItemPanel.add(itemNameTitle);
            addNewItemPanel.add(traders);
            addNewItemPanel.add(inventoryItemTitle);
            addNewItemPanel.add(inventoryItems);
            addNewItemModal.add(addNewItemPanel);
            addNewItemModal.add(itemSubmitButton, BorderLayout.SOUTH);
            addNewItemModal.setModal(true);
            addNewItemModal.setVisible(true);
        });
        return addWishlistItemButton;
    }

    private JButton addInventoryItemButton(String traderId, Font regular, Font bold, Font italic, Font boldItalic) {
        JButton addInventoryItemButton = new JButton("Request Item to Inventory");
        addInventoryItemButton.setForeground(Color.CYAN);
        addInventoryItemButton.setBackground(bg);
        addInventoryItemButton.setFont(boldItalic.deriveFont(25f));
        addInventoryItemButton.setHorizontalAlignment(JButton.RIGHT);
        addInventoryItemButton.setOpaque(true);
        addInventoryItemButton.setBorderPainted(false);
        addInventoryItemButton.addActionListener(e -> {
            if (traderId.equals("")) return;
            JDialog addNewItemModal = new JDialog();
            addNewItemModal.setTitle("Add New Item");
            addNewItemModal.setSize(500, 500);
            addNewItemModal.setResizable(false);
            addNewItemModal.setLocationRelativeTo(null);

            JPanel addNewItemPanel = new JPanel();
            addNewItemPanel.setPreferredSize(new Dimension(500, 500));
            addNewItemPanel.setBackground(bg);

            JLabel itemNameTitle = new JLabel("Item Name");
            itemNameTitle.setFont(italic.deriveFont(20f));
            itemNameTitle.setPreferredSize(new Dimension(450, 50));
            itemNameTitle.setOpaque(false);
            itemNameTitle.setForeground(Color.WHITE);

            JTextField itemNameInput = new JTextField();
            itemNameInput.setFont(regular.deriveFont(20f));
            itemNameInput.setBackground(gray2);
            itemNameInput.setForeground(Color.BLACK);
            itemNameInput.setPreferredSize(new Dimension(450, 50));
            itemNameInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel itemDescTitle = new JLabel("Short Description of Item:");
            itemDescTitle.setFont(italic.deriveFont(20f));
            itemDescTitle.setPreferredSize(new Dimension(450, 50));
            itemDescTitle.setOpaque(false);
            itemDescTitle.setForeground(Color.WHITE);

            JTextField itemDescInput = new JTextField();
            itemDescInput.setFont(regular.deriveFont(20f));
            itemDescInput.setBackground(gray2);
            itemDescInput.setForeground(Color.BLACK);
            itemDescInput.setPreferredSize(new Dimension(450, 50));
            itemDescInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JButton itemSubmitButton = new JButton("Submit Request");
            itemSubmitButton.setFont(bold.deriveFont(25f));
            itemSubmitButton.setBackground(green);
            itemSubmitButton.setOpaque(true);
            itemSubmitButton.setForeground(Color.WHITE);
            itemSubmitButton.setPreferredSize(new Dimension(225, 75));
            itemSubmitButton.setBorder(BorderFactory.createLineBorder(bg, 15));
            itemSubmitButton.addActionListener(event -> {
                if (itemNameInput.getText().trim().length() > 0 && itemDescInput.getText().trim().length() > 0) {
                    try {
                        traderManager.addRequestItem(traderId, itemNameInput.getText().trim(), itemDescInput.getText().trim());
                        addNewItemModal.dispose();
                    } catch (UserNotFoundException | AuthorizationException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            addNewItemPanel.add(itemNameTitle);
            addNewItemPanel.add(itemNameInput);
            addNewItemPanel.add(itemDescTitle);
            addNewItemPanel.add(itemDescInput);

            addNewItemModal.add(addNewItemPanel);
            addNewItemModal.add(itemSubmitButton, BorderLayout.SOUTH);
            addNewItemModal.setModal(true);
            addNewItemModal.setVisible(true);
        });
        return addInventoryItemButton;
    }

    private void getInventory() throws UserNotFoundException, AuthorizationException {
        List<String> availableItems = traderId.equals("") ? new ArrayList<>() : userQuery.getAvailableItems(traderId);
        int numRows = availableItems.size();
        if (numRows < 4) numRows = 4;
        inventoryItemsContainer = new JPanel(new GridLayout(numRows, 1));
        inventoryItemsContainer.setBackground(gray2);
        inventoryItemsContainer.setBorder(null);

        for (String itemId : availableItems) {
            try {
                JPanel itemPanel = new JPanel(new GridLayout(1, 4, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));
                itemPanel.setBackground(gray);
                itemPanel.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(itemQuery.getName(itemId));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(itemQuery.getDesc(itemId));
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JLabel itemIdTitle = new JLabel("<html><pre>#" + itemId.substring(itemId.length() - 12) + "</pre></html>");
                itemIdTitle.setFont(regular.deriveFont(20f));
                itemIdTitle.setForeground(Color.BLACK);
                itemIdTitle.setHorizontalAlignment(JLabel.LEFT);

                JButton removeItemButton = new JButton("Remove");
                removeItemButton.setFont(bold.deriveFont(20f));
                removeItemButton.setForeground(Color.WHITE);
                removeItemButton.setBackground(red);
                removeItemButton.setOpaque(true);
                removeItemButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(itemIdTitle);
                itemPanel.add(removeItemButton);
                inventoryItemsContainer.add(itemPanel);

                removeItemButton.addActionListener(event -> {
                    try {
                        traderManager.removeFromInventory(traderId, itemId);
                        inventoryItemsContainer.remove(itemPanel);
                        inventoryItemsContainer.revalidate();
                        inventoryItemsContainer.repaint();
                    } catch (UserNotFoundException | AuthorizationException e) {
                        e.printStackTrace();
                    }
                });

            } catch (TradableItemNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void getWishlist() throws UserNotFoundException, AuthorizationException {
        List<String> wishlist = traderId.equals("") ? new ArrayList<>() : userQuery.getWishlist(traderId);
        int numRows = wishlist.size();
        if (numRows < 4) numRows = 4;
        wishlistItemsContainer = new JPanel(new GridLayout(numRows, 1));
        wishlistItemsContainer.setBackground(gray2);
        wishlistItemsContainer.setBorder(null);

        for (String itemId : wishlist) {
            try {
                JPanel itemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));
                itemPanel.setBackground(gray);
                itemPanel.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(itemQuery.getName(itemId));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(itemQuery.getDesc(itemId));
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JLabel itemIdTitle = new JLabel("<html><pre>#" + itemId.substring(itemId.length() - 12) + "</pre></html>");
                itemIdTitle.setFont(regular.deriveFont(20f));
                itemIdTitle.setForeground(Color.BLACK);
                itemIdTitle.setHorizontalAlignment(JLabel.LEFT);

                JLabel itemOwnerName = new JLabel(userQuery.getUsername(userQuery.getTraderThatHasTradableItemId(itemId)));
                itemOwnerName.setFont(regular.deriveFont(20f));
                itemOwnerName.setForeground(Color.BLACK);
                itemOwnerName.setHorizontalAlignment(JLabel.CENTER);

                JButton removeItemButton = new JButton("Remove");
                removeItemButton.setFont(bold.deriveFont(20f));
                removeItemButton.setForeground(Color.WHITE);
                removeItemButton.setBackground(red);
                removeItemButton.setOpaque(true);
                removeItemButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(itemIdTitle);
                itemPanel.add(itemOwnerName);
                itemPanel.add(removeItemButton);
                wishlistItemsContainer.add(itemPanel);

                removeItemButton.addActionListener(event -> {
                    try {
                        traderManager.removeFromWishList(traderId, itemId);
                        wishlistItemsContainer.remove(itemPanel);
                        wishlistItemsContainer.revalidate();
                        wishlistItemsContainer.repaint();
                    } catch (UserNotFoundException | AuthorizationException e) {
                        e.printStackTrace();
                    }
                });

            } catch (TradableItemNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }
}
