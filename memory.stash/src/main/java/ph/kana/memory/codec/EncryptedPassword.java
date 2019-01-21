package ph.kana.memory.codec;

public final class EncryptedPassword {

	private final byte[] initializationVector;
	private final byte[] value;

	public EncryptedPassword(byte[] initializationVector, byte[] value) {
		this.initializationVector = initializationVector;
		this.value = value;
	}

	public byte[] getInitializationVector() {
		return initializationVector;
	}

	public byte[] getValue() {
		return value;
	}
}
