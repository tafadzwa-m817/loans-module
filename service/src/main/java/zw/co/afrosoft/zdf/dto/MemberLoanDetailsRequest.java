package zw.co.afrosoft.zdf.dto;

import lombok.Data;
import zw.co.afrosoft.zdf.member.Address;
import zw.co.afrosoft.zdf.member.Gender;
import zw.co.afrosoft.zdf.member.MaritalStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDate;



@Data
public class MemberLoanDetailsRequest {

    @Schema(description = "Member's first name", example = "John", required = true)
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @Schema(description = "Member's last name", example = "Doe", required = true)
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Schema(description = "Member's initials", example = "J.D")
    private String initials;

    @Schema(description = "National ID of the member", example = "98-123456Z-12")
    @NotBlank(message = "National ID cannot be blank")
    private String nationalId;

    @Schema(description = "Marital status of the member", example = "SINGLE")
    @NotNull(message = "Marital status is required")
    private MaritalStatus maritalStatus;

    @Schema(description = "Gender of the member", example = "MALE")
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Schema(description = "Date of birth of the member", example = "1990-01-01")
    @NotNull(message = "Date of birth is required")
    private LocalDate dOB;

    @Schema(description = "Address of the member")
    @Valid
    private Address address;
}

