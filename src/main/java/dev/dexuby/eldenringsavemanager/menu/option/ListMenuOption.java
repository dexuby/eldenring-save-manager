package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.file.FileManager;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class ListMenuOption extends MenuOption {

    private final FileManager fileManager;
    private final PrintStream printStream;

    public ListMenuOption(@NotNull final Menu parent,
                          final int index,
                          @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.fileManager = serviceProvider.get(FileManager.class);
        this.printStream = serviceProvider.get(PrintStream.class);

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Lists all available custom saves.", super.getIndex());

    }

    @Override
    public void execute() {

        this.printStream.println();
        this.printStream.println("Custom saves:");
        final List<File> saveFiles = this.fileManager.getExtractedSaveFiles();
        if (!saveFiles.isEmpty()) {
            for (final File file : saveFiles) {
                if (!file.getName().endsWith(".er")) continue;
                this.printStream.println("- " + file.getName());
            }
        } else {
            this.printStream.println("- <None>");
        }
        this.printStream.println();
        super.getParent().render();

    }

}
