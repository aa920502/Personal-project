import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.jayway.jsonpath.JsonPath;


public class InfoPrinter {
	// array of property
	JSONArray propertyArray = new JSONArray();
	// array of information for each property
	JSONArray propertyInfoArray = new JSONArray();
	  public String divideBar =" --------------------------------------------------------------------------------------------------------------------------------------- ";

	public InfoPrinter(){}
	
	@SuppressWarnings("unchecked")
	// constructor
	public InfoPrinter(JSONObject properties){
		for(Object j:properties.keySet()){
			String key = (String) j;
		    propertyArray.add(key);
		    propertyInfoArray.add(properties.get(key));}
	}
	
	// Get needed properties for entity type
	public ArrayList<String> GetNeededProperties(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable){
		ArrayList<String> checkProperty=new ArrayList<String>();
		for (String entity:entityType){
			for(String key:TypeOfInterestTable.keySet()){
				if(key.equals(entity)){
					for(int i=0;i<TypeOfInterestTable.get(key).size();i++){
						checkProperty.add(TypeOfInterestTable.get(key).get(i));}
				}
			}
		}	
		return checkProperty;
	}
	
	/************************
	* "Person"
	************************/
	public void PrintPersonInfo(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable){
		String Name="",Birthday="",PlaceOfBirth="",PlaceOfDeath="",DateOfDeath="",CauseOfDeath="",Description="";
		ArrayList<String> siblings=new ArrayList<String>();
		ArrayList<String> spouseName=new ArrayList<String>();
		ArrayList<String> spouseFrom=new ArrayList<String>();
		ArrayList<String> spouseTo=new ArrayList<String>();
		ArrayList<String> marriagePlace=new ArrayList<String>();
		boolean nameFlag=false,birthdayFlag=false,placeOfBirthFlag=false,placeOfDeathFlag=false,dateOfDeathFlag=false,causeOfDeathFlag=false,descriptionFlag=false,siblingFlag=false,spouseFlag=false;
		
		ArrayList<String> personEntity=new ArrayList<String>();
		personEntity.add("Person");
		ArrayList<String> neededProperties = GetNeededProperties(personEntity,TypeOfInterestTable);
		
		// Name
		if(propertyArray.contains(neededProperties.get(0))){
			nameFlag=true;
			JSONArray name = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(0))),"$.values");
			Name= JsonPath.read(name.get(0),"$.text").toString();
		}
		// Birthday
		if(propertyArray.contains(neededProperties.get(1))){
			birthdayFlag=true;
			JSONArray birthday = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(1))),"$.values");
			Birthday= JsonPath.read(birthday.get(0),"$.text").toString();
		}
		// Place of birth
		if(propertyArray.contains(neededProperties.get(2))){
			placeOfBirthFlag=true;
			JSONArray placeOfBirth = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(2))),"$.values");
			PlaceOfBirth=JsonPath.read(placeOfBirth.get(0),"$.text").toString();
		}
		// Place of death
		if(propertyArray.contains(neededProperties.get(3))){
			placeOfDeathFlag=true;
			JSONArray deathPlace = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(3))),"$.values");
			PlaceOfDeath=JsonPath.read(deathPlace.get(0),"$.text").toString();
		}
		// Date of death
		if(propertyArray.contains(neededProperties.get(4))){
			dateOfDeathFlag=true;
			JSONArray deathDate = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(4))),"$.values");
			DateOfDeath=JsonPath.read(deathDate.get(0),"$.text").toString();
		}
		// Cause of death
		if(propertyArray.contains(neededProperties.get(5))){
			causeOfDeathFlag=true;
			JSONArray deathCause = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(5))),"$.values");
			CauseOfDeath=JsonPath.read(deathCause.get(0),"$.text").toString();
		}
		// Siblings
		if(propertyArray.contains(neededProperties.get(6))){
			siblingFlag=true;
			JSONArray siblingValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(6))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<siblingValue.size();i++){
				try {
					JSONObject siblingValueParse = (JSONObject)parser.parse(siblingValue.get(i).toString());
					String propertyStr = JsonPath.read(siblingValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/people/sibling_relationship/")){
							if(o.toString().equals("/people/sibling_relationship/sibling")){
								String siblingInfo = JsonPath.read(siblingValueParse,"$.property['/people/sibling_relationship/sibling']").toString();
								JSONObject siblingInfoParse = (JSONObject)parser.parse(siblingInfo);
							    if (JsonPath.read(siblingInfoParse,"$.values").toString().equals("[]")){}
							    else{siblings.add(JsonPath.read(siblingInfoParse,"$.values[0].text").toString());}
							}
						}
					}
				}catch(org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		
		// Spouse
		if(propertyArray.contains(neededProperties.get(7))){
			spouseFlag=true;
			JSONArray spouseValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(7))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<spouseValue.size();i++){
				try {
					JSONObject spouseValueParse = (JSONObject)parser.parse(spouseValue.get(i).toString());
					String propertyStr = JsonPath.read(spouseValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/people/marriage/")){
							// Spouse name
							if(o.toString().equals("/people/marriage/spouse")){
								String nameInfo = JsonPath.read(spouseValueParse,"$.property['/people/marriage/spouse']").toString();
							    JSONObject nameInfoParse = (JSONObject)parser.parse(nameInfo);
							    if (JsonPath.read(nameInfoParse,"$.values").toString().equals("[]")){
							    }else{
							    	spouseName.add(JsonPath.read(nameInfoParse,"$.values[0].text").toString());}}
							// Marriage from date
							if(o.toString().equals("/people/marriage/from")){
								 String fromDateInfo = JsonPath.read(spouseValueParse,"$.property['/people/marriage/from']").toString();
								    JSONObject fromDateInfoParse = (JSONObject)parser.parse(fromDateInfo);
								    if (JsonPath.read(fromDateInfoParse,"$.values").toString().equals("[]")){
								    }else{
							    		spouseFrom.add(JsonPath.read(fromDateInfoParse,"$.values[0].text").toString());}}
							// Marriage to date
							if(o.toString().equals("/people/marriage/to")){
								String toDateInfo = JsonPath.read(spouseValueParse,"$.property['/people/marriage/to']").toString();
							    JSONObject toDateParse = (JSONObject)parser.parse(toDateInfo);
							    if (JsonPath.read(toDateParse,"$.values").toString().equals("[]")){
							    	spouseTo.add("now");
							    }else{
						    		spouseTo.add(JsonPath.read(toDateParse,"$.values[0].text").toString());}}
							// Marriage location
							if(o.toString().equals("/people/marriage/location_of_ceremony")){
								String locationInfo = JsonPath.read(spouseValueParse,"$.property['/people/marriage/location_of_ceremony']").toString();
							    JSONObject locationInfoParse = (JSONObject)parser.parse(locationInfo);
							    if (JsonPath.read(locationInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		marriagePlace.add(JsonPath.read(locationInfoParse,"$.values[0].text").toString());}}
						}
					}
					//Make sure all four arraylists have same sizes, if not, just add " "
					if((spouseName.size()==spouseFrom.size())&&(spouseName.size()==spouseTo.size())&&(spouseName.size()==marriagePlace.size())){
						continue;
					}
					else{
						int correctLen = Math.max(Math.max(Math.max(spouseName.size(),spouseFrom.size()),spouseTo.size()),marriagePlace.size());
						if(spouseName.size()<correctLen){spouseName.add(" ");}
						if(spouseFrom.size()<correctLen){spouseFrom.add(" ");}
						if(spouseTo.size()<correctLen){spouseTo.add(" ");}
						if(marriagePlace.size()<correctLen){marriagePlace.add(" ");}
					}
				} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		//Description
		if(propertyArray.contains(neededProperties.get(8))){
			descriptionFlag=true;
			JSONArray description = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(8))),"$.values");
			Description=JsonPath.read(description.get(0),"$.value").toString();}
		
		// Info Box
		String longestTitle="| Place of birth: ";
		//HeadLine
		StringBuffer headline=new StringBuffer();
		headline.append(Name);
		System.out.println(divideBar);
		// Print headline information
		if(entityType.size()==1&&entityType.get(0).equals("League")){
			headline.append("(LEAGUE)");}
		else if(entityType.size()==1&&entityType.get(0).equals("SportsTeam")){
			headline.append("(SPORTS TEAM)");}
		else if(entityType.size()==1&&entityType.get(0).equals("Person")){
			headline.append("(PERSON)");}
		else if(entityType.size()>1&&entityType.get(0).equals("Person")){
			headline.append("(");
			for (int i=1;i<entityType.size();i++){
				if(i<entityType.size()-1){
					headline.append(entityType.get(i).toUpperCase()+", ");
				}else{
					headline.append(entityType.get(i).toUpperCase()+")");}
			}
		}
		int headLinelen = (divideBar.length()-2-headline.length())/2;
		StringBuffer space=new StringBuffer();
		for(int i=0;i<headLinelen;i++){
			space.append(" ");
		}
		headline.insert(0, "|"+space.toString());
		headline.append(space.toString()+"|");
		System.out.println(headline.toString());
		System.out.println(divideBar);
		// Print Name
		if(nameFlag){
			StringBuffer NameBuffer=new StringBuffer();
			NameBuffer.append("| Name:");
			int NameSpaceFront=longestTitle.length()-NameBuffer.length();
			for(int i=0;i<NameSpaceFront;i++){
				NameBuffer.append(" ");
			}
			NameBuffer.append(Name);
			int NameSpaceEnd=divideBar.length()-NameBuffer.length()-1;
			for(int i=0;i<NameSpaceEnd;i++){
				NameBuffer.append(" ");
			}
			NameBuffer.append("|");
			System.out.println(NameBuffer.toString());
			System.out.println(divideBar);}
		// Print Birthday
		if(birthdayFlag){
			StringBuffer birthdayBuffer=new StringBuffer();
			birthdayBuffer.append("| Birthday:");
			int birthdaySpaceFront=longestTitle.length()-birthdayBuffer.length();
			for(int i=0;i<birthdaySpaceFront;i++){
				birthdayBuffer.append(" ");
			}
			birthdayBuffer.append(Birthday);
			int birthdaySpaceEnd=divideBar.length()-birthdayBuffer.length()-1;
			for(int i=0;i<birthdaySpaceEnd;i++){
				birthdayBuffer.append(" ");
			}
			birthdayBuffer.append("|");
			System.out.println(birthdayBuffer.toString());
			System.out.println(divideBar);}
		//Print Death
		if(placeOfDeathFlag||dateOfDeathFlag||causeOfDeathFlag){
			StringBuffer deathBuffer=new StringBuffer();
			deathBuffer.append("| Death:");
			int deathSpaceFront=longestTitle.length()-deathBuffer.length();
			for(int i=0;i<deathSpaceFront;i++){
				deathBuffer.append(" ");}
			if(dateOfDeathFlag){
				deathBuffer.append(DateOfDeath+" ");}
			if(placeOfDeathFlag){
				deathBuffer.append("at "+PlaceOfDeath+" ");}
			if(causeOfDeathFlag){
				deathBuffer.append("cause: ("+CauseOfDeath+")");}
			int deathSpaceEnd=divideBar.length()-deathBuffer.length()-1;
			for(int i=0;i<deathSpaceEnd;i++){
				deathBuffer.append(" ");}
			deathBuffer.append("|");
			System.out.println(deathBuffer.toString());
			System.out.println(divideBar);
		}
		//PPrint lace of birth
		if(placeOfBirthFlag){
			StringBuffer placeOfBirthBuffer=new StringBuffer();
			placeOfBirthBuffer.append("| Place of birth:");
			int POBSpaceFront=longestTitle.length()-placeOfBirthBuffer.length();
			for(int i=0;i<POBSpaceFront;i++){
				placeOfBirthBuffer.append(" ");}
			placeOfBirthBuffer.append(PlaceOfBirth);
			int POBSpaceEnd=divideBar.length()-placeOfBirthBuffer.length()-1;
			for(int i=0;i<POBSpaceEnd;i++){
				placeOfBirthBuffer.append(" ");}
			placeOfBirthBuffer.append("|");
			System.out.println(placeOfBirthBuffer.toString());
			System.out.println(divideBar);}
		//Print Description
		if(descriptionFlag){
			Description = Description.replaceAll("(\\r|\\n)", "");
			StringBuffer descriptionBuffer=new StringBuffer();
			descriptionBuffer.append("| Description:");
			int descriptionSpaceFront=longestTitle.length()-descriptionBuffer.length();
			for(int i=0;i<descriptionSpaceFront;i++){
				descriptionBuffer.append(" ");}
			int index=0;
			int textSpace=0;
			int realLeft=0;
			textSpace = divideBar.length()-descriptionBuffer.length()-1;
			descriptionBuffer.append(Description.substring(index, index+textSpace));
			index+=textSpace;
			descriptionBuffer.append("|");
			System.out.println(descriptionBuffer.toString());
			while(index<Description.length()){
				StringBuffer rowBuffer=new StringBuffer();
				rowBuffer.append("|");
				for(int i=0;i<longestTitle.length()-1;i++){
					rowBuffer.append(" ");}
				realLeft=Description.length()-index-1;
				if(realLeft>=textSpace){
					rowBuffer.append(Description.substring(index, index+textSpace));
					index+=textSpace;
					rowBuffer.append("|");
					System.out.println(rowBuffer.toString());
				}
				else if (realLeft<textSpace){
					rowBuffer.append(Description.substring(index,index+realLeft));
					int tmpLeft=divideBar.length()-rowBuffer.length()-1;
					for(int i=0;i<tmpLeft;i++){
						rowBuffer.append(" ");}
					rowBuffer.append("|");
					System.out.println(rowBuffer.toString());
					System.out.println(divideBar);
					break;
				}
			}
		}
		//Print Sibling
		if(siblingFlag){
			for(int i=0;i<siblings.size();i++){
				if(i==0){
					StringBuffer sibBuffer=new StringBuffer();
					sibBuffer.append("| Siblings:");
					int sibFront=longestTitle.length()-sibBuffer.length();
					for(int j=0;j<sibFront;j++){
						sibBuffer.append(" ");}
					sibBuffer.append(siblings.get(i));
					int sibEnd=divideBar.length()-sibBuffer.length()-1;
					for(int k=0;k<sibEnd;k++){
						sibBuffer.append(" ");}
					sibBuffer.append("|");
					System.out.println(sibBuffer.toString());
				}
				else{
					StringBuffer sibBuffer=new StringBuffer();
					sibBuffer.append("|");
					int sibFront=longestTitle.length()-sibBuffer.length();
					for(int j=0;j<sibFront;j++){
						sibBuffer.append(" ");}
					sibBuffer.append(siblings.get(i));
					int sibEnd=divideBar.length()-sibBuffer.length()-1;
					for(int k=0;k<sibEnd;k++){
						sibBuffer.append(" ");}
					sibBuffer.append("|");
					System.out.println(sibBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
		// Print Spouse
		if(spouseFlag){
			int len=0;
			StringBuffer FirstBuffer=new StringBuffer();
			FirstBuffer.append("| Spouse:");
			int firstSpaceFront=longestTitle.length()-FirstBuffer.length();
			for(int j=0;j<firstSpaceFront;j++){
				FirstBuffer.append(" ");}
			len=(divideBar.length()-FirstBuffer.length())/3;
			FirstBuffer.append("|Name");
			for(int k=0;k<len-("|Name").length();k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|Marriage Place");
			for(int k=0;k<len-("|Marriage Place").length();k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|From/To");
			int tmp=divideBar.length()-FirstBuffer.length();
			for(int k=0;k<tmp-1;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|");
			System.out.println(FirstBuffer.toString());
			StringBuffer tmpDivider=new StringBuffer();
			tmpDivider.append("|");
			for(int k=0;k<longestTitle.length()-1;k++){
				tmpDivider.append(" ");}
			tmp=divideBar.length()-tmpDivider.length()-1;
			for(int k=0;k<tmp;k++){
				tmpDivider.append("-");}
			tmpDivider.append("|");
			System.out.println(tmpDivider.toString());
			for(int i=0;i<spouseName.size();i++){
				StringBuffer infoBuffer=new StringBuffer();
				infoBuffer.append("|");
				for (int j=0;j<longestTitle.length()-1;j++){
					infoBuffer.append(" ");
				}
				infoBuffer.append("|"+spouseName.get(i));
				int tmpNum=("|"+spouseName.get(i)).length();
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				infoBuffer.append("|"+marriagePlace.get(i));
				tmpNum=("|"+marriagePlace.get(i)).length();
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				infoBuffer.append("|"+spouseFrom.get(i)+" / "+spouseTo.get(i));
				tmpNum=divideBar.length()-infoBuffer.length()-1;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				infoBuffer.append("|");
				System.out.println(infoBuffer.toString());
			}
			System.out.println(divideBar);
		}
		/*********************** 
		 * Contain other types *
		 ***********************/
		if(entityType.size()>1){
			entityType.remove("Person");
			for(int i=0;i<entityType.size();i++){
				String type =entityType.get(i);
				if(type.equals("Author")){
					ArrayList<String> tmpEntityType = new ArrayList<String>();
					tmpEntityType.add(type);
					PrintAuthorInfo(tmpEntityType,TypeOfInterestTable);
				}
				else if(type.equals("Actor")){
					ArrayList<String> tmpEntityType = new ArrayList<String>();
					tmpEntityType.add(type);
					PrintActorInfo(tmpEntityType,TypeOfInterestTable);
				}
				else if(type.equals("BusinessPerson")){
					ArrayList<String> tmpEntityType = new ArrayList<String>();
					tmpEntityType.add(type);
					PrintBusinessPersonInfo(tmpEntityType,TypeOfInterestTable);
				}
			}
		}
	}
	
	/************************
	   * "Author"
	************************/
	public void PrintAuthorInfo(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable){
		ArrayList<String> book=new ArrayList<String>();
		ArrayList<String> bookAbout=new ArrayList<String>();
		ArrayList<String> influence=new ArrayList<String>();
		ArrayList<String> influenceBy=new ArrayList<String>();
		boolean bookFlag=false,bookAboutFlag=false,influenceFlag=false,influenceByFlag=false;
		
		ArrayList<String> neededProperties = GetNeededProperties(entityType,TypeOfInterestTable);
		
		// Books
		if(propertyArray.contains(neededProperties.get(0))){
			bookFlag=true;
			JSONArray bookValues = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(0))),"$.values");
			for (int i=0;i<bookValues.size();i++){
					book.add(JsonPath.read(bookValues.get(i),"$.text").toString());}
		}
		// Book about author
		if(propertyArray.contains(neededProperties.get(1))){
			bookAboutFlag=true;
			JSONArray bookAboutValues = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(1))),"$.values");
			for (int i=0;i<bookAboutValues.size();i++){
				bookAbout.add(JsonPath.read(bookAboutValues.get(i),"$.text").toString());}
		}
		// Influenced people
		if(propertyArray.contains(neededProperties.get(2))){
			influenceFlag=true;
			JSONArray influencedValues = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(2))),"$.values");
			for (int i=0;i<influencedValues.size();i++){
				influence.add(JsonPath.read(influencedValues.get(i),"$.text").toString());}
		}
		// Influenced_by
		if(propertyArray.contains(neededProperties.get(3))){
			influenceByFlag=true;
			JSONArray influencedByValues = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(3))),"$.values");
			for (int i=0;i<influencedByValues.size();i++){
				influenceBy.add(JsonPath.read(influencedByValues.get(i),"$.text").toString());}
		}
		//Info Box
		String longestTitle="| Place of birth: ";
		//Print books
		if(bookFlag){
			for(int i=0;i<book.size();i++){
				if(i==0){
					StringBuffer bookBuffer=new StringBuffer();
					bookBuffer.append("| Books:");
					int bookFront=longestTitle.length()-bookBuffer.length();
					for(int j=0;j<bookFront;j++){
						bookBuffer.append(" ");}
					bookBuffer.append(book.get(i));
					int bookEnd=divideBar.length()-bookBuffer.length()-1;
					for(int k=0;k<bookEnd;k++){
						bookBuffer.append(" ");}
					bookBuffer.append("|");
					System.out.println(bookBuffer.toString());
				}
				else{
					StringBuffer bookBuffer=new StringBuffer();
					bookBuffer.append("|");
					int bookFront=longestTitle.length()-bookBuffer.length();
					for(int j=0;j<bookFront;j++){
						bookBuffer.append(" ");}
					bookBuffer.append(book.get(i));
					int bookEnd=divideBar.length()-bookBuffer.length()-1;
					for(int k=0;k<bookEnd;k++){
						bookBuffer.append(" ");}
					bookBuffer.append("|");
					System.out.println(bookBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
		//Print books about author
		if(bookAboutFlag){
			for(int i=0;i<bookAbout.size();i++){
				if(i==0){
					StringBuffer bookAboutBuffer=new StringBuffer();
					bookAboutBuffer.append("| Books about:");
					int bookAboutFront=longestTitle.length()-bookAboutBuffer.length();
					for(int j=0;j<bookAboutFront;j++){
						bookAboutBuffer.append(" ");}
					bookAboutBuffer.append(bookAbout.get(i));
					int bookAboutEnd=divideBar.length()-bookAboutBuffer.length()-1;
					for(int k=0;k<bookAboutEnd;k++){
						bookAboutBuffer.append(" ");}
					bookAboutBuffer.append("|");
					System.out.println(bookAboutBuffer.toString());
				}
				else{
					StringBuffer bookAboutBuffer=new StringBuffer();
					bookAboutBuffer.append("|");
					int bookAboutFront=longestTitle.length()-bookAboutBuffer.length();
					for(int j=0;j<bookAboutFront;j++){
						bookAboutBuffer.append(" ");}
					bookAboutBuffer.append(bookAbout.get(i));
					int bookAboutEnd=divideBar.length()-bookAboutBuffer.length()-1;
					for(int k=0;k<bookAboutEnd;k++){
						bookAboutBuffer.append(" ");}
					bookAboutBuffer.append("|");
					System.out.println(bookAboutBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
		//Print influenced person
		if(influenceFlag){
			for(int i=0;i<influence.size();i++){
				if(i==0){
					StringBuffer infuenceBuffer=new StringBuffer();
					infuenceBuffer.append("| Influenced:");
					int Front=longestTitle.length()-infuenceBuffer.length();
					for(int j=0;j<Front;j++){
						infuenceBuffer.append(" ");}
					infuenceBuffer.append(influence.get(i));
					int End=divideBar.length()-infuenceBuffer.length()-1;
					for(int k=0;k<End;k++){
						infuenceBuffer.append(" ");}
					infuenceBuffer.append("|");
					System.out.println(infuenceBuffer.toString());
				}
				else{
					StringBuffer infuenceBuffer=new StringBuffer();
					infuenceBuffer.append("|");
					int Front=longestTitle.length()-infuenceBuffer.length();
					for(int j=0;j<Front;j++){
						infuenceBuffer.append(" ");}
					infuenceBuffer.append(influence.get(i));
					int End=divideBar.length()-infuenceBuffer.length()-1;
					for(int k=0;k<End;k++){
						infuenceBuffer.append(" ");}
					infuenceBuffer.append("|");
					System.out.println(infuenceBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
		//Print influenced by
		if(influenceByFlag){
			for(int i=0;i<influenceBy.size();i++){
				if(i==0){
					StringBuffer infuenceByBuffer=new StringBuffer();
					infuenceByBuffer.append("| Influenced by:");
					int Front=longestTitle.length()-infuenceByBuffer.length();
					for(int j=0;j<Front;j++){
						infuenceByBuffer.append(" ");}
					infuenceByBuffer.append(influenceBy.get(i));
					int End=divideBar.length()-infuenceByBuffer.length()-1;
					for(int k=0;k<End;k++){
						infuenceByBuffer.append(" ");}
					infuenceByBuffer.append("|");
					System.out.println(infuenceByBuffer.toString());
				}
				else{
					StringBuffer infuenceByBuffer=new StringBuffer();
					infuenceByBuffer.append("|");
					int Front=longestTitle.length()-infuenceByBuffer.length();
					for(int j=0;j<Front;j++){
						infuenceByBuffer.append(" ");}
					infuenceByBuffer.append(influenceBy.get(i));
					int End=divideBar.length()-infuenceByBuffer.length()-1;
					for(int k=0;k<End;k++){
						infuenceByBuffer.append(" ");}
					infuenceByBuffer.append("|");
					System.out.println(infuenceByBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
	}
	
	/************************
	   * "Actor"
	************************/
	public void PrintActorInfo(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable){
		ArrayList<String> film=new ArrayList<String>();
		ArrayList<String> character=new ArrayList<String>();
		boolean filmFlag=false;
		
		ArrayList<String> neededProperties = GetNeededProperties(entityType,TypeOfInterestTable);
		//FilmsParticipated(Film Name, Character)
		if(propertyArray.contains(neededProperties.get(0))){
			filmFlag=true;
			JSONArray filmInfoValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(0))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<filmInfoValue.size();i++){
				try{
					JSONObject filmInfoValueParse = (JSONObject)parser.parse(filmInfoValue.get(i).toString());
					String propertyStr = JsonPath.read(filmInfoValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/film/performance/")){
							// film
							if(o.toString().equals("/film/performance/film")){
								String filmInfo = JsonPath.read(filmInfoValueParse,"$.property['/film/performance/film']").toString();
								JSONObject filmInfoParse = (JSONObject)parser.parse(filmInfo);
							    if (JsonPath.read(filmInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		film.add(JsonPath.read(filmInfoParse,"$.values[0].text").toString());}
							}
							// character
							if(o.toString().equals("/film/performance/character")){
								String filmCharacterInfo = JsonPath.read(filmInfoValueParse,"$.property['/film/performance/character']").toString();
								JSONObject filmCharacterInfoParse = (JSONObject)parser.parse(filmCharacterInfo);
							    if (JsonPath.read(filmCharacterInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		character.add(JsonPath.read(filmCharacterInfoParse,"$.values[0].text").toString());}
							}
						}
					}
					// Make sure all arraylists have same size
					if(film.size()==character.size()){
						continue;
					}
					else{
						int correctLen = Math.max(film.size(),character.size());
						if(film.size()<correctLen){film.add(" ");}
						if(character.size()<correctLen){character.add(" ");}
					}
				}catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		//Info Box
		String longestTitle="| Place of birth: ";
		//Print Film
		if(filmFlag){
			int len=0;
			StringBuffer FirstBuffer=new StringBuffer();
			FirstBuffer.append("| Films:");
			int firstSpaceFront=longestTitle.length()-FirstBuffer.length();
			for(int j=0;j<firstSpaceFront;j++){
				FirstBuffer.append(" ");}
			len=(divideBar.length()-FirstBuffer.length())/2;
			FirstBuffer.append("|Character");
			for(int k=0;k<len-("Character").length();k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|Film Name");
			int tmp=divideBar.length()-FirstBuffer.length();
			for(int k=0;k<tmp-1;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|");
			System.out.println(FirstBuffer.toString());
			StringBuffer tmpDivider=new StringBuffer();
			tmpDivider.append("|");
			for(int k=0;k<longestTitle.length()-1;k++){
				tmpDivider.append(" ");}
			tmp=divideBar.length()-tmpDivider.length()-1;
			for(int k=0;k<tmp;k++){
				tmpDivider.append("-");}
			tmpDivider.append("|");
			System.out.println(tmpDivider.toString());
			for(int i=0;i<film.size();i++){
				StringBuffer infoBuffer=new StringBuffer();
				infoBuffer.append("|");
				for (int j=0;j<longestTitle.length()-1;j++){
					infoBuffer.append(" ");}
				infoBuffer.append("|"+character.get(i));
				int tmpNum=("|"+character.get(i)).length();
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				infoBuffer.append("|"+film.get(i));
					tmpNum=divideBar.length()-infoBuffer.length()-1;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				infoBuffer.append("|");
				System.out.println(infoBuffer.toString());
			}
			System.out.println(divideBar);
		}
	}
	
	/************************
	   * "BusinessPerson"
	************************/
	public void PrintBusinessPersonInfo(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable){
		ArrayList<String> lfrom=new ArrayList<String>();
		ArrayList<String> lto=new ArrayList<String>();
		ArrayList<String> lrole=new ArrayList<String>();
		ArrayList<String> ltitle=new ArrayList<String>();
		ArrayList<String> lorg=new ArrayList<String>();
		ArrayList<String> bfrom=new ArrayList<String>();
		ArrayList<String> bto=new ArrayList<String>();
		ArrayList<String> brole=new ArrayList<String>();
		ArrayList<String> btitle=new ArrayList<String>();
		ArrayList<String> borg=new ArrayList<String>();
		ArrayList<String> foundedOrg=new ArrayList<String>();
		boolean leadershipFlag=false,boardMemberFlag=false,foundedOrgFlag=false;
		
		ArrayList<String> neededProperties = GetNeededProperties(entityType,TypeOfInterestTable);
		
		// Leadership (From, To, Organization, Role, Title)
		if(propertyArray.contains(neededProperties.get(0))){
			leadershipFlag=true;
			JSONArray leadershipValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(0))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<leadershipValue.size();i++){
				try {
					JSONObject leadershipValueParse = (JSONObject)parser.parse(leadershipValue.get(i).toString());
					String propertyStr = JsonPath.read(leadershipValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/organization/leadership/")){
							// Leadership From
							if(o.toString().equals("/organization/leadership/from")){
								String fromInfo = JsonPath.read(leadershipValueParse,"$.property['/organization/leadership/from']").toString();
							    JSONObject fromInfoParse = (JSONObject)parser.parse(fromInfo);
							    if (JsonPath.read(fromInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		lfrom.add(JsonPath.read(fromInfoParse,"$.values[0].text").toString());}}
							// Leadership To
							if(o.toString().equals("/organization/leadership/to")){
								String toInfo = JsonPath.read(leadershipValueParse,"$.property['/organization/leadership/to']").toString();
							    JSONObject toInfoParse = (JSONObject)parser.parse(toInfo);
							    if (JsonPath.read(toInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		lto.add(JsonPath.read(toInfoParse,"$.values[0].text").toString());}}
							// Leadership organization
							if(o.toString().equals("/organization/leadership/organization")){
								 String orgInfo = JsonPath.read(leadershipValueParse,"$.property['/organization/leadership/organization']").toString();
								 JSONObject orgInfoParse = (JSONObject)parser.parse(orgInfo);
								 if (JsonPath.read(orgInfoParse,"$.values").toString().equals("[]")){
								 }else{
							    	lorg.add(JsonPath.read(orgInfoParse,"$.values[0].text").toString());}}
							// Leadership Role
							if(o.toString().equals("/organization/leadership/role")){
								String roleInfo = JsonPath.read(leadershipValueParse,"$.property['/organization/leadership/role']").toString();
							    JSONObject roleInfoParse = (JSONObject)parser.parse(roleInfo);
							    if (JsonPath.read(roleInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		lrole.add(JsonPath.read(roleInfoParse,"$.values[0].text").toString());}}
							//Leadership Title
							if(o.toString().equals("/organization/leadership/title")){
								String titleInfo = JsonPath.read(leadershipValueParse,"$.property['/organization/leadership/title']").toString();
							    JSONObject titleInfoParse = (JSONObject)parser.parse(titleInfo);
							    if (JsonPath.read(titleInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		ltitle.add(JsonPath.read(titleInfoParse,"$.values[0].text").toString());}}
						}
					}
					// Make sure all arraylists have same size
					if((lfrom.size()==lto.size())&&(lfrom.size()==lrole.size())&&(lfrom.size()==ltitle.size())&&(lfrom.size()==lorg.size())){
						continue;
					}
					else{
						int correctLen = Math.max(Math.max(Math.max(Math.max(lfrom.size(),lto.size()),ltitle.size()),lorg.size()),lrole.size());
						if(lfrom.size()<correctLen){lfrom.add(" ");}
						if(lto.size()<correctLen){lto.add(" ");}
						if(ltitle.size()<correctLen){ltitle.add(" ");}
						if(lorg.size()<correctLen){lorg.add(" ");}
						if(lrole.size()<correctLen){lrole.add(" ");}
					}
				} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		//BoardMember(From, To, Organization, Role, Title)
		if(propertyArray.contains(neededProperties.get(1))){
			boardMemberFlag=true;
			JSONArray boardMemberValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(1))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<boardMemberValue.size();i++){
				try {
					JSONObject boardMemberValueParse = (JSONObject)parser.parse(boardMemberValue.get(i).toString());
					String propertyStr = JsonPath.read(boardMemberValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/organization/organization_board_membership/", 0)){
							// board member From
							if(o.toString().equals("/organization/organization_board_membership/from")){
								String fromInfo = JsonPath.read(boardMemberValueParse,"$.property['/organization/organization_board_membership/from']").toString();
							    JSONObject fromInfoParse = (JSONObject)parser.parse(fromInfo);
							    if (JsonPath.read(fromInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		bfrom.add(JsonPath.read(fromInfoParse,"$.values[0].text").toString());}}
							// board member To
							if(o.toString().equals("/organization/organization_board_membership/to")){
								String toInfo = JsonPath.read(boardMemberValueParse,"$.property['/organization/organization_board_membership/to']").toString();
							    JSONObject toInfoParse = (JSONObject)parser.parse(toInfo);
							    if (JsonPath.read(toInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		bto.add(JsonPath.read(toInfoParse,"$.values[0].text").toString());}}
							// board member organization
							if(o.toString().equals("/organization/organization_board_membership/organization")){
								 String orgInfo = JsonPath.read(boardMemberValueParse,"$.property['/organization/organization_board_membership/organization']").toString();
								 JSONObject orgInfoParse = (JSONObject)parser.parse(orgInfo);
								 if (JsonPath.read(orgInfoParse,"$.values").toString().equals("[]")){
								 }else{
							    	borg.add(JsonPath.read(orgInfoParse,"$.values[0].text").toString());}}
							// board member Role
							if(o.toString().equals("/organization/organization_board_membership/role")){
								String roleInfo = JsonPath.read(boardMemberValueParse,"$.property['/organization/organization_board_membership/role']").toString();
							    JSONObject roleInfoParse = (JSONObject)parser.parse(roleInfo);
							    if (JsonPath.read(roleInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		brole.add(JsonPath.read(roleInfoParse,"$.values[0].text").toString());}}
							//board member Title
							if(o.toString().equals("/organization/organization_board_membership/title")){
								String titleInfo = JsonPath.read(boardMemberValueParse,"$.property['/organization/organization_board_membership/title']").toString();
							    JSONObject titleInfoParse = (JSONObject)parser.parse(titleInfo);
							    if (JsonPath.read(titleInfoParse,"$.values").toString().equals("[]")){
							    }else{
						    		btitle.add(JsonPath.read(titleInfoParse,"$.values[0].text").toString());}}
						}
					}
					// Make sure all arraylists have same size
					if((bfrom.size()==bto.size())&&(bfrom.size()==brole.size())&&(bfrom.size()==btitle.size())&&(bfrom.size()==borg.size())){
						continue;
					}
					else{
						int correctLen = Math.max(Math.max(Math.max(Math.max(bfrom.size(),bto.size()),btitle.size()),borg.size()),brole.size());
						if(bfrom.size()<correctLen){bfrom.add(" ");}
						if(bto.size()<correctLen){bto.add(" ");}
						if(btitle.size()<correctLen){btitle.add(" ");}
						if(borg.size()<correctLen){borg.add(" ");}
						if(brole.size()<correctLen){brole.add(" ");}
					}
				} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		//Founded(OrganizationName)
		if(propertyArray.contains(neededProperties.get(2))){
			foundedOrgFlag=true;
			JSONArray foundedValues = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(2))),"$.values");
			for (int i=0;i<foundedValues.size();i++){
				foundedOrg.add(JsonPath.read(foundedValues.get(i),"$.text").toString());}
		}
		
		//InfoBox
		String longestTitle="| Place of birth: ";
		//Print Founded
		if(foundedOrgFlag){
			for(int i=0;i<foundedOrg.size();i++){
				if(i==0){
					StringBuffer FirstBuffer=new StringBuffer();
					FirstBuffer.append("| Founded:");
					int Front=longestTitle.length()-FirstBuffer.length();
					for(int j=0;j<Front;j++){
						FirstBuffer.append(" ");}
					FirstBuffer.append(foundedOrg.get(i));
					int End=divideBar.length()-FirstBuffer.length()-1;
					for(int k=0;k<End;k++){
						FirstBuffer.append(" ");}
					FirstBuffer.append("|");
					System.out.println(FirstBuffer.toString());
				}
				else{
					StringBuffer foundBuffer=new StringBuffer();
					foundBuffer.append("|");
					int Front=longestTitle.length()-foundBuffer.length();
					for(int j=0;j<Front;j++){
						foundBuffer.append(" ");}
					foundBuffer.append(foundedOrg.get(i));
					int End=divideBar.length()-foundBuffer.length()-1;
					for(int k=0;k<End;k++){
						foundBuffer.append(" ");}
					foundBuffer.append("|");
					System.out.println(foundBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
		//Print Leadership
		if(leadershipFlag){
			int len=0;
			int tmp=0;
			StringBuffer FirstBuffer=new StringBuffer();
			FirstBuffer.append("| Leadership:");
			int firstSpaceFront=longestTitle.length()-FirstBuffer.length();
			for(int j=0;j<firstSpaceFront;j++){
				FirstBuffer.append(" ");}
			
			len=(divideBar.length()-longestTitle.length())/4;
			
			FirstBuffer.append("|Organization");
			tmp=len-("|Organization").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			
			FirstBuffer.append("|Role");
			tmp=len-("|Role").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			
			FirstBuffer.append("|Title");
			tmp=len-("|Title").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|From/To");
			tmp=divideBar.length()-FirstBuffer.length();
			for(int k=0;k<tmp-1;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|");
			System.out.println(FirstBuffer.toString());
			StringBuffer tmpDivider=new StringBuffer();
			tmpDivider.append("|");
			for(int k=0;k<longestTitle.length()-1;k++){
				tmpDivider.append(" ");}
			tmp=divideBar.length()-tmpDivider.length()-1;
			for(int k=0;k<tmp;k++){
				tmpDivider.append("-");}
			tmpDivider.append("|");
			System.out.println(tmpDivider.toString());
			
			// Print leadership
			for(int i=0;i<lorg.size();i++){
				int tmpNum=0;
				StringBuffer infoBuffer=new StringBuffer();
				infoBuffer.append("|");
				for (int j=0;j<longestTitle.length()-1;j++){
					infoBuffer.append(" ");
				}		
				//Print Org
				String strName="|"+lorg.get(i);
				if(strName.length()>len){
					infoBuffer.append((strName).substring(0, len));
					tmpNum=len;}
				else if (strName.length()<=len){
					infoBuffer.append(strName);
					tmpNum=(strName).length();}
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				//Print Role
				String strPosition="|"+lrole.get(i);
				if(strPosition.length()>len){
					infoBuffer.append(strPosition.substring(0, len));
					tmpNum=len;}
				else if(strPosition.length()<=len){
					infoBuffer.append(strPosition);
					tmpNum=strPosition.length();}		
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				//Print Title
				String strNumber ="|"+ltitle.get(i);
				if(strNumber.length()>len){
					infoBuffer.append(strNumber.substring(0, len));
					tmpNum=len;}
				else if(strNumber.length()<=len){
					infoBuffer.append(strNumber);
					tmpNum=strNumber.length();}	
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				//Print From/To
				String strFromTo="|"+lfrom.get(i)+" / "+lto.get(i);
				int restLen=divideBar.length()-infoBuffer.length()-1;
				if(strFromTo.length()>restLen){
					infoBuffer.append(strFromTo.substring(0, restLen));
				}
				else{
					infoBuffer.append(strFromTo);
				}
				tmpNum=divideBar.length()-infoBuffer.length()-1;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				infoBuffer.append("|");
				System.out.println(infoBuffer.toString());
			}
			System.out.println(divideBar);
		}
		//Print Board Member
		if(boardMemberFlag){
			int len=0;
			int tmp=0;
			StringBuffer FirstBuffer=new StringBuffer();
			FirstBuffer.append("| Board Member:");
			int firstSpaceFront=longestTitle.length()-FirstBuffer.length();
			for(int j=0;j<firstSpaceFront;j++){
				FirstBuffer.append(" ");}
			
			len=(divideBar.length()-longestTitle.length())/4;
			
			FirstBuffer.append("|Organization");
			tmp=len-("|Organization").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			
			FirstBuffer.append("|Role");
			tmp=len-("|Role").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			
			FirstBuffer.append("|Title");
			tmp=len-("|Title").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|From/To");
			tmp=divideBar.length()-FirstBuffer.length();
			for(int k=0;k<tmp-1;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|");
			System.out.println(FirstBuffer.toString());
			StringBuffer tmpDivider=new StringBuffer();
			tmpDivider.append("|");
			for(int k=0;k<longestTitle.length()-1;k++){
				tmpDivider.append(" ");}
			tmp=divideBar.length()-tmpDivider.length()-1;
			for(int k=0;k<tmp;k++){
				tmpDivider.append("-");}
			tmpDivider.append("|");
			System.out.println(tmpDivider.toString());
			
			for(int i=0;i<borg.size();i++){
				int tmpNum=0;
				StringBuffer infoBuffer=new StringBuffer();
				infoBuffer.append("|");
				for (int j=0;j<longestTitle.length()-1;j++){
					infoBuffer.append(" ");
				}		
				//Print Org
				String strName="|"+borg.get(i);
				if(strName.length()>len){
					infoBuffer.append((strName).substring(0, len));
					tmpNum=len;}
				else if (strName.length()<=len){
					infoBuffer.append(strName);
					tmpNum=(strName).length();}
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				//Print Role
				String strPosition="|"+brole.get(i);
				if(strPosition.length()>len){
					infoBuffer.append(strPosition.substring(0, len));
					tmpNum=len;}
				else if(strPosition.length()<=len){
					infoBuffer.append(strPosition);
					tmpNum=strPosition.length();}		
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				//Print Title
				String strNumber ="|"+btitle.get(i);
				if(strNumber.length()>len){
					infoBuffer.append(strNumber.substring(0, len));
					tmpNum=len;}
				else if(strNumber.length()<=len){
					infoBuffer.append(strNumber);
					tmpNum=strNumber.length();}	
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				//Print From/To
				String strFromTo="|"+bfrom.get(i)+" / "+bto.get(i);
				int restLen=divideBar.length()-infoBuffer.length()-1;
				if(strFromTo.length()>restLen){
					infoBuffer.append(strFromTo.substring(0, restLen));
				}
				else{
					infoBuffer.append(strFromTo);
				}
				tmpNum=divideBar.length()-infoBuffer.length()-1;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");
				}
				infoBuffer.append("|");
				System.out.println(infoBuffer.toString());
			}
			System.out.println(divideBar);
		}
	}
	
	/**********
	* "League" 
	***********/
	public void PrintLeagueInfo(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable){
		String Name="",Sport="",Slogan="",OfficialWebsite="",Description="";
		ArrayList<String> Teams=new ArrayList<String>();
		ArrayList<String> Championships=new ArrayList<String>();
		boolean nameFlag=false,sportFlag=false,sloganFlag=false,officialwebsiteFlag=false,championshipFlag=false,teamFlag=false,descriptionFlag=false;
		ArrayList<String> neededProperties = GetNeededProperties(entityType,TypeOfInterestTable);
		//Name
		if(propertyArray.contains(neededProperties.get(0))){
			nameFlag=true;
			JSONArray name = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(0))),"$.values");
			Name=JsonPath.read(name.get(0),"$.text").toString();}
		//Championship
		if(propertyArray.contains(neededProperties.get(1))){
			championshipFlag=true;
			JSONArray championship = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(1))),"$.values");
			for(int i=0;i<championship.size();i++){
				Championships.add(JsonPath.read(championship.get(i),"$.text").toString());}}
		//Sport
		if(propertyArray.contains(neededProperties.get(2))){
			sportFlag=true;
			JSONArray sport = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(2))),"$.values");
			Sport=JsonPath.read(sport.get(0),"$.text").toString();}
		//Slogan
		if(propertyArray.contains(neededProperties.get(3))){	
			sloganFlag=true;
			JSONArray slogan = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(3))),"$.values");
			Slogan=JsonPath.read(slogan.get(0),"$.text").toString();}
		//Official website
		if(propertyArray.contains(neededProperties.get(4))){
			officialwebsiteFlag=true;
			JSONArray website = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(4))),"$.values");
			OfficialWebsite=JsonPath.read(website.get(0),"$.text").toString();}
		//Description
		if(propertyArray.contains(neededProperties.get(5))){
			descriptionFlag=true;
			JSONArray description = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(5))),"$.values");
			Description=JsonPath.read(description.get(0),"$.value").toString();}
		//Team
		if(propertyArray.contains(neededProperties.get(6))){
			teamFlag=true;
			JSONArray teamValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(6))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<teamValue.size();i++){
				try {
					JSONObject teamValueParse = (JSONObject)parser.parse(teamValue.get(i).toString());
				    //String searchKey = JsonPath.read(teamValueParse,"$.property['/type/object/type'].values[0].id").toString();
				    String propertyStr = JsonPath.read(teamValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if(o.toString().startsWith("/sports/sports_league_participation/",0)){
							Teams.add(JsonPath.read(teamValueParse,"$.property['/sports/sports_league_participation/team'].values[0].text").toString());}
					}
				} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		
		// Info box
		String longestTitle="| Official Website: ";
		//Print HeadLine
		StringBuffer headline=new StringBuffer();
		headline.append(Name);
		System.out.println(divideBar);
		// Print headline
		if(entityType.size()==1&&entityType.get(0).equals("League")){
			headline.append("(LEAGUE)");}
		else if(entityType.size()==1&&entityType.get(0).equals("SportsTeam")){
			headline.append("(SPORTS TEAM)");}
		else if(entityType.size()==1&&entityType.get(0).equals("Person")){
			headline.append("(PERSON)");}
		else if(entityType.size()>1&&entityType.get(0).equals("Person")){
			headline.append("(");
			for (int i=1;i<entityType.size();i++){
				if(i<entityType.size()-1){
					headline.append(entityType.get(i).toUpperCase()+", ");
				}else{
					headline.append(entityType.get(i).toUpperCase()+")");}
			}
		}
		int headLinelen = (divideBar.length()-2-headline.length())/2;
		StringBuffer space=new StringBuffer();
		for(int i=0;i<headLinelen;i++){
			space.append(" ");
		}
		headline.insert(0, "|"+space.toString());
		headline.append(space.toString()+"|");
		System.out.println(headline.toString());
		System.out.println(divideBar);
		//Print Name
		if(nameFlag){
			StringBuffer NameBuffer=new StringBuffer();
			NameBuffer.append("| Name:");
			int NameSpaceFront=longestTitle.length()-NameBuffer.length();
			for(int i=0;i<NameSpaceFront;i++){
				NameBuffer.append(" ");
			}
			NameBuffer.append(Name);
			int NameSpaceEnd=divideBar.length()-NameBuffer.length()-1;
			for(int i=0;i<NameSpaceEnd;i++){
				NameBuffer.append(" ");
			}
			NameBuffer.append("|");
			System.out.println(NameBuffer.toString());
			System.out.println(divideBar);}
		// Print Sport
		if(sportFlag){
			StringBuffer SportBuffer=new StringBuffer();
			SportBuffer.append("| Sport:");
			int SportSpaceFront=longestTitle.length()-SportBuffer.length();
			for(int i=0;i<SportSpaceFront;i++){
				SportBuffer.append(" ");}
			SportBuffer.append(Sport);
			int SportSpaceEnd=divideBar.length()-SportBuffer.length()-1;
			for(int i=0;i<SportSpaceEnd;i++){
				SportBuffer.append(" ");}
			SportBuffer.append("|");
			System.out.println(SportBuffer.toString());
			System.out.println(divideBar);}
		//Print Slogan
		if(sloganFlag){
			StringBuffer sloganBuffer=new StringBuffer();
			sloganBuffer.append("| Slogan:");
			int sloganSpaceFront=longestTitle.length()-sloganBuffer.length();
			for(int i=0;i<sloganSpaceFront;i++){
				sloganBuffer.append(" ");}
			sloganBuffer.append(Slogan);
			int sloganSpaceEnd=divideBar.length()-sloganBuffer.length()-1;
			for(int i=0;i<sloganSpaceEnd;i++){
				sloganBuffer.append(" ");}
			sloganBuffer.append("|");
			System.out.println(sloganBuffer.toString());
			System.out.println(divideBar);}
		//Print Official Website
		if(officialwebsiteFlag){
			StringBuffer officialwebsiteBuffer=new StringBuffer();
			officialwebsiteBuffer.append("| Official Website:");
			int websiteSpaceFront=longestTitle.length()-officialwebsiteBuffer.length();
			for(int i=0;i<websiteSpaceFront;i++){
				officialwebsiteBuffer.append(" ");}
			officialwebsiteBuffer.append(OfficialWebsite);
			int officialwebsiteSpaceEnd=divideBar.length()-officialwebsiteBuffer.length()-1;
			for(int i=0;i<officialwebsiteSpaceEnd;i++){
				officialwebsiteBuffer.append(" ");}
			officialwebsiteBuffer.append("|");
			System.out.println(officialwebsiteBuffer.toString());
			System.out.println(divideBar);}
		//Print Championships
		if(championshipFlag){
			for(int i=0;i<Championships.size();i++){
				if(i==0){
					StringBuffer champFirstBuffer=new StringBuffer();
					champFirstBuffer.append("| Championship:");
					int champSpaceFront=longestTitle.length()-champFirstBuffer.length();
					for(int j=0;j<champSpaceFront;j++){
						champFirstBuffer.append(" ");}
					champFirstBuffer.append(Championships.get(i));
					int champSpaceEnd=divideBar.length()-champFirstBuffer.length()-1;
					for(int k=0;k<champSpaceEnd;k++){
						champFirstBuffer.append(" ");}
					champFirstBuffer.append("|");
					System.out.println(champFirstBuffer.toString());
				}
				else{
					StringBuffer champBuffer=new StringBuffer();
					champBuffer.append("|");
					int champSpaceFront=longestTitle.length()-champBuffer.length();
					for(int j=0;j<champSpaceFront;j++){
						champBuffer.append(" ");}
					champBuffer.append(Championships.get(i));
					int champSpaceEnd=divideBar.length()-champBuffer.length()-1;
					for(int k=0;k<champSpaceEnd;k++){
						champBuffer.append(" ");}
					champBuffer.append("|");
					System.out.println(champBuffer.toString());}
			}
			System.out.println(divideBar);
		}
		//Print Teams
		if(teamFlag){
			for(int i=0;i<Teams.size();i++){
				if(i==0){
					StringBuffer teamFirstBuffer=new StringBuffer();
					teamFirstBuffer.append("| Teams:");
					int teamSpaceFront=longestTitle.length()-teamFirstBuffer.length();
					for(int j=0;j<teamSpaceFront;j++){
						teamFirstBuffer.append(" ");}
					teamFirstBuffer.append(Teams.get(i));
					int teamSpaceEnd=divideBar.length()-teamFirstBuffer.length()-1;
					for(int k=0;k<teamSpaceEnd;k++){
						teamFirstBuffer.append(" ");}
					teamFirstBuffer.append("|");
					System.out.println(teamFirstBuffer.toString());
				}
				else{
					StringBuffer teamBuffer=new StringBuffer();
					teamBuffer.append("|");
					int teamSpaceFront=longestTitle.length()-teamBuffer.length();
					for(int j=0;j<teamSpaceFront;j++){
						teamBuffer.append(" ");}
					teamBuffer.append(Teams.get(i));
					int champSpaceEnd=divideBar.length()-teamBuffer.length()-1;
					for(int k=0;k<champSpaceEnd;k++){
						teamBuffer.append(" ");}
					teamBuffer.append("|");
					System.out.println(teamBuffer.toString());}
			}
			System.out.println(divideBar);
		}
		//Print Description
		if(descriptionFlag){
			Description = Description.replaceAll("(\\r|\\n)", "");
			StringBuffer descriptionBuffer=new StringBuffer();
			descriptionBuffer.append("| Description:");
			int descriptionSpaceFront=longestTitle.length()-descriptionBuffer.length();
			for(int i=0;i<descriptionSpaceFront;i++){
				descriptionBuffer.append(" ");}
			int index=0;
			int textSpace=0;
			int realLeft=0;
			textSpace = divideBar.length()-descriptionBuffer.length()-1;
			descriptionBuffer.append(Description.substring(index, index+textSpace));
			index+=textSpace;
			descriptionBuffer.append("|");
			System.out.println(descriptionBuffer.toString());
			while(index<Description.length()){
				StringBuffer rowBuffer=new StringBuffer();
				rowBuffer.append("|");
				for(int i=0;i<longestTitle.length()-1;i++){
					rowBuffer.append(" ");}
				realLeft=Description.length()-index-1;
				if(realLeft>=textSpace){
					rowBuffer.append(Description.substring(index, index+textSpace));
					index+=textSpace;
					rowBuffer.append("|");
					System.out.println(rowBuffer.toString());
				}
				else if (realLeft<textSpace){
					rowBuffer.append(Description.substring(index,index+realLeft));
					int tmpLeft=divideBar.length()-rowBuffer.length()-1;
					for(int i=0;i<tmpLeft;i++){
						rowBuffer.append(" ");}
					rowBuffer.append("|");
					System.out.println(rowBuffer.toString());
					System.out.println(divideBar);
					break;
				}
			}
		}
	}
	
	/*****************
	* "SportsTeam" 
	******************/	
	public void PrintSportsTeamInfo(ArrayList<String> entityType,HashMap<String, ArrayList<String>> TypeOfInterestTable) throws NullPointerException{
		String Name="",Sport="",Arena="",Founded="",Leagues="",Locations="",Description="";
		ArrayList<String> Championships=new ArrayList<String>();
		ArrayList<String> CoachName=new ArrayList<String>();
		ArrayList<String> CoachPosition=new ArrayList<String>();
		ArrayList<String> CoachFrom=new ArrayList<String>();
		ArrayList<String> CoachTo=new ArrayList<String>();
		ArrayList<String> PlayerName=new ArrayList<String>();
		ArrayList<String> PlayerPosition=new ArrayList<String>();
		ArrayList<String> PlayerNumber=new ArrayList<String>();
		ArrayList<String> PlayerFrom=new ArrayList<String>();
		ArrayList<String> PlayerTo=new ArrayList<String>();
		boolean nameFlag=false,sportFlag=false,arenaFlag=false,championshipsFlag=false,foundedFlag=false,leaguesFlag=false,locationsFlag=false,coachesFlag=false,playersrosterFlag=false,descriptionFlag=false;
		ArrayList<String> neededProperties = GetNeededProperties(entityType,TypeOfInterestTable);
		
		//name
		if(propertyArray.contains(neededProperties.get(0))){
			nameFlag=true;
			JSONArray name = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(0))),"$.values");
			Name = JsonPath.read(name.get(0),"$.text").toString();}
		//description
		if(propertyArray.contains(neededProperties.get(1))){
			descriptionFlag=true;
			JSONArray description = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(1))),"$.values");
			Description=JsonPath.read(description.get(0),"$.value").toString();}
		//sport
		if(propertyArray.contains(neededProperties.get(2))){
			sportFlag=true;
			JSONArray sport = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(2))),"$.values");
			Sport=JsonPath.read(sport.get(0),"$.text").toString();}
		//Arena
		if(propertyArray.contains(neededProperties.get(3))){
			arenaFlag=true;
			JSONArray arena = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(3))),"$.values");
			Arena=JsonPath.read(arena.get(0),"$.text").toString();}
		//championships
		if(propertyArray.contains(neededProperties.get(4))){
			championshipsFlag=true;
			JSONArray championship = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(4))),"$.values");
			for (int i=0;i<championship.size();i++){
				Championships.add(JsonPath.read(championship.get(i),"$.text").toString());
			}
		}
		//Coaches
		if(propertyArray.contains(neededProperties.get(5))){
			coachesFlag=true;
			JSONArray coachValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(5))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<coachValue.size();i++){
				try {
					JSONObject coachValueParse = (JSONObject)parser.parse(coachValue.get(i).toString());
					String propertyStr = JsonPath.read(coachValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/sports/sports_team_coach_tenure/", 0)){
							// Coach name
							if(o.toString().equals("/sports/sports_team_coach_tenure/coach")){
								String nameInfo = JsonPath.read(coachValueParse,"$.property['/sports/sports_team_coach_tenure/coach']").toString();
							    JSONObject nameInfoParse = (JSONObject)parser.parse(nameInfo);
							    if (JsonPath.read(nameInfoParse,"$.values").toString().equals("[]")){
							    	System.out.println("Coach name: ");
							    }else{
							    	CoachName.add(JsonPath.read(nameInfoParse,"$.values[0].text").toString());}}
							// Coach position
							if(o.toString().equals("/sports/sports_team_coach_tenure/position")){
								String positionInfo = JsonPath.read(coachValueParse,"$.property['/sports/sports_team_coach_tenure/position']").toString();
							    JSONObject positionInfoParse = (JSONObject)parser.parse(positionInfo);
							    if (JsonPath.read(positionInfoParse,"$.values").toString().equals("[]")){
							    	System.out.println("Coach position: ");
							    }else{
							    	CoachPosition.add(JsonPath.read(positionInfoParse,"$.values[0].text").toString());}}
							// Coach from
							if(o.toString().equals("/sports/sports_team_coach_tenure/from")){
								 String fromDateInfo = JsonPath.read(coachValueParse,"$.property['/sports/sports_team_coach_tenure/from']").toString();
								    JSONObject fromDateInfoParse = (JSONObject)parser.parse(fromDateInfo);
								    if (JsonPath.read(fromDateInfoParse,"$.values").toString().equals("[]")){
								    	System.out.println("Coach from: ");
								    }else{
								    	CoachFrom.add(JsonPath.read(fromDateInfoParse,"$.values[0].text").toString());}}
							// Coach to
							if(o.toString().equals("/sports/sports_team_coach_tenure/to")){
								String toDateInfo = JsonPath.read(coachValueParse,"$.property['/sports/sports_team_coach_tenure/to']").toString();
							    JSONObject toDateParse = (JSONObject)parser.parse(toDateInfo);
							    if (JsonPath.read(toDateParse,"$.values").toString().equals("[]")){
							    	CoachTo.add("now");
							    }else{
							    	CoachTo.add(JsonPath.read(toDateParse,"$.values[0].text").toString());}}
						}
					}
					
					//Make sure all four arraylists have same sizes
					if((CoachName.size()==CoachPosition.size())&&(CoachName.size()==CoachFrom.size())&&(CoachName.size()==CoachTo.size())){
						continue;
					}
					else{
						int correctLen = Math.max(Math.max(Math.max(CoachName.size(),CoachPosition.size()),CoachFrom.size()),CoachTo.size());
						if(CoachName.size()<correctLen){CoachName.add(" ");}
						if(CoachPosition.size()<correctLen){CoachPosition.add(" ");}
						if(CoachFrom.size()<correctLen){CoachFrom.add(" ");}
						if(CoachTo.size()<correctLen){CoachTo.add(" ");}
					}
				} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		//Founded
		if(propertyArray.contains(neededProperties.get(6))){
			foundedFlag=true;
			JSONArray founded = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(6))),"$.values");
			Founded= JsonPath.read(founded.get(0),"$.text").toString();
		}
		//Leagues
		if(propertyArray.contains(neededProperties.get(7))){
			leaguesFlag=true;
			JSONArray leagueValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(7))),"$.values");
			JSONParser parser = new JSONParser();
			try {
				JSONObject leagueValueParse = (JSONObject)parser.parse(leagueValue.get(0).toString());
			    Leagues=JsonPath.read(leagueValueParse,"$.property['/sports/sports_league_participation/league'].values[0].text").toString();			    	
			} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
		}
		//Location
		if(propertyArray.contains(neededProperties.get(8))){
			locationsFlag=true;
			JSONArray location = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(8))),"$.values");
			Locations= JsonPath.read(location.get(0),"$.text").toString();
		}
		//PlayersRoster
		if(propertyArray.contains(neededProperties.get(9))){
			playersrosterFlag=true;
			JSONArray playerValue = JsonPath.read(propertyInfoArray.get(propertyArray.indexOf(neededProperties.get(9))),"$.values");
			JSONParser parser = new JSONParser();
			for(int i=0;i<playerValue.size();i++){
				try {
					JSONObject playerValueParse = (JSONObject)parser.parse(playerValue.get(i).toString());
				    //String searchKey = JsonPath.read(playerValueParse,"$.property['/type/object/type'].values[0].id").toString();
					String propertyStr = JsonPath.read(playerValueParse,"$.property").toString();
					JSONObject propertyStrParse = (JSONObject)parser.parse(propertyStr);
					for(Object o:propertyStrParse.keySet()){
						if (o.toString().startsWith("/sports/sports_team_roster/", 0)){
							if(o.toString().equals("/sports/sports_team_roster/player")){
							    PlayerName.add(JsonPath.read(playerValueParse,"$.property['/sports/sports_team_roster/player'].values[0].text").toString());
							}
							// Player position might be empty
							else if(o.toString().equals("/sports/sports_team_roster/position")){
								String positionInfo = JsonPath.read(playerValueParse,"$.property['/sports/sports_team_roster/position']").toString();
							    JSONObject positionInfoParse = (JSONObject)parser.parse(positionInfo);
							    if (JsonPath.read(positionInfoParse,"$.values").toString().equals("[]")){
							    	PlayerPosition.add(" ");
							    }else{
							    	PlayerPosition.add(JsonPath.read(positionInfoParse,"$.values[0].text").toString());}
							}
							// Player number might be empty  
							else if(o.toString().equals("/sports/sports_team_roster/number")){
								String numberInfo = JsonPath.read(playerValueParse,"$.property['/sports/sports_team_roster/number']").toString();
							    JSONObject numberInfoParse = (JSONObject)parser.parse(numberInfo);
							    if (JsonPath.read(numberInfoParse,"$.values").toString().equals("[]")){
							    	PlayerNumber.add(" ");
							    }else{
							    	PlayerNumber.add(JsonPath.read(numberInfoParse,"$.values[0].text").toString());}
							}
							// Player from date might be empty
							else if(o.toString().equals("/sports/sports_team_roster/from")){
						    	String fromDateInfo = JsonPath.read(playerValueParse,"$.property['/sports/sports_team_roster/from']").toString();
							    JSONObject fromDateInfoParse = (JSONObject)parser.parse(fromDateInfo);
							    if (JsonPath.read(fromDateInfoParse,"$.values").toString().equals("[]")){
							    	PlayerFrom.add(" ");
							    }else{
							    	PlayerFrom.add(JsonPath.read(fromDateInfoParse,"$.values[0].text").toString());}
							}
							// Player to date might be empty
							else if(o.toString().equals("/sports/sports_team_roster/to")){
								String toDateInfo = JsonPath.read(playerValueParse,"$.property['/sports/sports_team_roster/to']").toString();
							    JSONObject toDateParse = (JSONObject)parser.parse(toDateInfo);
							    if (JsonPath.read(toDateParse,"$.values").toString().equals("[]")){
							    	PlayerTo.add("now");
							    }else{
							    	PlayerTo.add(JsonPath.read(toDateParse,"$.values[0].text").toString());}
							}
						}
						
					}
					// Make sure all arraylists have same size
					if((PlayerName.size()==PlayerPosition.size())&&(PlayerName.size()==PlayerNumber.size())&&(PlayerName.size()==PlayerFrom.size())&&(PlayerName.size()==PlayerTo.size())){
						continue;
					}
					else{
						int correctLen = Math.max(Math.max(Math.max(Math.max(PlayerName.size(),PlayerPosition.size()),PlayerFrom.size()),PlayerTo.size()),PlayerNumber.size());
						if(PlayerName.size()<correctLen){PlayerName.add(" ");}
						if(PlayerPosition.size()<correctLen){PlayerPosition.add(" ");}
						if(PlayerFrom.size()<correctLen){PlayerFrom.add(" ");}
						if(PlayerTo.size()<correctLen){PlayerTo.add(" ");}
						if(PlayerNumber.size()<correctLen){PlayerNumber.add(" ");}
					}
				} catch (org.json.simple.parser.ParseException e) {e.printStackTrace();}
			}
		}
		
		// Info Box
		String longestTitle="| PlayersRoster: ";
		//Print HeadLine
		StringBuffer headline=new StringBuffer();
		headline.append(Name);
		System.out.println(divideBar);
		if(entityType.size()==1&&entityType.get(0).equals("League")){
			headline.append("(LEAGUE)");}
		else if(entityType.size()==1&&entityType.get(0).equals("SportsTeam")){
			headline.append("(SPORTS TEAM)");}
		else if(entityType.size()==1&&entityType.get(0).equals("Person")){
			headline.append("(PERSON)");}
		else if(entityType.size()>1&&entityType.get(0).equals("Person")){
			headline.append("(");
			for (int i=1;i<entityType.size();i++){
				if(i<entityType.size()-1){
					headline.append(entityType.get(i).toUpperCase()+", ");
				}else{
					headline.append(entityType.get(i).toUpperCase()+")");}}
		}
		int headLinelen = (divideBar.length()-2-headline.length())/2;
		StringBuffer space=new StringBuffer();
		for(int i=0;i<headLinelen;i++){
			space.append(" ");
		}
		headline.insert(0, "|"+space.toString());
		headline.append(space.toString()+"|");
		System.out.println(headline.toString());
		System.out.println(divideBar);
		//Print Name
		if(nameFlag){
			StringBuffer NameBuffer=new StringBuffer();
			NameBuffer.append("| Name:");
			int NameSpaceFront=longestTitle.length()-NameBuffer.length();
			for(int i=0;i<NameSpaceFront;i++){
				NameBuffer.append(" ");}
			NameBuffer.append(Name);
			int NameSpaceEnd=divideBar.length()-NameBuffer.length()-1;
			for(int i=0;i<NameSpaceEnd;i++){
				NameBuffer.append(" ");}
			NameBuffer.append("|");
			System.out.println(NameBuffer.toString());
			System.out.println(divideBar);}
		// Print Sport
		if(sportFlag){
			StringBuffer SportBuffer=new StringBuffer();
			SportBuffer.append("| Sport:");
			int SportSpaceFront=longestTitle.length()-SportBuffer.length();
			for(int i=0;i<SportSpaceFront;i++){
				SportBuffer.append(" ");}
			SportBuffer.append(Sport);
			int SportSpaceEnd=divideBar.length()-SportBuffer.length()-1;
			for(int i=0;i<SportSpaceEnd;i++){
				SportBuffer.append(" ");}
			SportBuffer.append("|");
			System.out.println(SportBuffer.toString());
			System.out.println(divideBar);}
		//Print Arena
		if(arenaFlag){
			StringBuffer ArenaBuffer=new StringBuffer();
			ArenaBuffer.append("| Arena:");
			int ArenaSpaceFront=longestTitle.length()-ArenaBuffer.length();
			for(int i=0;i<ArenaSpaceFront;i++){
				ArenaBuffer.append(" ");}
			ArenaBuffer.append(Arena);
			int ArenaSpaceEnd=divideBar.length()-ArenaBuffer.length()-1;
			for(int i=0;i<ArenaSpaceEnd;i++){
				ArenaBuffer.append(" ");}
			ArenaBuffer.append("|");
			System.out.println(ArenaBuffer.toString());
			System.out.println(divideBar);}
		//Print Championships
		if(championshipsFlag){
			for(int i=0;i<Championships.size();i++){
				if(i==0){
					StringBuffer champFirstBuffer=new StringBuffer();
					champFirstBuffer.append("| Championships:");
					int champSpaceFront=longestTitle.length()-champFirstBuffer.length();
					for(int j=0;j<champSpaceFront;j++){
						champFirstBuffer.append(" ");}
					champFirstBuffer.append(Championships.get(i));
					int champSpaceEnd=divideBar.length()-champFirstBuffer.length()-1;
					for(int k=0;k<champSpaceEnd;k++){
						champFirstBuffer.append(" ");}
					champFirstBuffer.append("|");
					System.out.println(champFirstBuffer.toString());
				}
				else{
					StringBuffer champBuffer=new StringBuffer();
					champBuffer.append("|");
					int champSpaceFront=longestTitle.length()-champBuffer.length();
					for(int j=0;j<champSpaceFront;j++){
						champBuffer.append(" ");}
					champBuffer.append(Championships.get(i));
					int champSpaceEnd=divideBar.length()-champBuffer.length()-1;
					for(int k=0;k<champSpaceEnd;k++){
						champBuffer.append(" ");}
					champBuffer.append("|");
					System.out.println(champBuffer.toString());
				}
			}
			System.out.println(divideBar);
		}
		//Print Founded
		if(foundedFlag){
			StringBuffer foundedBuffer=new StringBuffer();
			foundedBuffer.append("| Founded:");
			int ArenaSpaceFront=longestTitle.length()-foundedBuffer.length();
			for(int i=0;i<ArenaSpaceFront;i++){
				foundedBuffer.append(" ");}
			foundedBuffer.append(Founded);
			int foundedSpaceEnd=divideBar.length()-foundedBuffer.length()-1;
			for(int i=0;i<foundedSpaceEnd;i++){
				foundedBuffer.append(" ");}
			foundedBuffer.append("|");
			System.out.println(foundedBuffer.toString());
			System.out.println(divideBar);
		}
		//Print League
		if(leaguesFlag){
			StringBuffer leagueBuffer=new StringBuffer();
			leagueBuffer.append("| Leagues:");
			int leagueSpaceFront=longestTitle.length()-leagueBuffer.length();
			for(int i=0;i<leagueSpaceFront;i++){
				leagueBuffer.append(" ");}
			leagueBuffer.append(Leagues);
			int leagueSpaceEnd=divideBar.length()-leagueBuffer.length()-1;
			for(int i=0;i<leagueSpaceEnd;i++){
				leagueBuffer.append(" ");}
			leagueBuffer.append("|");
			System.out.println(leagueBuffer.toString());
			System.out.println(divideBar);
		}
		//Print Location
		if(locationsFlag){
			StringBuffer locationBuffer=new StringBuffer();
			locationBuffer.append("| Locations:");
			int locationSpaceFront=longestTitle.length()-locationBuffer.length();
			for(int i=0;i<locationSpaceFront;i++){
				locationBuffer.append(" ");}
			locationBuffer.append(Locations);
			int locationSpaceEnd=divideBar.length()-locationBuffer.length()-1;
			for(int i=0;i<locationSpaceEnd;i++){
				locationBuffer.append(" ");}
			locationBuffer.append("|");
			System.out.println(locationBuffer.toString());
			System.out.println(divideBar);
		}
		// Print Coach
		if(coachesFlag){
			int len=0;
			StringBuffer FirstBuffer=new StringBuffer();
			FirstBuffer.append("| Coaches:");
			int firstSpaceFront=longestTitle.length()-FirstBuffer.length();
			for(int j=0;j<firstSpaceFront;j++){
				FirstBuffer.append(" ");}
			len=(divideBar.length()-FirstBuffer.length())/3;
			FirstBuffer.append("|Name");
			for(int k=0;k<len-("|Name").length();k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|Position");
			for(int k=0;k<len-("|Position").length();k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|From/To");
			int tmp=divideBar.length()-FirstBuffer.length();
			for(int k=0;k<tmp-1;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|");
			System.out.println(FirstBuffer.toString());
			StringBuffer tmpDivider=new StringBuffer();
			tmpDivider.append("|");
			for(int k=0;k<longestTitle.length()-1;k++){
				tmpDivider.append(" ");}
			tmp=divideBar.length()-tmpDivider.length()-1;
			for(int k=0;k<tmp;k++){
				tmpDivider.append("-");}
			tmpDivider.append("|");
			System.out.println(tmpDivider.toString());
			for(int i=0;i<CoachName.size();i++){
				StringBuffer infoBuffer=new StringBuffer();
				infoBuffer.append("|");
				for (int j=0;j<longestTitle.length()-1;j++){
					infoBuffer.append(" ");}
				infoBuffer.append("|"+CoachName.get(i));
				int tmpNum=("|"+CoachName.get(i)).length();
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				infoBuffer.append("|"+CoachPosition.get(i));
				tmpNum=("|"+CoachPosition.get(i)).length();
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				infoBuffer.append("|"+CoachFrom.get(i)+" / "+CoachTo.get(i));
				tmpNum=divideBar.length()-infoBuffer.length()-1;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				infoBuffer.append("|");
				System.out.println(infoBuffer.toString());
			}
			System.out.println(divideBar);
		}
		//Print Players Roster
		if(playersrosterFlag){
			int len=0;
			int tmp=0;
			StringBuffer FirstBuffer=new StringBuffer();
			FirstBuffer.append("| PlayersRoster:");
			int firstSpaceFront=longestTitle.length()-FirstBuffer.length();
			for(int j=0;j<firstSpaceFront;j++){
				FirstBuffer.append(" ");}
			
			len=(divideBar.length()-longestTitle.length())/4;
			FirstBuffer.append("|Name");
			tmp=len-("|Name").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			
			FirstBuffer.append("|Position");
			tmp=len-("|Position").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			
			FirstBuffer.append("|Number");
			tmp=len-("|Number").length();
			for(int k=0;k<tmp;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|From/To");
			tmp=divideBar.length()-FirstBuffer.length();
			for(int k=0;k<tmp-1;k++){
				FirstBuffer.append(" ");}
			FirstBuffer.append("|");
			System.out.println(FirstBuffer.toString());
			StringBuffer tmpDivider=new StringBuffer();
			tmpDivider.append("|");
			for(int k=0;k<longestTitle.length()-1;k++){
				tmpDivider.append(" ");}
			tmp=divideBar.length()-tmpDivider.length()-1;
			for(int k=0;k<tmp;k++){
				tmpDivider.append("-");}
			tmpDivider.append("|");
			System.out.println(tmpDivider.toString());
			
			// Print players
			for(int i=0;i<PlayerName.size();i++){
				int tmpNum=0;
				StringBuffer infoBuffer=new StringBuffer();
				infoBuffer.append("|");
				for (int j=0;j<longestTitle.length()-1;j++){
					infoBuffer.append(" ");}		
				//Name
				String strName="|"+PlayerName.get(i);
				if(strName.length()>len){
					infoBuffer.append((strName).substring(0, len));
					tmpNum=len;}
				else if (strName.length()<=len){
					infoBuffer.append(strName);
					tmpNum=(strName).length();}
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				//Position
				String strPosition="|"+PlayerPosition.get(i);
				if(strPosition.length()>len){
					infoBuffer.append(strPosition.substring(0, len));
					tmpNum=len;}
				else if(strPosition.length()<=len){
					infoBuffer.append(strPosition);
					tmpNum=strPosition.length();}		
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				//Number
				String strNumber ="|"+PlayerNumber.get(i);
				if(strNumber.length()>len){
					infoBuffer.append(strNumber.substring(0, len));
					tmpNum=len;}
				else if(strNumber.length()<=len){
					infoBuffer.append(strNumber);
					tmpNum=strNumber.length();}	
				tmpNum=len-tmpNum;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				//From/To
				String strFromTo="|"+PlayerFrom.get(i)+" / "+PlayerTo.get(i);
				int restLen=divideBar.length()-infoBuffer.length()-1;
				if(strFromTo.length()>restLen){
					infoBuffer.append(strFromTo.substring(0, restLen));}
				else{
					infoBuffer.append(strFromTo);}
				tmpNum=divideBar.length()-infoBuffer.length()-1;
				for(int k=0;k<tmpNum;k++){
					infoBuffer.append(" ");}
				infoBuffer.append("|");
				System.out.println(infoBuffer.toString());
			}
			System.out.println(divideBar);
		}
		//Print Description
		if(descriptionFlag){
			Description = Description.replaceAll("(\\r|\\n)", "");
			StringBuffer descriptionBuffer=new StringBuffer();
			descriptionBuffer.append("| Description:");
			int descriptionSpaceFront=longestTitle.length()-descriptionBuffer.length();
			for(int i=0;i<descriptionSpaceFront;i++){
				descriptionBuffer.append(" ");}
			int index=0;
			int textSpace=0;
			int realLeft=0;
			textSpace = divideBar.length()-descriptionBuffer.length()-1;
			descriptionBuffer.append(Description.substring(index, index+textSpace));
			index+=textSpace;
			descriptionBuffer.append("|");
			System.out.println(descriptionBuffer.toString());
			while(index<Description.length()){
				StringBuffer rowBuffer=new StringBuffer();
				rowBuffer.append("|");
				for(int i=0;i<longestTitle.length()-1;i++){
					rowBuffer.append(" ");}
				realLeft=Description.length()-index-1;
				if(realLeft>=textSpace){
					rowBuffer.append(Description.substring(index, index+textSpace));
					index+=textSpace;
					rowBuffer.append("|");
					System.out.println(rowBuffer.toString());
				}
				else if (realLeft<textSpace){
					rowBuffer.append(Description.substring(index,index+realLeft));
					int tmpLeft=divideBar.length()-rowBuffer.length()-1;
					for(int i=0;i<tmpLeft;i++){
						rowBuffer.append(" ");}
					rowBuffer.append("|");
					System.out.println(rowBuffer.toString());
					System.out.println(divideBar);
					break;
				}
			}
		}
	}
}
