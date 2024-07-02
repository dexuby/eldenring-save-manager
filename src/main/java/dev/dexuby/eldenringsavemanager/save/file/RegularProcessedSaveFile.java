package dev.dexuby.eldenringsavemanager.save.file;

import dev.dexuby.eldenringsavemanager.save.RegularSave;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegularProcessedSaveFile extends ProcessedSaveFile {

    private final GameSaveFile gameSaveFile;
    private final List<RegularSave> saves;

    public RegularProcessedSaveFile(@NotNull GameSaveFile gameSaveFile,
                                    @NotNull List<RegularSave> saves) {

        super(gameSaveFile.filePath());

        this.gameSaveFile = gameSaveFile;
        this.saves = saves;

    }

    public GameSaveFile getGameSaveFile() {

        return this.gameSaveFile;

    }

    public List<RegularSave> getSaves() {

        return this.saves;

    }

}
