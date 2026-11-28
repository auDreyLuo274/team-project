package use_case.view_pet_details;

import java.math.BigDecimal;
import java.util.List;

/**
 * Everything the detail page shows about one cat, as plain text, numbers and flags (not an entity).
 */
public class PetProfile {
    private final String petId;
    private final String name;
    private final String species;
    private final String breed;
    private final int ageYears;
    private final String sex;
    private final List<String> temperaments;
    private final String healthNotes;
    private final boolean needsMedication;
    private final boolean vaccinated;
    private final boolean spayedNeutered;
    private final BigDecimal adoptionFee;
    private final String photoPath;
    private final String status;
    private final String color;
    private final String hairLength;
    private final Boolean microchipped;

    /**
     * Creates the profile.
     *
     * @param petId the cat's id
     * @param name the cat's name
     * @param species the kind of animal, as a name such as "CAT"
     * @param breed the breed
     * @param ageYears the age in whole years
     * @param sex the sex, as a name such as "FEMALE"
     * @param temperaments the temperament tags, as names such as "GOOD_WITH_KIDS"
     * @param healthNotes the health notes, empty if none
     * @param needsMedication whether the cat needs daily medication
     * @param vaccinated whether the cat is vaccinated
     * @param spayedNeutered whether the cat is spayed or neutered
     * @param adoptionFee the adoption fee, or null if not listed
     * @param photoPath where the photo is, empty if none
     * @param status the adoption status, as a name such as "AVAILABLE"
     * @param color the coat colour, empty if not listed
     * @param hairLength the hair length, empty if not listed
     * @param microchipped whether the cat is microchipped, or null if not listed
     */
    public PetProfile(String petId, String name, String species, String breed, int ageYears, String sex,
                      List<String> temperaments, String healthNotes, boolean needsMedication, boolean vaccinated,
                      boolean spayedNeutered, BigDecimal adoptionFee, String photoPath, String status,
                      String color, String hairLength, Boolean microchipped) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.ageYears = ageYears;
        this.sex = sex;
        this.temperaments = List.copyOf(temperaments);
        this.healthNotes = healthNotes;
        this.needsMedication = needsMedication;
        this.vaccinated = vaccinated;
        this.spayedNeutered = spayedNeutered;
        this.adoptionFee = adoptionFee;
        this.photoPath = photoPath;
        this.status = status;
        this.color = color;
        this.hairLength = hairLength;
        this.microchipped = microchipped;
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
     * Returns the sex.
     *
     * @return the sex name
     */
    public String getSex() {
        return sex;
    }

    /**
     * Returns the temperament tags.
     *
     * @return the tag names, empty if none
     */
    public List<String> getTemperaments() {
        return temperaments;
    }

    /**
     * Returns the health notes.
     *
     * @return the notes, empty if none
     */
    public String getHealthNotes() {
        return healthNotes;
    }

    /**
     * Returns whether the cat needs daily medication.
     *
     * @return true if it does
     */
    public boolean needsMedication() {
        return needsMedication;
    }

    /**
     * Returns whether the cat is vaccinated.
     *
     * @return true if it is
     */
    public boolean isVaccinated() {
        return vaccinated;
    }

    /**
     * Returns whether the cat is spayed or neutered.
     *
     * @return true if it is
     */
    public boolean isSpayedNeutered() {
        return spayedNeutered;
    }

    /**
     * Returns the adoption fee.
     *
     * @return the fee, or null if not listed
     */
    public BigDecimal getAdoptionFee() {
        return adoptionFee;
    }

    /**
     * Returns where the photo is.
     *
     * @return the photo path, empty if none
     */
    public String getPhotoPath() {
        return photoPath;
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
     * Returns the coat colour.
     *
     * @return the colour, empty if not listed
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the hair length.
     *
     * @return the hair length, empty if not listed
     */
    public String getHairLength() {
        return hairLength;
    }

    /**
     * Returns whether the cat is microchipped.
     *
     * @return true or false, or null if not listed
     */
    public Boolean getMicrochipped() {
        return microchipped;
    }
}
