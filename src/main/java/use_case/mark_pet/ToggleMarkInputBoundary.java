package use_case.mark_pet;

/**
 * Starts the mark pet use case.
 */
public interface ToggleMarkInputBoundary {

    /**
     * Marks the cat if it is not marked, and removes the mark if it is.
     *
     * @param inputData the adopter and the cat
     */
    void execute(ToggleMarkInputData inputData);
}
