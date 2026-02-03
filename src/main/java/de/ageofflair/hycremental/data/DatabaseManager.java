package de.ageofflair.hycremental.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Database Manager - Handles all database connections and operations
 * Uses HikariCP for connection pooling
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class DatabaseManager {
    
    private static final Logger logger = Logger.getLogger("Hycremental-DB");
    
    private HikariDataSource dataSource;
    private boolean connected = false;
    
    // Database Configuration (TODO: Load from config.yml)
    private String host = "localhost";
    private int port = 5432;
    private String database = "hycremental";
    private String username = "postgres";
    private String password = "password";
    
    // Connection Pool Settings
    private int maximumPoolSize = 10;
    private int minimumIdle = 2;
    private long connectionTimeout = 30000;
    private long idleTimeout = 600000;
    private long maxLifetime = 1800000;
    
    /**
     * Connect to the database and initialize connection pool
     */
    public void connect() {
        try {
            logger.info("Initializing database connection pool...");
            
            // Configure HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
            
            // Pool Configuration
            config.setMaximumPoolSize(maximumPoolSize);
            config.setMinimumIdle(minimumIdle);
            config.setConnectionTimeout(connectionTimeout);
            config.setIdleTimeout(idleTimeout);
            config.setMaxLifetime(maxLifetime);
            
            // Performance Settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            // Connection Pool Name
            config.setPoolName("Hycremental-Pool");
            
            // Create DataSource
            this.dataSource = new HikariDataSource(config);
            this.connected = true;
            
            logger.info("Database connection pool initialized successfully!");
            logger.info("Pool: " + maximumPoolSize + " max connections, " + minimumIdle + " minimum idle");
            
            // Initialize database schema
            initializeSchema();
            
        } catch (Exception e) {
            logger.severe("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            this.connected = false;
        }
    }
    
    /**
     * Disconnect from the database and close connection pool
     */
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing database connection pool...");
            dataSource.close();
            this.connected = false;
            logger.info("Database connection pool closed successfully!");
        }
    }
    
    /**
     * Get a connection from the pool
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (!connected || dataSource == null) {
            throw new SQLException("Database is not connected!");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Initialize database schema if it doesn't exist
     */
    private void initializeSchema() {
        logger.info("Checking database schema...");
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create players table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS players (" +
                "    uuid VARCHAR(36) PRIMARY KEY," +
                "    username VARCHAR(16) NOT NULL," +
                "    essence DECIMAL(65,2) DEFAULT 0," +
                "    gems BIGINT DEFAULT 0," +
                "    crystals INT DEFAULT 0," +
                "    prestige_level INT DEFAULT 0," +
                "    ascension_level INT DEFAULT 0," +
                "    rebirth_count INT DEFAULT 0," +
                "    lifetime_essence DECIMAL(65,2) DEFAULT 0," +
                "    total_blocks_mined BIGINT DEFAULT 0," +
                "    generators_purchased BIGINT DEFAULT 0," +
                "    prestige_count BIGINT DEFAULT 0," +
                "    island_id VARCHAR(36)," +
                "    island_size INT DEFAULT 20," +
                "    generator_slots INT DEFAULT 10," +
                "    first_join BIGINT NOT NULL," +
                "    last_login BIGINT NOT NULL," +
                "    last_save BIGINT NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Create generators table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS generators (" +
                "    id BIGSERIAL PRIMARY KEY," +
                "    owner_uuid VARCHAR(36) NOT NULL," +
                "    island_id VARCHAR(36) NOT NULL," +
                "    type VARCHAR(50) NOT NULL," +
                "    tier INT NOT NULL," +
                "    level INT DEFAULT 1," +
                "    quality VARCHAR(20) DEFAULT 'COMMON'," +
                "    position_x INT NOT NULL," +
                "    position_y INT NOT NULL," +
                "    position_z INT NOT NULL," +
                "    world VARCHAR(50) NOT NULL," +
                "    enchantments TEXT," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE" +
                ")"
            );
            
            // Create islands table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS islands (" +
                "    id VARCHAR(36) PRIMARY KEY," +
                "    owner_uuid VARCHAR(36) NOT NULL," +
                "    world_name VARCHAR(50) NOT NULL," +
                "    center_x INT NOT NULL," +
                "    center_y INT NOT NULL," +
                "    center_z INT NOT NULL," +
                "    size INT DEFAULT 20," +
                "    generator_slots INT DEFAULT 10," +
                "    members TEXT," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE" +
                ")"
            );
            
            // Create transactions table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "    id BIGSERIAL PRIMARY KEY," +
                "    seller_uuid VARCHAR(36)," +
                "    buyer_uuid VARCHAR(36)," +
                "    item_type VARCHAR(50) NOT NULL," +
                "    item_data TEXT," +
                "    price DECIMAL(65,2) NOT NULL," +
                "    currency VARCHAR(20) NOT NULL," +
                "    timestamp BIGINT NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Create leaderboards cache table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS leaderboards (" +
                "    rank INT NOT NULL," +
                "    category VARCHAR(50) NOT NULL," +
                "    player_uuid VARCHAR(36) NOT NULL," +
                "    value DECIMAL(65,2) NOT NULL," +
                "    season VARCHAR(20) NOT NULL," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    PRIMARY KEY (category, season, rank)" +
                ")"
            );
            
            // Create indexes for performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_generators_owner ON generators(owner_uuid)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_generators_island ON generators(island_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_seller ON transactions(seller_uuid, timestamp)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_buyer ON transactions(buyer_uuid, timestamp)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_leaderboards_category ON leaderboards(category, season)");
            
            logger.info("Database schema initialized successfully!");
            
        } catch (SQLException e) {
            logger.severe("Failed to initialize database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Execute a prepared statement update (INSERT, UPDATE, DELETE)
     * @param sql SQL query
     * @param params Query parameters
     * @return Number of affected rows
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Check if database is connected
     * @return true if connected
     */
    public boolean isConnected() {
        return connected && dataSource != null && !dataSource.isClosed();
    }
    
    /**
     * Get connection pool statistics
     * @return Pool stats string
     */
    public String getPoolStats() {
        if (dataSource == null) {
            return "Database not connected";
        }
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
    
    // Getters for configuration (TODO: Load from config file)
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public void setDatabase(String database) {
        this.database = database;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
