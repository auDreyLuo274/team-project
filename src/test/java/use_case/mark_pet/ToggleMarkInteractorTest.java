package use_case.mark_pet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ToggleMarkInteractorTest {
    private final Map<String, Set<String>> store = new HashMap<>();
    private final List<String> events = new ArrayList<>();

    private final MarkedPetsDataAccessInterface dataAccess = new MarkedPetsDataAccessInterface() {
        @Override
        public Set<String> findMarkedPetIds(String adopterId) {
            return store.getOrDefault(adopterId, Set.of());
        }

        @Override
        public void mark(String adopterId, String petId) {
            store.computeIfAbsent(adopterId, id -> new HashSet<>()).add(petId);
        }

        @Override
        public void unmark(String adopterId, String petId) {
            store.getOrDefault(adopterId, new HashSet<>()).remove(petId);
        }
    };

    private final ToggleMarkOutputBoundary presenter = new ToggleMarkOutputBoundary() {
        @Override
        public void presentMarkChanged(String petId, boolean marked) {
            events.add(petId + ":" + marked);
        }

        @Override
        public void presentLoginRequired() {
            events.add("login required");
        }
    };

    private final ToggleMarkInteractor interactor = new ToggleMarkInteractor(dataAccess, presenter);

    @Test
    void anUnmarkedCatBecomesMarked() {
        interactor.execute(new ToggleMarkInputData("a1", "p1"));

        assertEquals(Set.of("p1"), store.get("a1"));
        assertEquals(List.of("p1:true"), events);
    }

    @Test
    void aMarkedCatBecomesUnmarked() {
        interactor.execute(new ToggleMarkInputData("a1", "p1"));

        interactor.execute(new ToggleMarkInputData("a1", "p1"));

        assertTrue(store.get("a1").isEmpty());
        assertEquals(List.of("p1:true", "p1:false"), events);
    }

    @Test
    void eachAdopterHasTheirOwnMarks() {
        interactor.execute(new ToggleMarkInputData("a1", "p1"));
        interactor.execute(new ToggleMarkInputData("a2", "p2"));

        assertEquals(Set.of("p1"), store.get("a1"));
        assertEquals(Set.of("p2"), store.get("a2"));
    }

    @Test
    void aVisitorWhoIsNotLoggedInIsAskedToLogIn() {
        interactor.execute(new ToggleMarkInputData("", "p1"));
        interactor.execute(new ToggleMarkInputData(null, "p1"));

        assertTrue(store.isEmpty());
        assertEquals(List.of("login required", "login required"), events);
    }
}
