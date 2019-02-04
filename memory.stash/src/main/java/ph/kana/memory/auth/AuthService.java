package ph.kana.memory.auth;

import ph.kana.memory.account.CorruptDataException;
import ph.kana.memory.auth.impl.DefaultAuthService;
import ph.kana.memory.codec.EncryptedPassword;
import ph.kana.memory.stash.StashException;

public interface AuthService {

	AuthService INSTANCE = new DefaultAuthService();

	String fetchDefaultPin();

	boolean initializePin() throws CorruptDataException;

	void saveClearPin(String pin) throws StashException;

	boolean checkValidPin(String pin) throws StashException;

	byte[] decryptPassword(EncryptedPassword encryptedPassword, String salt) throws StashException;
}
