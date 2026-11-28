package interface_adapter.browse_pets;

import java.awt.event.KeyEvent;
import java.util.List;

import use_case.browse_pets.HairLengthFilter;
import use_case.browse_pets.PetFilter;

/**
 * The hair length drop-down.
 */
public class HairLengthFilterSpec implements FilterSpec {
    private static final String ANY = "ANY";

    @Override
    public String getId() {
        return "hairLength";
    }

    @Override
    public String getLabel() {
        return "Hair length";
    }

    @Override
    public Control getControl() {
        return Control.SINGLE_CHOICE;
    }

    @Override
    public List<FilterOption> getOptions() {
        return List.of(new FilterOption(ANY, "Any length"), new FilterOption("Short", "Short"),
                new FilterOption("Medium", "Medium"), new FilterOption("Long", "Long"),
                new FilterOption("Hairless", "Hairless"));
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_H;
    }

    @Override
    public List<PetFilter> toFilters(List<String> chosen) {
        if (chosen.isEmpty() || ANY.equals(chosen.get(0))) {
            return List.of();
        }
        return List.of(new HairLengthFilter(chosen.get(0)));
    }
}
