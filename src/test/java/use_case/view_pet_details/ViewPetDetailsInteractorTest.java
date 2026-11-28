package use_case.view_pet_details;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import entity.Pet;
import entity.PetSex;
import entity.PetStatus;
import entity.Species;

class ViewPetDetailsInteractorTest {

    private static class FakePresenter implements ViewPetDetailsOutputBoundary {
        private PetProfile shown;
        private String error;

        @Override
        public void presentPet(ViewPetDetailsOutputData outputData) {
            shown = outputData.getPet();
        }

        @Override
        public void presentNotFound(String message) {
            error = message;
        }
    }

    private Pet pet(String id, PetStatus status) {
        return new Pet(id, "Miso", Species.CAT, "Tabby", 2, PetSex.FEMALE, List.of(), "", false, true, true,
                BigDecimal.TEN, "", status);
    }

    private FakePresenter run(String requestedId, Pet... stored) {
        final ViewPetDetailsDataAccessInterface dataAccess = petId -> List.of(stored).stream()
                .filter(pet -> pet.getId().equals(petId))
                .findFirst();
        final FakePresenter presenter = new FakePresenter();
        new ViewPetDetailsInteractor(dataAccess, presenter).execute(new ViewPetDetailsInputData(requestedId));
        return presenter;
    }

    @Test
    void availableCatIsShown() {
        final FakePresenter presenter = run("1", pet("1", PetStatus.AVAILABLE));

        assertEquals("1", presenter.shown.getPetId());
        assertNull(presenter.error);
    }

    @Test
    void unknownIdIsNotFound() {
        final FakePresenter presenter = run("9", pet("1", PetStatus.AVAILABLE));

        assertNull(presenter.shown);
        assertEquals("This cat is no longer available.", presenter.error);
    }

    @Test
    void adoptedCatIsNotShown() {
        final FakePresenter presenter = run("1", pet("1", PetStatus.ADOPTED));

        assertNull(presenter.shown);
        assertEquals("This cat is no longer available.", presenter.error);
    }

    @Test
    void emptyDataAccessIsNotFound() {
        final FakePresenter presenter = new FakePresenter();
        new ViewPetDetailsInteractor(petId -> Optional.empty(), presenter)
                .execute(new ViewPetDetailsInputData("1"));

        assertEquals("This cat is no longer available.", presenter.error);
    }

    @Test
    void theProfileCarriesPlainTextAndFlagsNotTheEntity() {
        final Pet stored = new Pet("1", "Miso", Species.CAT, "Tabby", 2, PetSex.FEMALE,
                List.of(entity.Temperament.CALM, entity.Temperament.GOOD_WITH_KIDS), "Eats special food", true,
                true, false, new BigDecimal("150"), "miso.jpg", PetStatus.AVAILABLE);

        final PetProfile profile = run("1", stored).shown;

        assertEquals("Miso", profile.getName());
        assertEquals("CAT", profile.getSpecies());
        assertEquals("Tabby", profile.getBreed());
        assertEquals(2, profile.getAgeYears());
        assertEquals("FEMALE", profile.getSex());
        assertEquals(List.of("CALM", "GOOD_WITH_KIDS"), profile.getTemperaments());
        assertEquals("Eats special food", profile.getHealthNotes());
        assertEquals(true, profile.needsMedication());
        assertEquals(true, profile.isVaccinated());
        assertEquals(false, profile.isSpayedNeutered());
        assertEquals(new BigDecimal("150"), profile.getAdoptionFee());
        assertEquals("miso.jpg", profile.getPhotoPath());
        assertEquals("AVAILABLE", profile.getStatus());
    }
}
