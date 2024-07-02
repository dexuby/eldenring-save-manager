package dev.dexuby.eldenringsavemanager.save;

import dev.dexuby.eldenringsavemanager.save.format.SaveHeaderInfo;
import org.jetbrains.annotations.NotNull;

public abstract class Save {

    private final SaveHeaderInfo saveHeaderInfo;
    private final byte[] headerData;
    private final byte[] saveDataChecksum;
    private final byte[] saveData;

    public Save(@NotNull final SaveHeaderInfo saveHeaderInfo,
                final byte[] headerData,
                final byte[] saveDataChecksum,
                final byte[] saveData) {

        this.saveHeaderInfo = saveHeaderInfo;
        this.headerData = headerData;
        this.saveDataChecksum = saveDataChecksum;
        this.saveData = saveData;

    }

    public SaveHeaderInfo getSaveHeaderInfo() {

        return this.saveHeaderInfo;

    }
    
    public byte[] getHeaderData() {
        
        return this.headerData;
                
    }

    public byte[] getSaveDataChecksum() {

        return this.saveDataChecksum;

    }

    public byte[] getSaveData() {

        return this.saveData;

    }

}
