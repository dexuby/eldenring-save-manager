package dev.dexuby.eldenringsavemanager.menu.action;

import dev.dexuby.eldenringsavemanager.save.RegularSave;
import dev.dexuby.eldenringsavemanager.save.file.ExtractedProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.ProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class PrintSaveAction extends Action {

    private final PrintStream printStream;
    private final ProcessedSaveFile processedSaveFile;

    public PrintSaveAction(@NotNull final PrintStream printStream,
                           @NotNull final ProcessedSaveFile processedSaveFile) {

        this.printStream = printStream;
        this.processedSaveFile = processedSaveFile;

    }

    @Override
    public void execute() {

        this.printStream.println();
        switch (this.processedSaveFile) {
            case ExtractedProcessedSaveFile extractedProcessedSaveFile -> {
                this.printStream.println("Type: Extracted save file (Singular save)");
                this.printStream.println("File path: " + processedSaveFile.getFilePath().toString());
                this.printStream.println("Save:");
                this.printStream.println(extractedProcessedSaveFile.getSave().toString());
            }
            case RegularProcessedSaveFile regularProcessedSaveFile -> {
                this.printStream.println("Type: Regular save file (Multiple saves)");
                this.printStream.println("File path: " + processedSaveFile.getFilePath().toString());
                this.printStream.println("Saves:");
                this.printStream.println("==============================");
                for (final RegularSave regularSave : regularProcessedSaveFile.getSaves()) {
                    this.printStream.println(regularSave);
                    this.printStream.println("==============================");
                }
            }
            default -> {
            }
        }

    }

}
