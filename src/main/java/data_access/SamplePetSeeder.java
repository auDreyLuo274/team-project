package data_access;

import entity.Pet;
import use_case.manage_pet.ManagePetDataAccessInterface;

/**
 * Puts a few sample cats into an empty pet store so a fresh install has something to show.
 */
public final class SamplePetSeeder {

    private SamplePetSeeder() {
    }

    /**
     * Adds the sample cats, but only if the store has no pets at all.
     *
     * @param store the pet store to fill
     * @return true if the sample cats were added
     */
    public static boolean seedIfEmpty(ManagePetDataAccessInterface store) {
        if (!store.getPets().isEmpty()) {
            return false;
        }
        for (Pet pet : InMemoryPetDataAccess.sampleCats()) {
            store.add(pet);
        }
        return true;
    }
}
