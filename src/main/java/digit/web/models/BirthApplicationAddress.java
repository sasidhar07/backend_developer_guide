package digit.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

import org.egov.common.contract.models.Address;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * BirthApplicationAddress
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-03T11:52:56.302336279+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthApplicationAddress   {
    
    /**
     * Unique identifier for the birth application address.
     */
    @JsonProperty("id")
    @Valid
    private String id = null;

    /**
     * Tenant ID for identifying the jurisdiction (mandatory field).
     */
    @JsonProperty("tenantId")
    @NotNull
    @Size(min=2,max=64)         
    private String tenantId = null;

    /**
     * Application number associated with the birth registration.
     */
    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    /**
     * Address details of the applicant.
     */
    @JsonProperty("applicantAddress")
    @Valid
    private Address applicantAddress = null;
}
