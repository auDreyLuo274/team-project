package interface_adapter.browse_pets;

import java.util.List;
import java.util.stream.Collectors;

import use_case.browse_pets.PetSummary;
import use_case.browse_pets.BrowsePetsOutputBoundary;
import use_case.browse_pets.BrowsePetsOutputData;

/**
 * Turns matching pets into card text and hands it to the view model.
 */
public class BrowsePetsPresenter implements BrowsePetsOutputBoundary {
    private final GalleryViewModel viewModel;

    /**
     * Creates the presenter.
     *
     * @param viewModel the model the gallery view listens to
     */
    public BrowsePetsPresenter(GalleryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentPets(BrowsePetsOutputData outputData) {
        final List<PetCardData> cards = outputData.getPets().stream()
                .map(this::toCard)
                .collect(Collectors.toList());
        final GalleryState state = new GalleryState();
        state.setCards(cards);
        viewModel.setState(state);
        viewModel.notifyStateChanged();
    }

    private PetCardData toCard(PetSummary pet) {
        final String age = pet.getAgeYears() == 1 ? "1 year old" : pet.getAgeYears() + " years old";
        final String summary = capitalize(pet.getSpecies()) + ", " + pet.getBreed() + ", " + age;
        return new PetCardData(pet.getPetId(), pet.getName(), summary, statusText(pet.getStatus()), pet.getPhotoPath());
    }

    private String statusText(String status) {
        return "Status: " + capitalize(status);
    }

    private String capitalize(String text) {
        return text.substring(0, 1) + text.substring(1).toLowerCase();
    }
}
