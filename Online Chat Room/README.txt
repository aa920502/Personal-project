***********************************************
Brief Description Of Code
***********************************************
In this code, I implemented a server class and client class to create a chat system. Multiple clients can connect to server using server's IP address and port number. A login validation mechanism is implemented to detect if user gives a valid combination of username and password, any cumulative three times of invalid password input on one username on one IP will lead to block of that username on that IP for 60 seconds, but user can still log into other usernames during that period of time. Also duplicate accounts login on either same IP or different IP is prohibited.
After user successfully logs into the system, he/she can type all the listed commands and perform corresponding functionality as listed in the requirement. User will also be automatically logged out if the account stays inactive for more than a certain amount of time.
In handling logout feature, this system allows user to log out by either typing "logout" command or simply clicking "ctrl C" on the keyboard, either way will lead to a graceful exit without causing any exception. Also on server side, when you type "ctrl C", server will exit gracefully and on client side, client will receive a shutting down message and exit gracefully as well.

For additional features, I implemented an offline message feature and an administrator account feature. Details are discussed below 


***********************************************
Details on development environment
***********************************************
I used Eclipse Version: 4.3.2. to write code for this program, and I have set JDK compliance to JAVA 1.6 for this project since testing machine runs JAVA 1.6. So this code should be able to run on machines in machines in CLIC lab.


***********************************************
Instructions on how to run your code
***********************************************
To run this code:
1. Change directory to ClientServer\src\clientServer
2. Compile server and client .java files using "javac XXX.java" command (This step can be replaced    be running "Makefile" file on MAC machine)
3. Invoke server and client program using:
	"java Server <port number>"
	"java Client <server ip address> <port number>"


***********************************************
Sample commands to invoke your code
***********************************************
cd C:\Users\david_000\Desktop\clientServer
javac Server.java
javac Client.java 
java Server 1999	// execution order matters
java Client 127.0.0.1 1999



PS: 
With Makefile File, you can also compile all .java file with command "make", and you can clean all
.class file with command "make clean".  Note: This Makefile is written on MAC computer.
   
----------------------------------------------------------------------------------------------
By typing above commands, I successfully start both server and client process on my computer.

After that, all you need to do is typing different commands on client side.


Some running examples of this code:

whoelse
**********************************************************
Currently Other Connected Online User(s): 
google
**********************************************************


wholasthr
***************************************************
Connected User(s) In Last Hour: 
1. google
2. facebook
***************************************************

--------------------------------------------------------------------------



*****************************************************
* Additional functionalities and how they should be *
* executed/tested				    *
*****************************************************


******************************
* 1. Offline message system: *
******************************
   For example, if only user "google" is logged on, and he wants to leave an offline message to user "facebook", all he need to do is just using the same private message command:
"message facebook XXXX", system will send a confirmation message "User facebook is currently offline, message will be saved and forwarded to that user once it's online." Next time when user facebook logs on, he/she will see the offline message from google immediately.

   To test this feature, just use "message <user name> <content>", user name can be any user that is currently offline, after that, once you log into that receiver user, you will instantly be able to see the offline message which also indicates the sender.

*************************
* 2. Admin user account *
*************************

Admin is a special account that one user can login with.
This account won't be kicked out due to inactive.

As this account can use all the commands normal accounts can use, it also have some special "admin command".

1. Type "display", it will show all current online users' information: 
        user name, ip address, inactive time

2. Type "kick <user name>", user with this user name will be kicked out of system.
   Message "You are kicked out by admin." will be sent to client to let him/her know.
 
3. Type "Add <user name> <password>", admin can add new account information into system, later    user can login into system with these information. Also this information will be written to    "user_pass.txt". 

4. Type "admin help", admin can see a list of all available commands that he/she can execute.

5. Type "silent <user name>", this will silent the user with <user name>, as a result that user    cannot broadcast anymore. User will receive notification from admin telling him/her that he/she     is silented.

6. Type "recover <user name>", this user will be able to broadcast again. User will receive    notification from admin telling him/her that he/she is recovered

To test this feature, log into the system with admin account (both username and password are admin), then you can check all commands by typing "admin help".








