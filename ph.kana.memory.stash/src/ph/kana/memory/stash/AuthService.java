package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.EncryptedPassword;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.codec.PinBcryptEncryptor;
import ph.kana.memory.stash.derby.AccountDerbyDbDao;
import ph.kana.memory.stash.file.AuthFileDao;

public class AuthService {

	private final AccountDao accountDao = new AccountDerbyDbDao();
	private final AuthDao authDao = new AuthFileDao();

	private final PasswordCodec passwordCodec = new PasswordCodec();
	private final PasswordService passwordService = PasswordService.getInstance();
	private final PinBcryptEncryptor pinEncryptor = new PinBcryptEncryptor();

	public final static String DEFAULT_PIN = "12345678";
	private final static AuthService INSTANCE = new AuthService();

	public static AuthService getInstance() {
		return INSTANCE;
	}

	public boolean initializePin() throws CorruptDataException {
		try {
			byte[] currentPin = authDao.readStoredPin();

			if (pinExists(currentPin)) {
				return true;
			} else {
				if (dataExists()) {
					throw new CorruptDataException("Missing PIN file");
				} else {
					saveClearPin(DEFAULT_PIN);
					return false;
				}
			}
		} catch (StashException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return false;
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

	private boolean dataExists() throws CorruptDataException, StashException {
		return accountDao.anyExists() || passwordService.storeExists();
	}
}
