package ph.kana.memory.file;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;

import static net.lingala.zip4j.util.Zip4jConstants.*;

public final class ZipFileService {

	private final static ZipFileService INSTANCE = new ZipFileService();

	public static ZipFileService getInstance() {
		return INSTANCE;
	}

	public ZipFile addDirectoryToZip(File file, File directory, String password) throws ZipException {
		var zipFile = new ZipFile(file);
		var params = buildZipParameters(password);
		zipFile.addFolder(directory, params);

		return zipFile;
	}

	public void removeFileFromZip(ZipFile zipFile, String filename) throws ZipException {
		zipFile.removeFile(filename);
	}

	private ZipParameters buildZipParameters(String password) {
		var params = new ZipParameters();

		params.setCompressionMethod(COMP_DEFLATE);
		params.setCompressionLevel(DEFLATE_LEVEL_FASTEST);

		params.setIncludeRootFolder(false);

		params.setEncryptFiles(true);
		params.setEncryptionMethod(ENC_METHOD_AES);
		params.setAesKeyStrength(AES_STRENGTH_256);
		params.setPassword(password);

		return params;
	}
}
