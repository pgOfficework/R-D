package com.sapiens.dba.Controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bson.Document;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sapiens.dba.Dao.Search1;
import com.sapiens.dba.Dao.Sugesstions;


@RestController
public class SearchController {
	
	 @Value("${name}")
	    private String name;
	
	private static String NAME_STATIC;
	
	public void setmongoClient(String name){
        PropertyController.NAME_STATIC = name;
    }
	
//	@Autowired
//	public SugesstionsRepository Sugesstions;
	
	@CrossOrigin
	@GetMapping("/cust360/search/query={urlParameter}&suggestion=true")
	public HashSet<String> getSugesstions(@PathVariable("urlParameter") String urlParameter) {
		
			return Sugesstions.getSugesstions(urlParameter);
		
	}
	
//	@GetMapping("/cust360/search/query={urlParameter}&sort={sort}")
//	@ResponseBody
//	public HashMap<String, List<Document>> getSearchResult(@PathVariable("urlParameter") String urlParameter,@PathVariable("sort") String sort) {
//			
//			Long sorting=(sort.equals("desc"))?-1L:1L;
//			
//			return Search.getSearch(urlParameter,sorting);
//		
//	}
	
	@CrossOrigin
	@GetMapping("/cust360/search/query={urlParameter}&on")
	public HashMap<String, List<Document>> getSearchResult(@PathVariable("urlParameter") String urlParameter) {
			
			
			return Search1.getSearch(urlParameter);
		
	}

}
