package ph.kana.memory.backup.impl;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import ph.kana.memory.backup.BackupException;
import ph.kana.memory.backup.BackupService;
import ph.kana.memory.file.FileLocationHolder;
import ph.kana.memory.file.ZipFileService;

import java.io.File;

public class DefaultBackupService implements BackupService {

    private final ZipFileService zipFileService = ZipFileService.getInstance();

    @Override
    public File createBackup(File backupFile, String password) throws BackupException {
        try {
            var file = new File(backupFile.getAbsolutePath());
            if (file.exists() && !file.delete()) {
                throw new BackupException("Backup File already exists!");
            }

            var lockerRoot = FileLocationHolder.getInstance()
                .getRoot();

            var zipFile = zipFileService.addDirectoryToZip(file, lockerRoot, password);
            removeAuthFile(zipFile);
            return zipFile.getFile();
        } catch (ZipException e) {
            throw new BackupException("Failed to create backup", e);
        }
    }

    @Override
    public void restoreBackup(File backupFile, String password) throws BackupException {

    }

    private void removeAuthFile(ZipFile zipFile) throws ZipException  {
        zipFileService.removeFileFromZip(zipFile, "a");
    }
}
