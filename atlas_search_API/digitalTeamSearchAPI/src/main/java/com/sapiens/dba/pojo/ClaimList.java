
package com.sapiens.dba.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Generated("jsonschema2pojo")
// this class is used for structuring list claim object
public class ClaimList {

	@JsonProperty("claims")

	@JsonAlias("")
	private List<Claim> claim = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@Override
	public String toString() {
		// making json string as per required format
		return " { claim : " + claim + "}";
	}

}
