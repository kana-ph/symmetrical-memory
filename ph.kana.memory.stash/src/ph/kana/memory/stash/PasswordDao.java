package ph.kana.memory.stash;

public interface PasswordDao {

	String storePassword(String password) throws StashException;

	String readPassword(String passwordFile) throws StashException;
}
