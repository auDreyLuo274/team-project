package use_case.update_application_status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.Application;
import entity.ApplicationStatus;
import entity.Pet;
import entity.PetSex;
import entity.PetStatus;
import entity.Species;

class UpdateApplicationInteractorTest {

    private FakeApplicationDataAccess applications;
    private FakePetDataAccess pets;
    private RecordingPresenter presenter;
    private UpdateApplicationInteractor interactor;

    @BeforeEach
    void setUp() {
        applications = new FakeApplicationDataAccess();
        applications.add(application("a1", "alice", "mochi"));
        pets = new FakePetDataAccess();
        pets.add(new Pet("mochi", "Mochi", Species.CAT, "Tabby", 2, PetSex.FEMALE, List.of(), "", false, true,
                true, BigDecimal.ZERO, "", PetStatus.PENDING));
        presenter = new RecordingPresenter();
        interactor = new UpdateApplicationInteractor(presenter, pets, applications);
    }

    private static Application application(String id, String adopterId, String petId) {
        return new Application(id, adopterId, petId, "House", "Had two cats", "2 adults", "me@example.com", "",
                LocalDate.of(2026, 11, 16));
    }

    @Test
    void savesTheNewStatusAndNote() {
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.INTERVIEW, "Call us on Friday"));

        final Application saved = applications.saved.get("a1");
        assertEquals(ApplicationStatus.INTERVIEW, saved.getStatus());
        assertEquals("Call us on Friday", saved.getAdminNote());
        assertEquals("a1", presenter.output.getApplicationId());
        assertEquals(ApplicationStatus.INTERVIEW, presenter.output.getApplicationStatus());
        assertEquals("Call us on Friday", presenter.output.getAdminNote());
        assertEquals(PetStatus.PENDING, pets.findById("mochi").getStatus());
        assertNull(presenter.error);
    }

    @Test
    void approvingMarksThePetAsAdopted() {
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, "Welcome!"));

        assertEquals(ApplicationStatus.APPROVED, applications.saved.get("a1").getStatus());
        assertEquals(PetStatus.ADOPTED, pets.updated.get("mochi").getStatus());
        assertEquals(ApplicationStatus.APPROVED, presenter.output.getApplicationStatus());
    }

    @Test
    void approvingRejectsTheOtherActiveApplicationsForThePet() {
        applications.add(application("a2", "bob", "mochi"));
        final Application interview = application("a3", "carol", "mochi");
        interview.updateStatus(ApplicationStatus.INTERVIEW, "Call us on Friday");
        applications.add(interview);
        final Application withdrawn = application("a4", "dave", "mochi");
        withdrawn.withdraw();
        applications.add(withdrawn);
        applications.add(application("a5", "erin", "tofu"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, "Welcome!"));

        assertEquals(ApplicationStatus.APPROVED, applications.saved.get("a1").getStatus());
        assertEquals("Welcome!", applications.saved.get("a1").getAdminNote());
        assertEquals(ApplicationStatus.REJECTED, applications.saved.get("a2").getStatus());
        assertEquals(UpdateApplicationInteractor.ADOPTED_BY_SOMEONE_ELSE, applications.saved.get("a2").getAdminNote());
        assertEquals(ApplicationStatus.REJECTED, applications.saved.get("a3").getStatus());
        assertFalse(applications.saved.containsKey("a4"));
        assertFalse(applications.saved.containsKey("a5"));
        assertEquals(ApplicationStatus.WITHDRAWN, applications.getApplicationById("a4").getStatus());
        assertEquals(ApplicationStatus.PENDING, applications.getApplicationById("a5").getStatus());
        assertEquals(ApplicationStatus.APPROVED, presenter.output.getApplicationStatus());
    }

    @Test
    void interviewOrRejectLeavesTheOtherApplicationsAlone() {
        applications.add(application("a2", "bob", "mochi"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.INTERVIEW, ""));
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.REJECTED, ""));

        assertFalse(applications.saved.containsKey("a2"));
        assertEquals(ApplicationStatus.PENDING, applications.getApplicationById("a2").getStatus());
    }

    @Test
    void rejectingLeavesThePetListed() {
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.REJECTED, ""));

        assertEquals(ApplicationStatus.REJECTED, applications.saved.get("a1").getStatus());
        assertTrue(pets.updated.isEmpty());
    }

    @Test
    void rejectsAStatusTheAdminCannotChoose() {
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.PENDING, ""));
        assertEquals("Application status has to be one of APPROVED, INTERVIEW or REJECTED.", presenter.error);

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.WITHDRAWN, ""));
        assertEquals("Application status has to be one of APPROVED, INTERVIEW or REJECTED.", presenter.error);

        interactor.execute(new UpdateApplicationInputData("a1", null, ""));
        assertEquals("Application status has to be one of APPROVED, INTERVIEW or REJECTED.", presenter.error);

        assertTrue(applications.saved.isEmpty());
        assertNull(presenter.output);
    }

    @Test
    void reportsAMissingApplication() {
        interactor.execute(new UpdateApplicationInputData("nope", ApplicationStatus.APPROVED, ""));

        assertEquals("Application not found", presenter.error);
        assertTrue(applications.saved.isEmpty());
        assertNull(presenter.output);
    }

    @Test
    void reportsASaveFailure() {
        applications.failOnSave = true;

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, ""));

        assertEquals("Could not save the change.", presenter.error);
        assertTrue(pets.updated.isEmpty());
        assertNull(presenter.output);
    }

    @Test
    void switchesToTheReviewApplicationView() {
        interactor.switchToReviewApplicationView();

        assertTrue(presenter.switchedToReview);
    }

    private static final class FakeApplicationDataAccess implements UpdateApplicationDataAccessInterface {
        private final Map<String, Application> applications = new LinkedHashMap<>();
        private final Map<String, Application> saved = new HashMap<>();
        private boolean failOnSave;

        void add(Application application) {
            applications.put(application.getId(), application);
        }

        @Override
        public Application getApplicationById(String applicationId) {
            return applications.get(applicationId);
        }

        @Override
        public List<Application> getApplicationsByPet(String petId) {
            final List<Application> result = new ArrayList<>();
            for (Application application : applications.values()) {
                if (application.getPetId().equals(petId)) {
                    result.add(application.copy());
                }
            }
            return result;
        }

        @Override
        public void save(Application application) {
            if (failOnSave) {
                throw new RuntimeException("disk full");
            }
            saved.put(application.getId(), application);
            applications.put(application.getId(), application.copy());
        }
    }

    private static final class FakePetDataAccess implements UpdateApplicationPetDataAccessInterface {
        private final Map<String, Pet> pets = new HashMap<>();
        private final Map<String, Pet> updated = new HashMap<>();

        void add(Pet pet) {
            pets.put(pet.getId(), pet);
        }

        @Override
        public Pet findById(String id) {
            return pets.get(id);
        }

        @Override
        public boolean update(Pet pet) {
            updated.put(pet.getId(), pet);
            return true;
        }
    }

    private static final class RecordingPresenter implements UpdateApplicationOutputBoundary {
        private UpdateApplicationOutputData output;
        private String error;
        private boolean switchedToReview;

        @Override
        public void prepareSuccessView(UpdateApplicationOutputData outputData) {
            output = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            error = errorMessage;
        }

        @Override
        public void switchToReviewApplicationView() {
            switchedToReview = true;
        }
    }
}
