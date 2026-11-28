package use_case.list_marked_pets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import entity.Pet;
import use_case.mark_pet.MarkedPetsDataAccessInterface;

/**
 * Looks up the cats the logged-in adopter marked. A marked cat that no longer exists is left out.
 */
public class ListMarkedPetsInteractor implements ListMarkedPetsInputBoundary {
    private final MarkedPetsDataAccessInterface markedData;
    private final ListMarkedPetsPetDataAccessInterface petData;
    private final ListMarkedPetsOutputBoundary presenter;

    /**
     * Creates the interactor.
     *
     * @param markedData where marked cats are kept
     * @param petData where cats are looked up
     * @param presenter where the result goes
     */
    public ListMarkedPetsInteractor(MarkedPetsDataAccessInterface markedData,
                                    ListMarkedPetsPetDataAccessInterface petData,
                                    ListMarkedPetsOutputBoundary presenter) {
        this.markedData = markedData;
        this.petData = petData;
        this.presenter = presenter;
    }

    @Override
    public void execute(String adopterId) {
        if (adopterId == null || adopterId.isBlank()) {
            presenter.presentLoginRequired();
            return;
        }
        final List<Pet> pets = new ArrayList<>();
        for (String petId : new TreeSet<>(markedData.findMarkedPetIds(adopterId))) {
            final Optional<Pet> pet = petData.findById(petId);
            pet.ifPresent(pets::add);
        }
        presenter.presentMarkedPets(pets);
    }
}
