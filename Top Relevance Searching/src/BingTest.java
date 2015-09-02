import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import org.apache.commons.codec.binary.Base64;



public class BingTest {
	static String accountKey;

 	/*
	 * Search in Bing given query and accountKey, return result.
	 */
	public static String searchResult(String query,String accountKey){
		byte[] contentRaw=null;
		// handle multiple words query input string
		// example: gates bill -->  %27gates%27%27bill%27
		String[] temp = query.split(" ");
		StringBuffer sb = new StringBuffer();
		for(String s: temp){
			sb.append("%27"+s+"%27");
		}
		String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query="+sb.toString()+"&$top=10&$format=Atom";
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);
		try{
			URL url = new URL(bingUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
					
			InputStream inputStream = (InputStream) urlConnection.getContent();		
			contentRaw = new byte[urlConnection.getContentLength()];
			inputStream.read(contentRaw);
		}catch(Exception e){e.printStackTrace();}
		//The content string is the xml/json output from Bing.
		String content = new String(contentRaw);
		return content;		
	}


	/*
	 * Printing out status messages before each loop2
	 */
	public static void initPrint(String accountKey,String query,String targetPrecision){
		System.out.println("Parameters: ");
		System.out.println("Client key  = "+accountKey);
		System.out.println("Query       = "+query);
		System.out.println("Precision   = "+targetPrecision);
		System.out.println("URL: "+"https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query+"%27&$top=10&$format=Atom");
		System.out.println("Total number of results : 10");
		System.out.println("Bing Search Results: ");
		System.out.println("======================");
	}

	/*
	 * Feedback message display
	 */
	public static void feedbackPrint(String query,String targetPrecision,String currentPrecision,String augmentation){
		System.out.println("======================");		
		System.out.println("FEEDBACK SUMMARY");
		System.out.println("Query "+query);
		System.out.println("Precision "+currentPrecision);
		double currentPrecisionDouble, targetPrecisionDouble;
		currentPrecisionDouble = Double.parseDouble(currentPrecision);
		targetPrecisionDouble = Double.parseDouble(targetPrecision);
		if (currentPrecisionDouble<targetPrecisionDouble){
			System.out.println("Still below the desired precision of "+targetPrecision);
			System.out.println("Indexing results......");
			System.out.println("Indexing results......");
			System.out.println("Augmenting by "+ augmentation);
		}
		else{
			System.out.println("Desired precision reached, done");
			System.exit(0);
		}
	}


	/* return number of relevant documents selected by users */
    public static int UserInteraction(ArrayList<ArrayList<String>> result) throws IOException {
        int numRelevantDocs=0;
        /* ask user for input relevance and collect result*/
        for (int i = 0; i < 10; i++){
            System.out.println("Result "+(i+1));
            System.out.println("[");
            System.out.println("URL: "+result.get(i).get(0));
            System.out.println("Title: "+result.get(i).get(1));
            System.out.println("Summary: "+result.get(i).get(2));
            System.out.println("]");
            System.out.println();
            System.out.print("Relevant (Y/N)?");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            String input = in.readLine();
            while (!input.equals("Y") && !input.equals("y") && !input.equals("N") && !input.equals("n")) {
                System.out.print("Please enter Y/y or N/n: ");
                input = in.readLine();
            }
            // add "1" if input is Y/y, "0" if input is N/n
            if (input.equals("Y") || input.equals("y")) {
                result.get(i).add("1");
                numRelevantDocs++;
            }else {
                result.get(i).add("0");
            }
            System.out.println();
        }
        return numRelevantDocs;
    }
	

    // return expanded query including original query
	public static ArrayList<String> GetExpandedQuery(ArrayList<String> fullQueryList,ArrayList<ArrayList<String>> queryResults){

        ArrayList<HashMap<String,Integer>> termCounter = new ArrayList<HashMap<String,Integer>>();
        HashMap<String,Double> idfTable = new HashMap<String,Double>();
        HashMap<String,Double> totalWeights = new HashMap<String,Double>();

        // for each document, return number of occurance of each word from(description+title)
        for (int i = 0; i < 10; i++){
            termCounter.add(CountTerm(queryResults.get(i).get(2)+" "+queryResults.get(i).get(1)));
        }
        
        for (int i = 0; i < 10; i++){
            if (queryResults.get(i).get(3).equals("1")){
                CalculateWeights(termCounter, totalWeights, idfTable, i);
            }
        }
        double firstWeight=0,secondWeight=0;
        String firstTerm=null,secondTerm=null;
        
        for (String s : fullQueryList) {
            if(totalWeights.containsKey(s.toLowerCase())) {
                totalWeights.remove(s.toLowerCase());
            }
        }
        // find top two terms with highest two weights
        for(String key : totalWeights.keySet()){
            if (totalWeights.get(key)>firstWeight){
                secondWeight = firstWeight;
                secondTerm = firstTerm;
                firstWeight =totalWeights.get(key);
                firstTerm = key;
            } else if (totalWeights.get(key)>secondWeight){
                secondWeight = totalWeights.get(key);
                secondTerm = key;
            }
        }
        
        fullQueryList.add(firstTerm);
        fullQueryList.add(secondTerm);
        return fullQueryList;
    }



    // calculate weight of term in relevant docs using "augmented frequency" for calculating term frequency
    public static void CalculateWeights(ArrayList<HashMap<String,Integer>> termCounter, HashMap<String,Double> allTermWeights, HashMap<String,Double> idfTable,int docNumber){
        for(String key : termCounter.get(docNumber).keySet()) {
            int value = termCounter.get(docNumber).get(key);
            double idf;
            if (idfTable.containsKey(key)){
                idf = idfTable.get(key);
            } 
            else {
                idf = Math.log(10f/numOfDocsContainingKey(key,termCounter));
                idfTable.put(key, idf);
            }
            double max = 0.0;
            for (String k: termCounter.get(docNumber).keySet()){
                if (termCounter.get(docNumber).get(k)>max)
                    max = termCounter.get(docNumber).get(k);
            }
            double augtf = 0.5 + (0.5*value)/max;

            // use logarithmically scaled frequency:
            // double tf = 1 + Math.log(value);
            // double weight = tf * idf;

            // use raw frequency of a term in a document
            //double weight = value * idf;

            // use augmented frequency
            double weight = augtf * idf;
            // store weight for the term
            if (allTermWeights.containsKey(key)){
                allTermWeights.put(key, allTermWeights.get(key)+weight);
            } else {
                allTermWeights.put(key, weight);
            }
        }
    }

    // return number of documents that contains the input key
    public static int numOfDocsContainingKey(String key, ArrayList<HashMap<String,Integer>> termCounter){
        int numOfDocumentsContainingKey = 0;
        for (int i = 0; i<10; i++){
            if (termCounter.get(i).containsKey(key)){
                numOfDocumentsContainingKey++;
            }
        }
        return numOfDocumentsContainingKey;
    }


    // for each result, return each term in the document with its raw term frequency (pure appearance).
    // use BreakIterator
    // reference: http://kodejava.org/how-do-i-breaks-a-text-or-sentence-into-words/
    public static HashMap<String, Integer> CountTerm(String str) {

        HashMap<String, Integer> rawtfMap = new HashMap<String, Integer>();
        ArrayList<String> splitString = new ArrayList<String>();
        BreakIterator bi = BreakIterator.getWordInstance(Locale.US);
        // Set the text string to be scanned.
        bi.setText(str);

        // Iterates the boundary / breaks
        int count = 0;
        int lastIndex = bi.first();
        while (lastIndex != BreakIterator.DONE) {
            int firstIndex = lastIndex;
            lastIndex = bi.next();

            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(str.charAt(firstIndex))) {
                String word = str.substring(firstIndex, lastIndex);
                splitString.add(word);
            }
        }
        
        for (String word : splitString){
            if(!rawtfMap.containsKey(word)) {
                rawtfMap.put(word, 1);
            } else {
                rawtfMap.put(word, rawtfMap.get(word)+1);
            }
        }
        return rawtfMap;
    }

