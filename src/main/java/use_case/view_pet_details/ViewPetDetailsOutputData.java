package use_case.view_pet_details;

/**
 * The cat to show on the detail page, as a plain profile.
 */
public class ViewPetDetailsOutputData {
    private final PetProfile pet;

    /**
     * Creates the output.
     *
     * @param pet the profile of the cat
     */
    public ViewPetDetailsOutputData(PetProfile pet) {
        this.pet = pet;
    }

    /**
     * Returns the cat's profile.
     *
     * @return the profile
     */
    public PetProfile getPet() {
        return pet;
    }
}
