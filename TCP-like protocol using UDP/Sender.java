import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/*
 * sender <filename> <remote_IP> <remote_port> <ack_port_num> <log_filename> <window_size>
 */
public class Sender {
	// UDP socket for sending file
	DatagramSocket senderSocket;
	// TCP socket for receiving ack from receiver
	static Socket ACKSocket;
	// TCP input reader for reading TCP incomming stream
	InputStream InputReader;
	//boolean for marking if TCP is finished
    boolean TCPDone = false;
    long FileLength;
    
    // sender base packet number
    int senderBasePacketNumber = 0;
    //sender current packet number
    int senderCurPacketNumber = 0;
    // timer for counting time out
    Timer timer;
    RoundTripTimeManager rttController;
    int curReceivedAckNumber;
    int totalSentPacketNumber = 0;
    BufferedWriter logfileWriter;
    
    static String filename;
    static String remoteIP;
    static int remotePort;
    static int port;
    static String logFileName;
    static int windowSize;
    static int mss;
    
	public static void main(String[] args) throws Exception {
	    if(args.length<6){
	    	System.out.print("Please Enter Correct Command");
	    }else{
	    	// parse user input
	    	filename = args[0];
	    	remoteIP = args[1];
	    	remotePort = Integer.parseInt(args[2]);
	    	port = Integer.parseInt(args[3]);
	    	logFileName = args[4];
	    	windowSize = Integer.parseInt(args[5]);
	    	//receiveAckFromReceiver = new ServerSocket(port);
	    	Sender s = new Sender(filename,remoteIP,remotePort,port,logFileName,TCPHeaderHelper.mss, windowSize);
	    	// send file with filename
	    	s.sendFile(filename,remoteIP,remotePort,port, TCPHeaderHelper.mss);
	    }
	}

	public void setupLogFile(String fileName) throws IOException{
		File senderLogFile = new File("Sender/"+logFileName);
		if(!senderLogFile.exists())
			senderLogFile.createNewFile();
		logfileWriter = new BufferedWriter(new FileWriter(senderLogFile));
	}
	
