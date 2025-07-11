package me.redshore.web_gagebu.common.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AesEncryptorTest {

    @Autowired
    private AesEncryptor aesEncryptor;

    @Nested
    class Describe_decrypt {

        private final byte[] testData = "Hello, World!".getBytes();

        @Test
        void it_should_decrypt_data() {
            final String dataEncrypted = assertDoesNotThrow(
                () -> aesEncryptor.encrypt(testData));
            final byte[] dataDecrypted = assertDoesNotThrow(
                () -> aesEncryptor.decrypt(dataEncrypted));
            assertArrayEquals(testData, dataDecrypted);
        }

    }
}
