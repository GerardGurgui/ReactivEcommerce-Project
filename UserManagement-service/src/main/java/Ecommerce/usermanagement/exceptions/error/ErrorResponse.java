package Ecommerce.usermanagement.exceptions.error;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {

    private String errorDetails;
    private Date timestamp;

    public ErrorResponse() {

    }

    public ErrorResponse(String errorDetails, Date timestamp) {
        this.errorDetails = errorDetails;
        this.timestamp = timestamp;
    }
}
