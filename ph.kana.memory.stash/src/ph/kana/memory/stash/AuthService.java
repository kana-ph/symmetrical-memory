package ph.kana.memory.stash;

import ph.kana.memory.codec.PinHasher;
import ph.kana.memory.stash.textfile.AuthFileDao;

public class AuthService {

	private final AuthDao authDao = new AuthFileDao();
	private final PinHasher pinHasher = new PinHasher();

	public boolean checkValidPin(String pin) throws StashException {
		String storedPin = authDao.readStoredPin();
		return pinHasher.validate(pin, storedPin);
	}
}
