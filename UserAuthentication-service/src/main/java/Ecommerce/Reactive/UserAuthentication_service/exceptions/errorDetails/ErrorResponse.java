package Ecommerce.Reactive.UserAuthentication_service.exceptions.errorDetails;

import lombok.Getter;
import lombok.Setter;

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
