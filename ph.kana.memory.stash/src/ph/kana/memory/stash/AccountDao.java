package ph.kana.memory.stash;

import ph.kana.memory.model.Account;

import java.util.List;

public interface AccountDao {

	List<Account> fetchAll() throws CorruptDataException, StashException;

	List<Account> findAccounts(String searchString) throws CorruptDataException, StashException;

	Account save(Account account) throws CorruptDataException, StashException;

	void delete(Account account) throws CorruptDataException, StashException;

	boolean anyExists() throws CorruptDataException, StashException;

	void deleteAll() throws StashException;
}
