package Ecommerce.Reactive.UserAuthentication_service.exceptions.errorDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    @JsonProperty("Error details")
    private String errorDetails;

    @JsonProperty("Timestamp")
    private String timestamp;

    public ErrorResponse(String errorDetails, String timestamp) {
        this.errorDetails = errorDetails;
        this.timestamp = timestamp;
    }

    public ErrorResponse(String errorDetails) {
        this.errorDetails = errorDetails;
    }

}
