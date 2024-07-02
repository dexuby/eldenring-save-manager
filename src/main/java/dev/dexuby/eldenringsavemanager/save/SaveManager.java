package dev.dexuby.eldenringsavemanager.save;

import dev.dexuby.eldenringsavemanager.ByteAnalyzer;
import dev.dexuby.eldenringsavemanager.ByteReader;
import dev.dexuby.eldenringsavemanager.common.dependencyinjection.ServiceProvider;
import dev.dexuby.eldenringsavemanager.common.tuple.ImmutableKeyValuePair;
import dev.dexuby.eldenringsavemanager.common.tuple.ImmutablePair;
import dev.dexuby.eldenringsavemanager.compression.Compressor;
import dev.dexuby.eldenringsavemanager.file.FileManager;
import dev.dexuby.eldenringsavemanager.hashing.MD5;
import dev.dexuby.eldenringsavemanager.save.file.ExtractedProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.file.RegularProcessedSaveFile;
import dev.dexuby.eldenringsavemanager.save.format.GameFile;
import dev.dexuby.eldenringsavemanager.save.format.GameFileHeader;
import dev.dexuby.eldenringsavemanager.save.file.GameSaveFile;
import dev.dexuby.eldenringsavemanager.save.format.SaveHeaderInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class SaveManager {

    private static final int CHECKSUM_LENGTH = 16;
    private static final int HEADER_DATA_OFFSET = 26221838;
    private static final int HEADER_DATA_LENGTH = 588;
    private static final int SAVE_DATA_LENGTH = 2621440;
    private static final int SAVE_HEADERS_SECTION_OFFSET = 26215344;
    private static final int SAVE_HEADERS_SECTION_LENGTH = 393216;
    private static final int ACTIVE_SAVE_SLOT_OFFSET = 26221828;
    private static final int STEAM_ID_OFFSET = 26215348;

    private static final String SAVE_IDENTIFIER = "USER_DATA";

    private final MD5 md5;
    private final Compressor compressor;
    private final FileManager fileManager;

    public SaveManager(@NotNull final ServiceProvider serviceProvider) {

        this.md5 = serviceProvider.get(MD5.class);
        this.compressor = serviceProvider.get(Compressor.class);
        this.fileManager = serviceProvider.get(FileManager.class);

    }

    @NotNull
    public RegularProcessedSaveFile processSaveFile(@NotNull final GameSaveFile gameSaveFile) {

        final ByteReader byteReader = new ByteReader(gameSaveFile.data());
        final List<RegularSave> saves = new ArrayList<>();
        for (final GameFile gameFile : gameSaveFile.gameFiles()) {
            final String name = gameFile.gameFileHeader().name();
            if (!name.startsWith(SAVE_IDENTIFIER)) continue;
            final int index = Integer.parseInt(name.substring(SAVE_IDENTIFIER.length()));
            if (index > 9) continue;
            byteReader.jump(HEADER_DATA_OFFSET + (index * HEADER_DATA_LENGTH));
            final boolean active = gameSaveFile.data()[ACTIVE_SAVE_SLOT_OFFSET + index] == 1;
            final byte[] headerData = byteReader.readBytes(HEADER_DATA_LENGTH);
            final SaveHeaderInfo saveHeaderInfo = this.extractSaveHeaderInfo(headerData);
            final byte[] saveDataChecksum = new byte[CHECKSUM_LENGTH];
            System.arraycopy(gameFile.data(), 0, saveDataChecksum, 0, CHECKSUM_LENGTH);
            final byte[] saveData = new byte[gameFile.data().length - CHECKSUM_LENGTH];
            System.arraycopy(gameFile.data(), CHECKSUM_LENGTH, saveData, 0, gameFile.data().length - CHECKSUM_LENGTH);
            saves.add(new RegularSave(index, active, gameFile, saveHeaderInfo, headerData, saveDataChecksum, saveData));
        }

        return new RegularProcessedSaveFile(gameSaveFile, saves);

    }

    @Nullable
    public GameSaveFile loadSaveFile(@NotNull final File file) {

        final byte[] bytes = this.fileManager.loadBytes(file);
        if (bytes.length == 0)
            throw new RuntimeException("Byte content length of file " + file.getName() + " was 0.");

        return this.loadSaveFile(file.toPath(), bytes);

    }

    @Nullable
    public GameSaveFile loadSaveFile(@NotNull final Path filePath, final byte[] bytes) {

        final ByteReader byteReader = new ByteReader(bytes);
        final String magic = new String(byteReader.readBytes(4));
        if (!magic.equals("BND4"))
            throw new IllegalArgumentException("The provided save file is not of type BND4.");
        byteReader.skip(5);

        // Read info.
        final boolean bigEndian = byteReader.readBoolean();
        final boolean bitBigEndian = !byteReader.readBoolean();
        byteReader.skip(1);
        final int fileCount = byteReader.readLittleEndianInteger();
        final long headerSize = byteReader.readLittleEndianLong();
        final String version = new String(byteReader.readBytes(8));
        final long fileHeaderSize = byteReader.readLittleEndianLong();
        byteReader.skip(8);
        final boolean unicode = byteReader.readBoolean();

        // Read format.
        final byte rawFormat = byteReader.readByte();
        final boolean keepFormat = bitBigEndian || (rawFormat & 0x1) != 0 && (rawFormat & 0x80) == 0;
        byte format = rawFormat;
        if (!keepFormat)
            format = (byte) (Integer.reverse(format) >>> (Integer.SIZE - Byte.SIZE));
        final byte extended = byteReader.readByte();
        byteReader.skip(13);

        final long expectedHeaderSize = this.calculateExpectedHeaderSize(format);
        if (fileHeaderSize != expectedHeaderSize)
            throw new RuntimeException("Expected file header size " + expectedHeaderSize + " but got: " + fileHeaderSize);

        final List<GameFileHeader> gameFileHeaders = this.readGameFileHeaders(byteReader, fileCount, format, unicode, bigEndian);
        final List<GameFile> gameFiles = this.readGameFiles(byteReader, gameFileHeaders, format);

        return new GameSaveFile(bigEndian, bitBigEndian, fileCount, headerSize, version, fileHeaderSize, unicode, rawFormat,
                format, extended, expectedHeaderSize, bytes, gameFileHeaders, gameFiles, filePath);

    }

    private long calculateExpectedHeaderSize(final byte format) {

        return 0x10
                + ((format & 0x10) != 0 ? 8 : 4) // Long offsets
                + ((format & 0x20) != 0 ? 8 : 0) // Compression
                + ((format & 0x2) != 0 ? 4 : 0) // Ids
                + ((format & (0x4 | 0x8)) != 0 ? 4 : 0) // Names
                + (format == 0x4 ? 8 : 0); // First name format

    }

    private List<GameFileHeader> readGameFileHeaders(@NotNull final ByteReader byteReader,
                                                     final int fileCount,
                                                     final byte format,
                                                     final boolean unicode,
                                                     final boolean bigEndian) {

        final List<GameFileHeader> gameFileHeaders = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            final byte fileFlags = byteReader.readByte();
            byteReader.skip(3);
            if (byteReader.readLittleEndianInteger() != -1)
                throw new IllegalStateException("Unknown file table format.");

            final long compressedSize = byteReader.readLittleEndianLong();
            long uncompressedSize = -1;
            if ((format & 0x20) != 0) // Compression
                uncompressedSize = byteReader.readLittleEndianLong();

            long dataOffset;
            if ((format & 0x10) != 0) { // Long offsets
                dataOffset = byteReader.readLittleEndianLong();
            } else {
                dataOffset = byteReader.readLittleEndianInteger();
            }

            int id = -1;
            if ((format & 0x2) != 0)
                id = byteReader.readLittleEndianInteger();

            String name = null;
            if ((format & (0x4 | 0x8)) != 0) {
                final int nameOffset = byteReader.readLittleEndianInteger();
                if (unicode) {
                    final int byteReaderOffset = byteReader.getOffset();
                    byteReader.jump(nameOffset);
                    final List<Byte> nameByteList = new ArrayList<>();
                    byte[] charBytes = byteReader.readBytes(2);
                    while (charBytes[0] != 0 || charBytes[1] != 0) {
                        nameByteList.add(charBytes[0]);
                        nameByteList.add(charBytes[1]);
                        charBytes = byteReader.readBytes(2);
                    }

                    // Unbox name bytes.
                    final byte[] nameBytes = new byte[nameByteList.size()];
                    for (int k = 0; k < nameByteList.size(); k++)
                        nameBytes[k] = nameByteList.get(k);

                    if (bigEndian) {
                        name = new String(nameBytes, StandardCharsets.UTF_16BE);
                    } else {
                        name = new String(nameBytes, StandardCharsets.UTF_16LE);
                    }
                    byteReader.jump(byteReaderOffset);
                } else {
                    // Japanese encoding (JIS), don't care for now.
                    name = "JIS";
                }
            }
            if (format == 0x4) {
                id = byteReader.readLittleEndianInteger();
                byteReader.readLittleEndianInteger();
            }
            gameFileHeaders.add(new GameFileHeader(fileFlags, compressedSize, uncompressedSize, dataOffset, id, name));
        }

        return gameFileHeaders;

    }

    private List<GameFile> readGameFiles(@NotNull final ByteReader byteReader,
                                         @NotNull final List<GameFileHeader> gameFileHeaders,
                                         final byte format) {

        final List<GameFile> gameFiles = new ArrayList<>();
        for (final GameFileHeader gameFileHeader : gameFileHeaders) {
            byte[] fileBytes;
            byteReader.jump((int) gameFileHeader.dataOffset());
            fileBytes = byteReader.readBytes((int) gameFileHeader.compressedSize());
            if ((format & 0x20) != 0) { // Compression
                final Inflater inflater = new Inflater();
                inflater.setInput(fileBytes);
                try {
                    inflater.inflate(fileBytes);
                } catch (final DataFormatException ex) {
                    Logger.error(ex, "Couldn't decompress compressed data for file {}", gameFileHeader.name());
                }
                inflater.end();
            }
            gameFiles.add(new GameFile(gameFileHeader, fileBytes));
        }

        return gameFiles;

    }

    public RegularProcessedSaveFile injectAndCreateUpdatedSave(@NotNull final Save save, @NotNull final RegularProcessedSaveFile target, final int targetIndex) {

        RegularSave targetSave = null;
        for (final RegularSave regularSave : target.getSaves()) {
            if (regularSave.getIndex() == targetIndex) {
                targetSave = regularSave;
                break;
            }
        }

        if (targetSave == null)
            throw new IllegalStateException("No target save found with index: " + targetIndex);

        final GameFileHeader gameFileHeader = targetSave.getGameFile().gameFileHeader();
        final byte[] updatedData = target.getGameSaveFile().data().clone();
        System.arraycopy(save.getSaveDataChecksum(), 0, updatedData, (int) gameFileHeader.dataOffset(), CHECKSUM_LENGTH);
        System.arraycopy(save.getSaveData(), 0, updatedData, ((int) gameFileHeader.dataOffset()) + CHECKSUM_LENGTH, save.getSaveData().length);
        System.arraycopy(save.getHeaderData(), 0, updatedData, HEADER_DATA_OFFSET + (targetIndex * HEADER_DATA_LENGTH), HEADER_DATA_LENGTH);
        updatedData[ACTIVE_SAVE_SLOT_OFFSET + targetIndex] = (byte) 1;

        // Theoretically this is unnecessary since the checksum of the save data should always be correct.
        System.arraycopy(this.md5.hash(save.getSaveData()), 0, updatedData, (int) gameFileHeader.dataOffset(), CHECKSUM_LENGTH);

        final ByteReader byteReader = new ByteReader(updatedData);
        byteReader.jump(SAVE_HEADERS_SECTION_OFFSET);
        final byte[] saveHeaderSectionData = byteReader.readBytes(SAVE_HEADERS_SECTION_LENGTH);
        System.arraycopy(this.md5.hash(saveHeaderSectionData), 0, updatedData, SAVE_HEADERS_SECTION_OFFSET - CHECKSUM_LENGTH, CHECKSUM_LENGTH);

        final GameSaveFile gameSaveFile = this.loadSaveFile(target.getGameSaveFile().filePath(), updatedData);
        if (gameSaveFile == null)
            throw new RuntimeException("Failed to create save from updated data.");

        return this.processSaveFile(gameSaveFile);

    }

    @Nullable
    public ExtractedProcessedSaveFile loadExtractedSaveFromFile(@NotNull final File file) {

        final byte[] bytes = this.fileManager.loadBytes(file);
        if (bytes.length == 0)
            throw new RuntimeException("Byte content length of file " + file.getName() + " was 0.");

        final byte[] decompressedBytes = this.compressor.decompress(bytes);
        final byte[] headerData = new byte[HEADER_DATA_LENGTH];
        System.arraycopy(decompressedBytes, 0, headerData, 0, HEADER_DATA_LENGTH);
        final SaveHeaderInfo saveHeaderInfo = this.extractSaveHeaderInfo(headerData);
        final byte[] saveDataChecksum = new byte[CHECKSUM_LENGTH];
        System.arraycopy(decompressedBytes, HEADER_DATA_LENGTH, saveDataChecksum, 0, CHECKSUM_LENGTH);
        final byte[] saveData = new byte[SAVE_DATA_LENGTH];
        System.arraycopy(decompressedBytes, HEADER_DATA_LENGTH + CHECKSUM_LENGTH, saveData, 0, SAVE_DATA_LENGTH);

        return new ExtractedProcessedSaveFile(file.toPath(), new ExtractedSave(bytes.length, decompressedBytes.length, saveHeaderInfo, headerData, saveDataChecksum, saveData));

    }

    public ImmutableKeyValuePair<Integer, Integer> extractSaveToFile(@NotNull final RegularSave save, @NotNull final File file) {

        final byte[] bytes = new byte[HEADER_DATA_LENGTH + CHECKSUM_LENGTH + SAVE_DATA_LENGTH];
        System.arraycopy(save.getHeaderData(), 0, bytes, 0, HEADER_DATA_LENGTH);
        System.arraycopy(save.getSaveDataChecksum(), 0, bytes, HEADER_DATA_LENGTH, CHECKSUM_LENGTH);
        System.arraycopy(save.getSaveData(), 0, bytes, HEADER_DATA_LENGTH + CHECKSUM_LENGTH, SAVE_DATA_LENGTH);

        final byte[] compressedBytes = this.compressor.compress(bytes);
        this.fileManager.saveBytes(file, compressedBytes);

        return ImmutablePair.of(bytes.length, compressedBytes.length);

    }

    public SaveHeaderInfo extractSaveHeaderInfo(final byte[] headerData) {

        final ByteReader byteReader = new ByteReader(headerData);
        /* We're reading 34 bytes as the name here even though the maximum length is 16 characters, this is
         * because the save file seems to reserve 34 bytes for it for some reason. */
        final String characterName = new String(byteReader.readBytes(34), StandardCharsets.UTF_16LE).trim();
        // The character level is stored as a 4 byte integer.
        final int characterLevel = byteReader.readLittleEndianInteger();
        // The seconds played are stored as a 4 byte integer.
        final int secondsPlayed = byteReader.readLittleEndianInteger();

        return new SaveHeaderInfo(characterName, characterLevel, secondsPlayed);

    }

    @Nullable
    public RegularSave findSaveByIndex(@NotNull final RegularProcessedSaveFile saveFile, final int index) {

        RegularSave save = null;
        for (final RegularSave regularSave : saveFile.getSaves()) {
            if (regularSave.getIndex() == index) {
                save = regularSave;
                break;
            }
        }

        return save;

    }

    public void steamIdScan(@NotNull final RegularProcessedSaveFile saveFile) {

        // Replacing steam ids is necessary when copying saves from someone else.
        final ByteReader byteReader = new ByteReader(saveFile.getGameSaveFile().data());
        byteReader.jump(STEAM_ID_OFFSET);
        final long steamId = byteReader.readLittleEndianLong();
        Logger.info("Detected primary steam id: {}", steamId);
        Logger.info("Scanning for additional primary steam id occurrences...");
        byteReader.jump(0);
        final ByteAnalyzer byteAnalyzer = new ByteAnalyzer(byteReader);
        for (final int offset : byteAnalyzer.scanForOffsets(steamId)) {
            byteReader.jump(offset);
            final long localSteamId = byteReader.readLittleEndianLong();
            Logger.info("Steam id at offset {}: {}", offset, localSteamId);
        }

    }

}
