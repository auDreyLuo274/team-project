package use_case.mark_pet;

/**
 * Which cat the adopter pressed the Mark button on.
 */
public class ToggleMarkInputData {
    private final String adopterId;
    private final String petId;

    /**
     * Creates the input.
     *
     * @param adopterId the adopter, empty if nobody is logged in
     * @param petId the cat
     */
    public ToggleMarkInputData(String adopterId, String petId) {
        this.adopterId = adopterId;
        this.petId = petId;
    }

    /**
     * Returns the adopter's id.
     *
     * @return the adopter id
     */
    public String getAdopterId() {
        return adopterId;
    }

    /**
     * Returns the cat's id.
     *
     * @return the pet id
     */
    public String getPetId() {
        return petId;
    }
}
