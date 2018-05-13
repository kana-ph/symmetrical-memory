package ph.kana.memory.model;

public class Account {
	private String id;
	private String domain;
	private String username;
	private String passwordFile;
	private long saveTimestamp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordFile() {
		return passwordFile;
	}

	public void setPasswordFile(String passwordFile) {
		this.passwordFile = passwordFile;
	}

	public long getSaveTimestamp() {
		return saveTimestamp;
	}

	public void setSaveTimestamp(long saveTimestamp) {
		this.saveTimestamp = saveTimestamp;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (!(that instanceof Account)) {
			return false;
		}

		Account account = (Account) that;
		return id.equals(account.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
