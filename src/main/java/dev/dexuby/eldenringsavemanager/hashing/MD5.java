package dev.dexuby.eldenringsavemanager.hashing;

import dev.dexuby.eldenringsavemanager.util.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private final MessageDigest messageDigest;

    public MD5() {

        try {
            this.messageDigest = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException ex) {
            Logger.error(ex);
            throw new RuntimeException(ex);
        }

    }

    public byte[] hash(final byte[] input) {

        return this.messageDigest.digest(input);

    }

    @NotNull
    public String toHexString(final byte[] input) {

        return NumberUtils.bytesToHexString(input);

    }

}
