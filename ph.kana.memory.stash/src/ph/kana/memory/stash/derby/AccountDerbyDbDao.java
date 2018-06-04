package ph.kana.memory.stash.derby;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.CorruptDataException;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.type.SortColumn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class AccountDerbyDbDao implements AccountDao {

	private DerbyDbConnection dbConnection = null;

	@Override
	public List<Account> fetchAll(SortColumn sortColumn) throws CorruptDataException, StashException {
		try {
			var connection = fetchConnection();
			var order = determineOrderBy(sortColumn);
			var statement = connection
				.prepareStatement("SELECT id, domain, username, password, timestamp FROM accounts ORDER BY " + order);
			return queryAndTransformAccounts(statement);
		} catch (SQLException e) {
			throw new StashException(e);
		}
	}

	@Override
	public List<Account> findAccounts(String searchString) throws CorruptDataException, StashException {
		try {
			var connection = fetchConnection();
			var statement = connection
				.prepareStatement("SELECT id, domain, username, password, timestamp FROM accounts WHERE domain LIKE ? OR username LIKE ?");

			String likeParameter = String.format("%%%s%%", searchString);
			statement.setString(1, likeParameter);
			statement.setString(2, likeParameter);

			return queryAndTransformAccounts(statement);
		} catch (SQLException e) {
			throw new StashException(e);
		}
	}

	@Override
	public Account save(Account account) throws CorruptDataException, StashException {
		try {
			var connection = fetchConnection();
			var searchStatement = connection
					.prepareStatement("SELECT id FROM accounts WHERE id = ?");
			searchStatement.setString(1, account.getId());
			var result = searchStatement.executeQuery();

			var statement = (result.next())?
				createUpdateStatement(connection, account):
				createInsertStatement(connection, account);

			statement.executeUpdate();
			return account;
		} catch (SQLException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void delete(Account account) throws CorruptDataException, StashException {
		try {
			var connection = fetchConnection();
			var statement = connection
					.prepareStatement("DELETE FROM accounts WHERE id = ?");
			statement.setString(1, account.getId());

			statement.executeUpdate();
		} catch (SQLException e) {
			throw new StashException(e);
		}
	}

	@Override
	public boolean anyExists() throws CorruptDataException, StashException {
		try {
			var connection = fetchConnection();
			var result = connection
					.createStatement()
					.executeQuery("SELECT count(1) FROM accounts");

			if (result.next()) {
				var count = result.getInt(1);
				return count > 0;
			}
			return false;
		} catch (SQLException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void deleteAll() throws StashException {
		try {
			DerbyDbConnection.deleteDbFile();
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	private Connection fetchConnection() throws CorruptDataException, SQLException {
		try {
			if (null == dbConnection) {
				dbConnection = DerbyDbConnection.getInstance();
			}
			return dbConnection.getSqlConnection();
		} catch (SQLException e) {
			if (e.getMessage().endsWith("Reason: Invalid authentication..")) {
				throw new CorruptDataException("Derby DB failed", e);
			} else {
				throw e;
			}
		}
	}

	private List<Account> queryAndTransformAccounts(PreparedStatement statement) throws SQLException {
		var results = statement.executeQuery();

		var accounts = new ArrayList<Account>();
		while (results.next()) {
			var account = new Account();
			account.setId(results.getString("id"));
			account.setDomain(results.getString("domain"));
			account.setUsername(results.getString("username"));
			account.setPasswordFile(results.getString("password"));
			account.setSaveTimestamp(results.getTimestamp("timestamp").getTime());

			accounts.add(account);
		}

		return accounts;
	}

	private PreparedStatement createUpdateStatement(Connection connection, Account account) throws SQLException {
		var statement = connection
				.prepareStatement("UPDATE accounts SET domain = ?, username = ?, password = ?, timestamp =? WHERE id = ?");
		statement.setString(1, account.getDomain());
		statement.setString(2, account.getUsername());
		statement.setString(3, account.getPasswordFile());
		statement.setTimestamp(4, new Timestamp(account.getSaveTimestamp()));
		statement.setString(5, account.getId());
		return statement;
	}

	private PreparedStatement createInsertStatement(Connection connection, Account account) throws SQLException {
		var statement = connection
				.prepareStatement("INSERT INTO accounts(id, domain, username, password, timestamp) VALUES (?, ?, ?, ?, ?)");
		statement.setString(1, account.getId());
		statement.setString(2, account.getDomain());
		statement.setString(3, account.getUsername());
		statement.setString(4, account.getPasswordFile());
		statement.setTimestamp(5, new Timestamp(account.getSaveTimestamp()));
		return statement;
	}

	private String determineOrderBy(SortColumn sortColumn) {
		return (SortColumn.DOMAIN == sortColumn)? "domain":
				(SortColumn.USERNAME == sortColumn)? "username":
				"timestamp";
	}
}
