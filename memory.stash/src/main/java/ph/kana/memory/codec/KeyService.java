package ph.kana.memory.codec;

public interface KeyService {

    byte[] fetchKey() throws CodecOperationException;
}
