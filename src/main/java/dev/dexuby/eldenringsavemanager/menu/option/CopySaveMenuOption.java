package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import dev.dexuby.eldenringsavemanager.menu.action.CopySaveAction;
import dev.dexuby.eldenringsavemanager.save.RegularSave;
import dev.dexuby.eldenringsavemanager.save.file.ExtractedProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.ProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.PrintStream;

public class CopySaveMenuOption extends MenuOption {

    private final PrintStream printStream;
    private final ServiceProvider serviceProvider;

    public CopySaveMenuOption(@NotNull final Menu parent,
                              final int index,
                              @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.printStream = serviceProvider.get(PrintStream.class);
        this.serviceProvider = serviceProvider;

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Copy a source save to a target save index.", super.getIndex());

    }

    @Override
    public void execute() {

        final ProcessedSaveFile source = super.getParent().getSession().getSource();
        final RegularProcessedSaveFile target = super.getParent().getSession().getTarget();
        if (source == null || target == null) {
            Logger.warn("Either the source or the target hasn't been loaded yet.");
            return;
        }

        if (source instanceof ExtractedProcessedSaveFile extractedProcessedSaveFile) {
            for (final RegularSave regularSave : target.getSaves())
                this.printStream.println(regularSave.toString());
            this.printStream.print("Please provide the index of the target save you want to override: ");
            super.getParent().getSession().queueInputConsumer(
                    new CopySaveAction(super.getParent().getSession(), extractedProcessedSaveFile.getSave(), this.serviceProvider)
            );
        } else if (source instanceof RegularProcessedSaveFile regularProcessedSaveFile) {
            for (final RegularSave regularSave : regularProcessedSaveFile.getSaves())
                this.printStream.println(regularSave.toString());
            this.printStream.print("Please provide the index of the source save you want to copy: ");
            super.getParent().getSession().queueInputConsumer(
                    new CopySaveAction(super.getParent().getSession(), regularProcessedSaveFile, this.serviceProvider)
            );
        }

    }

}
