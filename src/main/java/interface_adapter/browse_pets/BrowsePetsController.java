package interface_adapter.browse_pets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import use_case.browse_pets.BrowsePetsInputBoundary;
import use_case.browse_pets.BrowsePetsInputData;
import use_case.browse_pets.PetFilter;

/**
 * Receives the visitor's choices from the gallery view and starts the browse use case. It does not know any
 * particular filter: each {@link FilterSpec} it is given turns its own choice into filters.
 */
public class BrowsePetsController {
    private final BrowsePetsInputBoundary interactor;
    private final List<FilterSpec> specs;

    /**
     * Creates the controller.
     *
     * @param interactor the use case to run
     * @param specs the filters the gallery offers
     */
    public BrowsePetsController(BrowsePetsInputBoundary interactor, List<FilterSpec> specs) {
        this.interactor = interactor;
        this.specs = List.copyOf(specs);
    }

    /**
     * Loads the gallery using what the visitor picked in the filter panel.
     *
     * @param choices for each filter id, the ids of the chosen options (or the typed text); a filter with
     *                no entry has no choice
     */
    public void browse(Map<String, List<String>> choices) {
        final List<PetFilter> filters = new ArrayList<>();
        for (FilterSpec spec : specs) {
            filters.addAll(spec.toFilters(choices.getOrDefault(spec.getId(), List.of())));
        }
        interactor.execute(new BrowsePetsInputData(filters));
    }
}
