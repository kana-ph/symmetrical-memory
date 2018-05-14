package ph.kana.memory.stash;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.sqljet.AccountSqlJetDao;

import java.util.List;

import static java.util.UUID.randomUUID;

public class AccountService {

	private final AccountDao accountDao = new AccountSqlJetDao();
	private final PasswordService passwordService = PasswordService.getInstance();

	private static final AccountService INSTANCE = new AccountService();

	public static AccountService getInstance() {
		return INSTANCE;
	}

	private AccountService() {}

	public List<Account> fetchAccounts() throws StashException {
		return accountDao.fetchAll();
	}

	public Account saveAccount(String id, String domain, String username, String rawPassword) throws StashException {
		Account account = new Account();
		account.setId(ensureId(id));
		account.setDomain(domain);
		account.setUsername(username);
		account.setSaveTimestamp(System.currentTimeMillis());

		String passwordFile = passwordService.savePassword(account, rawPassword);
		account.setPasswordFile(passwordFile);

		return accountDao.save(account);
	}

	public void deleteAccount(Account account) throws StashException {
		accountDao.delete(account);
	}

	private String ensureId(String id) {
		if (id == null || id.isEmpty()) {
			return randomUUID().toString();
		} else {
			return id;
		}
	}
}
