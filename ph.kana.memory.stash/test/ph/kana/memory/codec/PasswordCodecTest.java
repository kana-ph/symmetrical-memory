package ph.kana.memory.codec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordCodecTest {

	@Test
	void encrypt_decrypt_symmetrical() throws Exception {
		String rawPassword = "password123";
		String salt = Long.toString(System.currentTimeMillis());
		PasswordCodec codec = new PasswordCodec();

		String encrypted = codec.encrypt(rawPassword, salt);
		String decrypted = codec.decrypt(encrypted, salt);

		assertEquals(rawPassword, decrypted);
	}
}