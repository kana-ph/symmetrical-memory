package ph.kana.memory.stash.file;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import ph.kana.memory.codec.EncryptedPassword;
import ph.kana.memory.stash.AuthDao;
import ph.kana.memory.stash.PasswordDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.util.UUID.randomUUID;
import static ph.kana.memory.stash.file.FileStoreConstants.TEMP_ROOT;
import static ph.kana.memory.stash.file.FileStoreConstants.ZIP_PATH;

public class PasswordZipFileDao implements PasswordDao {

	private final AuthDao authDao = new AuthFileDao();

	private final static String NO_ZIP_REASON = "zip file does not exist";

	@Override
	public String storePassword(EncryptedPassword password) throws StashException {
		var filename = generateFilename();
		var ivFile = createTempFile(filename + 'i');
		var valueFile = createTempFile(filename + 'v');

		try {
			Files.write(ivFile.toPath(), password.getInitializationVector());
			Files.write(valueFile.toPath(), password.getValue());

			addFilesToZip(ivFile, valueFile);

			ivFile.delete();
			valueFile.delete();
		} catch (IOException | ZipException e) {
			throw new StashException(e);
		}
		return filename;
	}

	@Override
	public EncryptedPassword readPassword(String passwordFile) throws StashException {
		try {
			var zipFile = new ZipFile(ZIP_PATH);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(fetchZipPassword());
			}

			var ivFilename = passwordFile + 'i';
			var valueFilename = passwordFile + 'v';

			var ivFile = createTempFile(ivFilename);
			var valueFile = createTempFile(valueFilename);

			zipFile.extractFile(ivFilename, TEMP_ROOT);
			zipFile.extractFile(valueFilename, TEMP_ROOT);

			var iv = Files.readAllBytes(ivFile.toPath());
			var value = Files.readAllBytes(valueFile.toPath());

			ivFile.delete();
			valueFile.delete();

			return new EncryptedPassword(iv, value);
		} catch (IOException | ZipException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void removePassword(String passwordFile) throws StashException {
		try {
			var zipFile = new ZipFile(ZIP_PATH);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(fetchZipPassword());
			}
			zipFile.removeFile(passwordFile);
		} catch (ZipException e) {
			if (!NO_ZIP_REASON.equals(e.getMessage())) {
				throw new StashException(e);
			}
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

	private void addFilesToZip(File file1, File file2) throws StashException, ZipException {
		var zipFile = new ZipFile(ZIP_PATH);
		var files = new ArrayList<>(List.of(file1, file2));
		zipFile.addFiles(files, buildZipParameters());
	}

	private ZipParameters buildZipParameters() throws StashException {
		var params = new ZipParameters();

		params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);

		params.setEncryptFiles(true);
		params.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
		params.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		params.setPassword(fetchZipPassword());

		return params;
	}

	private String fetchZipPassword() throws StashException {
		var pinBytes = authDao.readStoredPin();
		return Base64.getEncoder()
				.encodeToString(pinBytes);
	}
}
