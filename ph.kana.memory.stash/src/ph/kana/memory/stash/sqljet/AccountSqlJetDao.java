package ph.kana.memory.stash.sqljet;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.SqlJetDb;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.StashException;

import java.util.List;

import static ph.kana.memory.stash.sqljet.SqlJetStoreConstants.CREATE_IF_EXIST_TABLE_ACCOUNTS;

public class AccountSqlJetDao implements AccountDao {

	private SqlJetDb sqlJetDb;

	public AccountSqlJetDao() {
		try {
			sqlJetDb = SqlJetStoreConstants.getConnection();
			sqlJetDb.runWriteTransaction(db -> {
				db.createTable(CREATE_IF_EXIST_TABLE_ACCOUNTS);
			});
		} catch (SqlJetException e) {
			System.exit(1);
		}
	}

	@Override
	public List<Account> fetchAll() throws StashException {
		return null;
	}

	@Override
	public Account save(Account account) throws StashException {
		return null;
	}

	@Override
	public void delete(Account account) throws StashException {

	}
}
