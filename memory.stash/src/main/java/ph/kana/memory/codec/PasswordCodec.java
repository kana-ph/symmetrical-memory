package ph.kana.memory.codec;

import ph.kana.memory.file.FileLocationHolder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PasswordCodec {

	public EncryptedPassword encrypt(String rawPassword, String salt) throws CodecOperationException {
		try {
			var keySpec = createSecretKey(salt.getBytes());
			var cipher = fetchCipher();
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			var parameters = cipher.getParameters();
			var ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
			byte[] text = cipher.doFinal(stringToBytes(rawPassword));
			byte[] iv = ivParameterSpec.getIV();
			return new EncryptedPassword(iv, text);
		} catch (GeneralSecurityException e) {
			throw new CodecOperationException(e);
		}
	}

	public byte[] decrypt(EncryptedPassword encryptedPassword, String salt) throws CodecOperationException {
		try {
			var keySpec = createSecretKey(salt.getBytes());
			var cipher = fetchCipher();
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(encryptedPassword.getIv()));
			return cipher.doFinal(encryptedPassword.getValue());
		} catch (GeneralSecurityException e) {
			throw new CodecOperationException(e);
		}
	}

	private Cipher fetchCipher() throws GeneralSecurityException {
		return Cipher.getInstance("AES/CBC/PKCS5Padding");
	}

	private SecretKeySpec createSecretKey(byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
		var keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		var key = FileLocationHolder.getInstance()
			.getKey();
		var keySpec = new PBEKeySpec(key, salt, 42000, 128);
		var secretKey = keyFactory.generateSecret(keySpec);
		return new SecretKeySpec(secretKey.getEncoded(),"AES");
	}

	private byte[] stringToBytes(String string) {
		return string.getBytes(StandardCharsets.UTF_8);
	}
}
