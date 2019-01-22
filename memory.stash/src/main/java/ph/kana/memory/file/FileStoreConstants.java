package ph.kana.memory.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public final class FileStoreConstants {
	private FileStoreConstants() {}

	public static final String LOCKER_ROOT = System.getProperty("pstash.locker_root");
	private static final Logger log = Logger.getLogger(FileStoreConstants.class.getName());

	static {
		File rootDir = new File(LOCKER_ROOT);
		if (!rootDir.exists()) {
			createHiddenDir(rootDir);
		}
	}

	static final String TEMP_ROOT = System.getProperty("pstash.temp_dir");
	static final String AUTH_PATH = String.format("%s/a", LOCKER_ROOT);
	static final String ZIP_PATH = String.format("%s/p", LOCKER_ROOT);

	private static void createHiddenDir(File file) {
		var osName = System.getProperty("os.name").toLowerCase();

		try {
			if (!file.mkdir()) {
				throw new IOException("Failed to create directory: " + file);
			}
			if (osName.startsWith("windows")) {
				Files.setAttribute(file.toPath(), "dos:hidden", true);
			}
		} catch (IOException e) {
			log.severe(e::getMessage);
		}
	}
}
