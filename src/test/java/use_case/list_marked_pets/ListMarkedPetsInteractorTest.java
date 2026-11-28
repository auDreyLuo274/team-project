package use_case.list_marked_pets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import entity.Pet;
import entity.PetSex;
import entity.PetStatus;
import entity.Species;
import use_case.mark_pet.MarkedPetsDataAccessInterface;

class ListMarkedPetsInteractorTest {
    private final List<String> events = new ArrayList<>();
    private final List<String> shownNames = new ArrayList<>();

    private Pet pet(String id, String name) {
        return new Pet(id, name, Species.CAT, "Tabby", 2, PetSex.FEMALE, List.of(), "", false, true, true,
                BigDecimal.TEN, "", PetStatus.AVAILABLE);
    }

    private final Map<String, Pet> pets = Map.of("p1", pet("p1", "Miso"), "p2", pet("p2", "Biscuit"));

    private final ListMarkedPetsOutputBoundary presenter = new ListMarkedPetsOutputBoundary() {
        @Override
        public void presentMarkedPets(List<MarkedPetSummary> found) {
            events.add("pets");
            found.forEach(p -> shownNames.add(p.getName() + "/" + p.getStatus()));
        }

        @Override
        public void presentLoginRequired() {
            events.add("login required");
        }
    };

    private ListMarkedPetsInteractor interactor(Set<String> markedIds) {
        final MarkedPetsDataAccessInterface marked = new MarkedPetsDataAccessInterface() {
            @Override
            public Set<String> findMarkedPetIds(String adopterId) {
                return "a1".equals(adopterId) ? markedIds : Set.of();
            }

            @Override
            public void mark(String adopterId, String petId) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void unmark(String adopterId, String petId) {
                throw new UnsupportedOperationException();
            }
        };
        final ListMarkedPetsPetDataAccessInterface petData = id -> Optional.ofNullable(pets.get(id));
        return new ListMarkedPetsInteractor(marked, petData, presenter);
    }

    @Test
    void listsTheMarkedCatsInIdOrder() {
        interactor(Set.of("p2", "p1")).execute("a1");

        assertEquals(List.of("pets"), events);
        assertEquals(List.of("Miso/AVAILABLE", "Biscuit/AVAILABLE"), shownNames);
    }

    @Test
    void aMarkedCatThatNoLongerExistsIsLeftOut() {
        interactor(Set.of("p1", "gone")).execute("a1");

        assertEquals(List.of("Miso/AVAILABLE"), shownNames);
    }

    @Test
    void nothingMarkedGivesAnEmptyList() {
        interactor(Set.of()).execute("a1");

        assertEquals(List.of("pets"), events);
        assertTrue(shownNames.isEmpty());
    }

    @Test
    void aVisitorWhoIsNotLoggedInIsAskedToLogIn() {
        interactor(Set.of("p1")).execute("");

        assertEquals(List.of("login required"), events);
    }
}
