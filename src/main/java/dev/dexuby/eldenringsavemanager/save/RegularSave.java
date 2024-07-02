package dev.dexuby.eldenringsavemanager.save;

import dev.dexuby.eldenringsavemanager.save.format.GameFile;
import dev.dexuby.eldenringsavemanager.save.format.SaveHeaderInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Regular save found in the standard Elden Ring save file.
 */

public class RegularSave extends Save {

    private final int index;
    private final boolean active;
    private final GameFile gameFile;

    public RegularSave(final int index,
                       final boolean active,
                       @NotNull final GameFile gameFile,
                       @NotNull final SaveHeaderInfo saveHeaderInfo,
                       final byte[] headerData,
                       final byte[] saveDataChecksum,
                       final byte[] saveData) {

        super(saveHeaderInfo, headerData, saveDataChecksum, saveData);

        this.index = index;
        this.active = active;
        this.gameFile = gameFile;

    }

    public int getIndex() {

        return this.index;

    }

    public boolean isActive() {

        return this.active;

    }

    public GameFile getGameFile() {

        return this.gameFile;

    }

    @Override
    public String toString() {

        return String.format("[%d] Active: %s, %s", index, active ? "Yes" : "No", super.getSaveHeaderInfo().toString());

    }

}
