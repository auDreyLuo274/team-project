package interface_adapter.save_preferences;

import use_case.save_preferences.SavePreferencesOutputBoundary;
import use_case.save_preferences.SavedPreferences;

/**
 * Turns the result of saving or loading preferences into the state of the My profile page.
 */
public class SavePreferencesPresenter implements SavePreferencesOutputBoundary {
    private final PreferencesViewModel viewModel;

    /**
     * Creates the presenter.
     *
     * @param viewModel the model the My profile page listens to
     */
    public SavePreferencesPresenter(PreferencesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSaved(SavedPreferences preferences) {
        final PreferencesState state = stateFor(preferences);
        state.setMessage("Your profile is saved.");
        publish(state);
    }

    @Override
    public void presentLoaded(SavedPreferences preferences) {
        publish(stateFor(preferences));
    }

    @Override
    public void presentNothingSaved() {
        publish(new PreferencesState());
    }

    @Override
    public void presentError(String message) {
        final PreferencesState state = new PreferencesState();
        state.setError(message);
        publish(state);
    }

    private PreferencesState stateFor(SavedPreferences preferences) {
        final PreferencesState state = new PreferencesState();
        state.setHasChildren(preferences.hasChildren());
        state.setHasOtherPets(preferences.hasOtherPets());
        state.setCanHandleMedicalNeeds(preferences.canHandleMedicalNeeds());
        state.setCanHandleKitten(preferences.canHandleKitten());
        state.setCanHandlePregnantAnimal(preferences.canHandlePregnantAnimal());
        return state;
    }

    private void publish(PreferencesState state) {
        viewModel.setState(state);
        viewModel.notifyStateChanged();
    }
}
