package interface_adapter.browse_pets;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import use_case.browse_pets.PetFilter;
import use_case.browse_pets.SexFilter;

/**
 * The sex drop-down.
 */
public class SexFilterSpec implements FilterSpec {

    @Override
    public String getId() {
        return "sex";
    }

    @Override
    public String getLabel() {
        return "Sex";
    }

    @Override
    public Control getControl() {
        return Control.SINGLE_CHOICE;
    }

    @Override
    public List<FilterOption> getOptions() {
        final List<FilterOption> options = new ArrayList<>();
        for (SexFilterOption option : SexFilterOption.values()) {
            options.add(new FilterOption(option.name(), option.toString()));
        }
        return options;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_S;
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        if (chosen.isEmpty() || SexFilterOption.valueOf(chosen.get(0)).getSex() == null) {
            return List.of();
        }
        return List.of(new SexFilter(SexFilterOption.valueOf(chosen.get(0)).getSex()));
    }
}
