package ph.kana.memory.stash;

public interface AuthDao {

	String readStoredPin() throws StashException;
}
