package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import dev.dexuby.eldenringsavemanager.menu.action.PrintSaveAction;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class PrintSourceInfoMenuOption extends MenuOption {

    private final PrintStream printStream;

    public PrintSourceInfoMenuOption(@NotNull final Menu parent,
                                     final int index,
                                     @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.printStream = serviceProvider.get(PrintStream.class);

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Print source file info.", super.getIndex());

    }

    @Override
    public void execute() {

        if (super.getParent().getSession().getSource() == null) {
            this.printStream.println("No source file is currently loaded.");
            super.getParent().render();
            return;
        }

        new PrintSaveAction(this.printStream, super.getParent().getSession().getSource()).execute();
        super.getParent().render();

    }

}
