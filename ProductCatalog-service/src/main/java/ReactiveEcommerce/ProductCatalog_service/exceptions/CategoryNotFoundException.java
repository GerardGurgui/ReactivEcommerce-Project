package ReactiveEcommerce.ProductCatalog_service.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CategoryNotFoundException extends RuntimeException {

    private final Long categoryId;
    private final LocalDateTime timestamp;

    public CategoryNotFoundException(Long categoryId) {
        super("Category with ID " + categoryId + " not found.");
        this.categoryId = categoryId;
        this.timestamp = LocalDateTime.now();
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }
}