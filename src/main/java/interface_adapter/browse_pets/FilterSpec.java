package interface_adapter.browse_pets;

import java.util.List;

import use_case.browse_pets.PetFilter;

/**
 * One filter in the gallery's filter panel. It says what control to show and how to turn the visitor's choice
 * into {@link PetFilter}s. The controller and the gallery view only know this interface, so a new filter is a new
 * class that implements it and a line in
 * {@code META-INF/services/interface_adapter.browse_pets.FilterSpec}; nothing that exists is changed.
 */
public interface FilterSpec {

    /** The kind of control the gallery shows for a filter. */
    enum Control {
        /** A drop-down: the visitor picks one option, the first option means no filter. */
        SINGLE_CHOICE,
        /** A group of tick boxes: the visitor ticks any number of options. */
        MULTI_CHOICE,
        /** A text box: the one value is what the visitor typed. */
        TEXT
    }

    /**
     * Returns a short name for the filter, used to find its value.
     *
     * @return the id, unique among filters
     */
    String getId();

    /**
     * Returns the heading shown above the control.
     *
     * @return the label
     */
    String getLabel();

    /**
     * Returns the kind of control to show.
     *
     * @return the control
     */
    Control getControl();

    /**
     * Returns the options of a drop-down or a group of tick boxes.
     *
     * @return the options, empty for a text box
     */
    List<FilterOption> getOptions();

    /**
     * Returns the keyboard shortcut letter for the control.
     *
     * @return a {@link java.awt.event.KeyEvent} key code, or 0 for none
     */
    default int getMnemonic() {
        return 0;
    }

    /**
     * Turns what the visitor chose into filters.
     *
     * @param chosen the ids of the chosen options, or the one typed text for a text box; empty for no choice
     * @return the filters to apply, empty if the choice means no filter
     */
    List<PetFilter> toFilters(List<String> chosen);
}
