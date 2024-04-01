package jsonata.test.constants;

import com.api.jsonata4java.expressions.functions.Function;
import com.api.jsonata4java.expressions.utils.Constants;

public class ConstantsForCustomFunctions {

	public static final String key = "key";
	
	public static final String FUNCTION_dateDiff = "$dateDiff";
	public static final String FUNCTION_Dedupe = "$dedup";
	public static final String Handle_Dedup_Ind = "Handle_Dedup_Ind";
	public static final String dedup_url = "dedup_url";
	public static final String dedupJNfunction  = "$dedup";

	public static final String ERR_MSG_ARG1_BAD_TYPE_DATE = "Argument 1 of function %s must be an array of date strings";
	public static final String ERR_MSG_ARG_NOT_NULL = "Argument of function %s can not be null";

	public static void addFun(String fname, Object fobj) {

		Constants.FUNCTIONS.put(fname, (Function) fobj);
		System.out.println("function : " + fname + " registered ");
	}
 
}
