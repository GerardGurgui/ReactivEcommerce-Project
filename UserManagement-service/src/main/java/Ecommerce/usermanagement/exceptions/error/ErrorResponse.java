package Ecommerce.usermanagement.exceptions.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {

    private String errorDetails;
    private String timestamp;

    public ErrorResponse(String errorDetails, String timestamp) {
        this.errorDetails = errorDetails;
        this.timestamp = timestamp;
    }

    public ErrorResponse(String errorDetails) {
        this.errorDetails = errorDetails;
    }

}
