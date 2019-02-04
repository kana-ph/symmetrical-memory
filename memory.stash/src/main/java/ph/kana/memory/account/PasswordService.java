package ph.kana.memory.account;

import ph.kana.memory.account.impl.DefaultPasswordService;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;

public interface PasswordService {

	PasswordService INSTANCE = new DefaultPasswordService();

	byte[] fetchClearPassword(Account account) throws StashException;

	String savePassword(Account account, String rawPassword) throws StashException;

	void removePassword(Account account) throws StashException;

	void updateStoreEncryption(byte[] newPassword) throws StashException;

	boolean storeExists() throws StashException;

	void purge() throws StashException;
}
