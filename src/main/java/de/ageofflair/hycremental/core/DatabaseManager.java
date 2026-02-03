package de.ageofflair.hycremental.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.ageofflair.hycremental.Hycremental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Manages database connections and operations
 * Uses HikariCP for connection pooling
 * 
 * @author Kielian
 */
public class DatabaseManager {
    
    private final Hycremental plugin;
    private final Logger logger;
    
    private HikariDataSource dataSource;
    private boolean connected = false;
    
    public DatabaseManager(Hycremental plugin) {
        this.plugin = plugin;
        this.logger = plugin.getPluginLogger();
    }
    
    /**
     * Connect to the database
     */
    public void connect() {
        logger.info("Connecting to database...");
        
        try {
            HikariConfig config = new HikariConfig();
            
            // TODO: Load from config.yml
            String dbType = "postgresql"; // or mysql
            String host = "localhost";
            int port = 5432;
            String database = "hycremental";
            String username = "hycremental_user";
            String password = "change_me_please";
            
            // JDBC URL
            String jdbcUrl;
            if (dbType.equalsIgnoreCase("mysql")) {
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
            } else {
                config.setDriverClassName("org.postgresql.Driver");
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            }
            
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            this.dataSource = new HikariDataSource(config);
            this.connected = true;
            
            logger.info("Database connected successfully!");
            
        } catch (Exception e) {
            logger.severe("Failed to connect to database!");
            e.printStackTrace();
            this.connected = false;
        }
    }
    
    /**
     * Disconnect from database
     */
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing database connection...");
            dataSource.close();
            connected = false;
            logger.info("Database disconnected.");
        }
    }
    
    /**
     * Get a connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (!connected || dataSource == null) {
            throw new SQLException("Database is not connected!");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Create all necessary database tables
     */
    public void createTables() {
        logger.info("Creating database tables...");
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Players table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS players (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "username VARCHAR(16) NOT NULL," +
                "essence DECIMAL(65, 2) DEFAULT 1000.00," +
                "gems BIGINT DEFAULT 0," +
                "crystals INT DEFAULT 0," +
                "prestige_level INT DEFAULT 0," +
                "ascension_level INT DEFAULT 0," +
                "rebirth_count INT DEFAULT 0," +
                "total_essence_earned DECIMAL(65, 2) DEFAULT 0," +
                "blocks_mined BIGINT DEFAULT 0," +
                "first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_save TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            logger.info(" - Created/verified 'players' table");
            
            // Islands table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS islands (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "owner_uuid VARCHAR(36) NOT NULL," +
                "name VARCHAR(32)," +
                "size INT DEFAULT 20," +
                "generator_slots INT DEFAULT 10," +
                "member_slots INT DEFAULT 1," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            logger.info(" - Created/verified 'islands' table");
            
            // Generators table (simplified for now)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS generators (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "owner_uuid VARCHAR(36) NOT NULL," +
                "island_id VARCHAR(36) NOT NULL," +
                "type VARCHAR(50) NOT NULL," +
                "tier INT NOT NULL," +
                "level INT DEFAULT 1," +
                "quality VARCHAR(20) DEFAULT 'COMMON'," +
                "position_x INT NOT NULL," +
                "position_y INT NOT NULL," +
                "position_z INT NOT NULL," +
                "total_produced DECIMAL(65, 2) DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            logger.info(" - Created/verified 'generators' table");
            
            logger.info("All tables created/verified successfully!");
            
        } catch (SQLException e) {
            logger.severe("Failed to create tables!");
            e.printStackTrace();
        }
    }
    
    /**
     * Execute an update query (INSERT, UPDATE, DELETE)
     */
    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.severe("Failed to execute update: " + sql);
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Check if database is connected
     */
    public boolean isConnected() {
        return connected && dataSource != null && !dataSource.isClosed();
    }
}