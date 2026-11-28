package interface_adapter.browse_pets;

import java.awt.event.KeyEvent;
import java.util.List;

import use_case.browse_pets.BreedFilter;
import use_case.browse_pets.PetFilter;

/**
 * The breed text box.
 */
public class BreedFilterSpec implements FilterSpec {

    @Override
    public String getId() {
        return "breed";
    }

    @Override
    public String getLabel() {
        return "Breed";
    }

    @Override
    public Control getControl() {
        return Control.TEXT;
    }

    @Override
    public List<FilterOption> getOptions() {
        return List.of();
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_B;
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        if (chosen.isEmpty() || chosen.get(0).isBlank()) {
            return List.of();
        }
        return List.of(new BreedFilter(chosen.get(0)));
    }
}
