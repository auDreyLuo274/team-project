package interface_adapter.browse_pets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import entity.Pet;
import entity.PetSex;
import entity.PetStatus;
import entity.Species;
import entity.Temperament;
import use_case.browse_pets.BrowsePetsInputBoundary;
import use_case.browse_pets.BrowsePetsInputData;
import use_case.browse_pets.PetFilter;

class BrowsePetsControllerTest {

    private static class CapturingInteractor implements BrowsePetsInputBoundary {
        private BrowsePetsInputData received;

        @Override
        public void execute(BrowsePetsInputData inputData) {
            received = inputData;
        }
    }

    private final CapturingInteractor interactor = new CapturingInteractor();
    private final BrowsePetsController controller =
            new BrowsePetsController(interactor, FilterSpecs.loadRegistered());

    private Pet pet(String breed, int age, PetSex sex, boolean needsMedication, boolean vaccinated,
                    boolean spayedNeutered, Temperament... tags) {
        return new Pet("1", "Miso", Species.CAT, breed, age, sex, List.of(tags), "", needsMedication, vaccinated,
                spayedNeutered, BigDecimal.TEN, "", PetStatus.AVAILABLE);
    }

    private Pet pet() {
        return pet("Tabby", 2, PetSex.FEMALE, false, true, true);
    }

    private long matchCount(Pet pet) {
        return interactor.received.getFilters().stream().filter(filter -> filter.matches(pet)).count();
    }

    @Test
    void noChoicesSendsNoFilters() {
        controller.browse(Map.of());

        assertTrue(interactor.received.getFilters().isEmpty());
    }

    @Test
    void choicesThatMeanNoFilterSendNoFilters() {
        controller.browse(Map.of("age", List.of("ANY"), "sex", List.of("ANY"), "breed", List.of(" "),
                "color", List.of(""), "hairLength", List.of("ANY"), "temperament", List.of(), "health", List.of()));

        assertTrue(interactor.received.getFilters().isEmpty());
    }

    @Test
    void ageChoiceBecomesAnAgeFilter() {
        controller.browse(Map.of("age", List.of("ONE_TO_THREE")));

        assertEquals(1, interactor.received.getFilters().size());
        assertEquals(1, matchCount(pet("Tabby", 2, PetSex.FEMALE, false, true, true)));
        assertEquals(0, matchCount(pet("Tabby", 6, PetSex.FEMALE, false, true, true)));
    }

    @Test
    void sexChoiceBecomesASexFilter() {
        controller.browse(Map.of("sex", List.of("MALE")));

        assertEquals(1, matchCount(pet("Tabby", 2, PetSex.MALE, false, true, true)));
        assertEquals(0, matchCount(pet()));
    }

    @Test
    void breedTextBecomesABreedFilter() {
        controller.browse(Map.of("breed", List.of("tab")));

        assertEquals(1, matchCount(pet()));
        assertEquals(0, matchCount(pet("Siamese", 2, PetSex.FEMALE, false, true, true)));
    }

    @Test
    void temperamentChoicesBecomeOneTemperamentFilter() {
        controller.browse(Map.of("temperament", List.of("CALM", "SHY")));

        assertEquals(1, interactor.received.getFilters().size());
        assertEquals(1, matchCount(pet("Tabby", 2, PetSex.FEMALE, false, true, true,
                Temperament.CALM, Temperament.SHY)));
        assertEquals(0, matchCount(pet("Tabby", 2, PetSex.FEMALE, false, true, true, Temperament.CALM)));
    }

    @Test
    void eachTickedHealthBoxBecomesAFilter() {
        controller.browse(Map.of("health", List.of("NO_MEDICAL_NEEDS", "VACCINATED", "SPAYED_NEUTERED")));

        assertEquals(3, interactor.received.getFilters().size());
        assertEquals(3, matchCount(pet()));
        assertEquals(2, matchCount(pet("Tabby", 2, PetSex.FEMALE, true, true, true)));
    }

    @Test
    void severalChoicesSendSeveralFilters() {
        controller.browse(Map.of("age", List.of("ONE_TO_THREE"), "sex", List.of("FEMALE"),
                "breed", List.of("tab")));

        assertEquals(3, interactor.received.getFilters().size());
        assertEquals(3, matchCount(pet()));
    }

    @Test
    void aBrandNewFilterWorksWithoutChangingTheController() {
        final PetFilter onlyMiso = pet -> pet.getName().equals("Miso");
        final FilterSpec nameSpec = new FilterSpec() {
            @Override
            public String getId() {
                return "name";
            }

            @Override
            public String getLabel() {
                return "Name";
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
            public List<PetFilter> toFilters(List<String> chosen) {
                return chosen.isEmpty() ? List.of() : List.of(onlyMiso);
            }
        };

        new BrowsePetsController(interactor, List.of(nameSpec)).browse(Map.of("name", List.of("Miso")));

        assertEquals(1, interactor.received.getFilters().size());
        assertEquals(1, matchCount(pet()));
    }
}
