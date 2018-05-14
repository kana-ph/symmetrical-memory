package ph.kana.memory.stash;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.textfile.PasswordZipFileDao;

public class PasswordService {

	private final AuthService authService = new AuthService();
	private final PasswordDao passwordDao = new PasswordZipFileDao();
	private final PasswordCodec passwordCodec = new PasswordCodec();

	private static final PasswordService INSTANCE = new PasswordService();

	public static PasswordService getInstance() {
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
}
