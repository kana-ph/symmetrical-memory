package ph.kana.memory.stash.textfile;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class AccountFileDao implements AccountDao {

	private static final String STORE_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + ".pstash";
	private static final File ACCOUNT_STORE = new File(STORE_PATH);
	static {
		try {
			ACCOUNT_STORE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	@Override
	public List<Account> fetchAll() throws StashException {
		try {
			return Files.lines(ACCOUNT_STORE.toPath())
					.map(line -> line.split("#", 4))
					.map(this::mapToModel)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	@Override
	public Account save(final Account account) throws StashException {
		List<Account> accountList = fetchAll();
		try (PrintWriter writer = new PrintWriter(ACCOUNT_STORE)){
			boolean accountExisting = false;
			for (Account existing : accountList) {
				String printLine;
				if (account.isSameAccount(existing)) {
					printLine = formatAccount(account);
					accountExisting = true;
				} else {
					printLine = formatAccount(existing);
				}
				writer.write(printLine);
			}
			if (!accountExisting) {
				writer.write(formatAccount(account));
			}
			return account;
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	private Account mapToModel(String[] line) {
		String domain = line[0];
		String username = line[1];
		long timestamp = Long.valueOf(line[2]);
		String password = line[3];

		Account account = new Account();
		account.setDomain(domain);
		account.setUsername(username);
		account.setSaveTimestamp(timestamp);
		account.setEncryptedPassword(password);
		return account;
	}

	private String formatAccount(Account account) {
		String timestamp = Long.toString(account.getSaveTimestamp());
		return String.format("%s#%s#%s#%s", account.getDomain(), account.getUsername(), timestamp, account.getEncryptedPassword());
	}
}
