package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import dev.dexuby.eldenringsavemanager.menu.action.LoadSaveAction;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class LoadSourceMenuOption extends MenuOption {

    private final PrintStream printStream;
    private final ServiceProvider serviceProvider;

    public LoadSourceMenuOption(@NotNull final Menu parent,
                                final int index,
                                @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.printStream = serviceProvider.get(PrintStream.class);
        this.serviceProvider = serviceProvider;

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Load source.", super.getIndex());

    }

    @Override
    public void execute() {

        this.printStream.print("Provide the save file path: ");
        super.getParent().getSession().queueInputConsumer(new LoadSaveAction((processedSaveFile) -> {
            super.getParent().getSession().setSource(processedSaveFile);
            this.printStream.println("Source has been updated to: " + processedSaveFile.getFilePath());
            this.printStream.println();
            super.getParent().render();
        }, this.serviceProvider));

    }

}
