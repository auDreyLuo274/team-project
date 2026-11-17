package use_case.track_applications;

import java.util.ArrayList;
import java.util.List;

import entity.Application;

/**
 * The Track Applications Interactor.
 */
public class TrackApplicationsInteractor implements TrackApplicationsInputBoundary {

    private final TrackApplicationsDataAccessInterface applicationDataAccess;
    private final TrackApplicationsPetDataAccessInterface petDataAccess;
    private final TrackApplicationsOutputBoundary presenter;

    public TrackApplicationsInteractor(TrackApplicationsDataAccessInterface applicationDataAccess,
                                       TrackApplicationsPetDataAccessInterface petDataAccess,
                                       TrackApplicationsOutputBoundary presenter) {
        this.applicationDataAccess = applicationDataAccess;
        this.petDataAccess = petDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(TrackApplicationsInputData inputData) {
        final String adopterId = inputData.getAdopterId();
        if (adopterId == null || adopterId.isEmpty()) {
            presenter.prepareFailView("Please log in to see your applications.");
            return;
        }

        final List<ApplicationSummary> summaries = new ArrayList<>();
        for (Application application : applicationDataAccess.getApplicationsByAdopter(adopterId)) {
            summaries.add(new ApplicationSummary(application.getId(),
                    petDataAccess.getPetName(application.getPetId()), application.getSubmittedAt(),
                    application.getStatus().name(), application.getAdminNote()));
        }
        presenter.prepareSuccessView(new TrackApplicationsOutputData(summaries));
    }
}
