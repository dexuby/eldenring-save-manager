package dev.dexuby.eldenringsavemanager.menu.option;

import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.MenuOption;
import org.jetbrains.annotations.NotNull;

public class ExitMenuOption extends MenuOption {

    public ExitMenuOption(@NotNull final Menu parent,
                          final int index) {

        super(parent, index);

    }

    @Override
    public String prepareRender() {

        return String.format("[%d] Exit the application.", super.getIndex());

    }

    @Override
    public void execute() {

        System.exit(0);

    }

}
