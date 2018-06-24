package com.airteltv.restutils;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

import java.io.File;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;

import com.airteltv.authentication.GenerateToken;
import com.airteltv.utility.EnvProperties;
import com.github.dzieciou.testing.curl.CurlLoggingRestAssuredConfigFactory;
import com.github.dzieciou.testing.curl.Options;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

//import static com.jayway.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestUtil {
	

	public static StringWriter requestWriter;
	public static PrintStream requestCapture;

	public static StringWriter responseWriter;
	public static PrintStream responseCapture;
	
	public static RestAssuredConfig config;
	
    //Global Setup Variables
    public static String path; //Rest request path

    /*
    ***Sets Base URI***
    Before starting the test, we should set the RestAssured.baseURI
    */
    public static void setBaseURI (String baseURI){
        RestAssured.baseURI = baseURI;
    }

    /*
    ***Sets base path***
    Before starting the test, we should set the RestAssured.basePath
    */
    public static void setBasePath(String basePathTerm){
        RestAssured.basePath = basePathTerm;
    }

    /*
    ***Reset Base URI (after test)***
    After the test, we should reset the RestAssured.baseURI
    */
    public static void resetBaseURI (){
        RestAssured.baseURI = null;
    }

    /*
    ***Reset base path (after test)***
    After the test, we should reset the RestAssured.basePath
    */
    public static void resetBasePath(){
        RestAssured.basePath = null;
    }

    /*
    ***Sets ContentType***
    We should set content type as JSON or XML before starting the test
    */
    public static void setContentType (ContentType Type){
        given().contentType(Type);
    }

    /*
    ***search query path of first example***
    It is  equal to "barack obama/videos.json?num_of_videos=4"
    */
    public static void  createSearchQueryPath(String searchTerm, String jsonPathTerm, String param, String paramValue) {
        path = searchTerm + "/" + jsonPathTerm + "?" + param + "=" + paramValue;
    }

    /*
    ***Returns response***
    We send "path" as a parameter to the Rest Assured'a "get" method
    and "get" method returns response of API
    */
    public static Response getResponse() {
        return get(path);
    }

    /*
    ***Returns JsonPath object***
    * First convert the API's response to String type with "asString()" method.
    * Then, send this String formatted json response to the JsonPath class and return the JsonPath
    */
    public static JsonPath getJsonPath (Response res) {
        String json = res.asString();
        return new JsonPath(json);
    }
    
   /* public static RequestSpecification getRequestSpecification()
	{
		return RestAssured.given().contentType(ContentType.JSON);
	}*/
    
    public static RequestSpecification getRequestSpecification() {
		config = CurlLoggingRestAssuredConfigFactory.createConfig(); 
		Options.builder().printMultiliner().build();
		RestUtil.setContentType(ContentType.JSON);
		return RestAssured.given().config(config).contentType(ContentType.JSON).filter(new RequestLoggingFilter(requestCapture))
				.filter(new ResponseLoggingFilter(responseCapture));
	}
	
	public Response getResponse(RequestSpecification requestSpecification, String endPoint, int Status)
	{
		Response response = requestSpecification.get(endPoint);
		Assert.assertEquals(response.getStatusCode(), Status);
		response.then().log().all();
		return response;
	}
	
	public String getKeyString(JsonPath jp, String jsonPath) {
    	return jp.getString(jsonPath);
	}
	
	public static Headers getHeadersList(String api_name, String os_type, String url) {

		List<String> headersList = Arrays
				.asList(EnvProperties.getEnvProperty("config_", api_name + "_HEADERS").split(","));
		List<Header> list = new ArrayList<Header>();
		for (String headerName : headersList) {
			if (headerName.equals("x-atv-did")) {
				Header h1 = new Header(headerName, EnvProperties.getEnvProperty("config_", "X_ATV_DID_" + os_type));
				list.add(h1);
			} else if (headerName.equals("x-atv-utkn")) {
				Header h1 = new Header(headerName, GenerateToken.generateSignatureGET(url, "Jt7kWyfQxI7cjiyFQ0"));
				list.add(h1);
			}
			else if (headerName.equals("host")) {
				Header h1 = new Header(headerName, EnvProperties.getEnvProperty("config_", api_name + "_HOST"));
				list.add(h1);
			}
			else {
				Header h1 = new Header(headerName, EnvProperties.getEnvProperty("config_", headerName));
				list.add(h1);
			}

		}
		Headers header = new Headers(list);
		return header;
	}
	
	public static void schemaValidation(Response resp, String jsonFileName) {
		 JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
			      .setValidationConfiguration(
			        ValidationConfiguration.newBuilder()
			          .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
			            .freeze();
		resp.then().contentType(ContentType.JSON).assertThat().body(matchesJsonSchema(
				new File(System.getProperty("user.dir") + "/src/test/resources/schema/" + jsonFileName)).using(jsonSchemaFactory));
	}

	public static void main(String[] args) {
		System.out.println(getHeadersList("PLAYBACK", "ANDROID",
				"http://play-dev2.wynk.in/v2/user/content/playback?contentId=ALTBALAJI_EPISODE_1055"));
	}
}