package jsonata.test.customFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.ExpressionsVisitor;
import com.api.jsonata4java.expressions.functions.Function;
import com.api.jsonata4java.expressions.functions.FunctionBase;
import com.api.jsonata4java.expressions.generated.MappingExpressionParser.Function_callContext;
import com.api.jsonata4java.expressions.utils.Constants;
import com.api.jsonata4java.expressions.utils.FunctionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import jsonata.test.constants.ConstantsForCustomFunctions;

public class DedupFunction extends FunctionBase implements Function {


	public static Map<String, Object> payload = null;

	@SuppressWarnings("static-access")
	public DedupFunction(String postrequestURL, Map<String, Object> payload) {
		this.requestURL = postrequestURL;
		this.payload = payload;
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
			System.out.println("post request initiated : " + requestURL);

			int argCount = getArgumentCount(ctx);

			if (argCount != 0) {
				for (int i = 0; i < argCount; i++) {
					jsonBodyMap.put(ConstantsForCustomFunctions.key + (i + 1),
							FunctionUtils.getValuesListExpression(expressionVisitor, ctx, i));
				}
				try {
					jsonBody = mapper.writeValueAsString(jsonBodyMap);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			} else
				throw new EvaluateRuntimeException(ERR_ARGNULL);

			try {
				response = new TextNode(postRequest(requestURL, jsonBody));
				if (response.toString().contains("error")) {
					System.out.println("<" + DedupFunction.class.getSimpleName()
							+ "> - custom jsonata function $dedup not initialise - " + requestURL
							+ " Not Found, Unable to connect server");
					}
			} catch (IOException e) {
				System.out.println("error in function $dedup : " + e.getLocalizedMessage());
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String postRequest(String requestURL, String jsonBody) throws IOException {
		HttpPost postRequest = new HttpPost(requestURL);
		StringEntity requestBody = new StringEntity(jsonBody);
		postRequest.addHeader("content-type", "application/json");
		postRequest.addHeader("Accept", "application/json");
		postRequest.setEntity(requestBody);
		HttpResponse response = httpClient.execute(postRequest);

		InputStream result = response.getEntity().getContent();
		Reader reader = new InputStreamReader(result);
		BufferedReader bufferedReader = new BufferedReader(reader);
		return bufferedReader.readLine();
	}

	public int getMaxArgs() {
		return 100;
	}

	public int getMinArgs() {
		return 1; // account for context variable
	}

	public String getSignature() {
		// takes an string arguments, returns a string
		return "<s+:s>";
	}

}
