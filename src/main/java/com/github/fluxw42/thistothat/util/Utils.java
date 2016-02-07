package com.github.fluxw42.thistothat.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Date: 2/6/16 - 1:02 PM
 *
 * @author Jeroen Meulemeester
 */
public class Utils {

    /**
     * The default buffer size, used while reading data from an input stream
     */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * Hide default constructor, since this is a util class
     */
    private Utils() {
        throw new IllegalAccessError();
    }

    /**
     * Calculate the SHA-256 hash over the bytes read from the given input stream
     *
     * @param inputStream The input stream
     * @return The 32 byte long SHA-256 hash as a byte array
     * @throws IOException              When there occurred an error while reading from the given
     *                                  {@link InputStream}
     * @throws IllegalArgumentException When the given {@link InputStream} is 'null'
     */
    public static byte[] sha256(final InputStream inputStream) throws IOException, IllegalArgumentException {
        Objects.requireNonNull(inputStream);

        try {
            final byte[] buffer = new byte[BUFFER_SIZE];
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 hashing algorithm unknown in this VM.", e);
        }
    }

    /**
     * Convert the given byte array to a hex string
     *
     * @param bytes The bytes that have to be converted
     * @return The bytes represented as an upper case hex string
     */
    public static String asHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        if (bytes != null) {
            for (final byte b : bytes) {
                sb.append(String.format("%02X", (int) b & 0xFF));
            }
        }
        return sb.toString();
    }

    /**
     * Convert a hex string (eg. 0A12B781) back to the byte values.
     *
     * @param hexString The hex string to convert to byte array
     * @return The bytes represented in the given hex string
     * @throws IllegalArgumentException If the hex string contains illegal characters, has an
     *                                  invalid length or is 'null'
     */
    public static byte[] fromHex(final String hexString) throws IllegalArgumentException {
        if (hexString == null) {
            throw new IllegalArgumentException("Cannot convert hex string 'null' to byte array!");
        }

        final String cleanHexString = hexString.trim();
        if (cleanHexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string [" + cleanHexString + "] should have an even number of nibbles, but length was [" + cleanHexString.length() + "]");
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < cleanHexString.length(); i += 2) {
            final String hexByte = cleanHexString.substring(i, i + 2);
            final int byteValue = Integer.parseInt(hexByte, 16);
            out.write(byteValue);
        }
        return out.toByteArray();
    }

}
