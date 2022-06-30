
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
@JsonPropertyOrder({ "claimNumber", "claimStatus", "claimDate", "claimAmount", "claimDescription", "lobDescription",
		"policyHolderName", "assetDetails" })
@Generated("jsonschema2pojo")
// this class is used for structuring fields of claim object
public class Claim {

	@JsonProperty("claimNumber")
	private String claimNumber;
	@JsonProperty("claimStatus")
	private String claimStatus;
	@JsonProperty("claimDate")
	private String eventDate;
	@JsonProperty("claimAmount")
	private String claimAmount;
	@JsonProperty("claimDescription")
	private String claimDescription;
	@JsonProperty("lobDescription")
	private String lobDescription;
	@JsonProperty("policyHolderName")
	private String policyHolderName;
	@JsonProperty("assetDetails")
	private ClaimAssetDetails assetDetails;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@Override
	public String toString() {
		// making json string as per required format for claim object
		return "{claimNumber:" + claimNumber + ", claimStatus:" + claimStatus + ", claimDate:"
				+ (eventDate != null ? ("\"" + eventDate + "\"") : null) + ", claimAmount:" + claimAmount
				+ ", claimDescription:" + claimDescription + ", lobDescription:\"" + lobDescription
				+ "\", policyHolderName:\"" + policyHolderName + "\", assetDetails:" + assetDetails + "}";
	}

}
