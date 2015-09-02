import java.nio.ByteBuffer;

// implement a TCP Header
public class TCPHeader {
	short portNumber;
	short destinationPort;
	int sequenceNumber;
	int acknowledgementNumber;
	boolean FIN;
	boolean ACK;
	short window;
	// checksum is a short number here
	short checksum;
	
	// given a input packet, retrieve header from it
	public static TCPHeader parseHeader(byte[] inputPacket){
		TCPHeader header = new TCPHeader();
		header.portNumber= (short)((inputPacket[0]<<8 & 0xff00)|inputPacket[1] & 0xff);
		header.destinationPort= (short)((inputPacket[2]<<8&0xff00)|inputPacket[3]&0xff);
		header.sequenceNumber=(inputPacket[4]<<24 & 0xff000000 | inputPacket[5]<<16&0xff0000 | inputPacket[6]<<8&0xff00 | inputPacket[7]&0xff);
		header.acknowledgementNumber=(inputPacket[8]<<24 & 0xff000000|inputPacket[9]<<16&0xff0000 | inputPacket[10]<<8&0xff00 | inputPacket[11]&0xff);		
		header.ACK = getAckBoolean(inputPacket[13]);
		header.FIN = getFinBoolean(inputPacket[13]);
		header.window=(short)((inputPacket[14]<<8&0xff00)|inputPacket[15]&0xff);
		header.checksum=(short)((inputPacket[16]<<8&0xff00)|inputPacket[17]&0xff);
		return header;
	}
	// generate header
	public byte[] generateHeader (byte[] data){
		// allocate 20 bytes for a byte buffer
		ByteBuffer generatedHeader = ByteBuffer.allocate(20);
		generatedHeader.putShort(portNumber); // 2 bytes
		generatedHeader.putShort(destinationPort); // 2 bytes
		generatedHeader.putInt(sequenceNumber); // 4 bytes
		generatedHeader.putInt(acknowledgementNumber); // 4 bytes
		generatedHeader.put(wrapACKFIN(0,ACK,FIN));
		generatedHeader.putShort(window); // 2 bytes
		generatedHeader.putShort((short)calculateCheckSum2(generatedHeader.array(),data)); // 4 bytes  
		return generatedHeader.array();
	}
	public static int calculateCheckSum1(byte[] header){
		int result;
		result = addAllBytes(header);
		return(~result)&0xffff;
	}
	public static int calculateCheckSum2(byte[] generatedHeader, byte[] data){
		int result;
		result = addAllBytes(generatedHeader) + addAllBytes(data);
		return(~result)&0xffff;
	}
	// add all bytes together in a byte array
	public static int addAllBytes (byte[] arr){
		int result = 0;
		for(int i = 0; i<=arr.length-1; i+=2){
			if(i != arr.length-1){
				// OR neighbor two values
				result +=((arr[i]<<8 & 0xff00)|arr[i+1]&0xff);
			}else{ // reach end
				result +=(arr[i]<<8 & 0xff00);
			}
		}
		return result;
	}

	// wrap ACK and FIN into a 2-byte long byte array
	public static byte[] wrapACKFIN(int space, boolean ACK, boolean FIN){
		byte[] wrapper = new byte[2];
		byte b;
		wrapper[0]=(byte)(space<<4);
		if (ACK){
			b = (byte)(0x01<<4);
		}else{
			b = (byte)(0x00);
		}
		if(FIN){
			b=(byte)(b|0x01);
		}
		wrapper[1]=b;
		return wrapper;
	}
	
	// get ack boolean value
	public static boolean getAckBoolean(byte input){
		return ((input>>4 & 0x01)==1);
	}
	// get fin boolean value
	public static boolean getFinBoolean(byte input){
		return ((input & 0x01)==1);
	}

		
}
