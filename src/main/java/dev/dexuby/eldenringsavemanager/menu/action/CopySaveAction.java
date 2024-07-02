package dev.dexuby.eldenringsavemanager.menu.action;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.file.FileManager;
import dev.dexuby.eldenringsavemanager.menu.Session;
import dev.dexuby.eldenringsavemanager.menu.input.InputConsumer;
import dev.dexuby.eldenringsavemanager.save.RegularSave;
import dev.dexuby.eldenringsavemanager.save.Save;
import dev.dexuby.eldenringsavemanager.save.SaveManager;
import dev.dexuby.eldenringsavemanager.save.file.GameSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Objects;

public class CopySaveAction extends Action implements InputConsumer {

    private final Session session;
    private final RegularProcessedSaveFile regularProcessedSaveFile;
    private final PrintStream printStream;
    private final SaveManager saveManager;
    private final FileManager fileManager;

    private Save save = null;

    public CopySaveAction(@NotNull final Session session,
                          @NotNull final RegularProcessedSaveFile regularProcessedSaveFile,
                          @NotNull final ServiceProvider serviceProvider) {

        this.session = session;
        this.regularProcessedSaveFile = regularProcessedSaveFile;
        this.printStream = serviceProvider.get(PrintStream.class);
        this.saveManager = serviceProvider.get(SaveManager.class);
        this.fileManager = serviceProvider.get(FileManager.class);

    }

    public CopySaveAction(@NotNull final Session session,
                          @NotNull final Save save,
                          @NotNull final ServiceProvider serviceProvider) {

        this.session = session;
        this.regularProcessedSaveFile = null;
        this.save = save;
        this.printStream = serviceProvider.get(PrintStream.class);
        this.saveManager = serviceProvider.get(SaveManager.class);
        this.fileManager = serviceProvider.get(FileManager.class);

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

        if (this.save == null && this.regularProcessedSaveFile != null) {
            final RegularSave save = this.saveManager.findSaveByIndex(this.regularProcessedSaveFile, index);
            if (save == null) {
                Logger.warn("No save found for index {}.", index);
                return;
            }
            this.save = save;
            final RegularProcessedSaveFile target = this.session.getTarget();
            for (final RegularSave regularSave : Objects.requireNonNull(target).getSaves())
                this.printStream.println(regularSave.toString());
            this.printStream.print("Please provide the index of the target save you want to override: ");
            this.session.queueInputConsumer(this);
        } else {
            final RegularProcessedSaveFile targetSaveFile = this.session.getTarget();
            final RegularProcessedSaveFile updatedSaveFile = this.saveManager.injectAndCreateUpdatedSave(
                    this.save,
                    Objects.requireNonNull(targetSaveFile),
                    index
            );
            this.printStream.print("Please provide the target file path for the updated save or type 'override' to override the original file of the current target: ");
            this.session.queueInputConsumer((targetPath) -> {
                File targetFile;
                if (targetPath.equalsIgnoreCase("override")) {
                    targetFile = this.session.getTarget().getFilePath().toFile();
                    Logger.info("Overriding existing save file at {}...", targetFile.getAbsolutePath());
                } else {
                    if (!targetPath.contains("."))
                        targetPath = targetPath + ".sl2";
                    targetFile = Paths.get(targetPath).toFile();
                    Logger.info("Attempting to save to file {}...", targetFile.getAbsolutePath());
                }
                this.fileManager.saveBytes(targetFile, updatedSaveFile.getGameSaveFile().data());
                Logger.info("Updated save has been stored at {}.", targetFile.getAbsolutePath());
                this.printStream.print("Do you want to reload the current target from the updated one [Y/N]? ");
                this.session.queueInputConsumer((updateTarget) -> {
                    if (updateTarget.equalsIgnoreCase("y")) {
                        final GameSaveFile gameSaveFile = this.saveManager.loadSaveFile(targetFile);
                        if (gameSaveFile == null) {
                            Logger.error("Failed to load game save from the target file: {}", targetFile.getAbsolutePath());
                            return;
                        }
                        final RegularProcessedSaveFile saveFile = this.saveManager.processSaveFile(gameSaveFile);
                        this.session.setTarget(saveFile);
                        if (Objects.requireNonNull(this.session.getSource()).getFilePath().toString().equals(saveFile.getFilePath().toString())) {
                            this.printStream.println("Since the source and target were targeting the same file the source has also been reloaded.");
                            this.session.setSource(saveFile);
                        }
                        this.printStream.println("The loaded target has been switched to the updated target.");
                    } else if (updateTarget.equalsIgnoreCase("n")) {
                        this.printStream.println("The loaded target save has not been updated and will still represent the previous state.");
                    } else {
                        this.printStream.println("The provided input was unspecified, the loaded target save has not been updated and will still represent the previous state.");
                    }
                    this.printStream.println();
                    this.session.getActiveMenu().render();
                });
            });
        }

    }

}
