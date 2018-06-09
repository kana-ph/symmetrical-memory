package ph.kana.memory.stash;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.derby.AccountDerbyDbDao;

import java.util.List;
import java.util.Objects;

import static java.util.UUID.randomUUID;

public class AccountService {

	private final AccountDao accountDao = new AccountDerbyDbDao();
	private final PasswordService passwordService = PasswordService.getInstance();

	private static final AccountService INSTANCE = new AccountService();

	public static AccountService getInstance() {
		return INSTANCE;
	}

	private AccountService() {}

	public List<Account> fetchAccounts() throws CorruptDataException, StashException {
		return accountDao.fetchAll();
	}

	public Account saveAccount(Account account, String rawPassword) throws CorruptDataException, StashException {
		ensureId(account);
		ensureSaveTimestamp(account);
		account.setLastUpdateTimestamp(System.currentTimeMillis());

		var passwordFile = passwordService.savePassword(account, rawPassword);
		account.setPasswordFile(passwordFile);

		return accountDao.save(account);
	}

	@Deprecated
	public Account saveAccount(String id, String domain, String username, String rawPassword) throws CorruptDataException, StashException {
		Account account = new Account();
		account.setId(ensureId(id));
		account.setDomain(domain);
		account.setUsername(username);
		account.setSaveTimestamp(System.currentTimeMillis());

		String passwordFile = passwordService.savePassword(account, rawPassword);
		account.setPasswordFile(passwordFile);

		return accountDao.save(account);
	}

	public void deleteAccount(Account account) throws CorruptDataException, StashException {
		passwordService.removePassword(account);
		accountDao.delete(account);
	}

	public void purge() throws StashException {
		accountDao.deleteAll();
		passwordService.purge();
	}

	@Deprecated
	private String ensureId(String id) {
		if (id == null || id.isEmpty()) {
			return randomUUID().toString();
		} else {
			return id;
		}
	}

	private void ensureId(Account account) {
		var accountId = account.getId();
		if (Objects.isNull(accountId) || accountId.isEmpty()) {
			account.setId(randomUUID().toString());
		}
	}

	private void ensureSaveTimestamp(Account account) {
		if (0L == account.getSaveTimestamp()) {
			account.setSaveTimestamp(System.currentTimeMillis());
		}
	}
}
