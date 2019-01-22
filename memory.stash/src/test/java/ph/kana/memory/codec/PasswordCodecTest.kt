package ph.kana.memory.codec

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class PasswordCodecTest {

    @Test
    fun `decrypt should be able to return the original encrypted text`() {
        val codec = PasswordCodec()

        val original = "password123"
        val salt = "salt"
        val encrypted = codec.encrypt(original, salt)

        val decrypted = codec.decrypt(encrypted, salt)

        assertEquals(String(decrypted), original)
    }
}