package me.redshore.web_gagebu.common.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import me.redshore.web_gagebu.common.config.properties.EncryptionProperties;
import org.springframework.stereotype.Component;

@Component
public class AesEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTE = 12;
    private static final int TAG_LENGTH_BIT = 128;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Encoder B64_ENCODER = Base64.getEncoder();
    private static final Decoder B64_DECODER = Base64.getDecoder();

    private final SecretKey secretKey;
    private final Cipher cipher;

    public AesEncryptor(EncryptionProperties encryptionProperties) throws
                                                                   NoSuchAlgorithmException,
                                                                   NoSuchPaddingException {
        final var key = encryptionProperties.getKey();
        final var keyBytes = MessageDigest.getInstance("SHA-256")
                                          .digest(key.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        this.cipher = Cipher.getInstance(TRANSFORMATION);
    }

    public String encrypt(byte[] data) throws
                                       InvalidAlgorithmParameterException,
                                       InvalidKeyException,
                                       IllegalBlockSizeException,
                                       BadPaddingException {
        final byte[] iv = new byte[IV_LENGTH_BYTE];
        RANDOM.nextBytes(iv);

        final var gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, gcmParameterSpec);
        final byte[] encryptedData = this.cipher.doFinal(data);

        final var byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);
        final byte[] ivAndEncryptedData = byteBuffer.array();

        return B64_ENCODER.encodeToString(ivAndEncryptedData);
    }

    public byte[] decrypt(String data) throws
                                       InvalidAlgorithmParameterException,
                                       InvalidKeyException,
                                       IllegalBlockSizeException,
                                       BadPaddingException {
        final byte[] ivAndEncryptedData = B64_DECODER.decode(data);

        final var byteBuffer = ByteBuffer.wrap(ivAndEncryptedData);
        final byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);
        final byte[] encryptedData = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedData);

        final var gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey, gcmParameterSpec);
        return this.cipher.doFinal(encryptedData);
    }

}
