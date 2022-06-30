
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
@JsonPropertyOrder({ "assetDescription", "plateNumber" })
@Generated("jsonschema2pojo")
// this class is used for structuring fields for AssetDetails object of claim
// object
public class ClaimAssetDetails {

	@JsonProperty("assetDescription")
	private String assetDescription;
	@JsonProperty("plateNumber")
	private String plateNumber;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@Override
	public String toString() {
		// making json string as per required format for AssetDetails object of claim
		// object
		return "{assetDescription:\"" + assetDescription + "\", plateNumber:\"" + plateNumber + "\"}";
	}

}
