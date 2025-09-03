package zw.co.afrosoft.zdf.member.dto;

import lombok.Builder;
import zw.co.afrosoft.zdf.member.Gender;
import zw.co.afrosoft.zdf.member.MaritalStatus;
import zw.co.afrosoft.zdf.member.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;



@Builder
@Schema(description = "DTO for uploading member data via Excel")
public record MemberExcelUploadDTO(

        @Schema(description = "Unique identifier for member within the service", example = "ZNA123456")
        String forceNumber,

        @Schema(description = "First name of the member", example = "John")
        String firstName,

        @Schema(description = "Last name of the member", example = "Doe")
        String lastName,

        @Schema(description = "National identification number", example = "63-1234567X70")
        String nationalID,

        @Schema(description = "Date of birth", example = "1985-06-15")
        LocalDate dob,

        @Schema(description = "Marital status of the member", example = "MARRIED")
        MaritalStatus maritalStatus,

        @Schema(description = "Gender of the member", example = "MALE")
        Gender gender,

        @Schema(description = "Service type the member belongs to", example = "ZIMBABWE_NATIONAL_ARMY")
        ServiceType serviceType,

        @Schema(description = "Initials of the member", example = "J.D.")
        String initials,

        @Schema(description = "Previous force number, if applicable", example = "ZNA654321")
        String prevForceNumber,

        @Schema(description = "Unit the member is assigned to", example = "1 Brigade")
        String unit,

        @Schema(description = "Rank of the member", example = "Captain")
        String rank,

        @Schema(description = "Date when membership started", example = "2020-01-10")
        LocalDate membershipDate,

        @Schema(description = "Date of attestation", example = "2010-05-01")
        LocalDate dateOfAttestation,

        @Schema(description = "Gross monthly salary", example = "1200.00")
        Double grossSalary,

        @Schema(description = "Net monthly salary", example = "950.00")
        Double netSalary,

        @Schema(description = "Monthly tax amount", example = "250.00")
        Double tax,

        @Schema(description = "Primary address line", example = "123 Main St")
        String address1,

        @Schema(description = "Secondary address line", example = "Apt 4B")
        String address2,

        @Schema(description = "Additional address info", example = "10 Dunstable Circle Avonlea")
        String address3,

        @Schema(description = "Additional address info", example = "Harare")
        String address4,

        @Schema(description = "Member's phone number", example = "+263772123456")
        String phoneNumber
) {}

