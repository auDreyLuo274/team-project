package use_case.browse_pets;

/**
 * The facts about one cat that the gallery card needs, as plain text and numbers (not an entity).
 */
public class PetSummary {
    private final String petId;
    private final String name;
    private final String species;
    private final String breed;
    private final int ageYears;
    private final String status;
    private final String photoPath;

    /**
     * Creates the summary.
     *
     * @param petId the cat's id
     * @param name the cat's name
     * @param species the kind of animal, as a name such as "CAT"
     * @param breed the breed
     * @param ageYears the age in whole years
     * @param status the adoption status, as a name such as "AVAILABLE"
     * @param photoPath where the photo is, empty if none
     */
    public PetSummary(String petId, String name, String species, String breed, int ageYears, String status,
                      String photoPath) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.ageYears = ageYears;
        this.status = status;
        this.photoPath = photoPath;
    }

    /**
     * Returns the cat's id.
     *
     * @return the pet id
     */
    public String getPetId() {
        return petId;
    }

    /**
     * Returns the cat's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the kind of animal.
     *
     * @return the species name
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Returns the breed.
     *
     * @return the breed
     */
    public String getBreed() {
        return breed;
    }

    /**
     * Returns the age.
     *
     * @return the age in whole years
     */
    public int getAgeYears() {
        return ageYears;
    }

    /**
     * Returns the adoption status.
     *
     * @return the status name
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns where the photo is.
     *
     * @return the photo path, empty if none
     */
    public String getPhotoPath() {
        return photoPath;
    }
}
