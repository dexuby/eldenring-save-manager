package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import dev.dexuby.eldenringsavemanager.menu.action.ExtractSaveAction;
import dev.dexuby.eldenringsavemanager.save.RegularSave;
import dev.dexuby.eldenringsavemanager.save.file.ExtractedProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class ExtractSaveMenuOption extends MenuOption {

    private final PrintStream printStream;
    private final ServiceProvider serviceProvider;

    public ExtractSaveMenuOption(@NotNull final Menu parent,
                                 final int index,
                                 @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.printStream = serviceProvider.get(PrintStream.class);
        this.serviceProvider = serviceProvider;

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Extract a single source save to a compressed save file.", super.getIndex());

    }

    @Override
    public void execute() {

        switch (super.getParent().getSession().getSource()) {
            case null -> {
                this.printStream.println("There is currently no source loaded.");
            }
            case ExtractedProcessedSaveFile _ ->
                    this.printStream.println("The current source is already an extracted save and can't be extracted again.");
            case RegularProcessedSaveFile regularProcessedSaveFile -> {
                for (final RegularSave regularSave : regularProcessedSaveFile.getSaves())
                    this.printStream.println(regularSave.toString());
                this.printStream.println("==============================");
                this.printStream.print("Please provide the index of the save you want to extract: ");
                super.getParent().getSession().queueInputConsumer(
                        new ExtractSaveAction(super.getParent().getSession(), regularProcessedSaveFile, this.serviceProvider)
                );
            }
            default -> {
            }
        }

    }

}
