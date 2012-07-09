package openblocks.yacodeblocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import openblocks.yacodeblocks.DeviceReplCommController.ReadWriteThread;

/**
 * 
 * Handles the wireless connection between the phone and the computer
 * 
 * @author lmercer@mit.edu (Logan Mercer)
 *
 */
public class WirelessReplCommController {

	
	private String host;
	private int port;
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	
	public WirelessReplCommController(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	 public void setupSocket(){
		 try{
			
		    socket = new Socket(host, port);
		    // Note that this call does not fail even if there's nothing running on
		    // the other end.  Also, there's no problem getting the streams.  Sigh.
		    out = socket.getOutputStream();
		    in = socket.getInputStream();
		    int ch = in.read(); // We'll put it back before the thread tries to read it.
		 }catch(Exception e){
			 //Do something
		 }
		 
	  }
}
