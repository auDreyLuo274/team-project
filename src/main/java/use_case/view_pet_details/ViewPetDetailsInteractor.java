package use_case.view_pet_details;

import java.util.Optional;
import java.util.stream.Collectors;

import entity.Pet;
import entity.PetProfileDetails;
import entity.PetStatus;

/**
 * Looks up one available cat for the detail page.
 */
public class ViewPetDetailsInteractor implements ViewPetDetailsInputBoundary {
    private final ViewPetDetailsDataAccessInterface dataAccess;
    private final ViewPetDetailsOutputBoundary presenter;

    /**
     * Creates the interactor.
     *
     * @param dataAccess where cats are looked up
     * @param presenter where the result goes
     */
    public ViewPetDetailsInteractor(ViewPetDetailsDataAccessInterface dataAccess,
                                    ViewPetDetailsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewPetDetailsInputData inputData) {
        final Optional<Pet> found = dataAccess.findById(inputData.getPetId());
        if (found.isPresent() && found.get().getStatus() == PetStatus.AVAILABLE) {
            presenter.presentPet(new ViewPetDetailsOutputData(profileOf(found.get())));
        } else {
            presenter.presentNotFound("This cat is no longer available.");
        }
    }

    private static PetProfile profileOf(Pet pet) {
        final PetProfileDetails details = pet.getProfileDetails();
        return new PetProfile(pet.getId(), pet.getName(), pet.getSpecies().name(), pet.getBreed(),
                pet.getAgeYears(), pet.getSex().name(),
                pet.getTemperamentTags().stream().map(Enum::name).collect(Collectors.toList()),
                pet.getHealthNotes(), pet.needsMedication(), pet.isVaccinated(), pet.isSpayedNeutered(),
                pet.getAdoptionFee(), pet.getPhotoPath(), pet.getStatus().name(), details.getColor(),
                details.getHairLength(), details.getMicrochipped());
    }
}
