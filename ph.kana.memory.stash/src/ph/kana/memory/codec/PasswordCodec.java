package ph.kana.memory.codec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordCodec {

	private final static char[] WAGSTAFF_PRIME = "1298074214633706835075030044377087".toCharArray();
	private final static String SEPARATOR = ":";

	public String encrypt(String rawPassword, String salt) throws CodecOperationException {
		try {
			SecretKeySpec keySpec = createSecretKey(WAGSTAFF_PRIME, salt.getBytes());
			Cipher cipher = fetchCipher();
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			AlgorithmParameters parameters = cipher.getParameters();
			IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
			byte[] cryptoText = cipher.doFinal(rawPassword.getBytes("UTF-8"));
			byte[] iv = ivParameterSpec.getIV();
			return base64Encode(iv) + SEPARATOR + base64Encode(cryptoText);
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			throw new CodecOperationException(e);
		}
	}

	public byte[] decrypt(String encryptedPassword, String salt) throws CodecOperationException {
		try {
			String[] split = encryptedPassword.split(SEPARATOR);
			String iv = split[0];
			String cryptoText = split[1];

			SecretKeySpec keySpec = createSecretKey(WAGSTAFF_PRIME, salt.getBytes());
			Cipher cipher = fetchCipher();
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(base64Decode(iv)));
			return cipher.doFinal(base64Decode(cryptoText));
		} catch (GeneralSecurityException e) {
			throw new CodecOperationException(e);
		}
	}

	private Cipher fetchCipher() throws GeneralSecurityException {
		return Cipher.getInstance("AES/CBC/PKCS5Padding");
	}

	private SecretKeySpec createSecretKey(char[] password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec keySpec = new PBEKeySpec(password, salt, 42000, 128);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		return new SecretKeySpec(secretKey.getEncoded(),"AES");
	}

	private String base64Encode(byte[] bytes) {
		return Base64.getEncoder()
				.encodeToString(bytes);
	}

	private byte[] base64Decode(String base64) {
		return Base64.getDecoder()
				.decode(base64);
	}
}
