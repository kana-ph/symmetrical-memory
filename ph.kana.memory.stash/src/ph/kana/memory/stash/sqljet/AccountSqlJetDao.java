package ph.kana.memory.stash.sqljet;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ph.kana.memory.stash.sqljet.SqlJetStoreConstants.DB_PATH;

@Deprecated(forRemoval = true)
public class AccountSqlJetDao implements AccountDao {

	private SqlJetDb sqlJetDb;

	private final static String TABLE_NAME = "accounts";
	private final static String CREATE_TABLE_SQL =
		"CREATE TABLE IF NOT EXISTS accounts (id TEXT NOT NULL PRIMARY KEY, domain TEXT NOT NULL, username TEXT NOT NULL, password TEXT NOT NULL, timestamp INTEGER NOT NULL)";

	private final static String ID_INDEX_NAME = "accounts_pk";
	private final static String CREATE_ID_INDEX_SQL = "CREATE INDEX IF NOT EXISTS " + ID_INDEX_NAME + " ON accounts(id)";

	private final static String DOMAIN_INDEX_NAME = "domain_idx";
	private final static String CREATE_DOMAIN_INDEX_SQL = "CREATE INDEX IF NOT EXISTS " + DOMAIN_INDEX_NAME + " ON accounts(domain)";

	private final static String FIELD_ID = "id";
	private final static String FIELD_DOMAIN = "domain";
	private final static String FIELD_USERNAME = "username";
	private final static String FIELD_PASSWORD_FILE = "password";
	private final static String FIELD_SAVE_TIMESTAMP = "timestamp";

	public AccountSqlJetDao() {
		try {
			sqlJetDb = SqlJetStoreConstants.getConnection();
			sqlJetDb.runWriteTransaction(db -> {
				db.createTable(CREATE_TABLE_SQL);

				db.createIndex(CREATE_ID_INDEX_SQL);
				db.createIndex(CREATE_DOMAIN_INDEX_SQL);
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
			var allAccounts = (List<Account>) sqlJetDb.runReadTransaction(db -> {
				var table = db.getTable(TABLE_NAME);
				var cursor = table.order(ID_INDEX_NAME);

				return transformToAccountList(cursor);
			});
			return allAccounts;
		} catch (SqlJetException e) {
			throw new StashException(e);
		}
	}

	@Override
	public List<Account> findAccounts(String searchString) throws StashException {
		try {
			@SuppressWarnings("unchecked")
			var filteredAccounts = (List<Account>) sqlJetDb.runReadTransaction(db -> {
				var table = db.getTable(TABLE_NAME);
				var cursor = table.lookup(DOMAIN_INDEX_NAME, searchString);

				return transformToAccountList(cursor);
			});
			return filteredAccounts;
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
					FIELD_ID, account.getId(),
					FIELD_DOMAIN, account.getDomain(),
					FIELD_USERNAME, account.getUsername(),
					FIELD_PASSWORD_FILE, account.getPasswordFile(),
					FIELD_SAVE_TIMESTAMP, account.getSaveTimestamp()
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

	@Override
	public void deleteAll() throws StashException {
		try {
			Files.delete(new File(DB_PATH).toPath());
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	private List<Account> transformToAccountList(ISqlJetCursor cursor) throws SqlJetException {
		List<Account> accounts = new ArrayList<>();
		if (!cursor.eof()) {
			do {
				var account = new Account();
				account.setId(cursor.getString(FIELD_ID));
				account.setDomain(cursor.getString(FIELD_DOMAIN));
				account.setUsername(cursor.getString(FIELD_USERNAME));
				account.setPasswordFile(cursor.getString(FIELD_PASSWORD_FILE));
				account.setSaveTimestamp(cursor.getInteger(FIELD_SAVE_TIMESTAMP));
				accounts.add(account);
			} while (cursor.next());
		}
		return accounts;
	}
}
