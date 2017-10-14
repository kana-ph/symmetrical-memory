package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.textfile.AccountFileDao;

import java.util.List;
import java.util.UUID;

public class AccountService {

	private AccountDao accountDao = new AccountFileDao();
	private PasswordCodec passwordCodec = new PasswordCodec();

	public List<Account> fetchAccounts() throws StashException {
		return accountDao.fetchAll();
	}

	public Account saveAccount(String domain, String username, String rawPassword) throws StashException {
		try {
			long now = System.currentTimeMillis();
			String encryptedPassword = passwordCodec.encrypt(rawPassword, Long.toString(now));

			Account account = new Account();
			account.setId(generateId());
			account.setDomain(domain);
			account.setUsername(username);
			account.setSaveTimestamp(now);
			account.setEncryptedPassword(encryptedPassword);
			return accountDao.save(account);
		} catch (CodecOperationException e) {
			throw new StashException(e);
		}
	}

	private String generateId() {
		return UUID.randomUUID().toString();
	}
}
