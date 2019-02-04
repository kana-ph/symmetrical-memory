package ph.kana.memory.codec;

import ph.kana.memory.codec.impl.DefaultPasswordCodecService;

public interface PasswordCodecService {

    PasswordCodecService INSTANCE = new DefaultPasswordCodecService();

    EncryptedPassword encrypt(String rawPassword, String salt) throws CodecOperationException;

    byte[] decrypt(EncryptedPassword encryptedPassword, String salt) throws CodecOperationException;
}
