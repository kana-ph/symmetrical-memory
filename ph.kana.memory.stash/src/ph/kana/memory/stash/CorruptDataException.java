package ph.kana.memory.stash;

public class CorruptDataException extends Exception {

	public CorruptDataException(String message) {
		super(message);
	}

	public CorruptDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
