package ph.kana.memory.file;

import ph.kana.memory.auth.AuthDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AuthFileDao implements AuthDao {

	private static final File PIN_STORE = FileLocationHolder.getInstance()
		.getAuth();
	static {
		try {
			PIN_STORE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	@Override
	public byte[] readStoredPin() throws StashException {
		try {
			return Files.readAllBytes(PIN_STORE.toPath());
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	@Override
	public void savePin(byte[] hashedPin) throws StashException {
		try {
			Files.write(PIN_STORE.toPath(), hashedPin);
		} catch (IOException e) {
			throw new StashException(e);
		}
	}
}
