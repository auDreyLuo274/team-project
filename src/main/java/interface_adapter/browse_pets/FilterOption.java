package interface_adapter.browse_pets;

/**
 * One choice inside a filter: a drop-down entry or a tick box.
 */
public class FilterOption {
    private final String id;
    private final String label;

    /**
     * Creates an option.
     *
     * @param id what {@link FilterSpec#toFilters} receives when the option is chosen
     * @param label the text shown to the visitor
     */
    public FilterOption(String id, String label) {
        this.id = id;
        this.label = label;
    }

    /**
     * Returns the id of the option.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the text shown to the visitor.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
