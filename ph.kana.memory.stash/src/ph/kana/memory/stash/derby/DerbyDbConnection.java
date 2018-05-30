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
		}
		// TODO check if no database, initialize if needed
		return instance;
	}

	public static void deleteDbFile() throws IOException {
		Files.delete(new File(DB_FILE).toPath());
	}

	private DerbyDbConnection() throws SQLException {
		sqlConnection = DriverManager.getConnection(DB_CONNECTION_STRING);
	}

	public Connection getSqlConnection() {
		return sqlConnection;
	}
}
