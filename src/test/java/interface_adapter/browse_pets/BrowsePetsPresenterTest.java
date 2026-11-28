package interface_adapter.browse_pets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import use_case.browse_pets.BrowsePetsOutputData;
import use_case.browse_pets.PetSummary;

class BrowsePetsPresenterTest {

    private PetSummary pet(String name, int age) {
        return new PetSummary("1", name, "CAT", "Tabby", age, "AVAILABLE", "");
    }

    @Test
    void formatsCardText() {
        final GalleryViewModel viewModel = new GalleryViewModel();

        new BrowsePetsPresenter(viewModel).presentPets(new BrowsePetsOutputData(List.of(pet("Miso", 2))));

        final PetCardData card = viewModel.getState().getCards().get(0);
        assertEquals("1", card.getPetId());
        assertEquals("Miso", card.getName());
        assertEquals("Cat, Tabby, 2 years old", card.getSummary());
        assertEquals("Status: Available", card.getStatus());
    }

    @Test
    void tellsTheViewTheStateChanged() {
        final GalleryViewModel viewModel = new GalleryViewModel();
        final int[] calls = {0};
        viewModel.addListener(() -> calls[0]++);

        new BrowsePetsPresenter(viewModel).presentPets(new BrowsePetsOutputData(List.of(pet("Miso", 2))));

        assertEquals(1, calls[0]);
    }

    @Test
    void singularYear() {
        final GalleryViewModel viewModel = new GalleryViewModel();

        new BrowsePetsPresenter(viewModel).presentPets(new BrowsePetsOutputData(List.of(pet("Kit", 1))));

        assertEquals("Cat, Tabby, 1 year old", viewModel.getState().getCards().get(0).getSummary());
    }

    @Test
    void noMatchesGivesEmptyCards() {
        final GalleryViewModel viewModel = new GalleryViewModel();

        new BrowsePetsPresenter(viewModel).presentPets(new BrowsePetsOutputData(List.of()));

        assertTrue(viewModel.getState().getCards().isEmpty());
    }
}
