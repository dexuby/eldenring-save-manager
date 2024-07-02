package dev.dexuby.eldenringsavemanager.save.format;

public record GameFileHeader(byte fileFlags,
                             long compressedSize,
                             long uncompressedSize,
                             long dataOffset,
                             int id,
                             String name) {
}
