package ph.kana.memory.codec;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PinHasher {

	public byte[] hash(String pin) {
		Long pinNumber = Long.valueOf(pin);
		String hexPin = Long.toHexString(pinNumber);
		int hash = pin.hashCode() + pin.length()
				+ hexPin.hashCode() + hexPin.length();
		return fetchBytes(hash);
	}

	public boolean validate(String pin, byte[] savedPin) {
		byte[] hashedPin = hash(pin);
		return Arrays.equals(savedPin, hashedPin);
	}

	private byte[] fetchBytes(int hash) {
		return ByteBuffer.allocate(Integer.BYTES)
				.putInt(hash)
				.array();
	}

}
