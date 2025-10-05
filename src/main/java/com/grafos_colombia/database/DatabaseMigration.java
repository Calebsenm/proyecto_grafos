package com.grafos_colombia.database;

import org.flywaydb.core.Flyway;

public class DatabaseMigration {

    private static String dbUrl;

    public static void init() {
        try {
            // 1. Get database URL from the central connection manager
            dbUrl = DatabaseConnection.getInstance().getDbUrl();
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                throw new IllegalStateException("Database URL is not configured.");
            }

            // 2. Run Flyway migrations
            Flyway flyway = Flyway.configure()
                    .dataSource(dbUrl, null, null)
                    .locations("filesystem:target/classes/db/migration")
                    .loggers("slf4j") 
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();

            System.out.println("✅ Database migrations checked and applied successfully.");

        } catch (Exception e) {
            throw new RuntimeException("❌ Critical error during database migration: " + e.getMessage(), e);
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }
}
