package com.sapiens.dba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sapiens.dba.config.SearchAppConstants;
import com.sapiens.dba.dao.GenericSearchMQL;
import com.sapiens.dba.dao.Sugesstions;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(path = "/cust360")
public class SearchController {

	@Autowired
	Sugesstions sugesstions;

	@Autowired
	GenericSearchMQL GenericSearchMql;

	@CrossOrigin
	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getResult1(@RequestParam(value = "query") String urlParameter,
			@RequestParam(value = "suggestion", required = false) Boolean suggestion,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "limit", required = false) String limit) {

		log.debug("in SearchController.getSugesstions");

		// fetching Sugesstions result based on suggession parameters
		if (suggestion != null && suggestion) {

			return sugesstions.getSugesstions(urlParameter);
		}

		// fetching Search result based on search parameters like sort and limit
		else {
			Long sorting = SearchAppConstants.defaultSorting;
			Long lim = SearchAppConstants.defaultLimit;
			if (limit != null) {
				lim = Long.parseLong(limit);
			}

			if (sort != null) {
				sorting = (sort.equals(SearchAppConstants.desc)) ? -1L : 1L;
			}
			return GenericSearchMql.getSearch(urlParameter, sorting, lim);
		}
	}

}
