package use_case.track_applications;

/**
 * The Input Data for the Track Applications Use Case.
 */
public class TrackApplicationsInputData {

    private final String adopterId;

    public TrackApplicationsInputData(String adopterId) {
        this.adopterId = adopterId;
    }

    String getAdopterId() {
        return adopterId;
    }
}
