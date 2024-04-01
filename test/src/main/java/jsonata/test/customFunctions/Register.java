package jsonata.test.customFunctions;
import java.util.Map;

import jsonata.test.constants.ConstantsForCustomFunctions;

public class Register {

	public static void CustFunc() {

				ConstantsForCustomFunctions.addFun(ConstantsForCustomFunctions.FUNCTION_dateDiff,
						new DateDiff());
		
		}

}
