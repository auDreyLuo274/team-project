package interface_adapter.track_applications;

/**
 * One application, formatted as text for the My Applications table.
 */
public class ApplicationRow {

    private final String petName;
    private final String submittedAt;
    private final String status;
    private final String adminNote;

    public ApplicationRow(String petName, String submittedAt, String status, String adminNote) {
        this.petName = petName;
        this.submittedAt = submittedAt;
        this.status = status;
        this.adminNote = adminNote;
    }

    public String getPetName() {
        return petName;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminNote() {
        return adminNote;
    }
}
