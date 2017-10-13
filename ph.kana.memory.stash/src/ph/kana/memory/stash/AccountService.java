package ph.kana.memory.stash;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.textfile.AccountFileDao;

import java.util.List;

public class AccountService {

	private AccountDao accountDao = new AccountFileDao();

	public List<Account> fetchAccounts() throws StashException {
		return accountDao.fetchAll();
	}
}
