package com.sapiens.dba.config;

//declaring constants to remove the hardcoding wherever possible
public class SearchAppConstants {

	// used in SearchController class
	public final static Long defaultLimit = 10L;
	public final static Long defaultSorting = 1L;
	public final static String desc = "desc";

	// used in Sugesstions and GenericSearch class
	public final static String contact = "contact";
	public final static String policy = "policy";
	public final static String claim = "claim";
	public final static String cust360 = "cust360";
	public final static String asc = "asc";

	// used in Sugesstions class
	public final static String claimNumber = "claimNumber";
	public final static String policyNumber = "policyNumber";
	public final static String email = "email";
	public final static String telephoneNumber = "telephoneNumber";
	public final static String fullName = "fullName";
	public final static String contactNumber = "contactNumber";

	// used in GenericSearch class
	public final static String contacts = "contacts";
	public final static String policies = "policies";
	public final static String claims = "claims";

}
