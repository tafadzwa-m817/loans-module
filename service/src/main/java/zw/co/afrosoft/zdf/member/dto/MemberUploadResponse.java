package zw.co.afrosoft.zdf.member.dto;

import lombok.Builder;
import zw.co.afrosoft.zdf.member.Gender;
import zw.co.afrosoft.zdf.member.MaritalStatus;
import zw.co.afrosoft.zdf.member.ServiceType;
import zw.co.afrosoft.zdf.utils.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;



@Builder
@Schema(description = "Response for individual member upload status")
public record MemberUploadResponse(

        @Schema(description = "Unique force number identifier", example = "ZNA123456")
        String forceNumber,

        @Schema(description = "Type of military or service", example = "ZIMBABWE_NATIONAL_ARMY")
        ServiceType serviceType,

        @Schema(description = "Previous force number if applicable", example = "ZNA654321")
        String prevForceNumber,

        @Schema(description = "Member's rank", example = "Captain")
        String rank,

        @Schema(description = "Member's unit", example = "1 Brigade")
        String unit,

        @Schema(description = "Date the member was attested", example = "2010-05-01")
        LocalDate dateOfAttestation,

        @Schema(description = "Date the member joined", example = "2020-01-10")
        LocalDate membershipDate,

        @Schema(description = "Gross monthly salary", example = "1200.00")
        Double grossSalary,

        @Schema(description = "Net monthly salary", example = "950.00")
        Double netSalary,

        @Schema(description = "Monthly tax amount", example = "250.00")
        Double tax,

        @Schema(description = "Member's first name", example = "John")
        String firstName,

        @Schema(description = "Member's last name", example = "Doe")
        String lastName,

        @Schema(description = "Initials", example = "J.D.")
        String initials,

        @Schema(description = "National identification number", example = "63-1234567X70")
        String nationalId,

        @Schema(description = "Marital status", example = "MARRIED")
        MaritalStatus maritalStatus,

        @Schema(description = "Gender", example = "MALE")
        Gender gender,

        @Schema(description = "Date of birth", example = "1985-06-15")
        LocalDate dOB,

        @Schema(description = "Phone number", example = "+263772123456")
        String phoneNumber,

        @Schema(description = "Address line 1", example = "123 Main St")
        String addressLine_1,

        @Schema(description = "Address line 2", example = "Apt 4B")
        String addressLine_2,

        @Schema(description = "Address line 3", example = "Greendale")
        String addressLine_3,

        @Schema(description = "Address line 4", example = "Harare")
        String addressLine_4,

        @Schema(description = "Upload status", example = "SUCCESS")
        Status status,

        @Schema(description = "Reason for failure, if any", example = "Missing National ID")
        String reason
) {}
