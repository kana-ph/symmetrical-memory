package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.file.PasswordZipFileDao;

public final class PasswordService {

	private final AuthService authService = AuthService.getInstance();
	private final PasswordDao passwordDao = new PasswordZipFileDao();
	private final PasswordCodec passwordCodec = new PasswordCodec();

	private static PasswordService INSTANCE;

	public static PasswordService getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new PasswordService();
		}
		return INSTANCE;
	}

	private PasswordService() {}

	public byte[] fetchClearPassword(Account account) throws StashException {
		var passwordFile = account.getPasswordFile();

		var encryptedPassword = passwordDao.readPassword(passwordFile);
		var timestamp = Long.toString(account.getSaveTimestamp());
		return authService.decryptPassword(encryptedPassword, timestamp);
	}

	public String savePassword(Account account, String rawPassword) throws StashException {
		try {
			var encryptedPassword = passwordCodec.encrypt(rawPassword, Long.toString(account.getSaveTimestamp()));
			return passwordDao.storePassword(encryptedPassword);
		} catch (CodecOperationException e) {
			throw new StashException(e);
		}
	}

	public void removePassword(Account account) throws StashException {
		passwordDao.removePassword(account.getPasswordFile());
	}

	public void updateStoreEncryption(byte[] newPassword) throws StashException {
		passwordDao.updatePasswordStoreEncryption(newPassword);
	}

	public boolean storeExists() throws StashException {
		return passwordDao.passwordStoreExists();
	}
}
