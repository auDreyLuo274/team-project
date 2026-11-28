package use_case.save_preferences;

/**
 * Receives the result of the save preferences use case.
 */
public interface SavePreferencesOutputBoundary {

    /**
     * Tells the adopter the answers were saved.
     *
     * @param preferences what was saved
     */
    void presentSaved(SavedPreferences preferences);

    /**
     * Shows what the adopter saved before.
     *
     * @param preferences what was saved
     */
    void presentLoaded(SavedPreferences preferences);

    /**
     * Tells the page the adopter has not saved anything yet.
     */
    void presentNothingSaved();

    /**
     * Tells the adopter the answers could not be saved.
     *
     * @param message what went wrong
     */
    void presentError(String message);
}
