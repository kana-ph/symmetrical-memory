package ph.kana.memory.codec.impl;

import org.mindrot.jbcrypt.BCrypt;

public class PinBcryptEncryptor {

	public byte[] hash(String pin) {
		String hashed = BCrypt.hashpw(pin, BCrypt.gensalt());
		return hashed.getBytes();
	}

	public boolean validate(String pin, byte[] savedPin) {
		return BCrypt.checkpw(pin, new String(savedPin));
	}
}
