package ph.kana.memory.codec;

public final class EncryptedPassword {

	private final byte[] iv;
	private final byte[] value;

	public EncryptedPassword(byte[] iv, byte[] value) {
		this.iv = iv;
		this.value = value;
	}

	public byte[] getIv() {
		return iv;
	}

	public byte[] getValue() {
		return value;
	}
}
