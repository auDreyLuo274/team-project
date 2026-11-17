package interface_adapter.track_applications;

import java.util.ArrayList;
import java.util.List;

import use_case.track_applications.ApplicationSummary;
import use_case.track_applications.TrackApplicationsOutputBoundary;
import use_case.track_applications.TrackApplicationsOutputData;

/**
 * The Presenter for the Track Applications Use Case.
 */
public class TrackApplicationsPresenter implements TrackApplicationsOutputBoundary {

    private final TrackApplicationsViewModel trackApplicationsViewModel;

    public TrackApplicationsPresenter(TrackApplicationsViewModel trackApplicationsViewModel) {
        this.trackApplicationsViewModel = trackApplicationsViewModel;
    }

    @Override
    public void prepareSuccessView(TrackApplicationsOutputData outputData) {
        final List<ApplicationRow> rows = new ArrayList<>();
        for (ApplicationSummary summary : outputData.getApplications()) {
            String note = summary.getAdminNote();
            if (note.isEmpty()) {
                note = "(no note from the shelter yet)";
            }
            rows.add(new ApplicationRow(summary.getPetName(), summary.getSubmittedAt().toString(),
                    summary.getStatus(), note));
        }

        final TrackApplicationsState state = trackApplicationsViewModel.getState();
        state.setRows(rows);
        if (rows.isEmpty()) {
            state.setMessage("You have not submitted any applications yet.");
        }
        else {
            state.setMessage("Select an application to read the shelter's note.");
        }
        trackApplicationsViewModel.notifyStateChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final TrackApplicationsState state = trackApplicationsViewModel.getState();
        state.setRows(new ArrayList<>());
        state.setMessage(errorMessage);
        trackApplicationsViewModel.notifyStateChanged();
    }
}
