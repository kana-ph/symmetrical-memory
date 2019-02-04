package ph.kana.memory.backup;

import ph.kana.memory.backup.impl.DefaultBackupService;

import java.io.File;

public interface BackupService {

	BackupService INSTANCE = new DefaultBackupService();

	File createBackup(File backupFile, String password) throws BackupException;

	void restoreBackup(File backupFile, String password) throws BackupException;
}
