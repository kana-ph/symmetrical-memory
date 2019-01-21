package ph.kana.memory.backup;

public class BackupException extends Exception {

	public BackupException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackupException(String message) {
		super(message);
	}
}
