package ph.kana.memory.auth.impl;

import ph.kana.memory.account.AccountDao;
import ph.kana.memory.account.CorruptDataException;
import ph.kana.memory.account.PasswordService;
import ph.kana.memory.auth.AuthDao;
import ph.kana.memory.auth.AuthService;
import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.EncryptedPassword;
import ph.kana.memory.codec.PasswordCodecService;
import ph.kana.memory.codec.impl.PinBcryptEncryptor;
import ph.kana.memory.derby.AccountDerbyDbDao;
import ph.kana.memory.file.AuthFileDao;
import ph.kana.memory.stash.StashException;

public class DefaultAuthService implements AuthService {

    private final AccountDao accountDao = new AccountDerbyDbDao();
    private final AuthDao authDao = new AuthFileDao();

    private final PasswordService passwordService = PasswordService.INSTANCE;
    private final PasswordCodecService passwordCodec = PasswordCodecService.INSTANCE;

    private final PinBcryptEncryptor pinEncryptor = new PinBcryptEncryptor();

    private final static String DEFAULT_PIN = "12345678";

    @Override
    public String fetchDefaultPin() {
        return DEFAULT_PIN;
    }

    @Override
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

    @Override
    public void saveClearPin(String pin) throws StashException {
        byte[] hashPin = pinEncryptor.hash(pin);
        passwordService.updateStoreEncryption(hashPin);
        authDao.savePin(hashPin);
    }

    @Override
    public boolean checkValidPin(String pin) throws StashException {
        byte[] storedPin = authDao.readStoredPin();
        return pinEncryptor.validate(pin, storedPin);
    }

    @Override
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
