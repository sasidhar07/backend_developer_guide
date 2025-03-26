package digit.web.controllers;


import digit.config.ServiceConstants;
import digit.service.BirthRegistrationService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;


@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-03-07T11:10:12.732364039+05:30[Asia/Kolkata]")
@Controller
    @RequestMapping("")
    public class BirthApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private BirthRegistrationService birthRegistrationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    /**
     * Constructor to initialize dependencies.
     *
     * @param objectMapper
     * @param request
     * @param birthRegistrationService
     */
    @Autowired
    public BirthApiController(ObjectMapper objectMapper, HttpServletRequest request, BirthRegistrationService birthRegistrationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.birthRegistrationService = birthRegistrationService;
    }

    /**
     * Handles the birth registration creation request.
     *
     * @param birthRegistrationRequest The request body containing birth registration details.
     * @return ResponseEntity containing the created birth registration applications and response info.
     */
    @RequestMapping(value="/registration/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> v1RegistrationCreatePost(@ApiParam(value = ServiceConstants.BIRTH_REGISTRATION_DETAILS, required = true)  @Valid @RequestBody BirthRegistrationRequest birthRegistrationRequest) {
        List<BirthRegistrationApplication> applications = birthRegistrationService.registerBtRequest(birthRegistrationRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthRegistrationRequest.getRequestInfo(), true);
        BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

     /**
     * Handles the birth registration search request.
     *
     * @param birthApplicationSearchRequest The request body containing search criteria.
     * @return ResponseEntity containing the list of matching birth registration applications.
     */
    @RequestMapping(value="/v1/registration/_search", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> v1RegistrationSearchPost(@ApiParam(value = ServiceConstants.BIRTH_REGISTRATION_DETAILS, required = true)  @Valid @RequestBody BirthApplicationSearchRequest birthApplicationSearchRequest) {
        List<BirthRegistrationApplication> applications = birthRegistrationService.searchBtApplications(birthApplicationSearchRequest.getRequestInfo(), birthApplicationSearchRequest.getBirthApplicationSearchCriteria());
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthApplicationSearchRequest.getRequestInfo(), true);
        BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    /**
     * Handles the birth registration update request.
     *
     * @param birthRegistrationRequest The request body containing updated birth registration details.
     * @return ResponseEntity containing the updated birth registration application and response info.
     */
    @RequestMapping(value="/registration/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> v1RegistrationUpdatePost(@ApiParam(value = ServiceConstants.BIRTH_REGISTRATION_DETAILS, required = true)  @Valid @RequestBody BirthRegistrationRequest birthRegistrationRequest) {
        BirthRegistrationApplication application = birthRegistrationService.updateBtApplication(birthRegistrationRequest);

        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthRegistrationRequest.getRequestInfo(), true);
        BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(Collections.singletonList(application)).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
