package ph.kana.memory.account;

import ph.kana.memory.account.impl.DefaultAccountService;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;

import java.util.List;

public interface AccountService {

	AccountService INSTANCE = new DefaultAccountService();

	List<Account> fetchAccounts() throws CorruptDataException, StashException;

	Account saveAccount(Account account, String rawPassword) throws CorruptDataException, StashException;

	void deleteAccount(Account account) throws CorruptDataException, StashException;

	void purge() throws StashException;
}
