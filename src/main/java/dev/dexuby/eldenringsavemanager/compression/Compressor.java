package dev.dexuby.eldenringsavemanager.compression;

public interface Compressor {

    byte[] compress(final byte[] bytes);

    byte[] decompress(final byte[] compressedBytes);

}
