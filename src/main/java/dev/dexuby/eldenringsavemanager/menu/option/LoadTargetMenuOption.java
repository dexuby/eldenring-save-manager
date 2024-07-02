package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import dev.dexuby.eldenringsavemanager.menu.action.LoadSaveAction;
import dev.dexuby.eldenringsavemanager.save.file.ExtractedProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.PrintStream;

public class LoadTargetMenuOption extends MenuOption {

    private final PrintStream printStream;
    private final ServiceProvider serviceProvider;

    public LoadTargetMenuOption(@NotNull final Menu parent,
                                final int index,
                                @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.printStream = serviceProvider.get(PrintStream.class);
        this.serviceProvider = serviceProvider;

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Load target.", super.getIndex());

    }

    @Override
    public void execute() {

        this.printStream.print("Provide the save file path: ");
        super.getParent().getSession().queueInputConsumer(new LoadSaveAction((processedSaveFile) -> {
            if (processedSaveFile instanceof ExtractedProcessedSaveFile) {
                Logger.warn("You can't load an extracted save file as a target.");
                return;
            }
            super.getParent().getSession().setTarget((RegularProcessedSaveFile) processedSaveFile);
            this.printStream.println("Target has been updated to: " + processedSaveFile.getFilePath());
            this.printStream.println();
            super.getParent().render();
        }, this.serviceProvider));

    }

}
