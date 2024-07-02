package dev.dexuby.eldenringsavemanager.save.file;

import dev.dexuby.eldenringsavemanager.save.format.GameFile;
import dev.dexuby.eldenringsavemanager.save.format.GameFileHeader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * Elden Ring save file containing multiple saves and other data.
 */

public record GameSaveFile(boolean bigEndian,
                           boolean bitBigEndian,
                           int fileCount,
                           long headerSize,
                           @NotNull String version,
                           long fileHeaderSize,
                           boolean unicode,
                           byte rawFormat,
                           byte format,
                           byte extended,
                           long expectedHeaderSize,
                           byte[] data,
                           @NotNull List<GameFileHeader> gameFileHeaders,
                           @NotNull List<GameFile> gameFiles,
                           @NotNull Path filePath) {
}
