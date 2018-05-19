package ph.kana.memory.stash.textfile;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import ph.kana.memory.stash.PasswordDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.util.UUID.randomUUID;
import static ph.kana.memory.stash.textfile.FileStoreConstants.TEMP_ROOT;
import static ph.kana.memory.stash.textfile.FileStoreConstants.ZIP_PATH;

public class PasswordZipFileDao implements PasswordDao {

	@Override
	public String storePassword(String password) throws StashException {
		var filename = generateFilename();
		var passwordFile = createTempFile(filename);

		try {
			Files.write(passwordFile.toPath(), password.getBytes());

			addFileToZip(passwordFile);

			passwordFile.delete();
		} catch (IOException | ZipException e) {
			throw new StashException(e);
		}
		return filename;
	}

	@Override
	public String readPassword(String passwordFile) throws StashException {
		try {
			var zipFile = new ZipFile(ZIP_PATH);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword("test-pass"); // TODO implement
			}

			var targetFile = createTempFile(passwordFile);
			zipFile.extractFile(passwordFile, TEMP_ROOT);

			var content = Files.readAllBytes(targetFile.toPath());
			targetFile.delete();

			return new String(content);
		} catch (IOException | ZipException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void removePassword(String passwordFile) throws StashException {
		try {
			var zipFile = new ZipFile(ZIP_PATH);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword("test-pass"); // TODO implement
			}
			zipFile.removeFile(passwordFile);
		} catch (ZipException e) {
			throw new StashException(e);
		}
	}

	private static String generateFilename() {
		return randomUUID().toString()
				.replaceAll("-", "");
	}

	private static File createTempFile(String filename) {
		var tempFile = new File(TEMP_ROOT + filename);
		tempFile.deleteOnExit();
		return tempFile;
	}

	private static void addFileToZip(File file) throws ZipException {
		var zipFile = new ZipFile(ZIP_PATH);
		zipFile.addFile(file,  buildZipParameters());
	}

	private static ZipParameters buildZipParameters() {
		var params = new ZipParameters();

		params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);

		params.setEncryptFiles(true);
		params.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
		params.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		params.setPassword("test-pass"); // TODO implement

		return params;
	}
}
