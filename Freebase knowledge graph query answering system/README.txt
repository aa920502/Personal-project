README

a)     Your name and your partner's name 



b)     A list of all the files that you are submitting
		-----------------------------------------------
		JAVA file | InfoPrinter.java
				  | Initializer.java
				  | MqlReader.java
				  | SearchExample.java
		-----------------------------------------------
		Jar file  |commons-logging-1.1.1.jar
				  |google-http-client-1.19.0.jar
				  |gson-2.1.jar:httpclient-4.0.1.jar
				  |httpcore-4.0.1.jar
				  |jackson-core-2.1.3.jar
				  |jackson-core-asl-1.9.11.jar
				  |jetty-6.1.26.jar
				  |jetty-util-6.1.26.jar
				  |json-path-0.5.5.jar
				  |json-simple-1.1.jar
				  |json-smart-1.1.jar
				  |jsr305-1.3.9.jar
				  |protobuf-java-2.4.1.jar
				  |transaction-api-1.1.jar
				  |xpp3-1.1.4c.jar
		-----------------------------------------------
		txt file  |README.txt
				  |Transcript.txt
		-----------------------------------------------
		MakeFile
		-----------------------------------------------


c)     A clear description of how to run your program (note that your project must compile/run under Linux in your CS account)
		
		1) type "make"
		2) type "make run AIzaSyCy9TbR7X3_7Iw68GWJKgA8Dt6r2AcK5fU"
		3) type in whatever query you want (For example: "Bill Gates", "Who created Google?")


d)     A clear description of the internal design of your project, including listing the mapping that you used to map from Freebase properties to the entity properties of interest that you return

		Firstly, at the beginning of program, I initialized two hashmaps, one is "Freebase type table" which maps "type we want" and "corresponding type in freebase", second hashmap is "type of interest table" which maps "type we want" and "corresponding types of interests for the type in freebase", for example, for "Person", we have "/type/object/name" for name, "/people/person/date_of_birth" for birthday, etc. 

		Program starts by accepting a query from user, and then it decides whether it is an infobox query or a question query. 

		If it is an infobox query, it will search by using Freebase search API. Note here we don‘t use any filter defined in API. In our implementation, we only consider top 20 return results if there are more than 20 return resuts. For each result, we parse and retrieve its "mid" value, and use that "mid" value to query into Freebase topic API, which will return a series of result types under "id" in "type/object/type" of "Property". After that we search into "Freebase type table" to see if it has supported types, if it does, we have a hit, then we can print out related information; if it does not, we have a miss, we will go to next result, extract its "mid", and do the same querying and comparison procedure.

		If it is a question query, we check first two words and the "question string" that user inputs, then we issued two MQL queries to search results for the question because in this project only two types are required (book/author, organization/businessPerson). Two queries are shown below:
		book/author: "[{\"works_written\": [{\"b:name\": null,\"name~=\": \""+target+"\"}],\"name\": null,\"type\": \"/book/author\"}]"
		organization/businessPerson: "[{\"organizations_founded\": [{\"a:name\": null,\"name~=\": \""+target+"\"}],\"name\": null,\"type\": \"/organization/organization_founder\"}]"

		Design for printing: for printing of infobox query, we divide into three major types: Person, League, Team. The rationale is that a result must be a person in order to be actor/author/businessPerson. So after printing all information for a person, we check if there are any more entity types needed to be printed. For Leagure and Team, we print independantly for each of them. For printing of question query, we basically did a similar version of infobox print.

		Design for error handling: If user's input query's return result is empty, a String will be printed out reminding that no related result is found. However, the program will search through top 20 results if there are actually returning values, if the program couldn't find a result with supported type after searching all returning results, error message will be displaying similar to what the reference implementation does.


f)      Your Freebase API Key (so we can test your project) as well as the requests per second per user that you have set when you configured your Google project (see Freebase Basics section)
		
		API key: AIzaSyCy9TbR7X3_7Iw68GWJKgA8Dt6r2AcK5fU
		Requests per second per user: we didn't change that parameter, so it's default value.

