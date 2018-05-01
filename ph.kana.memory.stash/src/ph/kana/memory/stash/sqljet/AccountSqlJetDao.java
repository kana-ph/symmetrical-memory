package ph.kana.memory.stash.sqljet;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.StashException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountSqlJetDao implements AccountDao {

	private SqlJetDb sqlJetDb;

	private final static String TABLE_NAME = "accounts";
	private final static String CREATE_TABLE_SQL =
		"CREATE TABLE IF NOT EXISTS accounts (id TEXT NOT NULL PRIMARY KEY, domain TEXT NOT NULL, username TEXT NOT NULL, password TEXT NOT NULL, timestamp INTEGER NOT NULL)";
	private final static String ID_INDEX_NAME = "accounts_pkey";
	private final static String CREATE_ID_INDEX_SQL = "CREATE INDEX IF NOT EXISTS accounts_pkey ON accounts(id)";

	public AccountSqlJetDao() {
		try {
			sqlJetDb = SqlJetStoreConstants.getConnection();
			sqlJetDb.runWriteTransaction(db -> {
				db.createTable(CREATE_TABLE_SQL);
				db.createIndex(CREATE_ID_INDEX_SQL);
				return null;
			});
		} catch (SqlJetException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	@Override
	public List<Account> fetchAll() throws StashException {
		try {
			@SuppressWarnings("unchecked")
			List<Account> allAccounts = (List) sqlJetDb.runReadTransaction(db -> {
				ISqlJetTable table = db.getTable(TABLE_NAME);
				ISqlJetCursor cursor = table.order(ID_INDEX_NAME);

				List<Account> accounts = new ArrayList<>();
				if (!cursor.eof()) {
					do {
						Account account = new Account();
						account.setId(cursor.getString("id"));
						account.setDomain(cursor.getString("domain"));
						account.setUsername(cursor.getString("username"));
						account.setEncryptedPassword(cursor.getString("password"));
						account.setSaveTimestamp(cursor.getInteger("timestamp"));
						accounts.add(account);
					} while (cursor.next());
				}
				return accounts;
			});
			return allAccounts;
		} catch (SqlJetException e) {
			throw new StashException(e);
		}
	}

	@Override
	public Account save(Account account) throws StashException {
		try {
			return (Account) sqlJetDb.runWriteTransaction(db -> {
				ISqlJetTable table = db.getTable(TABLE_NAME);
				ISqlJetCursor cursor = table.lookup(ID_INDEX_NAME, account.getId());
				cursor.setLimit(1L);

				Map<String, Object> data = Map.of(
					"id", account.getId(),
					"domain", account.getDomain(),
					"username", account.getUsername(),
					"password", account.getEncryptedPassword(),
					"timestamp", account.getSaveTimestamp()
				);

				if (cursor.eof()) {
					table.insertByFieldNames(data);
				} else {
					cursor.updateByFieldNames(data);
				}
				return account;
			});
		} catch (SqlJetException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void delete(Account account) throws StashException {
		try {
			sqlJetDb.runWriteTransaction(db -> {
				ISqlJetTable table = db.getTable(TABLE_NAME);
				ISqlJetCursor cursor = table.lookup(ID_INDEX_NAME, account.getId());
				cursor.setLimit(1L);

				if (!cursor.eof()) {
					cursor.delete();
				}
				return null;
			});
			account.setId(null);
		} catch (SqlJetException e) {
			throw new StashException(e);
		}
	}

	@Override
	public boolean anyExists() throws StashException {
		try {
			return (boolean) sqlJetDb.runReadTransaction(db -> {
				ISqlJetTable table = db.getTable(TABLE_NAME);
				ISqlJetCursor cursor = table.order(ID_INDEX_NAME);
				return cursor.getRowCount() > 0L;
			});
		} catch (SqlJetException e) {
			throw new StashException(e);
		}
	}
}
