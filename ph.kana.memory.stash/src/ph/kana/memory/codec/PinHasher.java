package ph.kana.memory.codec;

import java.nio.ByteBuffer;
import java.util.Base64;

public class PinHasher {

	public String hash(String pin) {
		Long pinNumber = Long.valueOf(pin);
		String hexPin = Long.toHexString(pinNumber);
		int hash = pin.hashCode() + pin.length()
				+ hexPin.hashCode() + hexPin.length();
		return encodeBase64(hash);
	}

	public boolean validate(String pin, String savedPin) {
		String hashedPin = hash(pin);
		return savedPin.equals(hashedPin);
	}

	private String encodeBase64(int hash) {
		byte[] bytes = ByteBuffer.allocate(Integer.BYTES)
				.putInt(hash)
				.array();
		return Base64.getEncoder()
				.encodeToString(bytes);
	}

}
