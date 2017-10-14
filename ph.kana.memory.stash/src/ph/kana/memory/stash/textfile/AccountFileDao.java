package ph.kana.memory.stash.textfile;

import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountDao;
import ph.kana.memory.stash.StashException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class AccountFileDao implements AccountDao {

	private static final String STORE_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + ".pstash";
	private static final String ENTRY_SEPARATOR = "%";
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
					.map(this::decodeEntry)
					.map(entry -> entry.split(ENTRY_SEPARATOR, 4))
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
					printLine = formatAccountEntry(account);
					accountExisting = true;
				} else {
					printLine = formatAccountEntry(existing);
				}
				writer.write(printLine);
			}
			if (!accountExisting) {
				writer.write(formatAccountEntry(account));
			}
			return account;
		} catch (IOException e) {
			throw new StashException(e);
		}
	}

	private Account mapToModel(String[] line) {
		Account account = new Account();
		account.setId(line[0]);
		account.setDomain(line[1]);
		account.setUsername(line[2]);
		account.setSaveTimestamp(Long.valueOf(line[3]));
		account.setEncryptedPassword(line[4]);
		return account;
	}

	private String decodeEntry(String line) {
		byte[] content = Base64.getDecoder()
				.decode(line);
		return new String(content);
	}

	private String formatAccountEntry(Account account) {
		String entry = new StringBuilder()
				.append(account.getId()).append(ENTRY_SEPARATOR)
				.append(account.getDomain()).append(ENTRY_SEPARATOR)
				.append(account.getUsername()).append(ENTRY_SEPARATOR)
				.append(account.getSaveTimestamp()).append(ENTRY_SEPARATOR)
				.append(account.getEncryptedPassword())
				.toString();
		String b64 = Base64.getEncoder()
				.encodeToString(entry.getBytes());
		return String.format("%s\n", b64);
	}
}
