import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Scanner;

public class bfclient{
	long timeoutMillis;
	int portNumber;
	String localInfo=null;
	NeighborHandler nh = new NeighborHandler();
	NodeManager nm = new NodeManager();
	UDPReader udpReader;
	
	public static void main(String[] args){
		if (args.length < 2 || (args.length-2)%3!=0) {
            System.out.println("Please type userInput in correct format: java bfclient localport timeout [ipaddress1 port1 weight1 ...]");
            System.exit(1);
        }
		else{
			int portnumber = Integer.parseInt(args[0]);
			long timeoutPeriod = Long.parseLong(args[1]);
			@SuppressWarnings("resource")
			Scanner s = new Scanner(System.in);
			String[][] inputData = parseInput(args);
			bfclient bfc = new bfclient(portnumber,timeoutPeriod);
			if(inputData!=null){
				for(int i=0;i<inputData.length;i++){
					bfc.linkUp(inputData[i][0],Integer.parseInt(inputData[i][1]),Float.parseFloat(inputData[i][2]));
				}
			}
			bfc.sendToNeighbors();
			while(true){
				String userInput = s.nextLine();
				
				if(userInput.equals("SHOWRT")){
					bfc.tableIsChanged();
					bfc.SHOWRT();
				}
				else if(userInput.startsWith("LINKUP")){
					String[] tmps = userInput.split(" ");
					if(tmps.length==3){
						try{
							String ip = tmps[1];
							Integer port = Integer.parseInt(tmps[2]);
							bfc.linkUp(ip,port);
						}catch(NumberFormatException nfe){continue;}
					}
				}
				else if(userInput.startsWith("LINKDOWN")){
					String[] tmps = userInput.split(" ");
					if(tmps.length==3){
						try{
							String ip = tmps[1];
							Integer port = Integer.parseInt(tmps[2]);
							bfc.linkDown(ip,port);
						}catch(NumberFormatException nfe){continue;}
					}
				}
				else if(userInput.equals("CLOSE")){
					System.exit(-1);
				}
				else{
					System.out.println("Wrong Input Format! Please check!");
					continue;
				}
			}
		}
	}
	// constructor
	public bfclient(int portNumber, long timeoutInterval){
		udpReader=new UDPReader(portNumber);
		udpReader.start();
		new Timer().start();
		this.timeoutMillis=timeoutInterval*1000;
		this.portNumber=portNumber;
		// add shutdown hook to the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
	    public void run() {
	    	udpReader.socket.close();
	        System.out.println("Client is shut down");
	 	   }
		});
	}
	// Method for displaying RT
	public void SHOWRT(){
		for(Node n:nm.nodes.values()){
			String c;
			if(n.cost ==-1){
				c = "null";
			}else{
				c = String.valueOf(n.cost);
			}
			System.out.println("Destination = "+n.ipPort+", Cost = "+c+", Link = "+n.passedNei);
		}
	}
	// method to parse user input:     destIP - destPort - cost
	public static String[][] parseInput(String[] args){
		int len=(args.length-2)/3;
		if(len==0){return null;}
		// two dimension array to hold input information
		String[][] dataSet= new String[len][3];
		for(int i=0;i<len;i++){
			dataSet[i][0]=args[2+3*i];
			dataSet[i][1]=args[2+3*i+1];
			dataSet[i][2]=args[2+3*i+2];
		}
		return dataSet;
	}

	// UDP reader thread
	class UDPReader extends Thread{
		DatagramSocket socket;
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
		UDPReader(int portNumber){
			try{
				socket=new DatagramSocket(portNumber);
			}catch(IOException ie){
				System.out.println("This port is already taken");
				System.exit(-1);
			}
		}
		public void run(){
			while(true){
				try{
					for(int i=0;i<receiveData.length;i++){
						receiveData[i]='\0';
					}
					socket.receive(receivePacket);
					String str = new String(receivePacket.getData(), "UTF-8");
					String command =  str.split("\n")[0].split(":")[1];
					
					if(command.equals("con")){
						Connection c = new Connection(receivePacket);
						nm.addNode(c.info,c.cost,c.info);
						nh.neighbors.put(c.info,new Neighbor(c.info,c.cost));
						sendToNeighbors();
					}
					else if(command.equals("recon")){
						Connection c = new Connection(receivePacket);
						nh.neighbors.get(c.info).isConnected=true;
						nh.refreshFailureTime(c.info);			
					}
					else if(command.equals("distanceVector")){
						DistanceVector v = new DistanceVector(receivePacket);
						nh.refreshFailureTime(v.info);
						DistanceVector oldVector = nh.neighbors.get(v.info).vector;
						if(!v.equals(oldVector)){
							nh.neighbors.get(v.info).vector=v;
							if(tableIsChanged()){
								sendToNeighbors();
							}
						}
					}
				}catch (IOException e){}
			}
		}
	}
	
	// timer class
	class Timer extends Thread{
		public void run(){
			while(true){
				for(Neighbor n:nh.neighbors.values()){
					if(!(System.currentTimeMillis()>n.timeToFailure)&&System.currentTimeMillis()>n.nextSendingTime){
						n.sendDistanceVector();
					}
					if(System.currentTimeMillis()>n.timeToFailure){
						n.isConnected=false;
					}
				}
				try{
					Thread.sleep(7);
				}catch(InterruptedException ie){;}
			}
		}
	}
	
	/*
	 * LINKUP command
	 */
	public void linkUp(String ip, int port, float cost){
		nh.addNeighbor(ip,port,cost);
		nm.addNode(ip,port,cost,ip+":"+port);
		nh.neighbors.get(ip+":"+port).sendConnectMessage();
	}

	public void linkUp(String ip, int port){
		if(nh.neighbors.get(ip+":"+port)!=null){
			System.out.println("LINKUP is executed");
			nh.neighbors.get(ip+":"+port).isConnected=true;
			nh.neighbors.get(ip+":"+port).resetFailureTime();
			nh.neighbors.get(ip+":"+port).sendMessage(createReconnectMessage());
			tableIsChanged();
			sendToNeighbors();
		}else{
			System.out.println("error client specification");
		}
	}
	/*
	 * LINKDOWN command
	 */
	public void linkDown(String ip, int port){
		if(nh.neighbors.get(ip+":"+port)!=null){
			System.out.println("LINKDOWN is executed");
			nh.neighbors.get(ip+":"+port).isConnected=false;
			tableIsChanged();
			sendToNeighbors();
		}else{
			System.out.println("error client specification");
		}
	}

	// method to send distance vector to all current neighbors
	public void sendToNeighbors(){
		while(true){
			try{
				for(Neighbor n:nh.neighbors.values()){
					n.sendDistanceVector();
				}
				return;
			}catch(ConcurrentModificationException e){}
		}
	} 
	// method to calculate distance vectors, return whether the table has been modified or not.
	public boolean tableIsChanged(){
		// save old distance vector first
		Hashtable<String,Float> temp = new Hashtable<String,Float>();
		for(Node n:nm.nodes.values()){
			temp.put(n.ipPort,n.cost);
		}
		// If there are unknown clients reachable, add those
		for(Neighbor n:nh.neighbors.values()){
			if(n.isConnected){
				for(String address:n.vector.address){
					if(nm.nodes.get(address)==null){
						nm.addNode(address,-1,null);
					}
				}
			}
		}
		// clear all neighbor clients values and assign them with updated value
		for(Node n:nm.nodes.values()){
			n.cost=-1;
			n.passedNei=null;
		}
		for(Neighbor n:nh.neighbors.values()){
			if(n.isConnected){
				nm.nodes.get(n.info).cost=n.cost;
				nm.nodes.get(n.info).passedNei=n.info;
			}
		}
		for(Node n:nm.nodes.values()){
			float tmpMin;
			if (n.cost == -1){
				tmpMin = Float.MAX_VALUE;
			}else{
				tmpMin = n.cost;
			}
			String target=n.ipPort;
			// bellman ford algorithm
			for(Neighbor neighbor : nh.neighbors.values()){
				if(neighbor.isConnected&&neighbor.info!=target){
					float cost = neighbor.vector.getCost(target);
					if((cost+neighbor.cost<tmpMin)&&cost!=-1){
						tmpMin=cost+neighbor.cost;
						n.passedNei=neighbor.info;
					}
				}
			}
			if (tmpMin==Float.MAX_VALUE){
				n.cost = -1;
			}else{
				n.cost = tmpMin;
			}
		}
		// if there is a change in size in the table, send true
		if(nm.nodes.size()!=temp.size()){
			return true;
		}else{
			for(Node n:nm.nodes.values()){
				if(temp.get(n.ipPort)==null||temp.get(n.ipPort)!=n.cost){
					return true;
				}
			}
		}
		return false;
	}

	// neighbor handler class
	class NeighborHandler{
		public Hashtable<String, Neighbor> neighbors = new Hashtable<String, Neighbor>();
		public void addNeighbor(String ip, int port, float cost){
			String info=ip+":"+port;
			neighbors.put(info,new Neighbor(info,cost));
		}
		public void refreshFailureTime(String ipPort){
			neighbors.get(ipPort).resetFailureTime();
		}
		public void renewNextSendTime(String ipPort){
			neighbors.get(ipPort).resetTimeout();
		}
	}

	// Neighbor class
	public class Neighbor{
		InetAddress ip;
		DatagramSocket socket;
		String info;
		float cost;
		long nextSendingTime;
		long timeToFailure;
		boolean isConnected=true;
		
		public DistanceVector vector = new DistanceVector(info);
		public Neighbor(String ipPort, float cost){
			this.info=ipPort;
			this.cost=cost;
			try{
				ip = InetAddress.getByName((ipPort.split(":")[0]));
				socket = new DatagramSocket();
				resetTimeout();
				resetFailureTime();
			}catch(IOException ie){;}
		}
		public void resetTimeout(){
			nextSendingTime=System.currentTimeMillis()+timeoutMillis;
		}
		public void resetFailureTime(){
			timeToFailure=System.currentTimeMillis()+3*timeoutMillis;
		}
		public void sendMessage(String message){
			DatagramPacket sendPacket = new DatagramPacket(message.getBytes(),message.getBytes().length,ip,Integer.parseInt(info.split(":")[1]));
			try{
			socket.send(sendPacket);
			}catch(IOException ie){;}
		}
		public void sendDistanceVector(){
			if(isConnected){
				sendMessage(createDistanceVector(info));
				resetTimeout();
			}
		}
		public void sendConnectMessage(){
			sendMessage(createConnect(cost));
		}
	}
	// node manager class
	public class NodeManager{
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();
		public void addNode(String ipPort, float cost, String nextNeighbor){
			if(nodes.get(ipPort)==null){
				nodes.put(ipPort,new Node(ipPort,cost,nextNeighbor));
			}
		}
		public void addNode(String ip, int port, float cost, String nextNeighbor){
			String info=ip+":"+port;
			if(nodes.get(info)==null){
				nodes.put(info,new Node(info,cost,nextNeighbor));
			}
		}
	}
	private String createDistanceVector(String s){
		StringBuffer sb = new StringBuffer();
		sb.append("command:distanceVector\n"+"port:"+portNumber+"\n");
		for(Node n:nm.nodes.values()){
			if(!n.ipPort.equals(s)){
				sb.append(n.ipPort+" "+n.cost+"\n");
			}
		}
		return sb.toString();
	}
	private String createConnect(float cost){
		StringBuffer sb = new StringBuffer();
		sb.append("command:con\n"+"port:"+portNumber+"\n"+"cost:"+cost+"\n");
		return sb.toString();
	}
	private String createReconnectMessage(){
		StringBuffer sb = new StringBuffer();
		sb.append("command:recon\n"+"port:"+portNumber+"\n"+"cost:0\n");
		return sb.toString();
	}

	// node class
	class Node{
		float cost;
		String ipPort;
		String passedNei;
		InetAddress ip;
		DatagramSocket socket;
		public Node(String ipPort,float cost, String passedNei){
			this.cost=cost;
			this.ipPort=ipPort;
			this.passedNei=passedNei;
			try{
				this.ip = InetAddress.getByName((ipPort.split(":")[0]));
				this.socket = new DatagramSocket();
			}catch(IOException ie){;}
		}
	}
	
	class Connection{
		public String info;
		public float cost;
		public Connection(DatagramPacket packet){
			try{
				String ip = packet.getAddress().getHostAddress().toString();
				String str = new String(packet.getData(), "UTF-8");
				String[] strParse = str.split("\n");
				info=ip+":"+strParse[1].split(":")[1];
				if(strParse.length>2){
					cost=Float.parseFloat(strParse[2].split(":")[1]);
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// distance vector class
	class DistanceVector{
		public String info;
		public ArrayList<String> address = new ArrayList<String>();
		public ArrayList<Float> cost = new ArrayList<Float>(); 
		public int size;
		public DistanceVector(String hostIp){
			this.info=hostIp;
		}
		public DistanceVector(DatagramPacket packet){
			try{
				String ip = packet.getAddress().getHostAddress().toString();
				String str = new String(packet.getData(), "UTF-8");
				String[] context = str.split("\n");
				info=ip+":"+context[1].split(":")[1];
				int k=context.length-3;
				size=k;
				for(int i=0;i<k;i++){
					address.add(context[2+i].split(" ")[0]);
					cost.add(Float.parseFloat(context[2+i].split(" ")[1]));
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		public float getCost(String target){
			for(int i=0;i<address.size();i++){
				if(address.get(i).equals(target)){
					return cost.get(i);
				}
			}
			return -1;
		}
	}
}