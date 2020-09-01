package backend;


/**
 * For paths to database files
 */
public enum DatabaseFilePaths {
    /**
     * For the file path of all users
     */
    USER("./src/backend/databasefiles/users.ser"),
    /**
     * file path of all trades
     */
    TRADE("./src/backend/databasefiles/trades.ser"),
    /**
     * file path for all items that are traded
     */
    TRADABLE_ITEM("./src/backend/databasefiles/tradableitems.ser"),

    /**
     * file path for trader config file
     */
    TRADER_CONFIG("./src/backend/tradesystem/trader.properties", true);
    private final String FILE_PATH;
    private final boolean IS_CONFIG;

    /**
     * Makes a new database file path
     * @param filePath the file path
     */
    DatabaseFilePaths(String filePath) {
        this.FILE_PATH = filePath;
        IS_CONFIG = false;
    }

    /**
     * Makes a new database file path
     * @param filePath the file path
     * @param isConfig whether the file is a config file
     */
    DatabaseFilePaths(String filePath, boolean isConfig) {
        this.FILE_PATH = filePath;
        IS_CONFIG = isConfig;
    }

    /**
     * Gets the file path
     *
     * @return file path
     */
    public String getFilePath() {
        return FILE_PATH;
    }

    public boolean isConfig() {
        return IS_CONFIG;
    }
}
