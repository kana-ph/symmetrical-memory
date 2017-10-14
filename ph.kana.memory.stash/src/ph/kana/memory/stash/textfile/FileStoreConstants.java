package ph.kana.memory.stash.textfile;

import java.io.File;

public final class FileStoreConstants {
	private FileStoreConstants() {}

	private static final String LOCKER_ROOT = System.getProperty("user.home") + System.getProperty("file.separator") + ".pstash";
	static {
		File rootDir = new File(LOCKER_ROOT);
		if (!rootDir.exists()) {
			rootDir.mkdir();
		}
	}

	public static final String AUTH_PATH = String.format("%s/a", LOCKER_ROOT);
	public static final String STORE_PATH = String.format("%s/s", LOCKER_ROOT);

	public static final String ENTRY_SEPARATOR = "%";
}
