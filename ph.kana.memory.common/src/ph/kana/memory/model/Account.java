package ph.kana.memory.model;

public class Account {
	private String domain;
	private String username;
	private String encryptedPassword;
	private long saveTimestamp;

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

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public long getSaveTimestamp() {
		return saveTimestamp;
	}

	public void setSaveTimestamp(long saveTimestamp) {
		this.saveTimestamp = saveTimestamp;
	}

	public boolean isSameAccount(Account other) {
		return other.domain.equalsIgnoreCase(domain) &&
				other.username.equalsIgnoreCase(username);
	}
}
