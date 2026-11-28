package data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileMarkedPetsDataAccessObjectTest {
    @TempDir
    Path folder;

    @Test
    void nothingMarkedYetIsEmpty() {
        assertTrue(new FileMarkedPetsDataAccessObject(folder.resolve("m.json")).findMarkedPetIds("a1").isEmpty());
    }

    @Test
    void markedCatsComeBackAfterReopeningTheFile() {
        final Path file = folder.resolve("m.json");
        final FileMarkedPetsDataAccessObject dao = new FileMarkedPetsDataAccessObject(file);
        dao.mark("a1", "p1");
        dao.mark("a1", "p2");

        assertEquals(Set.of("p1", "p2"), new FileMarkedPetsDataAccessObject(file).findMarkedPetIds("a1"));
    }

    @Test
    void markingTwiceKeepsOneMark() {
        final FileMarkedPetsDataAccessObject dao = new FileMarkedPetsDataAccessObject(folder.resolve("m.json"));
        dao.mark("a1", "p1");
        dao.mark("a1", "p1");

        assertEquals(Set.of("p1"), dao.findMarkedPetIds("a1"));
    }

    @Test
    void unmarkRemovesTheCatAndIsSaved() {
        final Path file = folder.resolve("m.json");
        final FileMarkedPetsDataAccessObject dao = new FileMarkedPetsDataAccessObject(file);
        dao.mark("a1", "p1");

        dao.unmark("a1", "p1");
        dao.unmark("a1", "never-marked");

        assertTrue(new FileMarkedPetsDataAccessObject(file).findMarkedPetIds("a1").isEmpty());
    }

    @Test
    void adoptersAreKeptApart() {
        final FileMarkedPetsDataAccessObject dao = new FileMarkedPetsDataAccessObject(folder.resolve("m.json"));
        dao.mark("a1", "p1");

        assertTrue(dao.findMarkedPetIds("a2").isEmpty());
    }

    @Test
    void anUnreadableFileIsTreatedAsNothingMarked() throws IOException {
        final Path file = folder.resolve("m.json");
        Files.writeString(file, "not json");

        assertTrue(new FileMarkedPetsDataAccessObject(file).findMarkedPetIds("a1").isEmpty());
    }
}
