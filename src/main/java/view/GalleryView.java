package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interface_adapter.browse_pets.BrowsePetsController;
import interface_adapter.browse_pets.FilterOption;
import interface_adapter.browse_pets.FilterSpec;
import interface_adapter.browse_pets.GalleryViewModel;
import interface_adapter.browse_pets.GalleryViewModelListener;
import interface_adapter.browse_pets.PetCardData;
import interface_adapter.view_pet_details.ViewPetDetailsController;

/**
 * The pet gallery screen: a filter panel on the left and a scrollable grid of pet cards.
 */
public class GalleryView extends JPanel implements GalleryViewModelListener {
    /** Name this screen is registered under in the view manager. */
    public static final String VIEW_NAME = GalleryViewModel.VIEW_NAME;

    private static final int COLUMNS = 3;
    private static final int GAP = 12;
    private static final int PHOTO_WIDTH = 200;
    private static final int PHOTO_HEIGHT = 150;
    private static final int PHOTO_CAPTION_HEIGHT = 20;
    private static final int CARD_WIDTH = 236;

    private final BrowsePetsController controller;
    private final GalleryViewModel viewModel;
    private final ViewPetDetailsController detailsController;
    private final JPanel cardGrid = new JPanel(new GridLayout(0, COLUMNS, GAP, GAP));
    private final List<FilterSpec> specs;
    private final Map<String, Supplier<List<String>>> readers = new LinkedHashMap<>();
    private final List<Runnable> resetters = new ArrayList<>();
    private boolean updating;
    private int columns = COLUMNS;
    private boolean showingCards;

