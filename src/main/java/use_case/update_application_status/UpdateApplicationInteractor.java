package use_case.update_application_status;

import entity.Application;
import entity.ApplicationStatus;
import entity.Pet;
import entity.PetStatus;

public class UpdateApplicationInteractor implements UpdateApplicationInputBoundary {

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

        Pet pet = null;
        if (status == ApplicationStatus.APPROVED) {
            pet = petDataAccess.findById(application.getPetId());
            if (pet == null) {
                presenter.prepareFailView("This pet is no longer listed, so the application cannot be approved.");
                return;
            }
            if (pet.getStatus() == PetStatus.ADOPTED) {
                presenter.prepareFailView("This pet has already been adopted.");
                return;
            }
        }

        application.updateStatus(status, inputData.getAdminNote());
        final boolean saved;
        if (pet == null) {
            saved = saveApplication(application);
        } else {
            saved = approve(application, pet);
        }
        if (!saved) {
            presenter.prepareFailView("Could not save the change.");
            return;
        }

        presenter.prepareSuccessView(new UpdateApplicationOutputData(application.getId(),
                application.getStatus(), application.getAdminNote()));
    }

    private boolean saveApplication(Application application) {
        try {
            applicationDataAccess.save(application);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /**
     * Marks the pet ADOPTED and saves the approved application as one change.
     * If either write fails, the pet goes back to its previous status.
     */
    private boolean approve(Application application, Pet pet) {
        final PetStatus previousStatus = pet.getStatus();
        pet.updatePetStatus(PetStatus.ADOPTED);
        if (!updatePet(pet)) {
            pet.updatePetStatus(previousStatus);
            return false;
        }
        if (!saveApplication(application)) {
            pet.updatePetStatus(previousStatus);
            updatePet(pet);
            return false;
        }
        return true;
    }

    private boolean updatePet(Pet pet) {
        try {
            return petDataAccess.update(pet);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @Override
    public void switchToReviewApplicationView() {
        presenter.switchToReviewApplicationView();
    }
}
