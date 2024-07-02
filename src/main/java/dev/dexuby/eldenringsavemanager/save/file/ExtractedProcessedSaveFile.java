package dev.dexuby.eldenringsavemanager.save.file;

import dev.dexuby.eldenringsavemanager.save.ExtractedSave;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class ExtractedProcessedSaveFile extends ProcessedSaveFile {

    private final ExtractedSave save;

    public ExtractedProcessedSaveFile(@NotNull final Path filePath,
                                      @NotNull final ExtractedSave save) {

        super(filePath);

        this.save = save;

    }

    public ExtractedSave getSave() {

        return this.save;

    }

}
