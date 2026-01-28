package Ecommerce.Reactive.UserAuthentication_service.exceptions.errorDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse {

    private String message;
    private Map<String, String> errors;  // field → error message
    private String timestamp;
}