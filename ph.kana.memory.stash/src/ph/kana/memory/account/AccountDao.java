package ph.kana.memory.account;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;

import java.util.List;

public interface AccountDao {

	List<Account> fetchAll() throws CorruptDataException, StashException;

	Account save(Account account) throws CorruptDataException, StashException;

	void delete(Account account) throws CorruptDataException, StashException;

	boolean anyExists() throws CorruptDataException, StashException;

	void deleteAll() throws StashException;
}
