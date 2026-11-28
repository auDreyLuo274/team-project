package interface_adapter.browse_pets;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Finds every filter that is registered in
 * {@code META-INF/services/interface_adapter.browse_pets.FilterSpec}, in the order they are listed.
 */
public final class FilterSpecs {

    private FilterSpecs() {
    }

    /**
     * Loads the registered filters.
     *
     * @return one {@link FilterSpec} per registered filter
     */
    public static List<FilterSpec> loadRegistered() {
        final List<FilterSpec> specs = new ArrayList<>();
        for (FilterSpec spec : ServiceLoader.load(FilterSpec.class)) {
            specs.add(spec);
        }
        return specs;
    }
}
