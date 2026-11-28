package use_case.mark_pet;

/**
 * Marks a cat for the logged-in adopter, or removes the mark if the cat was already marked.
 */
public class ToggleMarkInteractor implements ToggleMarkInputBoundary {
    private final MarkedPetsDataAccessInterface dataAccess;
    private final ToggleMarkOutputBoundary presenter;

    /**
     * Creates the interactor.
     *
     * @param dataAccess where marked cats are kept
     * @param presenter where the result goes
     */
    public ToggleMarkInteractor(MarkedPetsDataAccessInterface dataAccess, ToggleMarkOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ToggleMarkInputData inputData) {
        final String adopterId = inputData.getAdopterId();
        if (adopterId == null || adopterId.isBlank()) {
            presenter.presentLoginRequired();
            return;
        }
        final String petId = inputData.getPetId();
        final boolean wasMarked = dataAccess.findMarkedPetIds(adopterId).contains(petId);
        if (wasMarked) {
            dataAccess.unmark(adopterId, petId);
        } else {
            dataAccess.mark(adopterId, petId);
        }
        presenter.presentMarkChanged(petId, !wasMarked);
    }
}
