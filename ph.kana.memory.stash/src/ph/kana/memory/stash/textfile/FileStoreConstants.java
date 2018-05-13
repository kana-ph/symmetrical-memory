package ph.kana.memory.stash.textfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

	static final String TEMP_ROOT = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
	static final String AUTH_PATH = String.format("%s/a", LOCKER_ROOT);
	static final String ZIP_PATH = String.format("%s/p", LOCKER_ROOT);

	private static void hideFile(File file) {
		String os = System.getProperty("os.name").toLowerCase();

		try {
			if (os.startsWith("windows")) {
				Files.setAttribute(file.toPath(), "dos:hidden", true);
			}
		} catch (IOException e) {}
	}
}
