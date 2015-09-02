import java.util.ArrayList;
import java.util.HashMap;


public class Initializer {
	
	public Initializer(){}
	
	/********************************
	 * Initialize Freebase type table
	 ********************************/
	public void initializeFreebaseTypeTable(HashMap<String, ArrayList<String>> FreebaseTypeTable){
		ArrayList<String> person = new ArrayList<String>();
		person.add("/people/person");
		FreebaseTypeTable.put("Person", person);
		ArrayList<String> author = new ArrayList<String>();
		author.add("/book/author");
		FreebaseTypeTable.put("Author", author);
		ArrayList<String> actor = new ArrayList<String>();
		actor.add("/film/actor");
		actor.add("/tv/tv_actor");
		FreebaseTypeTable.put("Actor", actor);
		ArrayList<String> businessPerson = new ArrayList<String>();
		businessPerson.add("/organization/organization_founder");
		businessPerson.add("/business/board_member");
		FreebaseTypeTable.put("BusinessPerson", businessPerson);
		ArrayList<String> league = new ArrayList<String>();
		league.add("/sports/sports_league");
		FreebaseTypeTable.put("League", league);
		ArrayList<String> sportsTeam = new ArrayList<String>();
		sportsTeam.add("/sports/sports_team");
		sportsTeam.add("/sports/professional_sports_team");
		FreebaseTypeTable.put("SportsTeam", sportsTeam);
	}
	
	/***********************************
	 * Initialize type of interest table
	 ***********************************/
	public void initializeTypeOfInterestTable(HashMap<String, ArrayList<String>> TypeOfInterestTable){
		ArrayList<String> person = new ArrayList<String>();
		person.add("/type/object/name"); //name
		person.add("/people/person/date_of_birth"); //birthday
		person.add("/people/person/place_of_birth"); //place of birth
		person.add("/people/deceased_person/place_of_death"); //death where
		person.add("/people/deceased_person/date_of_death"); //death date
		person.add("/people/deceased_person/cause_of_death"); // death cause
		person.add("/people/person/sibling_s"); //sibling
		person.add("/people/person/spouse_s"); //spouse
		person.add("/common/topic/description"); //description
		TypeOfInterestTable.put("Person", person);
		
		ArrayList<String> author = new ArrayList<String>();
		author.add("/book/author/works_written"); //books
		author.add("/book/book_subject/works"); //book about the author
		author.add("/influence/influence_node/influenced"); //influenced
		author.add("/influence/influence_node/influenced_by"); //influenced by
		TypeOfInterestTable.put("Author", author);
		
		ArrayList<String> actor = new ArrayList<String>();
		actor.add("/film/actor/film"); //film information
		TypeOfInterestTable.put("Actor", actor);
		
		ArrayList<String> businessPerson = new ArrayList<String>();
		businessPerson.add("/business/board_member/leader_of"); // leadership
		businessPerson.add("/business/board_member/organization_board_memberships");//BoardMember
		businessPerson.add("/organization/organization_founder/organizations_founded"); //founded
		TypeOfInterestTable.put("BusinessPerson", businessPerson);
		
		ArrayList<String> league = new ArrayList<String>();
		league.add("/type/object/name"); //name
		league.add("/sports/sports_league/championship"); //championship
		league.add("/sports/sports_league/sport"); //sport
		league.add("/organization/organization/slogan");//slogan
		league.add("/common/topic/official_website"); //official website
		league.add("/common/topic/description"); // description
		league.add("/sports/sports_league/teams"); //teams
		TypeOfInterestTable.put("League", league);
		
		ArrayList<String> sportsTeam = new ArrayList<String>();
		sportsTeam.add("/type/object/name"); //name
		sportsTeam.add("/common/topic/description"); //description
		sportsTeam.add("/sports/sports_team/sport"); //sport
		sportsTeam.add("/sports/sports_team/arena_stadium"); //arena
		sportsTeam.add("/sports/sports_team/championships"); //championship
		sportsTeam.add("/sports/sports_team/coaches"); //coach
		sportsTeam.add("/sports/sports_team/founded"); //founded
		sportsTeam.add("/sports/sports_team/league"); //league
		sportsTeam.add("/sports/sports_team/location"); //location
		sportsTeam.add("/sports/sports_team/roster"); //roster
		TypeOfInterestTable.put("SportsTeam", sportsTeam);
	}
}
