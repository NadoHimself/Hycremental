-- Hycremental Database Schema
-- PostgreSQL / MySQL Compatible

-- Players Table
CREATE TABLE IF NOT EXISTS players (
    uuid VARCHAR(36) PRIMARY KEY,
    username VARCHAR(16) NOT NULL,
    essence DECIMAL(65, 2) DEFAULT 1000.00,
    gems BIGINT DEFAULT 0,
    crystals INT DEFAULT 0,
    prestige_level INT DEFAULT 0,
    ascension_level INT DEFAULT 0,
    rebirth_count INT DEFAULT 0,
    total_essence_earned DECIMAL(65, 2) DEFAULT 0,
    blocks_mined BIGINT DEFAULT 0,
    first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_save TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_prestige (prestige_level),
    INDEX idx_last_login (last_login)
);

-- Islands Table
CREATE TABLE IF NOT EXISTS islands (
    id VARCHAR(36) PRIMARY KEY,
    owner_uuid VARCHAR(36) NOT NULL,
    name VARCHAR(32),
    size INT DEFAULT 20,
    generator_slots INT DEFAULT 10,
    member_slots INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE,
    INDEX idx_owner (owner_uuid)
);

-- Island Members Table
CREATE TABLE IF NOT EXISTS island_members (
    island_id VARCHAR(36) NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    role ENUM('OWNER', 'ADMIN', 'MEMBER', 'VISITOR') DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (island_id, player_uuid),
    FOREIGN KEY (island_id) REFERENCES islands(id) ON DELETE CASCADE,
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE
);

-- Generators Table
CREATE TABLE IF NOT EXISTS generators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_uuid VARCHAR(36) NOT NULL,
    island_id VARCHAR(36) NOT NULL,
    type ENUM('STONE', 'COAL', 'IRON', 'GOLD', 'DIAMOND', 'EMERALD', 
              'NETHERITE', 'ESSENCE_CRYSTAL', 'QUANTUM', 'REALITY_FORGE', 
              'VOID_REACTOR', 'DIVINE_NEXUS') NOT NULL,
    tier INT NOT NULL,
    level INT DEFAULT 1,
    quality ENUM('COMMON', 'UNCOMMON', 'RARE', 'EPIC', 'LEGENDARY') DEFAULT 'COMMON',
    position_x INT NOT NULL,
    position_y INT NOT NULL,
    position_z INT NOT NULL,
    enchantments JSON,
    total_produced DECIMAL(65, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_tick TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE,
    FOREIGN KEY (island_id) REFERENCES islands(id) ON DELETE CASCADE,
    INDEX idx_owner (owner_uuid),
    INDEX idx_island (island_id),
    INDEX idx_type (type)
);

-- Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_uuid VARCHAR(36),
    buyer_uuid VARCHAR(36),
    item_type VARCHAR(50) NOT NULL,
    item_data JSON,
    price DECIMAL(65, 2) NOT NULL,
    currency ENUM('ESSENCE', 'GEMS', 'CRYSTALS') NOT NULL,
    transaction_type ENUM('MARKET', 'TRADE', 'SHOP', 'ADMIN') DEFAULT 'MARKET',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_seller (seller_uuid, timestamp),
    INDEX idx_buyer (buyer_uuid, timestamp),
    INDEX idx_timestamp (timestamp)
);

-- Leaderboards Cache Table
CREATE TABLE IF NOT EXISTS leaderboards (
    rank INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    player_name VARCHAR(16) NOT NULL,
    value DECIMAL(65, 2) NOT NULL,
    season VARCHAR(20) DEFAULT 'all-time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (category, season, rank),
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE,
    INDEX idx_category (category, season),
    INDEX idx_player (player_uuid)
);

-- Achievements Table
CREATE TABLE IF NOT EXISTS player_achievements (
    player_uuid VARCHAR(36) NOT NULL,
    achievement_id VARCHAR(50) NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_uuid, achievement_id),
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE,
    INDEX idx_player (player_uuid)
);

-- Ascension Perks Table
CREATE TABLE IF NOT EXISTS ascension_perks (
    player_uuid VARCHAR(36) NOT NULL,
    perk_type ENUM('ESSENCE_MASTERY', 'GENERATOR_EFFICIENCY', 'SPEED_DEMON',
                   'ISLAND_AUTHORITY', 'DIVINE_FAVOR', 'VOID_WALKER',
                   'FUSION_MASTER', 'ECONOMY_KING') NOT NULL,
    level INT DEFAULT 0,
    PRIMARY KEY (player_uuid, perk_type),
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE
);

-- Daily Rewards Tracking
CREATE TABLE IF NOT EXISTS daily_rewards (
    player_uuid VARCHAR(36) PRIMARY KEY,
    last_claim TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    streak INT DEFAULT 1,
    total_claims INT DEFAULT 1,
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE
);

-- Event Participation Table
CREATE TABLE IF NOT EXISTS event_participation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_uuid VARCHAR(36) NOT NULL,
    event_id VARCHAR(50) NOT NULL,
    score DECIMAL(65, 2) DEFAULT 0,
    rank INT,
    rewards_claimed BOOLEAN DEFAULT FALSE,
    participated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE,
    INDEX idx_event (event_id, score DESC),
    INDEX idx_player (player_uuid)
);

-- Create Views for Common Queries
CREATE OR REPLACE VIEW player_stats AS
SELECT 
    p.uuid,
    p.username,
    p.essence,
    p.prestige_level,
    p.ascension_level,
    COUNT(DISTINCT g.id) as total_generators,
    SUM(g.total_produced) as lifetime_production,
    i.size as island_size
FROM players p
LEFT JOIN islands i ON p.uuid = i.owner_uuid
LEFT JOIN generators g ON p.uuid = g.owner_uuid
GROUP BY p.uuid, i.size;

-- Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_generators_active ON generators(island_id, last_tick);
CREATE INDEX IF NOT EXISTS idx_transactions_recent ON transactions(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_leaderboards_rank ON leaderboards(category, season, rank);