package Ecommerce.usermanagement.kafka.consumer;

import Ecommerce.usermanagement.kafka.events.UserLoginEvent;
import Ecommerce.usermanagement.services.UserManagementService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserLoginEventConsumer {

    private final UserManagementService userManagementService;
    private static final Logger LOGGER = Logger.getLogger(UserLoginEventConsumer.class.getName());

    public UserLoginEventConsumer(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @KafkaListener(
            topics = "${kafka.topic.user-login}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void handleUserLoginEvent(UserLoginEvent event) {

        LOGGER.info("📨 Received login event for user: " + event.getUserUuid());

        userManagementService.updateLatestAccess(
                        event.getUserUuid(),
                        event.getLoginTimestamp()
                )
                .doOnSuccess(v -> LOGGER.info("✅ Latest access updated for: " + event.getUserUuid() + "-ip:" + event.getIpAddress()))
                .doOnError(error -> LOGGER.severe("❌ Failed to update: " + error.getMessage()))
                .subscribe();
    }
}
