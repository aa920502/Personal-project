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
import java.util.*;

public class MqlReader {
	  public String divideBar =" --------------------------------------------------------------------------------------------------------------------------------------- ";
	  public static String api_key;
	  String question;
	  String target;
	  
	  public MqlReader(String question,String api_key){
		  this.question=question;
		  this.api_key=api_key;
	  }
  
  public boolean ParseQuestion(String question){
	  String[] arr =question.split(" ");
	  if(arr.length>=3 && arr[0].toLowerCase().equals("who") && arr[1].toLowerCase().equals("created")){
		  String lastWord=arr[arr.length-1];
		  char lastChar=lastWord.charAt(lastWord.length()-1);
		  if(lastChar=='?'){
			  StringBuffer sb=new StringBuffer();
			  for(int i=2;i<arr.length;i++){
				  if(i!=arr.length-1){sb.append(arr[i]+" ");}
				  else if(i==arr.length-1){
					  String tmp=arr[i].substring(0, arr[i].length()-1);
					  sb.append(tmp);
				  }
			  }
			  target=sb.toString();
			  return true;
		  }else if(lastChar!='?'){
			  StringBuffer sb=new StringBuffer();
			  for(int i=2;i<arr.length;i++){
				  if(i!=arr.length-1){sb.append(arr[i]+" ");}
				  else if(i==arr.length-1){
					  sb.append(arr[i]);
				  }
			  }
			  target=sb.toString();
			  return true;
		  }
	  }
	  return false;
  }
  
  public void AnswerQuestion() {
	  if(ParseQuestion(question)){
		  try {
		  	//Arraylists for holding print_out information
			  ArrayList<String> author=new ArrayList<String>();
			  List<List<String>> book=new ArrayList<List<String>>();
			  ArrayList<String> businessPerson=new ArrayList<String>();
			  List<List<String>> org=new ArrayList<List<String>>();
			  
		      HttpTransport httpTransport = new NetHttpTransport();
		      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		      JSONParser parser = new JSONParser();
		      
		      //Search with book MQL query and store information
		      String queryBook = "[{\"works_written\": [{\"b:name\": null,\"name~=\": \""+target+"\"}],\"name\": null,\"type\": \"/book/author\"}]";
		      GenericUrl urlbook = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
		      urlbook.put("query", queryBook);
		      urlbook.put("key", api_key);
		      HttpRequest requestbook = requestFactory.buildGetRequest(urlbook);
		      HttpResponse httpResponsebook = requestbook.execute();
		      JSONObject responsebook = (JSONObject)parser.parse(httpResponsebook.parseAsString());
		      JSONArray bookresults = (JSONArray)responsebook.get("result");
		      for (Object result : bookresults) {
		    	  author.add(JsonPath.read(result,"$.name").toString());
		    	 JSONArray arr =JsonPath.read(result,"$.works_written");
		    	 ArrayList<String> tmp=new ArrayList<String>();
		    	 for(int i=0;i<arr.size();i++){
		    		 tmp.add(JsonPath.read(arr.get(i),"$.b:name").toString());
		    	 }
		    	 book.add(tmp);
		      }
		      
		    //Search with organization MQL query and store information
			  String queryBusiness = "[{\"organizations_founded\": [{\"a:name\": null,\"name~=\": \""+target+"\"}],\"name\": null,\"type\": \"/organization/organization_founder\"}]";
			  GenericUrl urlbusiness = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
			  urlbusiness.put("query", queryBusiness);
			  urlbusiness.put("key", api_key);
			  HttpRequest requestbusiness = requestFactory.buildGetRequest(urlbusiness);
			  HttpResponse httpResponsebusiness = requestbusiness.execute();
			  JSONObject responsebusiness = (JSONObject)parser.parse(httpResponsebusiness.parseAsString());
			  JSONArray businessresults = (JSONArray)responsebusiness.get("result");
			  for (Object result : businessresults) {
				  businessPerson.add(JsonPath.read(result,"$.name").toString());
				  JSONArray arr =JsonPath.read(result,"$.organizations_founded");
				  ArrayList<String> tmp=new ArrayList<String>();
				  for(int i=0;i<arr.size();i++){
					  tmp.add(JsonPath.read(arr.get(i),"$.a:name").toString());
				  }
				  org.add(tmp);
			  }
			  
			  if(author.size()==0&&businessPerson.size()==0){
				  System.out.println("No related information about query ["+question+"] was found!");
			  }
			  else{
					// Info Box
					// Print HeadLine
					StringBuffer headline=new StringBuffer();
					headline.append(question);
					System.out.println(divideBar);
					int headLinelen = (divideBar.length()-2-headline.length())/2;
					StringBuffer space=new StringBuffer();
					for(int i=0;i<headLinelen;i++){
						space.append(" ");
					}
					headline.insert(0, "|"+space.toString());
					headline.append(space.toString()+"|");
					System.out.println(headline.toString());
					System.out.println(divideBar);
				  Printbook(author,book);
				  Printcompany(businessPerson,org);
			  }
		    } catch (Exception ex) {ex.printStackTrace();}
		}
	  else if(!ParseQuestion(question)){
		  System.out.println("No related information about query ["+question+"] was found!");
	  }
  	}
  