    // Convert an array of strings to String type
    public static String ListToString(ArrayList<String> keywords){
        StringBuilder sb = new StringBuilder();
        sb.append(keywords.get(0));
        
        for (int i = 1; i < keywords.size(); i++){
            sb.append(" "+keywords.get(i));
        }
        
        return sb.toString();
    }

    // Given a new string and an old string, return the augmentation in new string
    // Example: new string -> "hello world"    old string -> "hello"    return -> "world"
    public static String getAugmentation(String oldStr, String newStr){
    	String[] oldArr = oldStr.split(" ");
    	String[] newArr = newStr.split(" ");

    	StringBuffer sb= new StringBuffer();
    	for(int i=oldArr.length; i<newArr.length;i++){
    		sb.append(newArr[i]+" ");
    	}
    	return sb.toString();
    }


	public static void main(String[] args) throws Exception {
		//Default account key: Hx+8rg78o3Ly6+v9TGq1IX7U9olvALGFZBq19QLUkio
		//String accountKey = "Hx+8rg78o3Ly6+v9TGq1IX7U9olvALGFZBq19QLUkio";
        String accountKey = args[0];
        float currentPrecision = 0f;
		float targetPrecision = 0f;
		String query = args[2];
        String targetPrecisionStr = args[1];
		String[] queryArr;

        ArrayList<String> fullQueryList = new ArrayList<String>();
        ArrayList<String> queryHistory = new ArrayList<String>();

		queryArr = (query.split("\\s+"));
        for(String singleQueryWord : queryArr) {
            fullQueryList.add(singleQueryWord);
        }

	
        targetPrecision = Float.parseFloat(targetPrecisionStr);    
        // for message printing purpose
		int loop = 0;


		while (currentPrecision<targetPrecision){
			String fullQuery = ListToString(fullQueryList);
			queryHistory.add(fullQuery); // add to query history arraylist
			fullQuery = java.net.URLEncoder.encode(fullQuery, "utf8");
            // for first time, only print out initial information
			if (loop ==0){
				System.out.println();
				initPrint(accountKey,queryHistory.get(loop),targetPrecisionStr);
			}
            // in later loops, print feedback information first, then print initial information for next loop
			else if (loop>0){
				String augmentation = getAugmentation(queryHistory.get(loop-1),queryHistory.get(loop));
				feedbackPrint(queryHistory.get(loop-1),targetPrecisionStr,String.valueOf(currentPrecision),augmentation);
				initPrint(accountKey,queryHistory.get(loop),targetPrecisionStr);
			}

            // search and get parsed results, store for each file 
			String content = searchResult(fullQuery,accountKey);
            // write result to an output file -> note: when new result is written, old result will be cleared already since writer is closed each time.
            xmlWriter xw=new xmlWriter();
			xw.write(content);
            // read the output file and parse information for all ten results
			xmlReader xr=new xmlReader();
			ArrayList<ArrayList<String>> queryResults = xr.Analyze("searchResult.xml");

			loop++;
            //get number of relevant documents
			int numRelevantDoc = UserInteraction(queryResults);
            if (numRelevantDoc == 0){
                System.out.println("Number of relevant results is: "+numRelevantDoc+", system will exit now.");
                System.exit(0);
            } else {
                currentPrecision = numRelevantDoc/10f;
                // expand query if current precision is below expected precision
                if(currentPrecision < targetPrecision){ 
                	fullQueryList = GetExpandedQuery(fullQueryList,queryResults);
                }else{
                    break;
                }
            }
        }

        // if precision is reached, print out ending statement
        System.out.println("======================");
        System.out.println("FEEDBACK SUMMARY");
        System.out.println("Query "+queryHistory.get(queryHistory.size()-1));
        System.out.println("Precision "+currentPrecision);
        System.out.println("Desired precision reached, done.");		
	}

}



