package dev.dexuby.eldenringsavemanager.menu.impl;

import dev.dexuby.eldenringsavemanager.menu.Session;
import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.Menu;
import dev.dexuby.eldenringsavemanager.menu.option.*;
import dev.dexuby.eldenringsavemanager.save.file.ProcessedSaveFile;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Optional;

public class MainMenu extends Menu {

    public MainMenu(@NotNull final Session session,
                    @NotNull final ServiceProvider serviceProvider) {

        super(session, serviceProvider.getOrThrow(PrintStream.class));

        super.addOption(new ListMenuOption(this, 0, serviceProvider));
        super.addOption(new LoadSourceMenuOption(this, 1, serviceProvider));
        super.addOption(new PrintSourceInfoMenuOption(this, 2, serviceProvider));
        super.addOption(new LoadTargetMenuOption(this, 3, serviceProvider));
        super.addOption(new PrintTargetInfoMenuOption(this, 4, serviceProvider));
        super.addOption(new ExtractSaveMenuOption(this, 5, serviceProvider));
        super.addOption(new CopySaveMenuOption(this, 6, serviceProvider));
        super.addOption(new ExitMenuOption(this, 9));

    }

    @Override
    public void render() {

        super.getOutput().println();
        super.getOutput().println("-=============={ Elden Ring Save Manager }==============-");
        super.getOutput().println();
        super.getOutput().println(">> Loaded source file: " + Optional.ofNullable(super.getSession().getSource()).map(ProcessedSaveFile::toString).orElse("< None >"));
        super.getOutput().println(">> Loaded target file: " + Optional.ofNullable(super.getSession().getTarget()).map(ProcessedSaveFile::toString).orElse("< None >"));
        super.getOutput().println();
        super.render();

    }

}
