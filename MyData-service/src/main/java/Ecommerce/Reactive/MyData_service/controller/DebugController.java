package Ecommerce.Reactive.MyData_service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private static final Logger logger = Logger.getLogger(DebugController.class.getName());

    @GetMapping("/security")
    public Mono<String> getSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication != null) {
                        logger.info("SecurityContext Authentication: " + authentication);
                        logger.info("Principal: " + authentication.getPrincipal());
                        logger.info("Authorities: " + authentication.getAuthorities());
                        logger.info("Credentials: " + authentication.getCredentials());
                        return "Security context logged successfully!";
                    } else {
                        return "No Authentication in SecurityContext";
                    }
                });
    }
}

