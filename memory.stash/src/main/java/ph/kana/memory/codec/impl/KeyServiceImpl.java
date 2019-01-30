package ph.kana.memory.codec.impl;

import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.KeyService;
import ph.kana.memory.file.FileLocationHolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyServiceImpl implements KeyService {

    private final static int KEY_SIZE = 1024;

    @Override
    public byte[] fetchKey() throws CodecOperationException {
        var keyFile = FileLocationHolder.getInstance()
            .getKey();

        try {
            if (!keyFile.exists()) {
                buildKeyFile(keyFile);
            }

            return Files.readAllBytes(keyFile.toPath());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new CodecOperationException(e);
        }
    }

    private void buildKeyFile(File file) throws IOException, NoSuchAlgorithmException {
        var keyGen = KeyPairGenerator.getInstance("DSA");
        var rng = SecureRandom.getInstance("NativePRNGNonBlocking");
        keyGen.initialize(KEY_SIZE, rng);

        var keyPair = keyGen.generateKeyPair();
        var key = keyPair.getPublic();

        Files.write(file.toPath(), key.getEncoded());
    }
}
