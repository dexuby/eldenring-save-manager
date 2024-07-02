package dev.dexuby.eldenringsavemanager.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ByteAnalyzer {

    private final ByteReader byteReader;

    public ByteAnalyzer(@NotNull final ByteReader byteReader) {

        this.byteReader = byteReader;

    }

    public List<Integer> scanForOffsets(final long value) {

        final List<Integer> offsets = new ArrayList<>();
        while (this.byteReader.getOffset() <= (this.byteReader.getLength() - 8)) {
            final long current = byteReader.peakLittleEndianLong();
            if (current == value)
                offsets.add(this.byteReader.getOffset());
            byteReader.skip(1);
        }

        return offsets;

    }

}
