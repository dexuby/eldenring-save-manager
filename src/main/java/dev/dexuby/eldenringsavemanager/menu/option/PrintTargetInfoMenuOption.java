package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import dev.dexuby.eldenringsavemanager.menu.action.PrintSaveAction;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class PrintTargetInfoMenuOption extends MenuOption {

    private final PrintStream printStream;

    public PrintTargetInfoMenuOption(@NotNull final Menu parent,
                                     final int index,
                                     @NotNull final ServiceProvider serviceProvider) {

        super(parent, index);

        this.printStream = serviceProvider.get(PrintStream.class);

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Print target info.", super.getIndex());

    }

    @Override
    public void execute() {

        if (super.getParent().getSession().getTarget() == null) {
            this.printStream.println("No target is currently loaded.");
            return;
        }

        new PrintSaveAction(this.printStream, super.getParent().getSession().getTarget()).execute();
        super.getParent().render();

    }

}
