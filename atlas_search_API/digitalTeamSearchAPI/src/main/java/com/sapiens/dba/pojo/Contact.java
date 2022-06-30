
package com.sapiens.dba.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "contactName", "contactNumber", "contactId", "dateOfBirth", "maritalStatus", "contactStatus",
		"contactAddress", "activeClaims" })
@Generated("jsonschema2pojo")
// this class is used for structuring fields of contact object
public class Contact {

	@JsonProperty("contactName")
	private String fullName;
	@JsonProperty("contactNumber")
	private String contactNumber;
	@JsonProperty("contactId")
	private String contactId;
	@JsonProperty("dateOfBirth")
	private String dateOfBirth;
	@JsonProperty("maritalStatus")
	private String maritalStatus;
	@JsonProperty("contactStatus")
	private String contactStatus;
	@JsonProperty("contactAddress")
	private ContactAddress contactAddress;
	@JsonProperty("noOfClaims")
	private String noOfClaims;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@Override
	public String toString() {
		// making json string as per required format for contact object
		return "{contactName:\"" + fullName + "\", contactNumber:" + contactNumber + ", contactId:" + contactId
				+ ", dateOfBirth:" + (dateOfBirth != null ? ("\"" + dateOfBirth + "\"") : null) + ", maritalStatus:"
				+ maritalStatus + ", contactStatus:" + contactStatus + ", contactAddress:" + contactAddress
				+ ",noOfClaims:" + noOfClaims + "}";

	}

}
