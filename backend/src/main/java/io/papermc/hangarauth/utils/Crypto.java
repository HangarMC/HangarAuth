package io.papermc.hangarauth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jetbrains.annotations.NotNull;

public final class Crypto {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private Crypto() {
    }

    public static @NotNull String bytesToHex(final byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        int j = 0;
        while (j < bytes.length) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];

            j++;
        }
        return new String(hexChars);
    }

    public static @NotNull String md5ToHex(final byte[] bytes) {
        try {
            return bytesToHex(MessageDigest.getInstance("MD5").digest(bytes));
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
