package digit.validators;

import digit.config.ServiceConstants;
import digit.repository.BirthRegistrationRepository;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class BirthApplicationValidator {

    @Autowired
    private BirthRegistrationRepository repository;
    /**
     * Validates the birth registration request to ensure mandatory fields are present.
     *
     * @param birthRegistrationRequest The request containing birth registration applications.
     * @throws CustomException If the tenant ID is missing in any application.
     */
    public void validateBirthApplication(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            if(ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException(ServiceConstants.BIRTH_APP_ERROR_CODE, ServiceConstants.TENANT_ID_MANDATORY);
        });
    }
    /**
     * Validates the existence of a birth registration application by searching for it in the repository.
     *
     * @param birthRegistrationApplication The birth registration application to check.
     * @return The existing BirthRegistrationApplication if found.
     */
    public BirthRegistrationApplication validateApplicationExistence(BirthRegistrationApplication birthRegistrationApplication) {
        return repository.getApplications(BirthApplicationSearchCriteria.builder().applicationNumber(birthRegistrationApplication.getApplicationNumber()).build()).get(0);
    }
}