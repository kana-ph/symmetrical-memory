package ph.kana.memory.stash.derby;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Comparator;
import java.util.Properties;

import static ph.kana.memory.stash.file.FileStoreConstants.LOCKER_ROOT;

final class DerbyDbConnection {

	private final Connection sqlConnection;

	private static DerbyDbConnection instance = null;

	private final static String DB_FILE = LOCKER_ROOT + "/d";
	private final static String DB_CONNECTION_STRING = "jdbc:derby:" + DB_FILE;

	private final static String DB_USERNAME = "LOCKER";
	private final static String DB_PASSWORD = Base64.getEncoder()
		.encodeToString(String.format("%s,%s",System.getProperty("os.name"), DB_FILE).getBytes());

	public static DerbyDbConnection getInstance() throws SQLException {
		if (null == instance) {
			instance = new DerbyDbConnection();
			ensureDatabaseSettings();
		}
		return instance;
	}

	static void deleteDbFile() throws IOException {
		Files.walk(new File(DB_FILE).toPath())
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
	}

	private static void ensureDatabaseSettings() throws SQLException {
		var connection = instance.getSqlConnection();

		var dbMetaData = connection.getMetaData();
		var tableQuery = dbMetaData.getTables(null, "APP", "ACCOUNTS", null);

		if (!tableQuery.next()) {
			var statement = connection.createStatement();
			setupDatabaseProperties(statement);
			setupDatabaseTables(statement);
		}
	}

	private static void setupDatabaseProperties(Statement statement) throws SQLException {
		statement.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication', 'true')");
		statement.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.authentication.provider', 'BUILTIN')");
		statement.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user." + DB_USERNAME + "', '" + DB_PASSWORD + "')");
		statement.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.propertiesOnly', 'true')");
	}


	private static void setupDatabaseTables(Statement statement) throws SQLException {
		statement.executeUpdate("CREATE TABLE app.accounts (" +
				"id VARCHAR(36) NOT NULL PRIMARY KEY, " +
				"domain VARCHAR(255) NOT NULL, " +
				"username VARCHAR(255) NOT NULL, " +
				"password VARCHAR(255) NOT NULL, " +
				"save_timestamp TIMESTAMP NOT NULL, " +
				"last_update_timestamp TIMESTAMP NOT NULL)");
		statement.executeUpdate("CREATE INDEX domain_idx ON app.accounts (domain)");
		statement.executeUpdate("CREATE INDEX username_idx ON app.accounts (username)");
	}

	private DerbyDbConnection() throws SQLException {
		var connectionProperties = new Properties();
		connectionProperties.setProperty("create", "true");
		connectionProperties.setProperty("user", DB_USERNAME);
		connectionProperties.setProperty("password", DB_PASSWORD);

		sqlConnection = DriverManager.getConnection(DB_CONNECTION_STRING, connectionProperties);
		sqlConnection.setSchema("APP");
	}

	Connection getSqlConnection() {
		return sqlConnection;
	}
}
