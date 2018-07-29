package ph.kana.memory.backup;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.stash.ZipFileService;

import java.io.File;

import static ph.kana.memory.stash.file.FileStoreConstants.LOCKER_ROOT;

public final class BackupService {

	private final static BackupService INSTANCE = new BackupService();

	private final ZipFileService zipFileService = ZipFileService.getInstance();

	public static BackupService getInstance() {
		return INSTANCE;
	}

	public File createBackup(File backupFile, String password) throws BackupException {
		try {
			var file = new File(backupFile.getAbsolutePath());
			if (file.exists() && !file.delete()) {
				throw new BackupException("Backup File already exists!");
			}

			var lockerRoot = new File(LOCKER_ROOT);

			var zipFile = zipFileService.addDirectoryToZip(file, lockerRoot, password);
			removeAuthFile(zipFile);
			return zipFile.getFile();
		} catch (StashException | ZipException e) {
			throw new BackupException("Failed to create backup", e);
		}
	}

	private void removeAuthFile(ZipFile zipFile) throws ZipException  {
		zipFileService.removeFileFromZip(zipFile, "a");
	}
}