  	// Print for book query result
  	public void Printbook(ArrayList<String> author,List<List<String>> book){
  		for(int i=0;i<author.size();i++){
  			int len=0;
  			len=(divideBar.length()-4)/3;
  			StringBuffer sb=new StringBuffer();
  			sb.append("|");
  			sb.append(" "+author.get(i) );
  			int tmp=0;
  			tmp=(" "+author.get(i)).length();
  			for(int j=0;j<len-tmp;j++){
  				sb.append(" ");
  			}
  			sb.append("|");
  			sb.append(" As");
  			tmp=(" As").length();
  			for(int j=0;j<len-tmp;j++){
  				sb.append(" ");
  			}
  			sb.append("|");
  			sb.append("Creation");
  			tmp=divideBar.length()-1-sb.length();
  			for(int j=0;j<tmp;j++){
  				sb.append(" ");
  			}
  			sb.append("|");
  			System.out.println(sb.toString());
  			// print divide bar
  			StringBuffer tmpBuff=new StringBuffer();
  			tmpBuff.append("|");
  			for(int j=0;j<len;j++){
  				tmpBuff.append(" ");
  			}
  			tmp=divideBar.length()-1-tmpBuff.length();
  			for(int j=0;j<tmp;j++){
  				tmpBuff.append("-");
  			}
  			tmpBuff.append("|");
  			System.out.println(tmpBuff.toString());
  			// print books
  			for(int k=0;k<book.get(i).size();k++){
  	  			StringBuffer infoBuff = new StringBuffer();
  	  			if(k==0){
  	  				infoBuff.append("|");
	  	  			for(int j=0;j<len;j++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|Author");
	  	  			tmp=("|Author").length();
	  	  			for(int l=0;l<len-tmp;l++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|");
	  	  			if(book.get(i).get(k).length()<=len){
	  	  				infoBuff.append(book.get(i).get(k));
	  	  				tmp=(book.get(i).get(k)).length();
		  	  			for(int l=0;l<len-tmp;l++){
		  	  				infoBuff.append(" ");
		  	  			}
		  	  			infoBuff.append("|");
	  	  			}
	  	  			else if ((book.get(i).get(k).length()>len)){
	  	  				infoBuff.append(book.get(i).get(k).substring(0, len));
		  	  			infoBuff.append("|");
	  	  			}
	  	  			System.out.println(infoBuff.toString());
  	  			}
  	  			else{
  	  				infoBuff = new StringBuffer();
  	  				infoBuff.append("|");
	  	  			for(int j=0;j<len;j++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|");
	  	  			tmp=("|").length();
	  	  			for(int l=0;l<len-tmp;l++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|");
	  	  			if(book.get(i).get(k).length()<=len){
	  	  				infoBuff.append(book.get(i).get(k));
	  	  				tmp=(book.get(i).get(k)).length();
		  	  			for(int l=0;l<len-tmp;l++){
		  	  				infoBuff.append(" ");
		  	  			}
		  	  			infoBuff.append("|");
	  	  			}
	  	  			else if ((book.get(i).get(k).length()>len)){
	  	  				infoBuff.append(book.get(i).get(k).substring(0, len));
		  	  			infoBuff.append("|");
	  	  			}
	  	  			System.out.println(infoBuff.toString());
  	  			}
  			}
  			System.out.println(divideBar);
  		}
  	}
  	public void Printcompany(ArrayList<String> businessPerson,List<List<String>> org){
  		for(int i=0;i<businessPerson.size();i++){
  			int len=0;
  			len=(divideBar.length()-4)/3;
  			StringBuffer sb=new StringBuffer();
  			sb.append("|");
  			sb.append(" "+businessPerson.get(i) );
  			int tmp=0;
  			tmp=(" "+businessPerson.get(i)).length();
  			for(int j=0;j<len-tmp;j++){
  				sb.append(" ");
  			}
  			sb.append("|");
  			sb.append(" As");
  			tmp=(" As").length();
  			for(int j=0;j<len-tmp;j++){
  				sb.append(" ");
  			}
  			sb.append("|");
  			sb.append("Creation");
  			tmp=divideBar.length()-1-sb.length();
  			for(int j=0;j<tmp;j++){
  				sb.append(" ");
  			}
  			sb.append("|");
  			System.out.println(sb.toString());
  			// print divide bar
  			StringBuffer tmpBuff=new StringBuffer();
  			tmpBuff.append("|");
  			for(int j=0;j<len;j++){
  				tmpBuff.append(" ");
  			}
  			tmp=divideBar.length()-1-tmpBuff.length();
  			for(int j=0;j<tmp;j++){
  				tmpBuff.append("-");
  			}
  			tmpBuff.append("|");
  			System.out.println(tmpBuff.toString());
  			// print business information
  			for(int k=0;k<org.get(i).size();k++){
  	  			StringBuffer infoBuff = new StringBuffer();
  	  			if(k==0){
  	  				infoBuff.append("|");
	  	  			for(int j=0;j<len;j++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|Business Person");
	  	  			tmp=("|Business Person").length();
	  	  			for(int l=0;l<len-tmp;l++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|");
	  	  			if(org.get(i).get(k).length()<=len){
	  	  				infoBuff.append(org.get(i).get(k));
	  	  				tmp=(org.get(i).get(k)).length();
		  	  			for(int l=0;l<len-tmp;l++){
		  	  				infoBuff.append(" ");
		  	  			}
		  	  			infoBuff.append("|");
	  	  			}
	  	  			else if ((org.get(i).get(k).length()>len)){
	  	  				infoBuff.append(org.get(i).get(k).substring(0, len));
		  	  			infoBuff.append("|");
	  	  			}
	  	  			System.out.println(infoBuff.toString());
  	  			}
  	  			else{
  	  				infoBuff = new StringBuffer();
  	  				infoBuff.append("|");
	  	  			for(int j=0;j<len;j++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|");
	  	  			tmp=("|").length();
	  	  			for(int l=0;l<len-tmp;l++){
	  	  				infoBuff.append(" ");
	  	  			}
	  	  			infoBuff.append("|");
	  	  			if(org.get(i).get(k).length()<=len){
	  	  				infoBuff.append(org.get(i).get(k));
	  	  				tmp=(org.get(i).get(k)).length();
		  	  			for(int l=0;l<len-tmp;l++){
		  	  				infoBuff.append(" ");
		  	  			}
		  	  			infoBuff.append("|");
	  	  			}
	  	  			else if ((org.get(i).get(k).length()>len)){
	  	  				infoBuff.append(org.get(i).get(k).substring(0, len));
		  	  			infoBuff.append("|");
	  	  			}
	  	  			System.out.println(infoBuff.toString());
  	  			}
  			}
  			System.out.println(divideBar);
  		}
  	}
}