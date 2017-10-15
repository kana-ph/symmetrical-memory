package ph.kana.memory.stash.textfile;

import ph.kana.memory.stash.AuthDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ph.kana.memory.stash.textfile.FileStoreConstants.AUTH_PATH;

public class AuthFileDao implements AuthDao {

	private static final File PIN_STORE = new File(AUTH_PATH);
	static {
		try {
			PIN_STORE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	@Override
	public String readStoredPin() throws StashException {
		try {
			return Files.lines(PIN_STORE.toPath())
					.findFirst()
					.orElse("");
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void savePin(String hashedPin) throws StashException {
		String printFormat = String.format("%s\n", hashedPin);
		try {
			Files.write(PIN_STORE.toPath(), printFormat.getBytes());
		} catch (IOException e) {
			throw new StashException(e);
		}
	}
}
