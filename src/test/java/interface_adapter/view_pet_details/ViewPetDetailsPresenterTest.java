package interface_adapter.view_pet_details;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import data_access.StarterPetCatalog;
import interface_adapter.ViewManagerModel;
import use_case.view_pet_details.PetProfile;
import use_case.view_pet_details.ViewPetDetailsInputData;
import use_case.view_pet_details.ViewPetDetailsInteractor;
import use_case.view_pet_details.ViewPetDetailsOutputData;

class ViewPetDetailsPresenterTest {
    private final PetDetailViewModel viewModel = new PetDetailViewModel();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewPetDetailsPresenter presenter = new ViewPetDetailsPresenter(viewModel, viewManagerModel);

    private PetProfile pet(String notes, List<String> tags) {
        return new PetProfile("7", "Miso", "CAT", "Tabby", 2, "FEMALE", tags, notes, true, true, false,
                new BigDecimal("150"), "", "AVAILABLE", "", "", null);
    }

    @Test
    void writesTheProfileAsText() {
        presenter.presentPet(new ViewPetDetailsOutputData(
                pet("Eats special food", List.of("CALM", "GOOD_WITH_KIDS"))));

        final PetDetailState state = viewModel.getState();
        assertEquals("7", state.getPetId());
        assertEquals("Miso", state.getName());
        assertEquals("Cat, Tabby, 2 years old", state.getSummary());
        assertTrue(state.getDetailLines().contains("Temperament: Calm, Good with kids"));
        assertTrue(state.getHealthLines().contains("Health notes: Eats special food"));
        assertTrue(state.getHealthLines().contains("Needs daily medication: Yes"));
        assertTrue(state.getHealthLines().contains("Vaccinated: Yes"));
        assertTrue(state.getHealthLines().contains("Spayed or neutered: No"));
        assertTrue(state.getDetailLines().contains("Adoption fee: $150.00"));
        assertTrue(state.getDetailLines().contains("Status: Available"));
    }

    @Test
    void emptyFieldsAreWrittenOutInWords() {
        presenter.presentPet(new ViewPetDetailsOutputData(pet("", List.of())));

        assertTrue(viewModel.getState().getDetailLines().contains("Temperament: None listed"));
        assertTrue(viewModel.getState().getHealthLines().contains("Health notes: None"));
    }

    @Test
    void starterProfileDisplaysTheVerifiedDetails() {
        new ViewPetDetailsInteractor(id -> Optional.of(StarterPetCatalog.createPets().get(0)), presenter)
                .execute(new ViewPetDetailsInputData("any"));

        final PetDetailState state = viewModel.getState();
        assertEquals("Piggy- CP", state.getName());
        assertEquals(List.of("Breed: Snowshoe", "Age: Senior", "Sex: Female", "Color: Brown or Chocolate (Mostly)",
                "Hair length: Short"), state.getBasicInfo());
        assertTrue(state.getDetailLines().contains("Microchipped: Yes"));
        assertTrue(state.getDetailLines().contains("Adoption fee: Not listed"));
        assertTrue(state.getHealthLines().contains("Vaccinated: Yes"));
        assertTrue(state.getHealthLines().contains("Spayed or neutered: Yes"));
    }

    @Test
    void switchesToTheDetailPageAndNotifiesTheView() {
        final List<String> events = new ArrayList<>();
        viewModel.addListener(() -> events.add("state"));
        viewManagerModel.addListener(() -> events.add("view:" + viewManagerModel.getActiveView()));

        presenter.presentPet(new ViewPetDetailsOutputData(pet("", List.of())));

        assertEquals(List.of("state", "view:" + PetDetailViewModel.VIEW_NAME), events);
    }

    @Test
    void notFoundShowsTheMessage() {
        presenter.presentNotFound("This cat is no longer available.");

        assertEquals("This cat is no longer available.", viewModel.getState().getErrorMessage());
        assertEquals(PetDetailViewModel.VIEW_NAME, viewManagerModel.getActiveView());
    }
}
