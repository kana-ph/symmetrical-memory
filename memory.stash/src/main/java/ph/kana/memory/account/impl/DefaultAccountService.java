package ph.kana.memory.account.impl;

import ph.kana.memory.account.AccountDao;
import ph.kana.memory.account.AccountService;
import ph.kana.memory.account.CorruptDataException;
import ph.kana.memory.account.PasswordService;
import ph.kana.memory.derby.AccountDerbyDbDao;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;

import java.util.List;
import java.util.Objects;

import static java.util.UUID.randomUUID;

public class DefaultAccountService implements AccountService {

    private final AccountDao accountDao;
    private final PasswordService passwordService;

    public DefaultAccountService() {
        accountDao = new AccountDerbyDbDao();
        passwordService = PasswordService.INSTANCE;
    }

    @Override
    public List<Account> fetchAccounts() throws CorruptDataException, StashException {
        return accountDao.fetchAll();
    }

    @Override
    public Account saveAccount(Account account, String rawPassword) throws CorruptDataException, StashException {
        ensureId(account);
        ensureSaveTimestamp(account);
        account.setLastUpdateTimestamp(System.currentTimeMillis());

        var passwordFile = passwordService.savePassword(account, rawPassword);
        account.setPasswordFile(passwordFile);

        return accountDao.save(account);
    }

    @Override
    public void deleteAccount(Account account) throws CorruptDataException, StashException {
        passwordService.removePassword(account);
        accountDao.delete(account);
    }

    @Override
    public void purge() throws StashException {
        accountDao.deleteAll();
        passwordService.purge();
    }

    private void ensureId(Account account) {
        var accountId = account.getId();
        if (Objects.isNull(accountId) || accountId.isEmpty()) {
            account.setId(randomUUID().toString());
        }
    }

    private void ensureSaveTimestamp(Account account) {
        if (0L == account.getSaveTimestamp()) {
            account.setSaveTimestamp(System.currentTimeMillis());
        }
    }
}
