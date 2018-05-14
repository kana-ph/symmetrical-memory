package ph.kana.memory.stash;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.textfile.PasswordZipFileDao;

public class PasswordService {

	private final AuthService authService = new AuthService();
	private final PasswordDao passwordDao = new PasswordZipFileDao();

	private static final PasswordService INSTANCE = new PasswordService();

	public static PasswordService getInstance() {
		return INSTANCE;
	}

	public byte[] fetchClearPassword(Account account) throws StashException {
		String passwordFile = account.getPasswordFile();

		String encryptedPassword = passwordDao.readPassword(passwordFile);
		String timestamp = Long.toString(account.getSaveTimestamp());
		return authService.decryptPassword(encryptedPassword, timestamp);
	}
}
