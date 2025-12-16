package ReactiveEcommerce.ProductCatalog_service.exceptions;

import ReactiveEcommerce.ProductCatalog_service.controller.ProductCatalogController;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final String name;
    private final LocalDateTime timestamp;

    public ProductNotFoundException(String name) {
        super("Product not found: " + name);
        this.name = name;
        this.timestamp = LocalDateTime.now();
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }
}
