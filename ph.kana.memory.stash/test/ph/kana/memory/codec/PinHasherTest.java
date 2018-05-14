package ph.kana.memory.codec;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

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

		byte[] hashString = pinHasher.hash(clearString);

		assertFalse(Arrays.equals(clearString.getBytes(), hashString));
	}

	@Test
	public void hash_idempotent() throws Exception {
		String clearString = "12345";

		byte[] hash1 = pinHasher.hash(clearString);
		byte[] hash2 = pinHasher.hash(clearString);

		assertArrayEquals(hash1, hash2);
	}
}