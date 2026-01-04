package ReactiveEcommerce.ProductCatalog_service.exceptions;

import ReactiveEcommerce.ProductCatalog_service.controller.ProductCatalogController;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;
    private final LocalDateTime timestamp;

    public ProductNotFoundException(Long productId) {
        super("Product not found: " + productId);
        this.productId = productId;
        this.timestamp = LocalDateTime.now();
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }
}
