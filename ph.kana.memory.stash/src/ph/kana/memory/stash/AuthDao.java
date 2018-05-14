package ph.kana.memory.stash;

public interface AuthDao {

	byte[] readStoredPin() throws StashException;

	void savePin(byte[] hashedPin) throws StashException;
}
