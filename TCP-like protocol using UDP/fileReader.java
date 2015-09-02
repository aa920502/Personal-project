import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class fileReader {
	// define mss and filename, mss is used to group mss length bytes into a string
	int mss;
	String fileName;
	
	public fileReader(int mss, String filename){
		this.mss = mss;
		this.fileName = filename;
	}
	// get file length in bytes
	public long fileLength(){
		File file = new File(fileName);
		return file.length();
	}
	
	// read a file with filename, return all content in arraylist<byte> format
	public ArrayList<byte[]> readFile(String filename){ 
		ArrayList<byte[]> ret = new ArrayList<byte[]>();
		File file = new File(fileName);
		if(!file.exists()) 
			System.out.print("File with filename " + fileName + " does not exist.");
		else{
			try{
				@SuppressWarnings("resource")
				FileInputStream reader = new FileInputStream(file);
				int readInput;
				byte[] byteSet = new byte[mss];
				int byteCounter =0; // counter for mss
				
				while((readInput= reader.read())!=-1){
					byteSet[byteCounter%mss]=(byte)readInput;
					byteCounter++;
					if(byteCounter%mss==0){
						ret.add(Arrays.copyOfRange(byteSet,0,mss));
						byteSet=new byte[mss];
					}
				}
				if(byteCounter%mss!=0){
					ret.add(Arrays.copyOfRange(byteSet,0,byteCounter%mss));
				}
			}catch(IOException e){e.printStackTrace();}
		}
		return ret;
	}
}
