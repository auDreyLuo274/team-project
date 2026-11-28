package use_case.mark_pet;

/**
 * Receives the result of the mark pet use case.
 */
public interface ToggleMarkOutputBoundary {

    /**
     * Tells the screen the cat is now marked or no longer marked.
     *
     * @param petId the cat
     * @param marked true if it is now marked
     */
    void presentMarkChanged(String petId, boolean marked);

    /**
     * Tells a visitor who is not logged in to log in first.
     */
    void presentLoginRequired();
}
