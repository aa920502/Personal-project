import java.nio.ByteBuffer;
import java.util.Arrays;


public class TCPHeaderHelper {
	// set mss to be 576
	static int mss=576;
	public static byte[] createSegment(byte[] data, short portNumber, short destinationNumber, int sequenceNumber, int acknowledgementNumber, boolean ACK,boolean FIN,  short window){
		TCPHeader tcpHeader = new TCPHeader();
		tcpHeader.portNumber = portNumber;
		tcpHeader.destinationPort = destinationNumber;
		tcpHeader.sequenceNumber = sequenceNumber;
		tcpHeader.acknowledgementNumber = acknowledgementNumber;
		tcpHeader.ACK = ACK;
		tcpHeader.FIN = FIN;
		tcpHeader.window = window;

		// create wrapped packet and return as byte array
		ByteBuffer result = ByteBuffer.allocate(20+ data.length);
		byte[] segment = tcpHeader.generateHeader(data);
		result.put(segment);
		result.put(data);
		return result.array();
	}
	
	public static byte[] retrieveData(byte[] data, int dataSize){
		return Arrays.copyOfRange(data,20,dataSize);
	}
	
	public static boolean isCorrupt(byte[] input){
		if(TCPHeader.calculateCheckSum1(input)!=0 || input.length<20){
			return true;
		}
		return false;
	}

}

