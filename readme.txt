INITIAL ADMIN:
    username: admin
    password: adminPassword1

INITIAL TRADERS:
    username: Ilan
    password: bestTrader2020

    username: James
    password: 0verlyStr0ngP4ss

    username: Navinn
    password: 123GUIsensei

    username: William
    password: valorantPRO1

    username: Andrew
    password: superSearcher3000

    username: Nilay
    password: 1234yaliN5678

    username: Clara
    password: AW0nderfulP4ss

    username: Morteza
    password: designMaster100


To run the program, run the Main class in src -> frontend -> Main.

A note:
Running the GUI may sometimes appear flickering, that's because every time there is any update,
the GUI refreshes to show the latest version.


THE TRADING PROCESS:
    1. Register (or login with) a user, request items in the user, and accept items in the admin

    2. Trade with other users (send trade requests). Feel free to use the trade suggestion buttons to see what the
       program considers the "best" trade between you and another user.

    3. Have the other users accept/edit/deny trades. If a user decides to edit a trade, then the other trader involved
       in that trade will have to decide whether to accept/edit/deny, and so on.

    4. Confirm trades that are accepted (NOTE: temporary trades need to be confirmed twice, one for the first meeting and another time for
    the second)

    5. You automatically receive your items once all of the meetings of the trade have been confirmed

    Some More notes:
        - Lending to one user does NOT count as that other user borrowing (or vice versa).
        - For a trade to count towards your borrows/lends, you must be the one who originally sent the trade request
          (i.e. some other trader editing a trade still makes it count towards the original trader's borrow/lend count,
          provided that it's still a borrow/lend)
        - You can only ever borrow once the count of amount lent minus amount borrowed surpasses the limit set by
          an admin (default limit is 1)
        - You can only trade so much in a week (the default trade limit is 10)
        - You can only have so many ongoing trades (incomplete trades) at one time before an admin may freeze your account (the
          default limit is 3 ongoing trades, so any more trades means you risk being frozen). This does not happen automatically,
          so as long as you keep your ongoing trade count below the limit before an admin sees, you will be safe.
          A frozen account is an account that cannot do most actions, except changing their username, password, city, or reporting a user.
          A frozen account can also send an un-freeze request to an admin.

The following section will explain the purpose and usage of each section of the GUI and is meant to guide new users
through our design.

TRADES:
This section of the GUI consists of:
    1. Ongoing trades. These are trades that you have started, but have not yet ended. You can look at the details of
       the trade, such as the items each trader offered, and accept each trade. If you wish to cancel an accepted trade,
       you must ask the admin.
    2. Trade Requests. These are trades that others would like to conduct with you. You can edit the trade, sending back
       a modified version of this trade offer, accept the trade, or deny the trade. To request a trade with another user
       (or have the program suggest to you a possible trade), click the corresponding buttons on the top of the screen.


ITEMS:
This section of the GUI consists of:
    1. Your inventory. These are items that are currently available for trade. Other traders will be able to see these
       items in your inventory.
    2. Your wishlist. These are items that you wish to have. Adding items here not only tells other traders what items
       you would prefer, but allows for the automated trade suggestion to pick out the best trade they can find.


NOTIFICATIONS:
This section of the GUI consists of:
    1. Messages you have with other users. You can reply/delete messages when you get them and have full conversations
       with other users.
    2. Information related to you. This includes your most recently traded items (both received and sent), and the
       top 3 traders you most frequently traded with.


SEARCH:
This section of the GUI consists of:
    1. Trader search. You can search for any specific trader here. Clicking on the "details" button will display
       useful information regarding that trader, including their reviews.
    2. Tradable item search. Here you can search for any item, and decide whether you want to add it to your wishlist

SETTINGS:
This section of the GUI consists of:
    1. Changing your username.
    2. Changing your password.
    3. Changing your city.
    4. Entering idle mode. Idle mode is a mode in which you become unable to trade, or be sent trade requests.
    5. Reporting a trader. If some trader has conducted a serious offense, we encourage you to submit a report. All admins
       see this report, and can decide on a course of action given the information in the report.


Admin usage:

UNDOING ONGOING TRADES:
An admin can decide to undo an ongoing trade. This removes the trade and returns the items back to their original holders.

INFILTRADE:
An admin can search for traders and then "infiltrade" an account. This lets them take control of a user and do all the actions
the user could normally be able to do. This gives admin a greater ability to control user actions.





Citations:

All code is referenced from oracle documentation, and any other code that was directly taken from or used as
inspiration has been attributed in the classes that use them.

