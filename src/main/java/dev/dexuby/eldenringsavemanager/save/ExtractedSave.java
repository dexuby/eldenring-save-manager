package dev.dexuby.eldenringsavemanager.save;

import dev.dexuby.eldenringsavemanager.save.format.SaveHeaderInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Extracted save stored in the .er file format.
 */

public class ExtractedSave extends Save {

    private final int compressedSize;
    private final int decompressedSize;

    public ExtractedSave(final int compressedSize,
                         final int decompressedSize,
                         @NotNull final SaveHeaderInfo saveHeaderInfo,
                         final byte[] headerData,
                         final byte[] saveDataChecksum,
                         final byte[] saveData) {

        super(saveHeaderInfo, headerData, saveDataChecksum, saveData);

        this.compressedSize = compressedSize;
        this.decompressedSize = decompressedSize;

    }

    public int getCompressedSize() {

        return this.compressedSize;

    }

    public int getDecompressedSize() {

        return this.decompressedSize;

    }

    @Override
    public String toString() {

        return String.format("%s [Compressed size: %d, Decompressed size: %d]", super.getSaveHeaderInfo().toString(), compressedSize, decompressedSize);

    }

}
