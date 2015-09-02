import java.net.*;
import java.util.*;
import java.io.*;

// Written by Junchao Lu

public class Server {
	// Store all ClientThreadHandler Created
	ArrayList<ClientThreadHandler> clients = new ArrayList<ClientThreadHandler >();
	// Store all currently online users' user names
	HashSet<String> OnlineUsernames = new HashSet<String>();
	// hashtable <username+IPaddress, ClientInSysInfo>  
	Hashtable <String, ClientInSysInfo> clientInfo = new Hashtable<String, ClientInSysInfo>();
	// create hashtable <username, current status>
	Hashtable<String, String> accountStatus = new Hashtable<String, String>();
	// hashtable <receiver, sender + message> to store offline message related with a username 
	Hashtable<String, OfflineMessage> offlineMessage = new Hashtable<String, OfflineMessage>();
	// Array list storing user names that are silenced.
	ArrayList<String> silentList = new ArrayList<String>();
	// used to store username & password combination
	static ArrayList<String> LoginInformation= new ArrayList<String>();
	
	
	// time out period = 30 minutes
	protected int TIME_OUT = 1800000;
	// last hour period = 60 minutes
	protected int LAST_HOUR = 3600000;
	// block time period = 60 seconds
	protected int BLOCK_TIME = 60000;
	
