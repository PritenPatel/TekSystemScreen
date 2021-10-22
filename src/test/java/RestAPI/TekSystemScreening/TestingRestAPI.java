package RestAPI.TekSystemScreening;

import static io.restassured.RestAssured.given;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.internal.org.jline.utils.Log;

import org.junit.Assert;

import java.util.Scanner;

public class TestingRestAPI {

	public String responseInString;
	public String countryNameOrCode;
	public int inputCharacterCount;
	public String basePathForCountryName;
	public String basePathForCountryCode;
	RequestSpecification request;
	static int statusCode;
	TestingRestAPI tpiObject;
	static String retry;
	static String countryName;
	static String capitalName;

	
	
	@Test(priority = 2)
	public void getInputFromUser() {
		System.out.println("Please Enter the Country Name Or Country Code whose Capital you Wish to check");
		System.out.println("For Example: United States or US");
		Scanner sc = new Scanner(System.in);
		countryNameOrCode = sc.nextLine();
		System.out.print("You requested to get the capital for: " + countryNameOrCode + "\n");

	}

	@Test(priority = 3)
	public void getRequest() throws JsonMappingException, JsonProcessingException {
		RestAssured.baseURI = "https://restcountries.com/v3.1/";

		Response response;
		basePathForCountryName = "name/" + countryNameOrCode;
		basePathForCountryCode = "alpha/" + countryNameOrCode;

		inputCharacterCount = countryNameOrCode.length();
		
		if (inputCharacterCount > 3) //If condition could have been made be Based on NAME,PARTIAL NAME, or CODE NAME
		{
					request = RestAssured.given().basePath(basePathForCountryName).queryParam("fullText", "true")
					.contentType(ContentType.JSON);
		} else {

			request = RestAssured.given().basePath(basePathForCountryCode).contentType(ContentType.JSON);

		}
		response = request.when().get().then().extract().response();

		responseInString = response.asString();
		JsonPath js = new JsonPath(responseInString);

		ObjectMapper objectMapper = new ObjectMapper();

		// Get tree representation of json
		JsonNode jsonTree = objectMapper.readTree(responseInString);
		statusCode = response.getStatusCode();
		System.out.println("before extracting size");
		String xx=response.asString();
		String connectionHeader=response.header("Connection");
		String keepAliveHeader=response.header("Keep-Alive");
		
		Assert.assertEquals("Keep-Alive",connectionHeader);
		Assert.assertEquals("timeout=5, max=100",keepAliveHeader);
		int size=xx.length();
		if(size==2) {
			System.out.println("The Country code/Name you have entered is invalid, Please enter a valid country");
			retry="Yes";
		}
		else if (statusCode == 200 && size>2) {
			capitalName = jsonTree.get(0).get("capital").get(0).asText(); // Using get method
			countryName = jsonTree.at("/0/name/common").asText(); // Using at() method
			System.out.println("The Capital City of " + countryName + " is " + capitalName + ".\n\n");
			
			Assert.assertEquals("Keep-Alive",connectionHeader);
			System.out.println("**************************NOTE BEGIN**************************************");
			System.out.println("When a Valid Country Value= "+countryNameOrCode+" is Passed in the Get Request");
			System.out.println("POSITIVE TESTCASE PASS- Connection Header Value as Expected");
			Assert.assertEquals("timeout=5, max=100",keepAliveHeader);
			System.out.println("POSITIVE TESTCASE PASS- Keep-Alive Header Value as Expected");
			Assert.assertEquals(200, statusCode);
			System.out.println("POSITIVE TESTCASE PASS- Status Code 200 as Expected \n");
			System.out.println("**************************NOTE END**************************************");
		}

	}

	
	@Test(priority = 4)
	public void lastMethod() throws JsonMappingException, JsonProcessingException {
        
		if (statusCode == 404 || statusCode == 400) {
			System.out.println("Uh Ohh!!! There is No Such Country, Please try again with Valid Country \n \n");
			System.out.println(
					"If you would like to try again? Please Enter Yes to Continue " + "Or Type Exit to Quit \n");
			Scanner sc = new Scanner(System.in);
			retry = sc.nextLine();
			while (!retry.equalsIgnoreCase("Yes") && !retry.equalsIgnoreCase("exit")) {
				System.out.println("INVALID RESPONSE!!!- Please Type YES or EXIT");
				retry = sc.nextLine();
			}

			// tpiObject.getRequest();
		}
	}

