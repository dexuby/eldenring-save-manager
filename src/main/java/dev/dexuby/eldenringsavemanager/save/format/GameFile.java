package dev.dexuby.eldenringsavemanager.save.format;

public record GameFile(GameFileHeader gameFileHeader,
                       byte[] data) {
}
