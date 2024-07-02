package dev.dexuby.eldenringsavemanager.compression;

import com.github.luben.zstd.Zstd;

public class ZstdCompressor implements Compressor {

    // Compression level 8 should provide a good enough ratio without slowing the compression process down too much.
    private static final int COMPRESSION_LEVEL = 8;

    @Override
    public byte[] compress(final byte[] bytes) {

        return Zstd.compress(bytes, COMPRESSION_LEVEL);

    }

    public byte[] decompress(final byte[] compressedBytes) {

        final int decompressedSize = (int) Zstd.getFrameContentSize(compressedBytes);
        final byte[] decompressedBytes = new byte[decompressedSize];
        Zstd.decompress(decompressedBytes, compressedBytes);

        return decompressedBytes;

    }

}
