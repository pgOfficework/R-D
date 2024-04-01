package com.sapiens.ssi.noSql.core.transformer.register;

import com.api.jsonata4java.expressions.functions.Function;
import com.api.jsonata4java.expressions.utils.Constants;
import com.sapiens.ssi.noSql.core.JsonImpl;

public class ConstantsForCustomFunctions extends Constants {
	
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(ConstantsForCustomFunctions.class);
	
	private static final long serialVersionUID = 1L;

	public static final String FUNCTION_MaxDate = "$maxDate";
	public static final String FUNCTION_Dedupe = "$dedup";
	
	public static final String ERR_MSG_ARG1_BAD_TYPE_DATE = "Argument 1 of function %s must be an array of date strings";
	public static final String ERR_MSG_ARG_NOT_NULL = "Argument of function %s can not be null";
	
		public static void addFun(String fname,Object fobj) {
			
			Constants.FUNCTIONS.put(fname, (Function) fobj);
			log.debug("function : "+fname+ " registered ");
		}
}
