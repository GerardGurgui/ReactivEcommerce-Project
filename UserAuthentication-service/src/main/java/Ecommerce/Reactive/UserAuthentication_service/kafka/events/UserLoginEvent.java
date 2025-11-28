package Ecommerce.Reactive.UserAuthentication_service.kafka.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class UserLoginEvent {

    private final String userUuid;
    private final Instant loginTimestamp;
    private final String ipAddress;

    @JsonCreator
    public UserLoginEvent(
            @JsonProperty("userUuid") String userUuid,
            @JsonProperty("loginTimestamp") Instant loginTimestamp,
            @JsonProperty("ipAddress") String ipAddress) {
        this.userUuid = userUuid;
        this.loginTimestamp = loginTimestamp;
        this.ipAddress = ipAddress;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public Instant getLoginTimestamp() {
        return loginTimestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return "UserLoginEvent{userUuid='" + userUuid + "', loginTimestamp=" + loginTimestamp + "}";
    }
}