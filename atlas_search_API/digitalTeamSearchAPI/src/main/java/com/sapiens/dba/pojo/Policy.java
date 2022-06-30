
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
@JsonPropertyOrder({ "policyNumber", "policyStatus", "policyEndDate", "yearlyPremium", "lobDescription",
		"policyHolderName", "assetDetails" })
@Generated("jsonschema2pojo")
// this class is used for structuring fields of policy object
public class Policy {

	@JsonProperty("policyNumber")
	private String policyNumber;
	@JsonProperty("policyStatus")
	private String policyStatus;
	@JsonProperty("policyEndDate")
	private String endDate;
	@JsonProperty("yearlyPremium")
	private String policyPremium;
	@JsonProperty("lobDescription")
	private String lobDescription;
	@JsonProperty("policyHolderName")
	private String policyHolderName;
	@JsonProperty("assetDetails")
	private PoliocyAssetDetails assetDetails;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@Override
	public String toString() {
		// making json string as per required format for policy object
		return "{policyNumber:" + policyNumber + ", policyStatus:" + policyStatus + ", policyEndDate:"
				+ (endDate != null ? ("\"" + endDate + "\"") : null) + ", yearlyPremium:" + policyPremium
				+ ", lobDescription:\"" + lobDescription + "\", policyHolderName:\"" + policyHolderName
				+ "\", assetDetails:" + assetDetails + "}";
	}

}
