package ph.kana.memory.account.impl;

import ph.kana.memory.account.PasswordDao;
import ph.kana.memory.account.PasswordService;
import ph.kana.memory.auth.AuthService;
import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodecService;
import ph.kana.memory.file.PasswordZipFileDao;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;

public class DefaultPasswordService implements PasswordService {

    private final AuthService authService = AuthService.INSTANCE;
    private final PasswordDao passwordDao = new PasswordZipFileDao();
    private final PasswordCodecService passwordCodec = PasswordCodecService.INSTANCE;

    @Override
    public byte[] fetchClearPassword(Account account) throws StashException {
        var passwordFile = account.getPasswordFile();

        var encryptedPassword = passwordDao.readPassword(passwordFile);
        var timestamp = Long.toString(account.getSaveTimestamp());
        return authService.decryptPassword(encryptedPassword, timestamp);
    }

    @Override
    public String savePassword(Account account, String rawPassword) throws StashException {
        try {
            var encryptedPassword = passwordCodec.encrypt(rawPassword, Long.toString(account.getSaveTimestamp()));
            return passwordDao.storePassword(encryptedPassword);
        } catch (CodecOperationException e) {
            throw new StashException(e);
        }
    }

    @Override
    public void removePassword(Account account) throws StashException {
        passwordDao.removePassword(account.getPasswordFile());
    }

    @Override
    public void updateStoreEncryption(byte[] newPassword) throws StashException {
        passwordDao.updatePasswordStoreEncryption(newPassword);
    }

    @Override
    public boolean storeExists() throws StashException {
        return passwordDao.passwordStoreExists();
    }

    @Override
    public void purge() throws StashException {
        passwordDao.deletePasswordStore();
    }
}
