package ph.kana.memory.stash.textfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;

final class FileStoreConstants {
	private FileStoreConstants() {}

	private static final String LOCKER_ROOT = System.getProperty("user.home") + System.getProperty("file.separator") + ".pstash";
	static {
		File rootDir = new File(LOCKER_ROOT);
		if (!rootDir.exists()) {
			hideFile(rootDir);
			rootDir.mkdir();
		}
	}

	public static final String AUTH_PATH = String.format("%s/a", LOCKER_ROOT);
	public static final String STORE_PATH = String.format("%s/s", LOCKER_ROOT);

	public static final String ENTRY_SEPARATOR = "%";

	private static void hideFile(File file) {
		String os = System.getProperty("os.name").toLowerCase();

		try {
			if (os.startsWith("windows")) {
				Files.setAttribute(file.toPath(), "dos:hidden", true);
			}
		} catch (IOException e) {}
	}
}
