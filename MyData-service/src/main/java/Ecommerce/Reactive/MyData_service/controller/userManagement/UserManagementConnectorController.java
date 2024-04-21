package Ecommerce.Reactive.MyData_service.controller.userManagement;

import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.service.userManagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/MyData/usermanagement")
public class UserManagementConnectorController {

    private final UserManagementConnectorService userManagementConnectorService;

    @Autowired
    public UserManagementConnectorController(UserManagementConnectorService userManagementConnectorService) {
        this.userManagementConnectorService = userManagementConnectorService;
    }

    @GetMapping("/getUserBasic/{uuid}")
    public Mono<UserDto> getUserByUuidBasic(@PathVariable String uuid) {
        return userManagementConnectorService.getUserByUuidBasic(uuid);
    }

}