	public Sender ( String filename,String remoteIP,int remotePort,int port,String logFileName, int mss,int windowSize) throws IOException{
		Sender.filename = filename;
		Sender.remoteIP = remoteIP;
		Sender.remotePort = remotePort;
		Sender.port = port;
		Sender.logFileName = logFileName;
		Sender.mss=mss;
		Sender.windowSize = windowSize;
		try {
			// set up timer = 5s
			timer= new Timer(5000);
			setupLogFile(logFileName);

			senderSocket = new DatagramSocket();
			new Thread(new TCPThread()).start();
			rttController = new RoundTripTimeManager(mss);
			System.out.println("UDP senderSocker Created");
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	class TCPThread implements Runnable{
		public void run(){
			try{
				@SuppressWarnings("resource")
				ServerSocket senderSocket = new ServerSocket(port);
				ACKSocket=senderSocket.accept();
				InputReader=ACKSocket.getInputStream();
				receiveAcks();
			}catch(IOException e){System.out.println("ERROR!");}
		}
	}
	// receive ack thread
	public void receiveAcks() throws IOException{
		Thread Receive = new Thread(new ReceivingAck());
		Receive.start();
	}
	
	// read in a file and send packets as segments
	public void sendFile(String filename, String remoteIP, int remotePort, int port,  int mss) throws InterruptedException {
	    fileReader fr= new fileReader(mss,filename);
	    FileLength = fr.fileLength();
	    ArrayList<byte[]> packetsToSend = fr.readFile(filename);
		while(ACKSocket==null){
			sendPacketWithSeq(packetsToSend.get(0),0,remoteIP,remotePort);
			Thread.sleep(100);
		}
		timer.startTimer();
		while(!TCPDone){
			if(timer.isTimeOut){
				senderCurPacketNumber=senderBasePacketNumber;
				timer.clearTimer();
			}
			if(senderCurPacketNumber<senderBasePacketNumber){
				senderCurPacketNumber=senderBasePacketNumber;
			}
			if(senderCurPacketNumber<senderBasePacketNumber+windowSize&&senderCurPacketNumber<packetsToSend.size()){
				rttController.save(senderCurPacketNumber*mss);
				sendPacketWithSeq(packetsToSend.get(senderCurPacketNumber),senderCurPacketNumber*mss, remoteIP, remotePort);
				senderCurPacketNumber++;
				totalSentPacketNumber++;
				
			}
			// send FIN to end connection
			while(!TCPDone&&curReceivedAckNumber>=(int)FileLength){
				sendPacket(onlyFINbit(curReceivedAckNumber),remoteIP, remotePort);
			}
			try{Thread.sleep(10);
			}catch(InterruptedException e){e.printStackTrace();}
		}
		// if the specified log filename is ¡°stdout¡±, display following message
		if(logFileName.equals("stdout.txt")){
			System.out.println("Dilivery completed successfully");
			System.out.println("Total bytes sent = "+FileLength);
			System.out.println("Segments sent = "+totalSentPacketNumber);
			System.out.println("Segments retransmitted = "+(totalSentPacketNumber-packetsToSend.size()));
		}
		System.exit(-1);
	}
	
	public void sendPacketWithSeq (byte[] data, int seq,String remoteIP, int remotePort){
		byte[] segment = TCPHeaderHelper.createSegment(data,(short)port,(short)remotePort, seq, 0, false, false, (short)100);
		sendPacket(segment,remoteIP, remotePort);
	}
	// method for sending packets to remote IP and remote port using UDP socket
	public void sendPacket(byte[] segment, String remoteIP, int remotePort) {
		try {
			InetAddress ipAddr = InetAddress.getByName(remoteIP);
			DatagramPacket senderPacket = new DatagramPacket(segment, segment.length, ipAddr, remotePort);
			senderSocket.send(senderPacket);
		} catch (IOException e) {e.printStackTrace();}
	}
	public byte[] onlyFINbit(int finNumber){
		return TCPHeaderHelper.createSegment(new byte[0],(short)port,(short)remotePort,finNumber,0,false,true,(short)1);
	}

	class ReceivingAck implements Runnable{
		public void run() {
			while (!TCPDone){
				try {
					byte[] bytes = new byte[20]; 
					for(int i=0;i<20;i++){
						byte temp = (byte)(InputReader.read());
						bytes[i]=temp;
					}
				    TCPHeader parsedHeader = TCPHeader.parseHeader(bytes);
				    writeToLogfile(parsedHeader,true); // write to log file
				    // if last packet is received, TCP session is done
				    if(parsedHeader.acknowledgementNumber==(int)FileLength+1){
						TCPDone=true;
						return;
					}
					rttController.reactOnACK(parsedHeader.acknowledgementNumber);
					if(rttController.estimatedTimeoutInterval()>0){
						if(rttController.RTTdeviation()!=0){
							timer.set((int)rttController.estimatedTimeoutInterval());
						}
					}
					if(parsedHeader.acknowledgementNumber>senderBasePacketNumber*mss){
						senderBasePacketNumber=parsedHeader.acknowledgementNumber/mss;
						timer.resetTimer();
					}
					curReceivedAckNumber=parsedHeader.acknowledgementNumber;
				    continue;
				} catch (IOException e) {e.printStackTrace();}
				System.out.println("ACK reveived and written");
			}
		}
	}

	public void writeToLogfile(TCPHeader header,boolean isGood){	
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
			if(!isGood){
				sb.append("Status: CORRUPT ");
			}
			else{
				sb.append("Status: GOOD ");
			}
			if(rttController!=null){
				sb.append("Estimated RTT: " + rttController.estimatedRTT);
			}
			String result=sb.toString();
			WriteLogFile(result);
		}catch(IOException e){e.printStackTrace();}
	}
	
	public void WriteLogFile(String Content) throws IOException{
		logfileWriter.write(Content);
		logfileWriter.newLine();
		logfileWriter.flush();
	}
	
	//timer class for count down
	class Timer implements Runnable{
		boolean isTimeOut=false;
		long timeMark;
		int timeOutInterval;
		public Timer(int interval){
			this.timeOutInterval=interval;
		}
		public void startTimer(){
			timeMark=System.currentTimeMillis();
			Thread newTimer = new Thread(this);
			newTimer.start();
		}
		public void set(int interval){
			this.timeOutInterval=interval;
		}
		
		public void run(){
			while(true){
			if(System.currentTimeMillis()-timeMark>timeOutInterval){
				isTimeOut=true;
				timeMark=System.currentTimeMillis();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		public void resetTimer(){
			timeMark = System.currentTimeMillis();
		}
		public void clearTimer(){
			isTimeOut=false;
		}
	}
	
	class RoundTripTimeManager{
		Hashtable<Integer,tableEntry> table;
		int mss;
		long sampleRTT=-1;
		// estimated RTT
		long estimatedRTT=-1;
		//mean deviation of the RTT samples
		long RTTdeviation=0;
		long estimatedTimeoutInterval=-1;
		
		public RoundTripTimeManager(int mss){
			table=new Hashtable<Integer,tableEntry>();
			this.mss=mss;
			this.estimatedRTT = estimatedRTT;
			this.RTTdeviation =RTTdeviation;
			this.estimatedTimeoutInterval = estimatedTimeoutInterval;
		}
		public long RTTdeviation(){
			return RTTdeviation;
		}
		public long estimatedRTT(){
			return estimatedRTT;
		}
		public long estimatedTimeoutInterval(){
			return estimatedTimeoutInterval;
		}
		
		public void save(int sequenceNumber){
			if(table.get(sequenceNumber)==null){
				table.put(sequenceNumber,new tableEntry());
			}else{
				table.get(sequenceNumber).status=false;
			}
		}
		
		public long reactOnACK(int ackNumber){
			int sequenceNumber;
			if (ackNumber%mss==0){
				sequenceNumber= ackNumber-mss;
			}else{
				sequenceNumber= ackNumber-ackNumber%mss;
			}
			
			if(table.get(sequenceNumber)!=null && table.get(sequenceNumber).status){
				long rtt =System.currentTimeMillis()-table.get(sequenceNumber).sentTime;
				if (estimatedRTT==-1){
					estimatedRTT=rtt;
				}else{
					estimatedRTT=(long) (0.85*estimatedRTT+0.15*rtt);
				}
				RTTdeviation=(long) (0.75*RTTdeviation+0.25*Math.abs(rtt-estimatedRTT));
				estimatedTimeoutInterval=(long)(estimatedRTT+4*RTTdeviation);
				table.get(sequenceNumber).status=false;
				sampleRTT=rtt;
			}
			sampleRTT=-1;
			return -1;
		}
		class tableEntry{
			long sentTime;
			boolean status;
			public tableEntry(){
				sentTime=System.currentTimeMillis();
				status=true;
			}
		}
	}

	
}
