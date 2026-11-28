package interface_adapter.browse_pets;

import java.awt.event.KeyEvent;
import java.util.List;

import use_case.browse_pets.ColorFilter;
import use_case.browse_pets.PetFilter;

/**
 * The coat colour text box.
 */
public class ColorFilterSpec implements FilterSpec {

    @Override
    public String getId() {
        return "color";
    }

    @Override
    public String getLabel() {
        return "Color";
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
        return KeyEvent.VK_O;
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        if (chosen.isEmpty() || chosen.get(0).isBlank()) {
            return List.of();
        }
        return List.of(new ColorFilter(chosen.get(0)));
    }
}
