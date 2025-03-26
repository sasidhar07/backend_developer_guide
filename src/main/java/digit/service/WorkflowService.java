package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.BTRConfiguration;
import digit.config.ServiceConstants;
import digit.repository.ServiceRequestRepository;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private BTRConfiguration config;
    /**
     * Updates the workflow status for each birth registration application.
     * @param birthRegistrationRequest The request containing birth registration applications.
     */
    public void updateWorkflowStatus(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            ProcessInstance processInstance = getProcessInstanceForBTR(application, birthRegistrationRequest.getRequestInfo());
            ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(birthRegistrationRequest.getRequestInfo(), Collections.singletonList(processInstance));
            callWorkFlow(workflowRequest);
        });
    }
//set tsate into birth registration status
    /**
     * Calls the workflow transition API and returns the updated state.
     * @param workflowReq The workflow request containing process instance details.
     * @return The updated state of the workflow.
     */
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
        Object optional = repository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }
    /**
     * Creates a ProcessInstance for a birth registration application.
     * @param application The birth registration application.
     * @param requestInfo The request information.
     * @return The created ProcessInstance object.
     */
    private ProcessInstance getProcessInstanceForBTR(BirthRegistrationApplication application, RequestInfo requestInfo) {
        Workflow workflow = application.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(application.getApplicationNumber());
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName(ServiceConstants.MODULE_NAME);
        processInstance.setTenantId(application.getTenantId());
        processInstance.setBusinessService(ServiceConstants.BUSINESS_SERVICE);
        processInstance.setDocuments(workflow.getDocuments());
        processInstance.setComment(workflow.getComments());

        if(!CollectionUtils.isEmpty(workflow.getAssignes())){
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });

            processInstance.setAssignes(users);
        }

        return processInstance;

    }
     /**
     * Retrieves the current workflow process instance based on business ID.
     * @param requestInfo The request information.
     * @param tenantId The tenant ID.
     * @param businessId The business ID.
     * @return The current process instance or null if not found.
     */
    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        StringBuilder url = getSearchURLWithParams(tenantId, businessId);

        Object res = repository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try {
            response = mapper.convertValue(res, ProcessInstanceResponse.class);
        } catch (Exception e) {
            throw new CustomException(ServiceConstants.PARSING_ERROR_MESSAGE, ServiceConstants.PARSING_ERROR_DESCRIPTION);
        }

        if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null)
            return response.getProcessInstances().get(0);

        return null;
    }
    /**
     * Fetches the business service details for a given birth registration application.
     * @param application The birth registration application.
     * @param requestInfo The request information.
     * @return The BusinessService object.
     */
    private BusinessService getBusinessService(BirthRegistrationApplication application, RequestInfo requestInfo) {
        String tenantId = application.getTenantId();
        StringBuilder url = getSearchURLWithParams(tenantId, ServiceConstants.BUSINESS_SERVICE);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = null;
        try {
            response = mapper.convertValue(result, BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ServiceConstants.BUSINESS_SERVICE_PARSING_ERROR, ServiceConstants.BUSINESS_SERVICE_PARSING_ERROR_DESCRIPTION);
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices()))
            throw new CustomException(ServiceConstants.BUSINESS_SERVICE_NOT_FOUND, ServiceConstants.BUSINESS_SERVICE_NOT_FOUND_DESCRIPTION);

        return response.getBusinessServices().get(0);
    }
    /**
     * Constructs a URL for searching workflow instances.
     * @param tenantId The tenant ID.
     * @param businessService The business service name.
     * @return The constructed URL as a StringBuilder.
     */
    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(businessService);
        return url;
    }
    /**
     * Creates a process instance request for payment processing in birth registration.
     * @param updateRequest The birth registration request.
     * @return The ProcessInstanceRequest object.
     */
    public ProcessInstanceRequest getProcessInstanceForBirthRegistrationPayment(BirthRegistrationRequest updateRequest) {
        BirthRegistrationApplication application = updateRequest.getBirthRegistrationApplications().get(0);

        ProcessInstance process = ProcessInstance.builder()
                .businessService(ServiceConstants.BUSINESS_SERVICE)
                .businessId(application.getApplicationNumber())
                .comment(ServiceConstants.PAYMENT_COMMENT)
                .moduleName(ServiceConstants.MODULE_NAME)
                .tenantId(application.getTenantId())
                .action(ServiceConstants.PAY_ACTION)
                .build();

        return ProcessInstanceRequest.builder()
                .requestInfo(updateRequest.getRequestInfo())
                .processInstances(Arrays.asList(process))
                .build();
    }
}