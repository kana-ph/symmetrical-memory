package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.textfile.AccountFileDao;

import java.util.List;

import static java.util.UUID.*;

public class AccountService {

	private AccountDao accountDao = new AccountFileDao();
	private PasswordCodec passwordCodec = new PasswordCodec();

	private static final AccountService INSTANCE = new AccountService();

	public static AccountService getInstance() {
		return INSTANCE;
	}

	private AccountService() {}

	public List<Account> fetchAccounts() throws StashException {
		return accountDao.fetchAll();
	}

	public Account saveAccount(String id, String domain, String username, String rawPassword) throws StashException {
		try {
			long now = System.currentTimeMillis();
			String encryptedPassword = passwordCodec.encrypt(rawPassword, Long.toString(now));

			Account account = new Account();
			account.setId(ensureId(id));
			account.setDomain(domain);
			account.setUsername(username);
			account.setSaveTimestamp(now);
			account.setEncryptedPassword(encryptedPassword);
			return accountDao.save(account);
		} catch (CodecOperationException e) {
			throw new StashException(e);
		}
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
