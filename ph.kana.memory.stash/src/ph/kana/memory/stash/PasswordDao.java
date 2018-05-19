package ph.kana.memory.stash;

import ph.kana.memory.codec.EncryptedPassword;

public interface PasswordDao {

	String storePassword(EncryptedPassword password) throws StashException;

	EncryptedPassword readPassword(String passwordFile) throws StashException;

	void removePassword(String passwordFile) throws StashException;
}
