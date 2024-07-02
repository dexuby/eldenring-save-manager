package dev.dexuby.eldenringsavemanager.util;

import java.nio.charset.Charset;

public final class NumberUtils {

    public static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static String byteToHexString(byte b) {

        final byte[] buffer = new byte[2];
        buffer[1] = (byte) DIGITS[b & 0xF];
        buffer[0] = (byte) DIGITS[b >>> 4 & 0xF];

        return new String(buffer, 0, 2, Charset.defaultCharset());

    }

    public static String bytesToHexString(final byte[] bytes) {

        final StringBuilder stringBuilder = new StringBuilder();
        for (final byte b : bytes)
            stringBuilder.append(byteToHexString(b));

        return stringBuilder.toString();

    }

    private NumberUtils() {

        throw new UnsupportedOperationException();

    }

}
