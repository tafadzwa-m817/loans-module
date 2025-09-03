package zw.co.afrosoft.zdf.alert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import zw.co.afrosoft.zdf.dto.ErrorResponseDto;
import zw.co.afrosoft.zdf.feign.Role;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/alerts")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "authorization")
@Tag(name = "REST APIs for Alerts", description = "REST APIs to send alerts to admin")
public class AlertsRestController {

    private final AlertService alertService;

    @Operation(
            summary = "Subscribe Admins To Alerts REST API",
            description = "REST API to subscribe logged in admins to alerts"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Date Retrieved Successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        try {
            Object principal = authentication.getPrincipal();
            Set<Object> roleObjects = (Set<Object>) principal.getClass()
                    .getDeclaredMethod("getRoles")
                    .invoke(principal);

            Set<Role> roles = roleObjects.stream()
                    .map(roleObj -> {
                        try {
                            String roleName = (String) roleObj.getClass()
                                    .getMethod("getName")
                                    .invoke(roleObj);
                            return Role.valueOf(roleName);
                        } catch (Exception e) {
                            // Consider logging instead of printing
                            System.err.println("Failed to get role name: " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            return alertService.subscribe(roles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to subscribe to alerts", e);
        }
    }
}
