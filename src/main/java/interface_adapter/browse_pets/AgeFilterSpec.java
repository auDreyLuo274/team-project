package interface_adapter.browse_pets;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import use_case.browse_pets.AgeRangeFilter;
import use_case.browse_pets.PetFilter;

/**
 * The age drop-down.
 */
public class AgeFilterSpec implements FilterSpec {

    @Override
    public String getId() {
        return "age";
    }

    @Override
    public String getLabel() {
        return "Age";
    }

    @Override
    public Control getControl() {
        return Control.SINGLE_CHOICE;
    }

    @Override
    public List<FilterOption> getOptions() {
        final List<FilterOption> options = new ArrayList<>();
        for (AgeFilterOption option : AgeFilterOption.values()) {
            options.add(new FilterOption(option.name(), option.getLabel()));
        }
        return options;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_A;
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        if (chosen.isEmpty()) {
            return List.of();
        }
        final AgeFilterOption option = AgeFilterOption.valueOf(chosen.get(0));
        if (option.getMinYears() == null) {
            return List.of();
        }
        return List.of(new AgeRangeFilter(option.getMinYears(), option.getMaxYears()));
    }
}
