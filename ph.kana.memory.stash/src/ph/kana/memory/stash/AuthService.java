package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.EncryptedPassword;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.codec.PinBcryptEncryptor;
import ph.kana.memory.model.PinStatus;
import ph.kana.memory.stash.file.AuthFileDao;
import ph.kana.memory.stash.sqljet.AccountSqlJetDao;

public class AuthService {

	private final AccountDao accountDao = new AccountSqlJetDao();
	private final AuthDao authDao = new AuthFileDao();

	private final PasswordCodec passwordCodec = new PasswordCodec();
	private final PasswordService passwordService = PasswordService.getInstance();
	private final PinBcryptEncryptor pinEncryptor = new PinBcryptEncryptor();

	public final static String DEFAULT_PIN = "12345678";
	private final static AuthService INSTANCE = new AuthService();

	public static AuthService getInstance() {
		return INSTANCE;
	}

	public PinStatus initializePin() {
		try {
			byte[] currentPin = authDao.readStoredPin();

			if (pinExists(currentPin)) {
				return PinStatus.EXISTING;
			} else {
				if (dataExists()) {
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
		byte[] hashPin = pinEncryptor.hash(pin);
		passwordService.updateStoreEncryption(hashPin);
		authDao.savePin(hashPin);
	}

	public boolean checkValidPin(String pin) throws StashException {
		byte[] storedPin = authDao.readStoredPin();
		return pinEncryptor.validate(pin, storedPin);
	}

	public byte[] decryptPassword(EncryptedPassword encryptedPassword, String salt) throws StashException {
		try {
			return passwordCodec.decrypt(encryptedPassword, salt);
		} catch (CodecOperationException e) {
			throw new StashException(e);
		}
	}

	private boolean pinExists(byte[] pin) {
		return pin.length > 0;
	}

	private boolean dataExists() throws StashException {
		return accountDao.anyExists() || passwordService.storeExists();
	}
}
