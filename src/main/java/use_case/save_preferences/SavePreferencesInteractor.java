package use_case.save_preferences;

import java.util.Optional;

import entity.AdopterPreferences;
import use_case.check_compatibility.AdopterPreferencesDataAccessInterface;

/**
 * Saves the answers from My profile so every compatibility check can use them.
 */
public class SavePreferencesInteractor implements SavePreferencesInputBoundary {
    private final AdopterPreferencesDataAccessInterface dataAccess;
    private final SavePreferencesOutputBoundary presenter;

    /**
     * Creates the interactor.
     *
     * @param dataAccess where preferences are kept
     * @param presenter where the result goes
     */
    public SavePreferencesInteractor(AdopterPreferencesDataAccessInterface dataAccess,
                                     SavePreferencesOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SavePreferencesInputData inputData) {
        if (isBlank(inputData.getAdopterId())) {
            presenter.presentError("Log in to save your profile.");
            return;
        }
        final AdopterPreferences preferences = new AdopterPreferences(inputData.hasChildren(),
                inputData.hasOtherPets(), inputData.canHandleMedicalNeeds(), inputData.canHandleKitten(),
                inputData.canHandlePregnantAnimal(), null);
        dataAccess.save(inputData.getAdopterId(), preferences);
        presenter.presentSaved(summarize(preferences));
    }

    @Override
    public void load(String adopterId) {
        if (isBlank(adopterId)) {
            presenter.presentError("Log in to save your profile.");
            return;
        }
        final Optional<AdopterPreferences> saved = dataAccess.findByAdopterId(adopterId);
        if (saved.isPresent()) {
            presenter.presentLoaded(summarize(saved.get()));
        } else {
            presenter.presentNothingSaved();
        }
    }

    private SavedPreferences summarize(AdopterPreferences preferences) {
        return new SavedPreferences(preferences.hasChildren(), preferences.hasOtherPets(),
                preferences.canHandleMedicalNeeds(), preferences.canHandleKitten(),
                preferences.canHandlePregnantAnimal());
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
