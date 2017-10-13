package ph.kana.memory.stash.textfile;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
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
					.map(line -> line.split(":", 3))
					.map(this::mapToModel)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	private Account mapToModel(String[] line) {
		String domain = line[0];
		String username = line[1];
		String password = line[2];

		Account account = new Account();
		account.setDomain(domain);
		account.setUsername(username);
		account.setEncryptedPassword(password);
		return account;
	}
}
