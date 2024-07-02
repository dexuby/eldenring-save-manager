package dev.dexuby.eldenringsavemanager.save.format;

import org.jetbrains.annotations.NotNull;

public record SaveHeaderInfo(@NotNull String characterName,
                             int characterLevel,
                             int secondsPlayed) {

    @Override
    public String toString() {

        return String.format("Character Name: %s, Level: %d, Seconds played: %d",
                !characterName.isBlank() ? characterName : "/",
                characterLevel,
                secondsPlayed
        );

    }

}
