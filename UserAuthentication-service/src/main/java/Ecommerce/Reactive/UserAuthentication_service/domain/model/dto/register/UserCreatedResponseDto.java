package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserCreatedResponseDto {

    private String uuid;
    private String username;
    private String email;
    private Instant createdAt;

}
