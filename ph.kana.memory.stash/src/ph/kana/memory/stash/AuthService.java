package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.codec.PinHasher;
import ph.kana.memory.stash.textfile.AuthFileDao;

public class AuthService {

	private final AuthDao authDao = new AuthFileDao();
	private final PasswordCodec passwordCodec = new PasswordCodec();
	private final PinHasher pinHasher = new PinHasher();

	public boolean checkValidPin(String pin) throws StashException {
		String storedPin = authDao.readStoredPin();
		return pinHasher.validate(pin, storedPin);
	}

	public String decryptPassword(String encryptedPassword, String salt) throws StashException {
		try {
			return passwordCodec.decrypt(encryptedPassword, salt);
		} catch (CodecOperationException e) {
			throw new StashException(e);
		}
	}
}
