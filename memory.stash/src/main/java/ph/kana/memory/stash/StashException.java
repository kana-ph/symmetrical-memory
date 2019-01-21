package ph.kana.memory.stash;

public class StashException extends Exception {

	public StashException(Throwable throwable) {
		super(throwable);
	}

	public StashException(String message) {
		super(message);
	}
}
