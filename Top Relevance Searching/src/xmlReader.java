import java.util.*;
import org.w3c.dom.*;   
import javax.xml.parsers.*;  

// reference page:
// JAVA parse XML: http://www.java-samples.com/showtutorial.php?tutorialid=152

/*
 * Given a XML file, parse it to get [ [URL,Tiltle,Summary] [URL,Tiltle,Summary] .....]
 */
public class xmlReader {

 	public ArrayList<ArrayList<String>> Analyze(String inputString) throws Exception{
 			// create an arraylist with ten empty arraylists to hold the results
	  		ArrayList<ArrayList<String>> queryResults = new ArrayList<ArrayList<String>>();
			for(int i=0; i<10; i++){
            	queryResults.add(new ArrayList<String>());
			}
	  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 	DocumentBuilder db;

			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputString);
			Element element = doc.getDocumentElement();
		    NodeList contentSegList = element.getElementsByTagName("content");

		    if (contentSegList.getLength() < 10){
		    	System.out.println("Number of query results returned by Bing is less than 10, system will exit");
           		System.exit(0);
		    }

		    for(int i=0;i<10;i++){
			    Node contentSeg=contentSegList.item(i);
			    NodeList propertyList=contentSeg.getChildNodes();
			    Node property=propertyList.item(0);
			    NodeList items=property.getChildNodes();

			    Node Title=items.item(1);
			    Node Summary=items.item(2);
			    Node URL=items.item(3);
	     
	     		// add parsed queryResultss
				queryResults.get(i).add(URL.getFirstChild().getNodeValue());
				queryResults.get(i).add(Title.getFirstChild().getNodeValue());
			    if(Summary.getFirstChild()==null){ 
				    queryResults.get(i).add(" ");
			    }
			    else{queryResults.get(i).add(Summary.getFirstChild().getNodeValue());}	
		     }

			return queryResults;
  		}
}
