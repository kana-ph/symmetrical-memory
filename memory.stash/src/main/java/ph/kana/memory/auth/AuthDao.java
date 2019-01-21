package ph.kana.memory.auth;

import ph.kana.memory.stash.StashException;

public interface AuthDao {

	byte[] readStoredPin() throws StashException;

	void savePin(byte[] hashedPin) throws StashException;
}
