package use_case.browse_pets;

import java.util.List;

/**
 * The cats that passed the filters, as plain summaries.
 */
public class BrowsePetsOutputData {
    private final List<PetSummary> pets;

    /**
     * Creates the output.
     *
     * @param pets the matching cats
     */
    public BrowsePetsOutputData(List<PetSummary> pets) {
        this.pets = List.copyOf(pets);
    }

    /**
     * Returns the matching cats.
     *
     * @return the summaries, empty if none matched
     */
    public List<PetSummary> getPets() {
        return pets;
    }
}
