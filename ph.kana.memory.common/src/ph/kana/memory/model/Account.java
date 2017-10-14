package ph.kana.memory.model;

import java.util.UUID;

public class Account {
	private String id;
	private String domain;
	private String username;
	private String encryptedPassword;
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

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null || !(that instanceof Account)) {
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
