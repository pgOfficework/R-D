package jsonata.test;

import java.io.IOException;
import java.io.InputStream;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapiens.ssi.noSql.core.transformer.register.Register;

import jsonata.test.input.Input;
import jsonata.test.jntTransformation.JntTrans;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	jsonata.test.customFunctions.Register.CustFunc();
    	//"yyyy-MM-dd'T'HH:mm:ss"
    	JsonNode result = null;
    	JsonNode mapper;
		try {
			mapper = new ObjectMapper().readTree(Input.claim);
			result = Expression.jsonata(JntTrans.claim).evaluate(mapper);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
