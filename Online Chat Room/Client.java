
import java.net.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.*;

public class Client {
	BufferedReader inputFromServer;
	BufferedWriter outputToServer;
	Scanner scanner;
	public void attachShutDownHook(){
		  Runtime.getRuntime().addShutdownHook(new ShutDown());
		  System.out.println("Shutdown Hook Attached.");
	}
	class ShutDown extends Thread{
		public void run(){

			sendMessage("logout");
		}
	}
	public void sendMessage(String s){	 
		try{
			outputToServer.write(s);
			outputToServer.newLine();
			outputToServer.flush();
		}catch (IOException e){
			System.out.println();
		}
	}
	
	public void ClientLogin(String ServerIP, int ServerPort){
		Socket socket = null;
		try{
			System.out.println("Socket client initialized");
			// establish a socket connection
			socket = new Socket (ServerIP, ServerPort);
			System.out.println("Connected to localhost in port " + ServerPort);
			// receive input from server
			inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// output to server
			outputToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String msgToSend = "BufferedReader/Writer on client side is initialized!!!";
			sendMessage(msgToSend);
			
			scanner = new Scanner(System.in);
			attachShutDownHook();
			ReadThread t = new ReadThread(socket);
       	 	t.start();
			while (true){
           	 	// prompt user input
				try{
					String userInput = scanner.nextLine();
					sendMessage(userInput);	
					if(userInput.equals("logout")){
						inputFromServer.close();
						outputToServer.close();
						System.exit(-1);
						break;
					}
				}catch(NoSuchElementException e){};
			}	
			}catch (UnknownHostException e) {
	            System.err.println("Don't know about host");
	            System.exit(1);
	        } catch (Exception e) {
	        	e.printStackTrace();
	            System.exit(1);
	       }
	}
	
	/*
	 * Thread class for reading from server side so that main thread can handle sending
	 */
	public class ReadThread extends Thread{
		Socket socket;
		public ReadThread (Socket socket){
			this.socket = socket;
		}
		public void run(){
			while (true){
				try{
					String serverInput = inputFromServer.readLine();
					System.out.println(serverInput);
					if(("You are kicked out due to inacitivity.").equals(serverInput) || 
							("You are kicked out by admin.").equals(serverInput)|| 
							("System is shutting down").equals(serverInput)){
						System.exit(-1);
					}
				}catch (IOException e){
					return;
				}
			}
		}
	}
	
	public static void main (String[] args){
		if (args.length != 2) {
            System.out.println("Please type command in this format: java Client <IP address> <port>");
            System.exit(1);
        }
		
		String serverIP = args[0];
		int serverPort = Integer.parseInt(args[1]);
		Client ic = new Client();
		ic.ClientLogin(serverIP, serverPort);

	}

}
