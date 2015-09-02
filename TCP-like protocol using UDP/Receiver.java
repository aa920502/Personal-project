import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

/*
 * receiver <filename> <listening_port> <sender_IP> <sender_port> <log_filename>
 */
public class Receiver {

	// UDP socket for receiving data
	static DatagramSocket receiverSocket;
	// TCP socket for sending ACK
	Socket sendACK = null;
	//output/logfile writer
	FileOutputStream fileWriter;
	BufferedWriter logfileWriter;
	//boolean for marking if TCP is finished
	boolean TCPDone = false;
	// output stream to send ack back to sender
	OutputStream tcpOut;
	
	static String filename;
	static int port;
	static String remoteIP;
	static int remotePort;
	static String logFileName;
	
	public static void main(String[] args)throws IOException  {
        if(args.length<5){
	    	System.out.print("Please Enter Correct Command");
	    }else{
	    	filename = args[0];
	    	port = Integer.parseInt(args[1]);
	    	remoteIP = args[2];
	    	remotePort = Integer.parseInt(args[3]);
	    	logFileName = args[4];
	    	Receiver r = new Receiver(filename,port,remoteIP,remotePort,logFileName,TCPHeaderHelper.mss );
	    	r.receiveDataFromSender(remoteIP, remotePort,filename, logFileName);
	    }
	}
	// constructor
	public Receiver(String filename, int listeningPort, String remoteIP, int remotePort, String logFileName, int mss) throws IOException{
		try {
			// create two file writters
			File f1 = new File("Receiver/"+filename);
			File f = new File("Receiver/"+logFileName);
			if(!f.exists())
				f.createNewFile();
			if(!f1.exists())
				f1.createNewFile();
			fileWriter= new FileOutputStream (f1);
			logfileWriter = new BufferedWriter(new FileWriter(f));
			receiverSocket = new DatagramSocket(listeningPort);
			System.out.println("UDP receiverSocker is created");
		} catch (SocketException e) {e.printStackTrace();}
	}
	public byte[] onlyACKbit(int ackNumber){
		return TCPHeaderHelper.createSegment(new byte[0],(short)port,(short)remotePort,0,ackNumber,true,false,(short)1);
	}

	public void receiveDataFromSender(String remoteIP,int remotePort, String filename, String logfilename) throws UnknownHostException, IOException{
		Thread Receive = new Thread(new Receiving(remoteIP,remotePort,filename, logfilename));
		Receive.start();
	}
	
	class Receiving implements Runnable{	
		int remotePort;
		String filename;
		String logfilename;
		byte[] DataArray = new byte[TCPHeaderHelper.mss+20];
		DatagramPacket receivePacket = new DatagramPacket(DataArray,DataArray.length);
		int ackNumber=0;//for receiver
		
		// use a tree set data structure to order all data in the order of sequence number
		TreeSet<setEntry> set = new TreeSet<setEntry>(new setEntry());
		class setEntry implements Comparator<setEntry>{
			public int sequenceNumber;
			public byte[] data;
			public setEntry(){};
			public setEntry(int sequenceNumber,byte[] data){
				this.sequenceNumber=sequenceNumber;
				this.data=data;
			}
			public int compare(setEntry s1,setEntry s2){
				return s1.sequenceNumber-s2.sequenceNumber;
			}
		}
		
		public Receiving(String remoteIP, int remotePort,String filename, String logfilename){
			this.remotePort = remotePort;	
			this.filename = filename;
			this.logfilename = logfilename;
		}
		public void run() {
			while(!TCPDone){
			try {
				for(int i=0;i<DataArray.length;i++){
					// initialize with null character every time for reading in new data
					DataArray[i]='\0';
				}
				// receive data and write data to the files
				receiverSocket.receive(receivePacket);
				
				byte[] bytesReceived = receivePacket.getData();
				if (sendACK == null){
					sendACK = new Socket(InetAddress.getByName(remoteIP),remotePort);
					tcpOut=sendACK.getOutputStream();
					continue;
				}
				
				TCPHeader header = TCPHeader.parseHeader(bytesReceived);
				byte[] data = TCPHeaderHelper.retrieveData(bytesReceived,receivePacket.getLength());
				boolean isCorrupt = TCPHeaderHelper.isCorrupt(bytesReceived);
				System.out.println("Data is Good: " +!isCorrupt);
				writeToLogfile(header,isCorrupt);
				
				if(!isCorrupt){
					if(header.FIN == true){
						sendACK(onlyACKbit(ackNumber+1));
						TCPDone=true;
						continue;
					}else{
						reactOnGoodPacket(header.sequenceNumber,data);
						continue;
					}
				}else{
					sendACK(onlyACKbit(ackNumber));
					continue;
				}
			} catch (IOException e) {e.printStackTrace();}
			}
			System.out.println("Data transmission is done!!!!");
			System.exit(-1);
		} 
		public void reactOnGoodPacket(int sequenceNumber,byte[] data) throws IOException{
			if(sequenceNumber<ackNumber){
				return;
			}
			set.add(new setEntry(sequenceNumber,data));
			while(!set.isEmpty() && set.first().sequenceNumber==ackNumber){
				byte[] dataToWrite=set.pollFirst().data;
				WriteContent(dataToWrite);
				ackNumber+=dataToWrite.length;
			}
			// if all data has been written, send ACK
			sendACK(onlyACKbit(ackNumber));
		}
	}

	public void sendACK(byte[] ack) throws IOException{
		tcpOut.write(ack);
		tcpOut.flush();
	}
	
	public void writeToLogfile(TCPHeader header,boolean isBad){
		StringBuffer sb = new StringBuffer();
		try{
			Date timeStamp = new Date();
			sb.append(timeStamp.toString()+" ");
			sb.append(header.portNumber+" ");
			sb.append(header.destinationPort+" ");
			sb.append("Sequence # "+header.sequenceNumber+" ");
			sb.append("ACK # "+header.acknowledgementNumber+" ");
			if(header.ACK){
				sb.append("Type: ACK ");
			}
			if(header.FIN){
				sb.append("Type: FIN ");
			}
			if(!header.ACK&&!header.FIN){
				sb.append("Type: NORMAL ");
			}
			if(isBad){
				sb.append("Status: CORRUPT ");
			}
			else{
				sb.append("Status: GOOD ");
			}

			String result=sb.toString();
			WriteLogFile(result);
			if(logFileName.equals("stdout.txt")){
				System.out.println(result);
			}
		}catch(IOException e){e.printStackTrace();}
	}
	public void WriteLogFile(String Content) throws IOException{
		logfileWriter.write(Content);
		logfileWriter.newLine();
		logfileWriter.flush();
	}
	public void WriteContent(byte[] Content) throws IOException{
		fileWriter.write(Content);
		fileWriter.flush();
	}
}
