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

public class PasswordCodec {

	private final static char[] WAGSTAFF_PRIME = "1298074214633706835075030044377087".toCharArray();

	public EncryptedPassword encrypt(String rawPassword, String salt) throws CodecOperationException {
		try {
			SecretKeySpec keySpec = createSecretKey(salt.getBytes());
			Cipher cipher = fetchCipher();
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			AlgorithmParameters parameters = cipher.getParameters();
			IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
			byte[] text = cipher.doFinal(rawPassword.getBytes("UTF-8"));
			byte[] iv = ivParameterSpec.getIV();
			return new EncryptedPassword(iv, text);
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			throw new CodecOperationException(e);
		}
	}

	public byte[] decrypt(EncryptedPassword encryptedPassword, String salt) throws CodecOperationException {
		try {
			SecretKeySpec keySpec = createSecretKey(salt.getBytes());
			Cipher cipher = fetchCipher();
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(encryptedPassword.getInitializationVector()));
			return cipher.doFinal(encryptedPassword.getValue());
		} catch (GeneralSecurityException e) {
			throw new CodecOperationException(e);
		}
	}

	private Cipher fetchCipher() throws GeneralSecurityException {
		return Cipher.getInstance("AES/CBC/PKCS5Padding");
	}

	private SecretKeySpec createSecretKey(byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec keySpec = new PBEKeySpec(WAGSTAFF_PRIME, salt, 42000, 128);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		return new SecretKeySpec(secretKey.getEncoded(),"AES");
	}
}
