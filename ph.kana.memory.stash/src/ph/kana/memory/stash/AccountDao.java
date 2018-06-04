package ph.kana.memory.stash;

import ph.kana.memory.model.Account;
import ph.kana.memory.type.SortColumn;

import java.util.List;

public interface AccountDao {

	List<Account> fetchAll(SortColumn sortColumn) throws CorruptDataException, StashException;

	List<Account> findAccounts(String searchString) throws CorruptDataException, StashException;

	Account save(Account account) throws CorruptDataException, StashException;

	void delete(Account account) throws CorruptDataException, StashException;

	boolean anyExists() throws CorruptDataException, StashException;

	void deleteAll() throws StashException;
}
