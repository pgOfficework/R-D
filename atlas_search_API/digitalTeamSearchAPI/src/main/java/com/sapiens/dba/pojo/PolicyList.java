
package com.sapiens.dba.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAlias;
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
// this class is used for structuring list policy object
public class PolicyList {

	@JsonProperty("policies")
	@JsonAlias("")
	private List<Policy> policy = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@Override
	public String toString() {
		// making json string as per required format
		return " { policy : " + policy + "}";
	}

}
