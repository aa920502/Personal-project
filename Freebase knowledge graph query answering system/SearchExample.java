import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;

public class SearchExample {
	public static String query;
	public static String api_key;
	public static  HashMap<String, ArrayList<String>> FreebaseTypeTable=new HashMap<String, ArrayList<String>>();
	public static  HashMap<String, ArrayList<String>> TypeOfInterestTable=new HashMap<String, ArrayList<String>>();
  
	// Decide type and print 
	public static boolean ParseJSONObject(JSONObject topic){
		  JSONParser parser = new JSONParser();
		  JSONObject properties= (JSONObject) topic.get("property");
		  // ArrayList for holding types
		  ArrayList<String> types=new ArrayList<String>();
		  ArrayList<String> entityType=new ArrayList<String>();
		  // initialize info printer
		  InfoPrinter infoPrinter = new InfoPrinter(properties);
		  // add Freebase Entity type from /type/object/type-id
		  for(int i=0;i<infoPrinter.propertyInfoArray.size();i++){
			  try {
				JSONObject tmpArr = (JSONObject) parser.parse(infoPrinter.propertyInfoArray.get(i).toString());
				JSONArray tmpValueArr = (JSONArray)tmpArr.get("values");
				if(infoPrinter.propertyArray.get(i).toString().equals("/type/object/type")){
					for (int j=0;j<tmpValueArr.size();j++){
						types.add(JsonPath.read(tmpValueArr.get(j), "$.id").toString());}
					break;
				}} catch (ParseException e) {e.printStackTrace();}
		  }
		  // get type of entity (Person, Author, Actor, BusinessPerson, League, SportsTeam)                    
		  entityType = DecideType(types);
		  if(entityType.size()==0){return false;}
		  /************************
		   * "Person"
		   ************************/
		  else if(entityType.contains("Person")){
			 infoPrinter.PrintPersonInfo(entityType,TypeOfInterestTable);
			 return true;}
		  /************************
		   * "League"
		   ************************/
		  else if(entityType.contains("League")){
			  infoPrinter.PrintLeagueInfo(entityType,TypeOfInterestTable);
			  return true;}
		  /************************
		   * "SportsTeam"
		   ************************/
		  else if(entityType.contains("SportsTeam")){
			  infoPrinter.PrintSportsTeamInfo(entityType,TypeOfInterestTable);
			  return true;}
		  
		  return false;
	}

	// Decide type of entity based on /type/object/type
	public static ArrayList<String> DecideType(ArrayList<String> types){
		ArrayList<String> result = new ArrayList<String>();
		boolean isPerson = false;
		// First check if it's a "Person"
		for (String s: types){
			if (s.equals("/people/person")){
				isPerson=true;
				break;}
		}
		if (isPerson){
			result.add("Person");
			for (String s: types){
				if (s.equals("/book/author") && (!result.contains("Author"))){
					result.add("Author");}
				else if((s.equals("/film/actor")||s.equals("/tv/tv_actor"))&& (!result.contains("Actor"))){
					result.add("Actor");}
				else if((s.equals("/organization/organization_founder")||s.equals("/business/board_member"))&& (!result.contains("BusinessPerson"))){
					result.add("BusinessPerson");}
			}
		}
		else{
			for (String s: types){
				if (s.equals("/sports/sports_league") && (!result.contains("League"))){
					result.add("League");}
				if((s.equals("/sports/sports_team")||s.equals("/sports/professional_sports_team"))&& (!result.contains("SportsTeam"))){
					result.add("SportsTeam");}
			}
		}
		return result;
	}
  
	// Freebase Topic API
	public static boolean FreebaseTopicAPI(String mid){
		boolean match=false;
		try {
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			String topicId = mid;
			GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicId);
			url.put("key",api_key);
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			JSONObject topic = (JSONObject)parser.parse(httpResponse.parseAsString());
			// Parse return result from topic api
			match=ParseJSONObject(topic);
			} catch (Exception ex) {ex.printStackTrace();}	
		return match;
  	}
  
  	// Freebase Search API 
  	public static void FreebaseSearchAPI(String query){
	  try {
	  		boolean match=false;
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
			url.put("query", query);
			url.put("limit", "20");
			url.put("indent", "true");
			url.put("key", api_key);
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			// Freebase Search API result
			JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
			JSONArray results = (JSONArray)response.get("result");

			if(results.equals(null)||results.isEmpty()){
			  System.out.println("No related information about query ["+query+"] was found!");
			}
			else if(results.size()>0){
			  for(int i=0;i<results.size();i++){
			  	if(FreebaseTopicAPI(JsonPath.read(results.get(i),"$.mid").toString())){
			  		match=true;
			  		break;
			  	}else{
			  		if(i==4){System.out.println("Top 5 Search API result entries were considered. None of them of a supported type.");}
			  		if(i==9){System.out.println("Top 10 Search API result entries were considered. None of them of a supported type.");}
			  		if(i==14){System.out.println("Top 15 Search API result entries were considered. None of them of a supported type.");}
			  		if(i==19){System.out.println("Top 20 Search API result entries were considered. None of them of a supported type.");}
			  		continue;
			  	}
			  }
			}
	    } catch (Exception ex) {ex.printStackTrace();}
  	}
  
  	public static void main(String[] args) {
  		if(args.length!=1){
  			System.out.println("Format: make run <API_KEY>");
  			System.exit(0);
  		}
  		api_key=args[0];
  		Scanner Input=new Scanner(System.in);
		Initializer initializer = new Initializer();
		initializer.initializeFreebaseTypeTable(FreebaseTypeTable);
		initializer.initializeTypeOfInterestTable(TypeOfInterestTable);
		System.out.println("------------------------------");
		System.out.println("| Welcome to info box system |");
		System.out.println("------------------------------");
		
		while(true){
			System.out.println("Please enter your query: ");
			query=Input.nextLine();
			// query type
			if(!query.contains("Who created")&&!query.contains("who created")){
				FreebaseSearchAPI(query);
			}
			// question type
			else if(query.contains("Who created")||query.contains("who created")){
				MqlReader m = new MqlReader(query,api_key);
				m.AnswerQuestion();
			}
		}
  	}
}