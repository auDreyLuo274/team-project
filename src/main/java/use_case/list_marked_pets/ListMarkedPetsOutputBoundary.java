package use_case.list_marked_pets;

import java.util.List;

import entity.Pet;

/**
 * Receives the result of the list marked pets use case.
 */
public interface ListMarkedPetsOutputBoundary {

    /**
     * Shows the marked cats.
     *
     * @param pets the cats, empty if the adopter marked none
     */
    void presentMarkedPets(List<Pet> pets);

    /**
     * Tells a visitor who is not logged in to log in first.
     */
    void presentLoginRequired();
}
