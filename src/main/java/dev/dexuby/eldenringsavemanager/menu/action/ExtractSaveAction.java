package dev.dexuby.eldenringsavemanager.menu.action;

import dev.dexuby.eldenringsavemanager.menu.Session;
import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.common.tuple.ImmutableKeyValuePair;
import dev.dexuby.eldenringsavemanager.file.FileManager;
import dev.dexuby.eldenringsavemanager.menu.input.InputConsumer;
import dev.dexuby.eldenringsavemanager.save.RegularSave;
import dev.dexuby.eldenringsavemanager.save.SaveManager;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class ExtractSaveAction extends Action implements InputConsumer {

    private final Session session;
    private final RegularProcessedSaveFile regularProcessedSaveFile;
    private final PrintStream printStream;
    private final SaveManager saveManager;

    public ExtractSaveAction(@NotNull final Session session,
                             @NotNull final RegularProcessedSaveFile regularProcessedSaveFile,
                             @NotNull final ServiceProvider serviceProvider) {

        this.session = session;
        this.regularProcessedSaveFile = regularProcessedSaveFile;
        this.printStream = serviceProvider.get(PrintStream.class);
        this.saveManager = serviceProvider.get(SaveManager.class);

    }

    @Override
    public void processInput(@NotNull final String input) {

        int index;
        try {
            index = Integer.parseInt(input);
        } catch (final NumberFormatException ex) {
            Logger.error(ex, "The provided index was invalid.");
            return;
        }

        final RegularSave save = this.saveManager.findSaveByIndex(this.regularProcessedSaveFile, index);
        if (save == null) {
            Logger.warn("No save found for index {}.", index);
            return;
        }

        this.printStream.print("Please provide the target file name: ");
        this.session.queueInputConsumer(fileName -> {
            try {
                Logger.info("Extracting save at index {}...", index);
                if (!fileName.toLowerCase().endsWith(".er"))
                    fileName = fileName + ".er";
                File file = Paths.get(FileManager.WORKING_DIRECTORY_PATH.toString(), fileName).toFile();
                if (file.exists()) {
                    Logger.warn("The provided file path already contains a file.");
                    return;
                }
                final ImmutableKeyValuePair<Integer, Integer> sizePair = this.saveManager.extractSaveToFile(save, file);
                Logger.info("Save at index {} has been extracted to file: {} - Uncompressed size: {} / Compressed size: {}", index,
                        file.getAbsolutePath(), sizePair.getKey(), sizePair.getValue());
                this.printStream.println();
                this.session.getActiveMenu().render();
            } catch (final InvalidPathException | NullPointerException ex) {
                Logger.error(ex, "The provided extraction target path is invalid.");
            }
        });

    }

}
