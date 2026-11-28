package interface_adapter.browse_pets;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import entity.Temperament;
import use_case.browse_pets.PetFilter;
import use_case.browse_pets.TemperamentFilter;

/**
 * The temperament tick boxes: a cat must have every ticked tag.
 */
public class TemperamentFilterSpec implements FilterSpec {

    @Override
    public String getId() {
        return "temperament";
    }

    @Override
    public String getLabel() {
        return "Temperament (must have all)";
    }

    @Override
    public Control getControl() {
        return Control.MULTI_CHOICE;
    }

    @Override
    public List<FilterOption> getOptions() {
        final List<FilterOption> options = new ArrayList<>();
        for (Temperament temperament : Temperament.values()) {
            options.add(new FilterOption(temperament.name(), FilterLabels.temperament(temperament)));
        }
        return options;
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        if (chosen.isEmpty()) {
            return List.of();
        }
        final Set<Temperament> required = EnumSet.noneOf(Temperament.class);
        for (String id : chosen) {
            required.add(Temperament.valueOf(id));
        }
        return List.of(new TemperamentFilter(required));
    }
}
