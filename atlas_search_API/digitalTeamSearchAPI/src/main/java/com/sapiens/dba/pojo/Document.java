
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
@JsonPropertyOrder({ "owner", "docType", "docName", "creationDate", "size" })
@Generated("jsonschema2pojo")
// this class is used for structuring fields of document object
public class Document {

	@JsonProperty("owner")
	private String owner;
	@JsonProperty("docType")
	private String docType;
	@JsonProperty("docName")
	private String docName;
	@JsonProperty("creationDate")
	private String creationDate;
	@JsonProperty("size")
	private String size;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

}
