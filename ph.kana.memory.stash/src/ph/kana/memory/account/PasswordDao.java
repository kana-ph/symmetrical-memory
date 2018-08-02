package ph.kana.memory.account;

import ph.kana.memory.codec.EncryptedPassword;
import ph.kana.memory.stash.StashException;

public interface PasswordDao {

	String storePassword(EncryptedPassword password) throws StashException;

	EncryptedPassword readPassword(String passwordFile) throws StashException;

	void removePassword(String passwordFile) throws StashException;

	void updatePasswordStoreEncryption(byte[] encyrption) throws StashException;

	boolean passwordStoreExists() throws StashException;

	void deletePasswordStore() throws StashException;
}
