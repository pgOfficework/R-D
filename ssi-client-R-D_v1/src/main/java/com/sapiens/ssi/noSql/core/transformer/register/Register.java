package com.sapiens.ssi.noSql.core.transformer.register;

import java.util.Map;

import com.api.jsonata4java.expressions.utils.Constants;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.transformer.functions.DedupFunction;

public class Register {

	public static void CustFunc(Map<String, Object> payload){

		if((!Constants.FUNCTIONS.containsKey(SSIConstant.dedupJNfunction)) && Constants.FUNCTIONS.get(SSIConstant.dedupJNfunction)==null) {
			
			if(payload.containsKey(SSIConstant.Handle_Dedup_Ind)) {
				String dedupurl= payload.get(SSIConstant.dedup_url).toString();

				ConstantsForCustomFunctions.addFun(ConstantsForCustomFunctions.FUNCTION_Dedupe, new DedupFunction(dedupurl));	
			
			}
		}
	}

}
