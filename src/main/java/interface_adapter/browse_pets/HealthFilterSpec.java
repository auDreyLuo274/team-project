package interface_adapter.browse_pets;

import java.util.ArrayList;
import java.util.List;

import use_case.browse_pets.HealthNeedsFilter;
import use_case.browse_pets.PetFilter;
import use_case.browse_pets.SpayedNeuteredFilter;
import use_case.browse_pets.VaccinatedFilter;

/**
 * The health tick boxes: no medical needs, vaccinated, spayed or neutered.
 */
public class HealthFilterSpec implements FilterSpec {
    private static final String NO_MEDICAL_NEEDS = "NO_MEDICAL_NEEDS";
    private static final String VACCINATED = "VACCINATED";
    private static final String SPAYED_NEUTERED = "SPAYED_NEUTERED";

    @Override
    public String getId() {
        return "health";
    }

    @Override
    public String getLabel() {
        return "Health";
    }

    @Override
    public Control getControl() {
        return Control.MULTI_CHOICE;
    }

    @Override
    public List<FilterOption> getOptions() {
        return List.of(
                new FilterOption(NO_MEDICAL_NEEDS, "Only cats with no medical needs"),
                new FilterOption(VACCINATED, "Only vaccinated cats"),
                new FilterOption(SPAYED_NEUTERED, "Only spayed or neutered cats"));
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        final List<PetFilter> filters = new ArrayList<>();
        if (chosen.contains(NO_MEDICAL_NEEDS)) {
            filters.add(new HealthNeedsFilter(false));
        }
        if (chosen.contains(VACCINATED)) {
            filters.add(new VaccinatedFilter());
        }
        if (chosen.contains(SPAYED_NEUTERED)) {
            filters.add(new SpayedNeuteredFilter());
        }
        return filters;
    }
}
