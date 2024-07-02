package dev.dexuby.eldenringsavemanager.menu;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.menu.impl.MainMenu;
import dev.dexuby.eldenringsavemanager.menu.input.InputConsumer;
import dev.dexuby.eldenringsavemanager.save.file.ProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

public class Session {

    private final Queue<InputConsumer> inputConsumers = new ArrayDeque<>();
    private final ServiceProvider serviceProvider;

    private boolean started = false;
    private Menu activeMenu;
    private ProcessedSaveFile source;
    private RegularProcessedSaveFile target;

    public Session(@NotNull final ServiceProvider serviceProvider) {

        this.serviceProvider = serviceProvider;

    }

    public void queueInputConsumer(@NotNull final InputConsumer inputConsumer) {

        this.inputConsumers.add(inputConsumer);

    }

    @NotNull
    public Menu getActiveMenu() {

        if (!this.started)
            throw new IllegalStateException("Session has not been started yet.");

        return this.activeMenu;

    }

    public void setActiveMenu(@NotNull final Menu activeMenu) {

        if (!this.started)
            throw new IllegalStateException("Session has not been started yet.");

        this.activeMenu = activeMenu;

    }

    @Nullable
    public ProcessedSaveFile getSource() {

        return this.source;

    }

    public void setSource(@Nullable final ProcessedSaveFile source) {

        this.source = source;

    }

    @Nullable
    public RegularProcessedSaveFile getTarget() {

        return this.target;

    }

    public void setTarget(@Nullable final RegularProcessedSaveFile target) {

        this.target = target;

    }

    public void start() {

        this.started = true;
        this.activeMenu = new MainMenu(this, this.serviceProvider);
        this.activeMenu.render();

    }

    public void processInput(@NotNull final String input) {

        if (!this.started) return;
        if (this.inputConsumers.isEmpty()) {
            if (this.activeMenu != null)
                this.activeMenu.processInput(input);
        } else {
            this.inputConsumers.poll().processInput(input);
        }

    }

}
