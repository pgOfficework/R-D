package com.sapiens.ssi.noSql.core.transformer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.EvaluateException;
import com.api.jsonata4java.expressions.ParseException;
import com.api.jsonata4java.expressions.utils.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.transformer.config.JNTConfig;
import com.sapiens.ssi.noSql.core.transformer.functions.DedupFunction;
import com.sapiens.ssi.noSql.core.transformer.register.ConstantsForCustomFunctions;


public class JsonHandler {


	public static String dedupurl=null;
	
	public static String jsonTransform(String input,String jnExpr,Map<String, Object> payload) throws ParseException, IOException, EvaluateException {
		JsonNode obj = new ObjectMapper().readTree(input);
		return transformation(obj,jnExpr,payload);

	}
	public static String jsonTransform(File input,String jnExpr,Map<String, Object> payload) throws ParseException, IOException, EvaluateException {
		JsonNode obj = new ObjectMapper().readTree(input);
		return transformation(obj,payload.get(SSIConstant.Event_type).toString(),payload);
	}
	
	public static String transformation(JsonNode obj,String jnExpr,Map<String, Object> payload) throws ParseException, IOException, EvaluateException {
		
		
		if(payload.containsKey(SSIConstant.dedup_url))
			dedupurl= payload.get(SSIConstant.dedup_url).toString();
		
		System.out.println(Constants.FUNCTIONS.get("$dedup")==null);
		
//		if(Constants.FUNCTIONS.get("$dedup")==null && Integer.parseInt(payload.get(SSIConstant.Handle_Dedup_Ind).toString())==1) {
//			ConstantsForCustomFunctions.addFun(ConstantsForCustomFunctions.FUNCTION_Dedupe, new DedupFunction(dedupurl));
//		}
			//Generate a ssi config jsonata Expression based on evaluating the supplied expression
		System.out.println("jnExpr "+JNTConfig.JNTConfigMap.get(jnExpr));
			Expression expression = Expression.jsonata(JNTConfig.JNTConfigMap.get(jnExpr));
			//Generate a result form the Expression's parsed ssi config jsonata expression 
			JsonNode result = expression.evaluate(obj);
			System.out.println("result : "+result.size());
		
			if(result.size()==0) {
				return null;
			}
			String jnRes = result.toString();
//			System.out.println("jnRes : "+jnRes);
						
			return jnRes;

	}

}
