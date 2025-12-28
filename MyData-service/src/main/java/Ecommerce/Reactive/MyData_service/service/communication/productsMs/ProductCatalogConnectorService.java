package Ecommerce.Reactive.MyData_service.service.communication.productsMs;

import Ecommerce.Reactive.MyData_service.DTO.cartProducts.ProductDetailsFromMsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

// Service class for communicating with microservice productcatalog

@Service
public class ProductCatalogConnectorService {

    private final static Logger LOGGER = Logger.getLogger(ProductCatalogConnectorService.class.getName());

    private final WebClient webClient;
    private final String PRODUCTMS_URL;

    @Value("${internal.api-key}")
    private String internalApiKey;

    public ProductCatalogConnectorService(@Value("${productcatalog.service.url}") String PRODUCTMS_URL,
                                          WebClient.Builder webClientBuilder) {
        this.PRODUCTMS_URL = PRODUCTMS_URL;
        this.webClient = webClientBuilder.baseUrl(PRODUCTMS_URL).build();
    }

    public Mono<ProductDetailsFromMsDto> getProductInfoFromProductsMs(Long productId) {

        LOGGER.info("Initiating GET request to fetch product info for productId: {" +
                productId + "}");

        return webClient.get()
                .uri("/internal/products/{id}", productId)
                .header("X-Internal-API-Key", internalApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    LOGGER.warning("4xx error when fetching product info for productId: {" + productId + "}");
                    return Mono.error(new RuntimeException("Product not found with id: " + productId));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    LOGGER.severe("5xx error when fetching product info for productId: {" + productId + "}");
                    return Mono.error(new RuntimeException("Server error when fetching product with id: " + productId));
                })
                .bodyToMono(ProductDetailsFromMsDto.class)
                .doOnSuccess(response -> LOGGER.info("Successfully fetched product info for productId: {" + productId + "}"))
                .doOnError(error -> LOGGER.severe("Error fetching product info for productId: {" + productId + "}: " + error.getMessage()));


    }

}
