package use_case.mark_pet;

import java.util.Set;

/**
 * Where each adopter's marked (favourite) cats are kept.
 */
public interface MarkedPetsDataAccessInterface {

    /**
     * Finds the ids of the cats an adopter marked.
     *
     * @param adopterId the adopter
     * @return the pet ids, empty if none
     */
    Set<String> findMarkedPetIds(String adopterId);

    /**
     * Marks a cat for an adopter. Marking a cat that is already marked changes nothing.
     *
     * @param adopterId the adopter
     * @param petId the cat
     */
    void mark(String adopterId, String petId);

    /**
     * Removes a cat from an adopter's marked cats. Removing a cat that is not marked changes nothing.
     *
     * @param adopterId the adopter
     * @param petId the cat
     */
    void unmark(String adopterId, String petId);
}
