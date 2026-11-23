package data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import entity.Pet;
import entity.PetSex;
import entity.PetStatus;
import entity.Species;

class SamplePetSeederTest {

    @TempDir
    Path tempDir;

    @Test
    void emptyStoreGetsTheSampleCats() {
        final FilePetDataAccessObject store = new FilePetDataAccessObject(tempDir.resolve("pets.json"));

        assertTrue(SamplePetSeeder.seedIfEmpty(store));

        assertEquals(InMemoryPetDataAccess.sampleCats().size(), store.getPets().size());
    }

    @Test
    void storeWithPetsIsLeftAlone() {
        final FilePetDataAccessObject store = new FilePetDataAccessObject(tempDir.resolve("pets.json"));
        store.add(new Pet("x", "Mine", Species.CAT, "Tabby", 2, PetSex.FEMALE, List.of(), "", false, true, true,
                BigDecimal.TEN, "", PetStatus.AVAILABLE));

        assertFalse(SamplePetSeeder.seedIfEmpty(store));

        assertEquals(1, store.getPets().size());
    }

    @Test
    void seededCatsSurviveARestart() {
        final Path file = tempDir.resolve("pets.json");
        SamplePetSeeder.seedIfEmpty(new FilePetDataAccessObject(file));

        assertEquals(InMemoryPetDataAccess.sampleCats().size(), new FilePetDataAccessObject(file).getPets().size());
    }
}
