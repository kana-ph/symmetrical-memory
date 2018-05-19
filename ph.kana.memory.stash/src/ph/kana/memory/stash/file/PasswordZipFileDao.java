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
import java.util.Objects;

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

			if (!ivFile.delete() || !valueFile.delete()) {
				throw new StashException("Failed to delete temp files.");
			}
		} catch (IOException | ZipException e) {
			throw new StashException(e);
		}
		return filename;
	}

	@Override
	public EncryptedPassword readPassword(String passwordFile) throws StashException {
		try {
			var zipFile = openZipFile();

			if (null == zipFile) {
				throw new StashException("Cannot fetch password; missing store file");
			}

			var ivFilename = passwordFile + 'i';
			var valueFilename = passwordFile + 'v';

			var ivFile = createTempFile(ivFilename);
			var valueFile = createTempFile(valueFilename);

			zipFile.extractFile(ivFilename, TEMP_ROOT);
			zipFile.extractFile(valueFilename, TEMP_ROOT);

			var iv = Files.readAllBytes(ivFile.toPath());
			var value = Files.readAllBytes(valueFile.toPath());


			if (!ivFile.delete() || !valueFile.delete()) {
				throw new StashException("Failed to delete temp files.");
			}

			return new EncryptedPassword(iv, value);
		} catch (IOException | ZipException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void removePassword(String passwordFile) throws StashException {
		try {
			var zipFile = openZipFile();

			if (null == zipFile) {
				throw new StashException("Cannot fetch password; missing store file");
			}

			zipFile.removeFile(passwordFile);
		} catch (ZipException e) {
			if (!NO_ZIP_REASON.equals(e.getMessage())) {
				throw new StashException(e);
			}
		}
	}

	@Override
	public void updatePasswordStoreEncryption(byte[] encryption) throws StashException {
		try {
			var zipFile = openZipFile();

			if (null == zipFile) {
				return;
			}

			var tempDestination = createTempFile(generateFilename());
			zipFile.extractAll(tempDestination.getAbsolutePath());

			if (zipFile.getFile().delete()) {
				var files = Objects.requireNonNull(tempDestination.listFiles());
				createNewZipFile(encryption, List.of(files));
			}
		} catch (ZipException e) {
			throw new StashException(e);
		}
	}

	private ZipFile openZipFile() throws StashException, ZipException  {
		var zipFile = new ZipFile(ZIP_PATH);

		if (zipFile.getFile().exists()) {
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(fetchZipPassword());
			}
			return zipFile;
		}
		return null;
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

	private void createNewZipFile(byte[] password, List<File> files) throws StashException, ZipException {
		var zipParameters = buildZipParameters();
		var zipPassword = Base64.getEncoder()
				.encodeToString(password);
		zipParameters.setPassword(zipPassword);

		var zipFile = new ZipFile(ZIP_PATH);
		zipFile.createZipFile(new ArrayList<>(files), zipParameters);
	}
}
