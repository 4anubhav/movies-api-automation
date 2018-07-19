package com.airteltv.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.parser.ParseException;

import com.airteltv.reports.LoggerWrapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

//import io.restassured.path.json.JsonPath;

@SuppressWarnings("rawtypes")
public class MongoManager {

    static Map<String, MongoClient> mongoClientMap = new HashMap<String, MongoClient>();
    private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
    private String                  host            		= null;
    private int                     port;
    private String					userName				= null;
    private String					authenticationDatabase		= null;
    private String 					password				= null;
    public MongoManager(String _host, int _port, String _authenticationDatabase, String _userName, String _password) {
        host = _host;
        port = _port;
        authenticationDatabase = _authenticationDatabase;
        userName = _userName;
        password = _password;
        connect();
    }
    
    public MongoManager(String _host, int _port, String _authenticationDatabase) {
        host = _host;
        port = _port;
        authenticationDatabase = _authenticationDatabase;
        connect();
    }

	public void connect() {
		try {
			MongoCredential credential = null;
			if (userName != null || password != null) {
				credential = MongoCredential.createScramSha1Credential(userName, authenticationDatabase, password.toCharArray());
				if (!mongoClientMap.containsKey(host + port))
					mongoClientMap.put(host + port,
							new MongoClient(new ServerAddress(host, port), Arrays.asList(credential)));
			} else {
				if (!mongoClientMap.containsKey(host + port))
					mongoClientMap.put(host + port, new MongoClient(host, port));
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to connect to Mongo Database", e.getCause());
		}
	}

    public DBObject findOne(String database, String collectionName) {
        DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
        DBObject result = collection.findOne();
        return result;
    }
    
    // FindOne based on the query
    public DBObject findOne(String database, String collectionName, String... keyValue) {
        Map<String, Object> queryParams = Utils.getMap(keyValue);
        return findOne(database, collectionName, queryParams);
    }

    // FindOne based on the query
    public List<DBObject> find(String database, String collectionName, String... keyValue) {
        Map<String, Object> queryParams = Utils.getMap(keyValue);
        return find(database, collectionName, queryParams);
    }

    // FindOne based on the queryƒ
    public DBObject findOne(String database, String collectionName, Map queryParams) {
        DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
        BasicDBObject query = new BasicDBObject(queryParams);
        DBObject result = collection.findOne(query);
        return result;
    }

    // Find with query
    public List<DBObject> find(String database, String collectionName, Map queryParams) {
        DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);

        BasicDBObject query = new BasicDBObject(queryParams);
        loggerWrapper.info(query.toString());
        DBCursor results = collection.find(query);
        List<DBObject> resultsList = new ArrayList<DBObject>();
        while(results.hasNext()) {
            DBObject next = results.next();
            resultsList.add(next);
        }
        return resultsList;
    }

    // Update query for updating the complete document
    public void updateDocument(String database, String collectionName, Map queryParams, Map newObj) {
        DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
        BasicDBObject query = new BasicDBObject(queryParams);
        BasicDBObject newObject = new BasicDBObject(newObj);
        collection.update(query, newObject);
    }

    // Updating some field in the document.
    public boolean setFieldInDocument(String database, String collectionName, Map<String, Object> queryParams, Map<String, Object> fieldValueMap) {
        DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
        BasicDBObject query = new BasicDBObject(queryParams);
        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start("$set", new BasicDBObject(fieldValueMap));
        DBObject setField = builder.get();
        WriteResult writeResult = collection.update(query, setField);
        return writeResult.isUpdateOfExisting();
    }

    // Remove some document
    public boolean removeDocument(String database, String collectionName, Map<String, Object> queryParams) {
        DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
        BasicDBObject query = new BasicDBObject(queryParams);
        WriteResult writeResult = collection.remove(query);
        return writeResult.isUpdateOfExisting();
    }

    public static void closeAll() {
        for(String key : mongoClientMap.keySet()) {
            mongoClientMap.get(key).close();
            mongoClientMap.remove(key);
        }
    }
    
