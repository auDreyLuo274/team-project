package use_case.list_marked_pets;

import java.util.Optional;

import entity.Pet;

/**
 * Where the marked cats are looked up.
 */
public interface ListMarkedPetsPetDataAccessInterface {

    /**
     * Finds a cat by its id.
     *
     * @param petId the pet id
     * @return the cat, or empty if there is none
     */
    Optional<Pet> findById(String petId);
}
