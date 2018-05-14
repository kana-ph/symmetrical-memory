package ph.kana.memory.stash.textfile;

import ph.kana.memory.stash.PasswordDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.util.UUID.randomUUID;
import static ph.kana.memory.stash.textfile.FileStoreConstants.TEMP_ROOT;

public class PasswordZipFileDao implements PasswordDao {

	@Override
	public String storePassword(String password) throws StashException {
		String filename = randomUUID().toString();
		String fullPath = TEMP_ROOT + filename;
		File passwordFile = new File(fullPath);
		passwordFile.deleteOnExit();

		try {
			Files.write(passwordFile.toPath(), password.getBytes());

			passwordFile.delete();
		} catch (IOException e) {
			throw new StashException(e);
		}
		return filename;
	}
}