    /**
     * Creates the gallery screen.
     *
     * @param controller starts the browse use case when a filter changes
     * @param viewModel the model whose cards this view draws
     * @param detailsController opens the detail page when a card is chosen
     * @param specs the filters to offer, one control each
     */
    public GalleryView(BrowsePetsController controller, GalleryViewModel viewModel,
                       ViewPetDetailsController detailsController, List<FilterSpec> specs) {
        super(new BorderLayout());
        this.specs = List.copyOf(specs);
        this.controller = controller;
        this.detailsController = detailsController;
        this.viewModel = viewModel;
        viewModel.addListener(this);
        getAccessibleContext().setAccessibleName("Pet gallery");

        final JLabel title = new JLabel("Adoptable Cats");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        title.getAccessibleContext().setAccessibleName("Gallery heading");
        add(title, BorderLayout.NORTH);

        final JPanel filterPanel = buildFilterPanel();
        final JScrollPane filterScroll = new JScrollPane(filterPanel);
        // Leave room for the scroll bar so it does not cover the controls.
        filterScroll.setPreferredSize(new Dimension(filterPanel.getPreferredSize().width
                + filterScroll.getVerticalScrollBar().getPreferredSize().width, 0));
        filterScroll.setBorder(BorderFactory.createEmptyBorder());
        filterScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(filterScroll, BorderLayout.WEST);

        cardGrid.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        final JPanel gridHolder = new JPanel(new BorderLayout());
        gridHolder.add(cardGrid, BorderLayout.NORTH);
        final JScrollPane scrollPane = new JScrollPane(gridHolder);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                updateColumns(scrollPane.getViewport().getWidth());
            }
        });
    }

    /**
     * Fits as many cards per row as the width allows, so the cards keep their size when the window changes.
     */
    private void updateColumns(int width) {
        final int fitted = Math.max(1, (width - 2 * GAP) / (CARD_WIDTH + GAP));
        if (fitted != columns) {
            columns = fitted;
            if (showingCards) {
                cardGrid.setLayout(new GridLayout(0, columns, GAP, GAP));
                cardGrid.revalidate();
                cardGrid.repaint();
            }
        }
    }

    private JPanel buildFilterPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        panel.getAccessibleContext().setAccessibleName("Filters");

        // Every control is built from a FilterSpec, so a new filter needs no change here.
        for (FilterSpec spec : specs) {
            final JLabel heading = new JLabel(spec.getLabel());
            if (spec.getMnemonic() != 0) {
                heading.setDisplayedMnemonic(spec.getMnemonic());
            }
            panel.add(heading);
            switch (spec.getControl()) {
                case SINGLE_CHOICE:
                    addChoiceBox(panel, spec, heading);
                    break;
                case MULTI_CHOICE:
                    addTickBoxes(panel, spec);
                    break;
                default:
                    addTextBox(panel, spec, heading);
                    break;
            }
            panel.add(Box.createVerticalStrut(GAP));
        }

        final JButton clearButton = new JButton("Clear filters");
        clearButton.setMnemonic(KeyEvent.VK_C);
        clearButton.getAccessibleContext().setAccessibleName("Clear filters");
        clearButton.addActionListener(event -> clearFilters());
        panel.add(clearButton);
        for (Component component : panel.getComponents()) {
            ((JComponent) component).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return panel;
    }

    private void addChoiceBox(JPanel panel, FilterSpec spec, JLabel heading) {
        final JComboBox<FilterOption> box = new JComboBox<>(spec.getOptions().toArray(new FilterOption[0]));
        heading.setLabelFor(box);
        box.getAccessibleContext().setAccessibleName(spec.getLabel() + " filter");
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, box.getPreferredSize().height));
        box.addActionListener(event -> applyFilters());
        readers.put(spec.getId(), () -> List.of(((FilterOption) box.getSelectedItem()).getId()));
        resetters.add(() -> box.setSelectedIndex(0));
        panel.add(box);
    }

    private void addTextBox(JPanel panel, FilterSpec spec, JLabel heading) {
        final JTextField field = new JTextField(12);
        heading.setLabelFor(field);
        field.getAccessibleContext().setAccessibleName(spec.getLabel() + " filter");
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                applyFilters();
            }
        });
        readers.put(spec.getId(), () -> List.of(field.getText()));
        resetters.add(() -> field.setText(""));
        panel.add(field);
    }

    private void addTickBoxes(JPanel panel, FilterSpec spec) {
        final Map<String, JCheckBox> boxes = new LinkedHashMap<>();
        for (FilterOption option : spec.getOptions()) {
            final JCheckBox box = new JCheckBox(option.getLabel());
            box.getAccessibleContext().setAccessibleName(spec.getLabel() + ": " + option.getLabel());
            box.addActionListener(event -> applyFilters());
            boxes.put(option.getId(), box);
            panel.add(box);
        }
        readers.put(spec.getId(), () -> {
            final List<String> chosen = new ArrayList<>();
            for (Map.Entry<String, JCheckBox> entry : boxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    chosen.add(entry.getKey());
                }
            }
            return chosen;
        });
        resetters.add(() -> boxes.values().forEach(box -> box.setSelected(false)));
    }

    private void applyFilters() {
        if (updating) {
            return;
        }
        final Map<String, List<String>> choices = new LinkedHashMap<>();
        for (Map.Entry<String, Supplier<List<String>>> entry : readers.entrySet()) {
            choices.put(entry.getKey(), entry.getValue().get());
        }
        controller.browse(choices);
    }

    private void clearFilters() {
        updating = true;
        resetters.forEach(Runnable::run);
        updating = false;
        applyFilters();
    }

    @Override
    public void stateChanged() {
        showCards(viewModel.getState().getCards());
    }

    private void showCards(List<PetCardData> cards) {
        cardGrid.removeAll();
        if (cards.isEmpty()) {
            showingCards = false;
            cardGrid.setLayout(new GridLayout(1, 1));
            final JLabel empty = new JLabel("No cats match your filters. Try clearing some filters.");
            empty.getAccessibleContext().setAccessibleName("No results");
            cardGrid.add(empty);
        } else {
            showingCards = true;
            cardGrid.setLayout(new GridLayout(0, columns, GAP, GAP));
            for (PetCardData card : cards) {
                cardGrid.add(buildCard(card));
            }
        }
        cardGrid.revalidate();
        cardGrid.repaint();
    }

    private JPanel buildCard(PetCardData data) {
        final JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setPreferredSize(new Dimension(CARD_WIDTH, 300));
        final JLabel photo = new JLabel();
        photo.setHorizontalAlignment(JLabel.CENTER);
        photo.setHorizontalTextPosition(JLabel.CENTER);
        photo.setVerticalTextPosition(JLabel.BOTTOM);
        final Dimension photoSize = new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT + PHOTO_CAPTION_HEIGHT);
        photo.setPreferredSize(photoSize);
        photo.setMinimumSize(photoSize);
        photo.setMaximumSize(photoSize);
        photo.getAccessibleContext().setAccessibleName("Cat photo");
        ListingPhoto.showFilled(photo, data.getPhotoPath(), PHOTO_WIDTH, PHOTO_HEIGHT);
        final JLabel name = new JLabel(data.getName());
        name.setFont(name.getFont().deriveFont(Font.BOLD, 15f));
        card.add(photo);
        card.add(Box.createVerticalStrut(6));
        card.add(name);
        card.add(new JLabel(data.getSummary()));
        card.add(new JLabel(data.getStatus()));
        final JButton detailsButton = new JButton("View details");
        detailsButton.getAccessibleContext().setAccessibleName("View details for " + data.getName());
        detailsButton.addActionListener(event -> detailsController.show(data.getPetId()));
        card.add(Box.createVerticalStrut(6));
        card.add(detailsButton);
        for (Component component : card.getComponents()) {
            ((JComponent) component).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                detailsController.show(data.getPetId());
            }
        });
        card.getAccessibleContext().setAccessibleName("Pet card: " + data.getName());
        card.getAccessibleContext().setAccessibleDescription(data.getSummary() + ". " + data.getStatus());
        return card;
    }

    /**
     * Loads the gallery with whatever the filters currently say, used when the screen first opens.
     */
    public void loadAll() {
        applyFilters();
    }
}