	public void attachShutDownHook(){
		  Runtime.getRuntime().addShutdownHook(new ShutDown());
		  System.out.println("Shutdown Hook Attached.");
	}
	class ShutDown extends Thread{
		public void run(){
			for (ClientThreadHandler c : clients){
				try {
					c.sendMessage("System is shutting down");
					c.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/*********************************************************************
	 * Method for starting server
	 *********************************************************************/
	public void StartServer(int port){
		// Open server socket for listening
        ServerSocket welcomeServerSocket;
		try{
			// set up a server socket
			welcomeServerSocket = new ServerSocket(port);
			System.out.println("Server Initialized On Port " + port);
			System.out.println("***********************************");
			attachShutDownHook();
			// start thread for detecting user time-out
			timeOut t = this.new timeOut();
			t.start();
			while (true){
				try{
					Socket connection = welcomeServerSocket.accept();
					System.out.println("Connection Received From "+ connection.getInetAddress().getHostName());
					System.out.println(connection.getInetAddress());
					ClientThreadHandler c = this.new ClientThreadHandler(connection); 
					 // add this client handler
	        		clients.add(c);
					c.start();
				}catch (Exception e) {System.err.println("Accept failed."); }
			}	
		}catch (Exception e) {
			System.err.println("Cannot Start Listening On Port " + port);
	        e.printStackTrace();
		}
	}

	/**************************************************************************
	 * handle multiple clients using thread, assign one thread for each client
	 **************************************************************************/
	public class ClientThreadHandler extends Thread{
		BufferedReader inputFromClient;
		BufferedWriter outputToClient;
		// user name attribute
		String username;
		// socket attribute
		Socket socket;
		// IP Address Attribute
		String IPAddr;
		/*
		 * We record a client handler's status
		 * Initial state: 0  ---> require log in
		 * Logged in state: 1 ---> user can do normal command inputs
		 * Kicked out state due to 3-time password error: 2 ---> user cannot log in again in a short period of time
		 * Logged out state: 3 ---> user is currently logged out
		 */
		int status;
		
		/*********************
		 * Constructor
		 *********************/
		public ClientThreadHandler(Socket socket) throws Exception{
			// get input and output streams
			inputFromClient = new BufferedReader( new InputStreamReader(socket.getInputStream())) ;
			outputToClient = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
			this.socket=socket;
			// set to initial state
			this.status = 0; 
			this.IPAddr = socket.getRemoteSocketAddress().toString().split(":")[0];
		}
		
		/*********************
		 * Send Message Method
		 *********************/
		public void sendMessage(String s){
			try{
				outputToClient.write(s);
				outputToClient.newLine();
				outputToClient.flush();
			}catch (IOException  e){
				System.out.println("sender error!");
			}
		}
		
		/*****************************************************************
		 * Method to test if a combination of username & password is valid
		 *****************************************************************/
		public boolean loginAccount(){
			String uName = null;
			String password = null;
			sendMessage("Please Enter User Name: ");
            try{
           	 	uName = inputFromClient.readLine();
				System.out.println("usename: " + uName);
            }catch (Exception e) {};
            
            sendMessage("Please Enter Password: ");
            try{
           	 	password = inputFromClient.readLine();
				System.out.println("password: " + password);
            }catch (Exception e) {};
            
            StringBuffer unps = new StringBuffer();
            unps.append(uName + " " + password);
            // set this.username
        	this.username = uName;     
        	// username is wrong, just return false
        	if (!accountStatus.containsKey(uName) ){
        		sendMessage("Username doesn't exist, please try again");
        		return false;
        	}
            // if it's correct, return true
        	else if (LoginInformation.contains(unps.toString())){
        		// if this entry is already logged on same IP, and its attempt < 3
        		if(clientInfo.containsKey(this.username+" "+this.IPAddr)
        				&& clientInfo.get(this.username+" "+this.IPAddr).attemp <3){
        			clientInfo.get(this.username+" "+this.IPAddr).attemp = 0;
        			clientInfo.get(this.username+" "+this.IPAddr).lastActiveTime = System.currentTimeMillis();
        			System.out.println("this entry is already logged on same IP, this is a successful log in, "
        					+ " only update lastActive time and attempts");
	        		// add this username to online user name list
	            	OnlineUsernames.add(uName); 
	            	System.out.println("Current online user: " + OnlineUsernames.size());
	            	return true;
	            }
        		// if this entry is already logged on same IP, and its attempt >= 3, then it should be in blocked status
				else if (clientInfo.containsKey(this.username+" "+this.IPAddr)
        				&& clientInfo.get(this.username+" "+this.IPAddr).attemp >=3){
					clientInfo.get(this.username+" "+this.IPAddr).attemp++;
					return false;
				}
        		// if this is a first time successful login on this ip, add a new entry into hastable
        		else if (!clientInfo.containsKey(this.username+" "+this.IPAddr)
        				&& !OnlineUsernames.contains(uName)){
        			clientInfo.put(this.username+" "+this.IPAddr, 
							new ClientInSysInfo(0,System.currentTimeMillis(), System.currentTimeMillis(),System.currentTimeMillis()));
        			OnlineUsernames.add(uName); 
	            	System.out.println("Current online user: " + OnlineUsernames.size());
	            	return true;
        		}
        		// this account has already been logged in other IP
				else if (!clientInfo.containsKey(this.username+" "+this.IPAddr)
						&& OnlineUsernames.contains(uName)){
					System.out.println("This account has already been logged on another IP");
					return false;
				}
            }
        	// user name is correct  but password is wrong, if "user name + ip" has already been stored, increment attempt only
        	else{
	        	if (clientInfo.containsKey(this.username+" "+this.IPAddr)){
	        		clientInfo.get(this.username+" "+this.IPAddr).attemp++;
	        	}
	        	//if "user name + ip" hasn't been  stored and not currently online, 
      		  	// write a new record, store attempts as 1, and continue
	        	else{ 
	        		if (!clientInfo.containsKey(this.username+" "+this.IPAddr) && !OnlineUsernames.contains(uName)){
	        			clientInfo.put(this.username+" "+this.IPAddr, 
							new ClientInSysInfo(1,System.currentTimeMillis(), System.currentTimeMillis(),System.currentTimeMillis()));
	        		}
	        		else if(!clientInfo.containsKey(this.username+" "+this.IPAddr) && OnlineUsernames.contains(uName)){
	        			System.out.println("This account has already been logged on another IP");
						return false;
	        		}
	        	}
        	}
            return false;
		}
		
		public void run() {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			b:
			while (true){
	         	/******************************************************
	              * Listen for user input 
	              ******************************************************/
				 String userInput=null;
	             try{
	            	 userInput = this.inputFromClient.readLine();
	 				 System.out.println(userInput);
	 				// update last active time every time user makes an input under login status
	 				 if(this.status == 1){
	 					 clientInfo.get(this.username+" "+this.IPAddr).lastActiveTime = System.currentTimeMillis(); 
	 				 }
	             }catch (Exception e) {};

                /********************
                 ********************
                 *  Initial State   *
                 ********************
                 ********************/
	            while (this.status == 0){
	            	boolean combValid = loginAccount();
		            // if entry exists and attempt < 3
					if (clientInfo.containsKey(this.username +" "+IPAddr) && clientInfo.get(this.username +" "+IPAddr).attemp<3){
						// user + password is valid
						if ( combValid ){
							// if this account has not been logged yet
							if ((accountStatus.get(this.username)).equals("logOut")){
								System.out.println("Logged in user is " + this.username.split(" ")[0]);
								System.out.println("User" + this.username +  " Has Successfully Logged Into System From " + IPAddr);
								sendMessage("------------ Welcome To Chat System ------------");
								sendMessage("---- You can start typing your command here ----");
								sendMessage("************************************************");
								if (this.username.equals("admin")){
									sendMessage("This is administor system login");
									sendMessage("You can type \"admin help\" to see all available admin commands" );
								}
								 /*
					              * Check if there is any offline message for this user, if there is, send all offline messages for this user
					              */
								if (offlineMessage.containsKey(this.username)){
									for (int i = 0; i<offlineMessage.get(this.username).Messages.split("AAA").length;i++ ){
										sendMessage ("[Offline Message From "+ offlineMessage.get(this.username).Sender + "]: "
												+ offlineMessage.get(this.username).Messages.split("AAA")[i]);
									}
									offlineMessage.remove(this.username);
								}
								
								// change status in accountStatus table
								accountStatus.put(this.username, "loggedIn");
								// change to logged in state
								this.status = 1; 
								continue b;
							}
							// if already has same account logged in, let user try another one
							else if ((accountStatus.get(this.username)).equals("loggedIn")){
								sendMessage ("This account has already been logged, please try another one.");
								clientInfo.remove(this.username +" "+IPAddr);
							}
						}// if entry is not valid, display error message and left attempt times
						else{
							sendMessage("Wrong password, please try again, " + 
									(3-clientInfo.get(this.username +" "+IPAddr).attemp) + " more attemps left.");
						}	
					}
					
					// entry exists and attempt >= 3, block for 60 seconds
					else if (clientInfo.containsKey(this.username +" "+IPAddr) && 
							clientInfo.get(this.username +" "+IPAddr).attemp >= 3 && 
							accountStatus.get(this.username).equals("logOut")){   
						// if this is third time, block account 
						if (clientInfo.get(this.username +" "+IPAddr).attemp == 3){
							sendMessage("You are temporarily blocked due to three fail attempts. "
									+ "You will be blocked by " + BLOCK_TIME/1000 + " seconds");
							clientInfo.get(this.username+" "+this.IPAddr).startLockTime = System.currentTimeMillis(); 
							continue b;
						}
						// if this is more than three times, change to block state
						else if(clientInfo.get(this.username +" "+IPAddr).attemp > 3){
							this.status = 2;
							break;
						}
					}
					
					else if (!clientInfo.containsKey(this.username +" "+IPAddr)&& OnlineUsernames.contains(this.username)){
						sendMessage("This account has already been logged on another IP, type any key to continue.");
						continue b;
					}
	            }
	            
	            // if detect a logout input, change to log out state
	            if ("logout".equals(userInput)){
					this.status = 3;
				}
	            
	           /********************
	            * Log in State
	            ********************/
	            if (this.status == 1){
	            	StringBuffer sb = new StringBuffer();
					// whoelse command
					if (userInput!=null && userInput.equals("whoelse")){
						sendMessage("**********************************************************");
						sendMessage("Currently Other Connected Online User(s): " );
						for (String s : OnlineUsernames){
							if (s != username){
								sendMessage(s);
							}
						}
						sendMessage("**********************************************************");
						continue;
					}
					
					// wholasthr command
					if (userInput!=null &&userInput.equals("wholasthr")){
						int index = 1;
						sendMessage("***************************************************");
						sendMessage("Connected User(s) In Last Hour: ");
						for(String keyName: clientInfo.keySet()){
							// check last login time with an hour... use most recent logout time for an username
							// remember to exclude those entries in table but has >= 3 attempts
							if (OnlineUsernames.contains(keyName.split(" ")[0])  || 
									((System.currentTimeMillis() - mostRecentLogoutTime(keyName) <= LAST_HOUR) 
											&& clientInfo.get(keyName).attemp ==0)){ 
								sendMessage(index + ". " + keyName.split(" ")[0]);
							}
							index ++;
						}
						sendMessage("***************************************************");
						continue;
					}

					// broadcast command
					if (userInput!=null && userInput.split(" ")[0].equals("broadcast")&& userInput.split(" ").length>=2){
						// collect all messages including spaces between them
						for (int i = 1; i< userInput.split(" ").length; i++){
							sb.append(userInput.split(" ")[i]);
							System.out.println(userInput.split(" ")[i]);
							if (i != userInput.split(" ").length - 1){
								sb.append(" ");
							}
						}
						if (silentList.contains(this.username)){
							sendMessage("You are silented by admin, please contact admin.");
						}else{
							for (ClientThreadHandler c : clients){
								c.sendMessage("[Broadcast] "+ this.username + ": " + sb.toString());
							}
						}
						continue;
					}
					
					// private message command
					if (userInput!=null && userInput.split(" ")[0].equals("message")&& userInput.split(" ").length>=3){
						// get username
						String un = userInput.split(" ")[1];
						// collect message
						for (int i = 2; i< userInput.split(" ").length; i++){
							sb.append(userInput.split(" ")[i]);
							if (i != userInput.split(" ").length - 1){
								sb.append(" ");
							}
						}
						// check if username is valid
						if (!accountStatus.containsKey(un)){
							sendMessage ("This username doesn't exist in the system, please try again.");
						}
						else{
							// if user is online, send message directly
							if (accountStatus.get(un).equals("loggedIn")){
								for (ClientThreadHandler c : clients){
									if (c.username.equals(un)){
										c.sendMessage("[Private] " + this.username + ": " + sb.toString());
										break;
									}
								}
							}
							else{// if user is offline
								if(offlineMessage.containsKey(un)){ // already has this receiver, append to original offline message
									offlineMessage.get(un).Messages += "AAA" + sb.toString();
								}
								else{
									offlineMessage.put(un, new OfflineMessage(this.username, sb.toString()));
								}
								sendMessage ("User "+ un + " is currently offline, meesage will be saved and forwarded to that user once it's online.");
							}
						}
						continue;
					}
					
					if (clientInfo.get(this.username+" "+this.IPAddr).lastActiveTime == 
									clientInfo.get(this.username+" "+this.IPAddr).logOutTime){continue;}
					
					// Admin Command: display ---> display all current online user's: user name, ip address, inactive time
					if (userInput.equals("display") && this.username.equals("admin")){
						sendMessage ("--------------------------------------------------------");
						sendMessage("User Name   IP Address        Inactive Time");
						for (ClientThreadHandler c : clients){
							if (OnlineUsernames.contains(c.username) && (!c.username.equals("admin"))){
								sendMessage(c.username + "     " + c.IPAddr + "  "+
								(System.currentTimeMillis() - clientInfo.get(c.username + " " + c.IPAddr).lastActiveTime)/1000
										+" seconds");
							}
						}
						sendMessage ("--------------------------------------------------------");
						continue;
					}
					
					// Admin Command: kick user  --> kick an user offline
					if (userInput.split(" ")[0].equals("kick") && this.username.equals("admin")&& userInput.split(" ").length==2){

						if (!userInput.split(" ")[1].equals("admin")){
							String username = userInput.split(" ")[1];
							for (ClientThreadHandler c : clients){
								if (c.username.equals(username)){
									c.sendMessage("You are kicked out by admin.");	
									c.status = 4;
									OnlineUsernames.remove(username);
									accountStatus.put(username, "logOut");
									clientInfo.get(c.username+" "+c.IPAddr).logOutTime = System.currentTimeMillis();
									//clientInfo.remove(key);
									try {
										c.socket.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									clients.remove(c);
									sendMessage ("Client " + c.username + " is kicked out of system.");
									break;
								}
							}
						}else{
							sendMessage("You cannot kick out yourself!");
						}
						continue;
					}
					
					// Admin Command: Add <user name> <password> 
					if (userInput.split(" ")[0].equals("add") && this.username.equals("admin") && userInput.split(" ").length==3){
						String Newusername = userInput.split(" ")[1];
						String Newpassword = userInput.split(" ")[2];
						if (accountStatus.containsKey(Newusername)){
							sendMessage("*********************************************************************");
							sendMessage("This usename is already stored in the system, please use another one.");
							sendMessage("*********************************************************************");
						}
						else{
							try {
							    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("user_pass.txt", true)));
							    out.println(Newusername + " " + Newpassword +"\n");
							    out.close();
							} catch (IOException e) {
							    //exception handling left as an exercise for the reader
							}
							LoginInformation.add(Newusername + " " + Newpassword);
							accountStatus.put(Newusername, "logOut");
						    sendMessage("New Account Information Saved");
						}
						continue;
					}					
					
					// Type "admin help", admin can see a list of all available commands that he/she can execute.
					if (userInput.equals("admin help") && this.username.equals("admin")){
						sendMessage("***********************************");
						sendMessage("* Available Commands:             *");						
						sendMessage("* display           		  *");
						sendMessage("* kick <user name>                *");
						sendMessage("* add <user name> <password>      *");
						sendMessage("* silent <user name>	       	  *");
						sendMessage("* recover <user name>	      	  *");
						sendMessage("***********************************");
						continue;
					}
					
					// Admin Command: "silent <user name>", this will silent the user with <user name>, as a result that user cannot broadcast anymore
					if (userInput.split(" ")[0].equals("silent") && this.username.equals("admin")&& userInput.split(" ").length==2){
						String silentUsername = userInput.split(" ")[1];
						if (!OnlineUsernames.contains(silentUsername)){
							sendMessage ("This user is currently offline, no need to slient this accout.");
						}else{
							silentList.add(silentUsername);
							sendMessage ("User " + silentUsername + " has been silented." );
							for (ClientThreadHandler c : clients){
								if (c.username.equals(silentUsername)){
									c.sendMessage("Your account is silented by admin. You cannot broadcast anymore.");
									break;
								}
							}
						}
						continue;
					}
					
					// Admin Command: "recover <user name>",
					if (userInput.split(" ")[0].equals("recover") && this.username.equals("admin")&& userInput.split(" ").length==2){
						String recoverUsername = userInput.split(" ")[1];
						if (!silentList.contains(recoverUsername)){
							sendMessage ("This user is not on silent list, no need to recover this accout.");
						}else{
							for (ClientThreadHandler c : clients){
								if (c.username.equals(recoverUsername)){
									c.sendMessage("Your account is recovered by admin.");
									break;
								}
							}
							silentList.remove(recoverUsername);
							sendMessage ("User " + recoverUsername + " has been recovered." );
						}
						continue;
					}
						sendMessage("Unknown command, please try again.");
						continue;
	            } // end of login state
	            
	            /********************
	              * Block State
	              ********************/
	            if (this.status == 2){
	            	long CurTime = System.currentTimeMillis();
	            	// still in block status
	            	if (CurTime - clientInfo.get(this.username+" "+this.IPAddr).startLockTime < BLOCK_TIME){
	            		sendMessage("Your account is temporarily blocked, " + 
	            				(BLOCK_TIME - (CurTime - clientInfo.get(this.username+" " + this.IPAddr).startLockTime))/1000 
	            				+ " seconds left. Press Any Key To Continue");
	            		this.status=0;
	            		continue b;
	            	}
	            	// block times out
	            	else{ 
	            		// reset block start time
	            		clientInfo.get(this.username+" "+this.IPAddr).startLockTime = System.currentTimeMillis(); 
	            		// reset this username's attempt within this ip addr
	            		clientInfo.get(this.username+" "+this.IPAddr).attemp = 0;
	            		sendMessage ("Type any key to initiate log in again");
	            		this.status = 0; // set back to initial state.
	            	}
	            	
	            }
	            
	            /********************
	              * Logged Out State 
	              ********************/
	            if (this.status == 3){
	            	this.status = 4; // break out of while loop
	            	sendMessage ("You are Logged Out Now.");
	            	// update log out time
	            	clientInfo.get(this.username+" "+this.IPAddr).logOutTime = System.currentTimeMillis();
	            	// update attempt
	            	clientInfo.get(this.username+" "+this.IPAddr).attemp = 0;
	            	// remove entry in usernames and clients
	            	OnlineUsernames.remove(this.username);
	            	clients.remove(this);
	            	// update accountStatus table
	            	accountStatus.put(this.username, "logOut");
	            	try{
	            		this.socket.close();
	            	}catch (Exception e){}
	            	continue;
	            }
	            
	            if (this.status == 4){
	            	break;
	            }
	            
			}// end of while
		}// end of run()	
	}// end of ClientThreadHandler
	
	// check if this key is stored in clients, which means this key is currently online
	public boolean isOnline (String key){
		String userName = key.split(" ")[0];
		String IP = key.split(" ")[1];
		for (ClientThreadHandler c : clients){
			try{
			if (c.username.equals(userName) && c.IPAddr.equals(IP)){
				return true;
			}}catch(NullPointerException e){}
		}
		return false;
	}
	
	// get most recent logout time within same username
	public long mostRecentLogoutTime (String key){
		String userName = key.split(" ")[0];
		long result = 0;
		for (String keyName:clientInfo.keySet()){
			if (keyName.split(" ")[0].equals(userName)){
				if (clientInfo.get(keyName).logOutTime > result){
					result = clientInfo.get(keyName).logOutTime;
				}
			}
		}
		return result;
	}
	
	public class timeOut extends Thread{
		public void run(){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true){
				//System.out.println(clients.size());
				if (!clientInfo.isEmpty()){
					for (String key:clientInfo.keySet()){
						String userName = key.split(" ")[0];
						String IP = key.split(" ")[1];
						if(isOnline(key)){
						// admin won't be kicked out 
						if (!userName.equals("admin") ){
							// if this user is online, and its inactive time > time_out, then we kick it out, but we still save its information
							if ( OnlineUsernames.contains(userName)){
								if(System.currentTimeMillis()- clientInfo.get(key).lastActiveTime> TIME_OUT){
									int index=0;
									while(index<clients.size()){
										if(clients.get(index).IPAddr.equals(IP) && clients.get(index).username.equals(userName)){
											clients.get(index).sendMessage("You are kicked out due to inacitivity.");	
											clients.get(index).status = 4;
											OnlineUsernames.remove(userName);
											accountStatus.put(userName, "logOut");
											// update log out time
											clientInfo.get(key).logOutTime = System.currentTimeMillis();
											// update attempt
							            	clientInfo.get(key).attemp = 0;
											try {
												clients.get(index).socket.close();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											clients.remove(index);
											break;
										}
										index++;
									}
								}
							}
						}
					}
					}// end of for
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*************************************************************************
	 * Read "user_pass.txt" class, save user name & password into an arraylist
	 **************************************************************************/
	public ArrayList<String> ReadFile (){
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br = null;
		try{
			String curLine;
			br = new BufferedReader(new FileReader("user_pass.txt"));
			while ((curLine = br.readLine())!=null){
				list.add(curLine);
				accountStatus.put(curLine.split(" ")[0], "logOut");
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try{
				if (br!=null) br.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static void main (String[] args){
		if (args.length != 1) {
            System.out.println("Please type command in this format: java Server <port>");
            System.exit(1);
        }
		// get port number from user input
		int port = Integer.parseInt(args[0]);
		Server server = new Server();
		// get store username/password combination
		LoginInformation = server.ReadFile();
		server.StartServer(port);
	}
}