	@Test(priority = 5)
	public void reRun() throws JsonMappingException, JsonProcessingException {
		tpiObject = new TestingRestAPI();
		if (statusCode == 200 && countryName != null) {
			if (countryName == null) {
				System.out.println("Please Enter a Valid Country code which matches to a Country");
			}
			System.out.println("\nGreat Awesome!!! You Found the Capital of " + countryName + ", "
					+ "\nNow Would you like to find Capitals of Other Countries?"
					+ "\nIf yes, Please type YES to Continue Or Type EXIT to Quit");
			Scanner sc = new Scanner(System.in);
			retry = sc.nextLine();

			while (!retry.equalsIgnoreCase("Yes") && !retry.equalsIgnoreCase("exit")) {
				System.out.println("INVALID RESPONSE!!!- Please Type YES or EXIT");
				retry = sc.nextLine();
			}

		}
		while (!retry.equalsIgnoreCase("Exit")) {
			tpiObject.getInputFromUser();
			tpiObject.getRequest();
			tpiObject.lastMethod();
			if (statusCode == 200 && countryName != null) {
				if (countryName.equalsIgnoreCase(null)) {
					System.out.println("Please Enter a Valid Country code which matches to a Country");
				}
				System.out.println("Awesome!!! You Found the Capital of" + countryName + ", "
						+ "\nNow Would you like to find Capitals of Other Countries?"
						+ "\nIf yes, Please type YES to Continue Or Type EXIT to Quit");
				Scanner sc = new Scanner(System.in);
				retry = sc.nextLine();

				while (!retry.equalsIgnoreCase("Yes") && !retry.equalsIgnoreCase("exit")) {
					System.out.println("INVALID RESPONSE!!!- Please Type YES or EXIT");
					retry = sc.nextLine();
				}

			}
		}

	

	}
	
	@Test(priority = 6)
	public void negativeTestCase01() throws JsonMappingException, JsonProcessingException {
		RestAssured.baseURI = "https://restcountries.com/v3.1/";
       
		Response response;
		basePathForCountryName = "name/" + countryNameOrCode+"blaBlaBla";
		inputCharacterCount = countryNameOrCode.length();
				request = RestAssured.given().basePath(basePathForCountryName).queryParam("fullText", "true")
					.contentType(ContentType.JSON);
				
				ObjectMapper objectMapper = new ObjectMapper();

				response = request.when().get().then().extract().response();
				responseInString = response.asString();
				JsonNode jsonTree = objectMapper.readTree(responseInString);
				statusCode = response.getStatusCode();
				Assert.assertEquals(404, statusCode);
				System.out.println("\n**************************NOTE BEGIN*******************************************************");
				 System.out.println("\nFirst Negative Test case is being executed: Using Incorrect Country Name");
				System.out.println("Negative Test Case: PASS : Error code 404 as Expected");
				String messageValue=jsonTree.at("/message").asText();
				Assert.assertEquals("Not Found", messageValue);
				System.out.println("Negative Test Case: PASS : Message Value in Response ="+messageValue+" As Expected\n");
				System.out.println("**************************NOTE END********************************************************\n");
				
				
		
	}
	
	@Test(priority = 7)
	public void negativeTestCase02() throws JsonMappingException, JsonProcessingException {
		RestAssured.baseURI = "https://restcountries.com/v3.1/";
        
		Response response;
		basePathForCountryCode = "name/" + countryNameOrCode+"blaBlaBla";
		inputCharacterCount = countryNameOrCode.length();
				request = RestAssured.given().basePath(basePathForCountryCode).queryParam("fullText", "true")
					.contentType(ContentType.JSON);
				
				ObjectMapper objectMapper = new ObjectMapper();

				response = request.when().get().then().extract().response();
				responseInString = response.asString();
				JsonNode jsonTree = objectMapper.readTree(responseInString);
				statusCode = response.getStatusCode();
				Assert.assertEquals(404, statusCode);
				System.out.println("\n**************************NOTE BEGIN*******************************************************");
				System.out.println("\nSecond Negative Test case is being executed: Using Incorrect Country code");
				System.out.println("Negative Test Case: PASS : Error code 404 as Expected");
				String messageValue=jsonTree.at("/message").asText();
				Assert.assertEquals("Not Found", messageValue);
				System.out.println("Negative Test Case: PASS : Message Value in Response ="+messageValue+" As Expected\n");
				System.out.println("**************************NOTE END********************************************************\n");
				
				
		
	}
	
}