	public List<DBObject> findAllWithRegex(String database, String collectionName, String... keyValue) {
		Map<String, Object> queryParams = Utils.getMap(keyValue);
		BasicDBObject regexQuery = new BasicDBObject();
		
		for(String key : queryParams.keySet()) {
		regexQuery.put(key, 
			new BasicDBObject("$regex", queryParams.get(key)));
		}
		DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
        DBCursor results = collection.find(regexQuery);
        List<DBObject> resultsList = new ArrayList<DBObject>();
        while(results.hasNext()) {
            DBObject next = results.next();
            resultsList.add(next);
        }
        return resultsList;
    }
	
	public DBObject findOneWithRegex(String database, String collectionName, String... keyValue) {
		Map<String, Object> queryParams = Utils.getMap(keyValue);
		BasicDBObject regexQuery = new BasicDBObject();
		
		for(String key : queryParams.keySet()) {
		regexQuery.put(key, 
			new BasicDBObject("$regex", queryParams.get(key)));
		}
		
		DBCollection collection = mongoClientMap.get(host + port).getDB(database).getCollection(collectionName);
		DBObject result = collection.findOne(regexQuery);
       return result;
   }
	
	public static void main(String[] args) throws ParseException {
		MongoManager mm = new MongoManager("10.50.0.27", 27017,"admin", "adminuser", "mongo@098");
		DBObject dbo = mm.findOneWithRegex("atv", "playable_content","_id", "ALTBALAJI_EPISODE", "meta.isDrm", "true");
		
		String m3u8Url = mm.getStringFromMongoDocument(dbo, "meta.m3u8Url");
		System.out.println(m3u8Url);
		System.out.println(mm.getListFromMongoDocument(dbo, "meta[?(@.m3u8Url == \"" + m3u8Url + "\")].mediaId"));
		
	}
	
	public String getStringFromMongoDocument_withoutLoggingInReports(DBObject dbo, String jsonPath) {
		String jsonString;
		String value;
		try {
			jsonString = JSON.serialize(dbo);
			loggerWrapper.myLogger.info("key value for " + jsonPath + " : " + jsonString);
			Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
			value = JsonPath.read(document, jsonPath);
			return value;
		} catch (PathNotFoundException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			loggerWrapper.myLogger.error(jsonPath + " is invalid | " + stacktrace);
			return null;
		}
	}
	// path by jayway -- import com.jayway.jsonpath.JsonPath;
	public String getStringFromMongoDocument(DBObject dbo, String jsonPath) {
		String jsonString;
		String value;
		try {
			jsonString = JSON.serialize(dbo);
			loggerWrapper.info("key value for " + jsonPath + " : " + jsonString);
			Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
			value = JsonPath.read(document, jsonPath);
			return value;
		} catch (PathNotFoundException e) {
			loggerWrapper.fail(jsonPath + " is invalid ", e);
			return null;
		}
	}

	/* for conditional and regex jsonpath using jayway */
	public List<String> getListFromMongoDocument(DBObject dbo, String jsonPath) {
		String jsonString;
		List<String> value;
		try {
			jsonString = JSON.serialize(dbo);
			loggerWrapper.info("key value is for " + jsonPath + " : " + jsonString);
			Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
			value = JsonPath.read(document, jsonPath);
			return value;
		} catch (PathNotFoundException e) {
			loggerWrapper.fail(jsonPath + " is invalid ", e);
			return null;
		}
	}
	
	// path by io.restassured
	/*public String getKeyFromMongoDocument(DBObject dbo, String jsonPath) {
		JSONObject output = new JSONObject(JSON.serialize(dbo));
    	JsonPath jp = new JsonPath(output.toString());
    	return jp.getString(jsonPath);
	}*/
	
	public static MongoManager connectBEMongo() {
	    
	    String host = EnvProperties.getEnvProperty("config_", "MW_MONGO_HOST");
	    int port = Integer.parseInt(EnvProperties.getEnvProperty("config_", "MW_MONGO_PORT"));
	    String auth_db = EnvProperties.getEnvProperty("config_", "MW_MONGO_AUTHENTICATION_DB");
	    String uname = EnvProperties.getEnvProperty("config_", "MW_MONGO_USER");
	    String pwd = EnvProperties.getEnvProperty("config_", "MW_MONGO_PASS");
	    
	    MongoManager mm = new MongoManager(host, port, auth_db, uname, pwd);
		return mm;
	 }
}
