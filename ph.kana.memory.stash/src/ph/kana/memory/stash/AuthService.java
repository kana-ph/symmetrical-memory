package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.codec.PinHasher;
import ph.kana.memory.model.PinStatus;
import ph.kana.memory.stash.sqljet.AccountSqlJetDao;
import ph.kana.memory.stash.textfile.AuthFileDao;

public class AuthService {

	private final AccountDao accountDao = new AccountSqlJetDao();
	private final AuthDao authDao = new AuthFileDao();

	private final PasswordCodec passwordCodec = new PasswordCodec();
	private final PinHasher pinHasher = new PinHasher();

	private final static String DEFAULT_PIN = "12345678";
	private final static AuthService INSTANCE = new AuthService();

	public static AuthService getInstance() {
		return INSTANCE;
	}

	public PinStatus initializePin() {
		try {
			String currentPin = authDao.readStoredPin();

			if (pinExists(currentPin)) {
				return PinStatus.EXISTING;
			} else {
				if (accountDao.anyExists()) {
					return PinStatus.MISSING;
				} else {
					saveClearPin(DEFAULT_PIN);
					return PinStatus.NEW;
				}
			}
		} catch (StashException e) {
			e.printStackTrace(System.err);
			System.exit(1);
			return null;
		}
	}

	public void saveClearPin(String pin) throws StashException {
		String hashPin = pinHasher.hash(pin);
		authDao.savePin(hashPin);
	}

	public boolean checkValidPin(String pin) throws StashException {
		String storedPin = authDao.readStoredPin();
		return pinHasher.validate(pin, storedPin);
	}

	public byte[] decryptPassword(String encryptedPassword, String salt) throws StashException {
		try {
			return passwordCodec.decrypt(encryptedPassword, salt);
		} catch (CodecOperationException e) {
			throw new StashException(e);
		}
	}

	private boolean pinExists(String pin) {
		return !"".equals(pin);
	}
}
