package dev.dexuby.eldenringsavemanager.menu.action;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.file.FileManager;
import dev.dexuby.eldenringsavemanager.menu.input.InputConsumer;
import dev.dexuby.eldenringsavemanager.save.SaveManager;
import dev.dexuby.eldenringsavemanager.save.file.GameSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.ProcessedSaveFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class LoadSaveAction extends Action implements InputConsumer {

    private final Consumer<ProcessedSaveFile> consumer;
    private final SaveManager saveManager;

    public LoadSaveAction(@NotNull final Consumer<ProcessedSaveFile> consumer,
                          @NotNull final ServiceProvider serviceProvider) {

        this.consumer = consumer;
        this.saveManager = serviceProvider.get(SaveManager.class);

    }

    @Override
    public void processInput(@NotNull final String input) {

        ProcessedSaveFile processedSaveFile = null;
        File file;
        if ((input.indexOf('.') == -1 && !input.contains(File.pathSeparator) && !input.contains("\\")) || input.toLowerCase().endsWith(".er")) {
            file = Paths.get(FileManager.WORKING_DIRECTORY_PATH.toString(), input.toLowerCase().endsWith(".er") ? input : input + ".er").toFile();
        } else {
            file = new File(input);
        }

        if (file.exists()) {
            if (file.getName().toLowerCase().endsWith(".er")) {
                processedSaveFile = this.saveManager.loadExtractedSaveFromFile(file);
            } else {
                final GameSaveFile gameSaveFile = this.saveManager.loadSaveFile(file);
                if (gameSaveFile == null)
                    throw new RuntimeException("Failed to load game save from file: " + file.getAbsolutePath());
                processedSaveFile = this.saveManager.processSaveFile(gameSaveFile);
            }
        }

        if (processedSaveFile == null)
            throw new RuntimeException("Failed to load processed save file from file: " + file.getAbsolutePath());

        this.consumer.accept(processedSaveFile);

    }

}
