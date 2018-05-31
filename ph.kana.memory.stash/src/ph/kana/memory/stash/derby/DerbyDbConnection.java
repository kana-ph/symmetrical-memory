package ph.kana.memory.stash.derby;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ph.kana.memory.stash.file.FileStoreConstants.LOCKER_ROOT;

final class DerbyDbConnection {

	private final Connection sqlConnection;

	private static DerbyDbConnection instance = null;
	private final static String DB_FILE = LOCKER_ROOT + "/d";
	private final static String DB_CONNECTION_STRING = "jdbc:derby:" + DB_FILE  + ";create=true";

	public static DerbyDbConnection getInstance() throws SQLException {
		if (null == instance) {
			instance = new DerbyDbConnection();
			ensureDatabase();
		}
		return instance;
	}

	static void deleteDbFile() throws IOException {
		Files.delete(new File(DB_FILE).toPath());
	}

	private static void ensureDatabase() throws SQLException {
		var connection = instance.getSqlConnection();

		var dbMetaData = connection.getMetaData();
		var tableQuery = dbMetaData.getTables(null, "APP", "ACCOUNTS", null);

		if (!tableQuery.next()) {
			var statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE accounts (id VARCHAR(64) NOT NULL PRIMARY KEY, domain VARCHAR(255) NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, timestamp TIMESTAMP NOT NULL)");
			statement.executeUpdate("CREATE INDEX domain_idx ON accounts (domain)");
			statement.executeUpdate("CREATE INDEX username_idx ON accounts (username)");
		}
	}


	private DerbyDbConnection() throws SQLException {
		sqlConnection = DriverManager.getConnection(DB_CONNECTION_STRING);
	}

	Connection getSqlConnection() {
		return sqlConnection;
	}
}
