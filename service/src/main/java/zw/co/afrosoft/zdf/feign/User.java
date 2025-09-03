package zw.co.afrosoft.zdf.feign;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;



@Data
@Schema(name = "User", description = "Represents a system user with roles and credentials")
public class User {

    @Schema(description = "Unique identifier for the user", example = "1001")
    private Long id;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @Schema(description = "Location or base of the user", example = "Harare")
    private String location;

    @Schema(description = "System username used for login", example = "johndoe")
    private String username;

    @Schema(description = "Encrypted user password", example = "hashed_password")
    private String password;

    @Schema(description = "Date and time the password was last updated", example = "2024-11-10T08:30:00")
    private LocalDateTime passwordLastUpdated;

    @Schema(description = "Phone number of the user", example = "+263771234567")
    private String phoneNumber;

    @Schema(description = "Indicates if OTP (Two-Factor Authentication) is enabled")
    private boolean otpEnabled;

    @Schema(description = "Number of failed login attempts", example = "2")
    private Integer loginAttempts;

    @Schema(description = "Gender of the user", example = "MALE")
    private Gender gender;

    @Schema(description = "Roles assigned to the user")
    private Set<Role> roles = new HashSet<>();
}

