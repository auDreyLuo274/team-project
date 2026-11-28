package use_case.list_marked_pets;

/**
 * Starts the list marked pets use case.
 */
public interface ListMarkedPetsInputBoundary {

    /**
     * Lists the cats an adopter marked.
     *
     * @param adopterId the adopter, empty if nobody is logged in
     */
    void execute(String adopterId);
}
