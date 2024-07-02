package dev.dexuby.eldenringsavemanager;

import dev.dexuby.eldenringsavemanager.common.dependencyinjection.InstanceServiceProvider;
import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.compression.Compressor;
import dev.dexuby.eldenringsavemanager.compression.ZstdCompressor;
import dev.dexuby.eldenringsavemanager.file.FileManager;
import dev.dexuby.eldenringsavemanager.menu.Session;
import dev.dexuby.eldenringsavemanager.save.SaveManager;
import dev.dexuby.eldenringsavemanager.hashing.MD5;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.*;

public class EldenRingSave {

    private static class EldenRingSaveSingleton {

        private static final EldenRingSave INSTANCE = new EldenRingSave();

    }

    public static void main(@NotNull final String[] args) {

        EldenRingSave.getInstance().initialize();

    }

    private final ServiceProvider serviceProvider = InstanceServiceProvider.builder()
            .service(Compressor.class, new ZstdCompressor())
            .service(PrintStream.class, System.out)
            .service(new MD5())
            .selfService()
            .build();

    private EldenRingSave() {

    }

    public void initialize() {

        final FileManager fileManager = new FileManager();
        this.serviceProvider.register(fileManager);
        fileManager.createDefaults();

        final SaveManager saveManager = new SaveManager(this.serviceProvider);
        this.serviceProvider.register(saveManager);

        final Session session = new Session(this.serviceProvider);
        session.start();

        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            final String input = scanner.next();
            session.processInput(input);
        }

    }

    public static EldenRingSave getInstance() {

        return EldenRingSaveSingleton.INSTANCE;

    }

}
