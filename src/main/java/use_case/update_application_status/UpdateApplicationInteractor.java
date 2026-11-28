package use_case.update_application_status;

import entity.Application;
import entity.ApplicationStatus;
import entity.Pet;
import entity.PetStatus;

public class UpdateApplicationInteractor implements UpdateApplicationInputBoundary {
    static final String ADOPTED_BY_SOMEONE_ELSE = "This pet has been adopted by another applicant.";

    private final UpdateApplicationOutputBoundary presenter;
    private final UpdateApplicationPetDataAccessInterface petDataAccess;
    private final UpdateApplicationDataAccessInterface applicationDataAccess;

    public UpdateApplicationInteractor(UpdateApplicationOutputBoundary presenter,
                                       UpdateApplicationPetDataAccessInterface petDataAccess,
                                       UpdateApplicationDataAccessInterface applicationDataAccess) {
        this.presenter = presenter;
        this.applicationDataAccess = applicationDataAccess;
        this.petDataAccess = petDataAccess;
    }

    @Override
    public void execute(UpdateApplicationInputData inputData) {
        final ApplicationStatus status = inputData.getNewStatus();

        if (status != ApplicationStatus.APPROVED && status != ApplicationStatus.INTERVIEW
                && status != ApplicationStatus.REJECTED) {
            presenter.prepareFailView("Application status has to be one of "
                    + "APPROVED, INTERVIEW or REJECTED.");
            return;
        }

        final Application application = applicationDataAccess.getApplicationById(inputData.getApplicationId());
        if (application == null) {
            presenter.prepareFailView("Application not found");
            return;
        }

        application.updateStatus(status, inputData.getAdminNote());
        try {
            applicationDataAccess.save(application);
            if (status == ApplicationStatus.APPROVED) {
                rejectOtherApplications(application);
                Pet pet = petDataAccess.findById(application.getPetId());
                pet.updatePetStatus(PetStatus.ADOPTED);
                petDataAccess.update(pet);
            }
        }
        catch (RuntimeException ex) {
            presenter.prepareFailView("Could not save the change.");
            return;
        }

        presenter.prepareSuccessView(new UpdateApplicationOutputData(application.getId(),
                application.getStatus(), application.getAdminNote()));
    }

    /**
     * Rejects every other application for the same pet that is still being considered,
     * because the pet can only be adopted once.
     * @param approved the application that was just approved
     */
    private void rejectOtherApplications(Application approved) {
        for (Application other : applicationDataAccess.getApplicationsByPet(approved.getPetId())) {
            if (!other.getId().equals(approved.getId()) && other.isActive()) {
                other.updateStatus(ApplicationStatus.REJECTED, ADOPTED_BY_SOMEONE_ELSE);
                applicationDataAccess.save(other);
            }
        }
    }

    @Override
    public void switchToReviewApplicationView() {
        presenter.switchToReviewApplicationView();
    }
}
