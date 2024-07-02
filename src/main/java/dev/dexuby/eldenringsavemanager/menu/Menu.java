package dev.dexuby.eldenringsavemanager.menu;

import dev.dexuby.eldenringsavemanager.menu.input.InputConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Menu implements InputConsumer {

    private final Session session;
    private final PrintStream printStream;
    private final Map<Integer, MenuOption> options;

    public Menu(@NotNull final Session session,
                @NotNull final PrintStream printStream) {

        this.session = session;
        this.printStream = printStream;
        this.options = new LinkedHashMap<>();

    }

    public Menu(@NotNull final Session session,
                @NotNull final PrintStream printStream,
                @NotNull final Map<Integer, MenuOption> options) {

        this.session = session;
        this.printStream = printStream;
        this.options = options;

    }

    public Session getSession() {

        return this.session;

    }

    public PrintStream getOutput() {

        return this.printStream;

    }

    public Map<Integer, MenuOption> getOptions() {

        return this.options;

    }

    public void addOption(@NotNull final MenuOption option) {

        this.options.put(option.getIndex(), option);

    }

    public void addOptions(@NotNull final MenuOption... options) {

        for (final MenuOption option : options)
            this.options.put(option.getIndex(), option);

    }

    public void render() {

        for (final Map.Entry<Integer, MenuOption> entry : this.options.entrySet())
            this.printStream.println(entry.getValue().prepareRender());
        this.printStream.println();
        this.printStream.print("Please provide your selection: ");

    }

    @Override
    public void processInput(@NotNull final String input) {

        int index;
        try {
            index = Integer.parseInt(input);
        } catch (final NumberFormatException ex) {
            index = -1;
        }

        if (index != -1) {
            final MenuOption option = this.options.get(index);
            if (option != null)
                option.execute();
        } else {
            this.render();
        }

    }

}
