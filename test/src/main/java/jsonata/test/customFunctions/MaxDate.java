package jsonata.test.customFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.ExpressionsVisitor;
import com.api.jsonata4java.expressions.functions.Function;
import com.api.jsonata4java.expressions.functions.FunctionBase;
import com.api.jsonata4java.expressions.generated.MappingExpressionParser.Function_callContext;
import com.api.jsonata4java.expressions.utils.Constants;
import com.api.jsonata4java.expressions.utils.FunctionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import jsonata.test.constants.ConstantsForCustomFunctions;
import lombok.extern.log4j.Log4j2;
@Log4j2
public class MaxDate extends FunctionBase implements Function {


	private long years_difference;
	private long months_difference;
	private long days_difference;
	private long hours_difference;
	private long minutes_difference;
	private long seconds_difference;
	public static Map<String, Object> payload = null;

	@SuppressWarnings("static-access")
	public MaxDate() {
		log.info("$dateDiff function added in jsonata");
	}

	public static String requestURL = null;
	static HttpClient httpClient = HttpClients.createDefault();

	private static final long serialVersionUID = -4281182554968881670L;

	public static String ERR_BAD_CONTEXT = String.format(Constants.ERR_MSG_BAD_CONTEXT,
			ConstantsForCustomFunctions.FUNCTION_Dedupe);
	public static String ERR_ARG1BADTYPE = String.format(Constants.ERR_MSG_ARG1_BAD_TYPE,
			ConstantsForCustomFunctions.FUNCTION_Dedupe);
	public static String ERR_ARGNULL = String.format(ConstantsForCustomFunctions.ERR_MSG_ARG_NOT_NULL,
			ConstantsForCustomFunctions.FUNCTION_Dedupe);

	public JsonNode invoke(ExpressionsVisitor expressionVisitor, Function_callContext ctx) {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, JsonNode> jsonBodyMap = new LinkedHashMap<String, JsonNode>();
		String jsonBody = null;
		JsonNode response = null;
		try {
			
			int argCount = getArgumentCount(ctx);
			SimpleDateFormat format = new SimpleDateFormat(FunctionUtils.getValuesListExpression(expressionVisitor, ctx, 0).toString());

			if (argCount != 0) {
				
				//"yyyy-MM-dd'T'HH:mm:ss"
				String dateStart = FunctionUtils.getValuesListExpression(expressionVisitor, ctx, 1).toString();
				String dateStop = FunctionUtils.getValuesListExpression(expressionVisitor, ctx, 2).toString();
				// Use parse method to get date object of both dates  
	            Date date1 = format.parse(dateStart);   
	            Date date2 = format.parse(dateStop);  
	            // Calucalte time difference in milliseconds   
	            long time_difference = date2.getTime() - date1.getTime(); 
	            
	         // Calculate time difference in years using TimeUnit class  
	            years_difference = TimeUnit.MILLISECONDS.toDays(time_difference) / 365; 
	         // Calucalte time difference in days using TimeUnit class  
	             months_difference = (TimeUnit.MILLISECONDS.toDays(time_difference) % 365)/30;
	            
	            if(months_difference >11)
	            {
	            	years_difference++;
	            	months_difference=0;
	            }
	            // Calucalte time difference in days using TimeUnit class  
	             days_difference =  (TimeUnit.MILLISECONDS.toDays(time_difference) % 365)%30;  
	         // Calucalte time difference in hours using TimeUnit class  
	             hours_difference = TimeUnit.MILLISECONDS.toHours(time_difference) % 24;   
	         // Calucalte time difference in minutes using TimeUnit class  
	             minutes_difference = TimeUnit.MILLISECONDS.toMinutes(time_difference) % 60;
	            // Calucalte time difference in seconds using TimeUnit class  
	             seconds_difference = TimeUnit.MILLISECONDS.toSeconds(time_difference) % 60;

			} else
				throw new EvaluateRuntimeException(ERR_ARGNULL);

			response = new TextNode(years_difference+" Y "+months_difference+" M "+days_difference+" D");
			if (response.toString().contains("error")) {
				log.error("<" + MaxDate.class.getSimpleName()
						+ "> - custom jsonata function $dateDiff not initialise");

			}
			;
		} catch (Exception e) {
			log.error(e);
		}
		return response;
	}


	public int getMaxArgs() {
		return 100;
	}

	public int getMinArgs() {
		return 2; // account for context variable
	}

	public String getSignature() {
		// takes an string arguments, returns a string
		return "<s+:s>";
	}

}
