package dev.dexuby.eldenringsavemanager.menu;

import org.jetbrains.annotations.NotNull;

public abstract class MenuOption {

    private final Menu parent;
    private final int index;

    public MenuOption(@NotNull final Menu parent,
                      final int index) {

        this.parent = parent;
        this.index = index;

    }

    public Menu getParent() {

        return this.parent;

    }

    public int getIndex() {

        return this.index;

    }

    public abstract String prepareRender();

    public abstract void execute();

}
