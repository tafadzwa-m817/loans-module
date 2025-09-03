package zw.co.afrosoft.zdf.member;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PersonalDetails", description = "Schema to hold personal details of a member")
public class PersonalDetails {

	@Schema(description = "First name of the member", example = "John")
	private String firstName;

	@Schema(description = "Last name of the member", example = "Doe")
	private String lastName;

	@Schema(description = "Initials of the member", example = "J.D.")
	private String initials;

	@Schema(description = "National ID number", example = "980413863V")
	private String nationalId;

	@Schema(description = "Marital status of the member", example = "SINGLE")
	private MaritalStatus maritalStatus;

	@Schema(description = "Gender of the member", example = "MALE")
	private Gender gender;

	@Schema(description = "Date of birth of the member", example = "1998-04-13", format = "date")
	private LocalDate dOB;

	@Schema(description = "Phone number of the member", example = "+263771234567")
	private String phoneNumber;

	@Embedded
	@Schema(description = "Address details of the member")
	private Address address;
}

