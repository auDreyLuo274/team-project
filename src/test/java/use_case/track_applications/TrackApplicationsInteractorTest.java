package use_case.track_applications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data_access.InMemoryApplicationDataAccessObject;
import entity.Application;
import entity.ApplicationStatus;

class TrackApplicationsInteractorTest {

    private InMemoryApplicationDataAccessObject applicationDataAccess;
    private RecordingPresenter presenter;
    private TrackApplicationsInteractor interactor;

    @BeforeEach
    void setUp() {
        applicationDataAccess = new InMemoryApplicationDataAccessObject();
        presenter = new RecordingPresenter();
        interactor = new TrackApplicationsInteractor(applicationDataAccess, petId -> "Name of " + petId,
                presenter);
    }

    private Application application(String id, String adopterId, String petId) {
        return new Application(id, adopterId, petId, "House", "Had two cats", "2 adults", "me@example.com",
                "", LocalDate.of(2026, 11, 16));
    }

    @Test
    void listsOnlyTheAdoptersApplicationsWithStatusAndNote() {
        final Application reviewed = application("a1", "adopter1", "mochi");
        reviewed.updateStatus(ApplicationStatus.INTERVIEW, "Call us on Friday");
        applicationDataAccess.save(reviewed);
        applicationDataAccess.save(application("a2", "adopter1", "tofu"));
        applicationDataAccess.save(application("a3", "someone-else", "mochi"));

        interactor.execute(new TrackApplicationsInputData("adopter1"));

        final List<ApplicationSummary> summaries = presenter.success.getApplications();
        assertEquals(2, summaries.size());
        assertEquals("Name of mochi", summaries.get(0).getPetName());
        assertEquals("INTERVIEW", summaries.get(0).getStatus());
        assertEquals("Call us on Friday", summaries.get(0).getAdminNote());
        assertEquals(LocalDate.of(2026, 11, 16), summaries.get(0).getSubmittedAt());
        assertEquals("PENDING", summaries.get(1).getStatus());
    }

    @Test
    void noApplicationsGivesEmptyList() {
        interactor.execute(new TrackApplicationsInputData("adopter1"));
        assertTrue(presenter.success.getApplications().isEmpty());
        assertNull(presenter.error);
    }

    @Test
    void notLoggedInFails() {
        interactor.execute(new TrackApplicationsInputData(""));
        assertNotNull(presenter.error);
        assertNull(presenter.success);
    }

    private static class RecordingPresenter implements TrackApplicationsOutputBoundary {
        private TrackApplicationsOutputData success;
        private String error;

        @Override
        public void prepareSuccessView(TrackApplicationsOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            error = errorMessage;
        }
    }
}
