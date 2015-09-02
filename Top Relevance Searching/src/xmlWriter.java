
import java.io.*;
// Write xml result to a file named "searchResult.xml"
public class xmlWriter {
	public void write(String s){
		try{ 
             BufferedWriter out=new BufferedWriter(new FileWriter( "searchResult.xml")); 
             out.write(s); 
             out.close(); 
		}catch(IOException e){ 
     }
	}

}