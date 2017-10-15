package ph.kana.memory.codec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PinHasherTest {

	private static final PinHasher pinHasher = new PinHasher();

	@Test
	public void hash_acceptNumberOnly() throws Exception {
		String clearString = "1234abcd";
		assertThrows(NumberFormatException.class, () -> {
			pinHasher.hash(clearString);
		});
	}

	@Test
	public void hash_clearString_hashString() throws Exception {
		String clearString = "12345";

		String hashString = pinHasher.hash(clearString);

		assertNotEquals(clearString, hashString);
	}

	@Test
	public void hash_idempotent() throws Exception {
		String clearString = "12345";

		String hash1 = pinHasher.hash(clearString);
		String hash2 = pinHasher.hash(clearString);

		assertEquals(hash1, hash2);
	}
}