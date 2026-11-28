package use_case.update_application_status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import entity.Application;
import entity.ApplicationStatus;
import entity.Pet;
import entity.PetSex;
import entity.PetStatus;
import entity.Species;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UpdateApplicationInteractorTest {

    private final FakePets pets = new FakePets();
    private final FakeApplications applications = new FakeApplications();
    private final RecordingPresenter presenter = new RecordingPresenter();
    private final UpdateApplicationInteractor interactor =
            new UpdateApplicationInteractor(presenter, pets, applications);

    @Test
    void approvingMarksThePetAdoptedAndSavesTheApplication() {
        pets.put(pet("p1"));
        applications.put(application("a1", "p1"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, "Welcome"));

        assertNull(presenter.error);
        assertEquals(ApplicationStatus.APPROVED, presenter.output.getApplicationStatus());
        assertEquals(ApplicationStatus.APPROVED, applications.stored("a1").getStatus());
        assertEquals(PetStatus.ADOPTED, pets.findById("p1").getStatus());
    }

    @Test
    void approvingAnApplicationForARemovedPetChangesNothing() {
        applications.put(application("a1", "p1"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, ""));

        assertEquals("This pet is no longer listed, so the application cannot be approved.", presenter.error);
        assertEquals(ApplicationStatus.PENDING, applications.stored("a1").getStatus());
    }

    @Test
    void aSecondApprovalForTheSamePetIsRefused() {
        pets.put(pet("p1"));
        applications.put(application("a1", "p1"));
        applications.put(application("a2", "p1"));
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, ""));

        interactor.execute(new UpdateApplicationInputData("a2", ApplicationStatus.APPROVED, ""));

        assertEquals("This pet has already been adopted.", presenter.error);
        assertEquals(ApplicationStatus.PENDING, applications.stored("a2").getStatus());
    }

    @Test
    void whenThePetCannotBeSavedTheApplicationStaysPending() {
        pets.put(pet("p1"));
        pets.failWrites = true;
        applications.put(application("a1", "p1"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, ""));

        assertEquals("Could not save the change.", presenter.error);
        assertEquals(ApplicationStatus.PENDING, applications.stored("a1").getStatus());
        assertEquals(PetStatus.AVAILABLE, pets.findById("p1").getStatus());
    }

    @Test
    void whenThePetIsNotFoundOnUpdateTheApplicationStaysPending() {
        pets.put(pet("p1"));
        pets.updateReturns = false;
        applications.put(application("a1", "p1"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, ""));

        assertEquals("Could not save the change.", presenter.error);
        assertEquals(PetStatus.AVAILABLE, pets.findById("p1").getStatus());
    }

    @Test
    void whenTheApplicationCannotBeSavedThePetGoesBackToAvailable() {
        pets.put(pet("p1"));
        applications.put(application("a1", "p1"));
        applications.failWrites = true;

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.APPROVED, ""));

        assertEquals("Could not save the change.", presenter.error);
        assertEquals(ApplicationStatus.PENDING, applications.stored("a1").getStatus());
        assertEquals(PetStatus.AVAILABLE, pets.stored("p1"));
    }

    @Test
    void otherStatusesDoNotTouchThePet() {
        pets.put(pet("p1"));
        applications.put(application("a1", "p1"));

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.INTERVIEW, "Call Friday"));

        assertEquals(ApplicationStatus.INTERVIEW, applications.stored("a1").getStatus());
        assertEquals("Call Friday", applications.stored("a1").getAdminNote());
        assertEquals(PetStatus.AVAILABLE, pets.findById("p1").getStatus());
    }

    @Test
    void aFailedSaveForOtherStatusesIsReported() {
        applications.put(application("a1", "p1"));
        applications.failWrites = true;

        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.REJECTED, ""));

        assertEquals("Could not save the change.", presenter.error);
    }

    @Test
    void unknownApplicationIsReported() {
        interactor.execute(new UpdateApplicationInputData("missing", ApplicationStatus.REJECTED, ""));

        assertEquals("Application not found", presenter.error);
    }

    @Test
    void pendingIsNotAnAllowedNewStatus() {
        interactor.execute(new UpdateApplicationInputData("a1", ApplicationStatus.PENDING, ""));

        assertEquals("Application status has to be one of APPROVED, INTERVIEW or REJECTED.", presenter.error);
    }

    @Test
    void switchingBackIsPassedToThePresenter() {
        interactor.switchToReviewApplicationView();

        assertEquals(1, presenter.switches);
    }

    private static Pet pet(String id) {
        return new Pet(id, "Miso", Species.CAT, "Tabby", 2, PetSex.FEMALE, List.of(), "", false, true, true,
                BigDecimal.TEN, "", PetStatus.AVAILABLE);
    }

    private static Application application(String id, String petId) {
        return new Application(id, "u1", petId, "House", "Some", "2", "u1@example.com", "", LocalDate.of(2026, 11, 1));
    }

    private static final class FakePets implements UpdateApplicationPetDataAccessInterface {
        private final Map<String, Pet> live = new HashMap<>();
        private final Map<String, PetStatus> saved = new HashMap<>();
        private boolean failWrites;
        private boolean updateReturns = true;

        void put(Pet pet) {
            live.put(pet.getId(), pet);
            saved.put(pet.getId(), pet.getStatus());
        }

        PetStatus stored(String id) {
            return saved.get(id);
        }

        @Override
        public Pet findById(String id) {
            return live.get(id);
        }

        @Override
        public boolean update(Pet pet) {
            if (failWrites) {
                throw new IllegalStateException("disk full");
            }
            if (updateReturns) {
                saved.put(pet.getId(), pet.getStatus());
            }
            return updateReturns;
        }
    }

    private static final class FakeApplications implements UpdateApplicationDataAccessInterface {
        private final Map<String, Application> saved = new HashMap<>();
        private boolean failWrites;

        void put(Application application) {
            saved.put(application.getId(), application.copy());
        }

        Application stored(String id) {
            return saved.get(id);
        }

        @Override
        public Application getApplicationById(String applicationId) {
            final Application application = saved.get(applicationId);
            return application == null ? null : application.copy();
        }

        @Override
        public void save(Application application) {
            if (failWrites) {
                throw new IllegalStateException("disk full");
            }
            saved.put(application.getId(), application.copy());
        }
    }

    private static final class RecordingPresenter implements UpdateApplicationOutputBoundary {
        private UpdateApplicationOutputData output;
        private String error;
        private int switches;

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
            switches++;
        }
    }
}
