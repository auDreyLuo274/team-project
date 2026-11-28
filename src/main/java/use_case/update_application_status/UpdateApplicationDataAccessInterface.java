package use_case.update_application_status;

import java.util.List;

import entity.Application;

public interface UpdateApplicationDataAccessInterface {
    Application getApplicationById(String applicationId);

    /**
     * Returns every application for one pet, from every adopter.
     * @param petId the pet's id
     * @return the pet's applications, empty if there are none
     */
    List<Application> getApplicationsByPet(String petId);

    void save(Application application);
}
