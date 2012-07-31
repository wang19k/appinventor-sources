package openblocks.codeblockutil;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.*;
import org.apache.commons.io.*;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

 
import openblocks.yacodeblocks.FeedbackReporter;
import openblocks.yacodeblocks.WorkspaceControllerHolder;
import openblocks.yacodeblocks.WorkspaceController;
import openblocks.yacodeblocks.AndroidController;
import openblocks.yacodeblocks.PhoneCommManager;

/**
 * Button for opening wireless connection between blocks editor and phone
 * 
 * @author lmercer@mit.edu (Logan Mercer)
 *
 */
public class CWirelessButton extends CSaveButton{

        private static final String YAIL_NEWLINE = "(newline)"; // Cribbed from PhoneCommManager
        private static final String REPL_CONFIRMATION = "Confirmation"; // ditto



        private WorkspaceController wc;

	public CWirelessButton() {
		super("Connect Wifi", "can't start connection");
	}

	private static JFrame frame;
	private static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
	private static Random rnd = new Random();
	public final String CONNECTING_MSG = randomString(5);
	public final String theUrl = "http://osiris.mit.edu/rendezvous/";
        private PhoneCommManager pcm; // When the OK option is selected from the message dialog we use this to start the REPL

	String randomString(int len) 
	{
		StringBuilder sb = new StringBuilder(len);
		for(int i=0; i<len; i++) 
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public void init() {
	    addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	          javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	              wc = (WorkspaceController) WorkspaceControllerHolder.get();
	              showWirelessNotice();
	              try{
	            	  URL url = new URL(theUrl + CONNECTING_MSG);
	            	  URLConnection con = url.openConnection();
	            	  InputStream in = con.getInputStream();
	            	  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	            	  String jsonString = reader.readLine();
	            	  JSONObject jsonObject = new JSONObject(jsonString);
	            	  String ipAddress = (String) jsonObject.get("ipaddr");
	            	  System.out.println("CWirelessButton: ipaddr = " + ipAddress);
	            	  WifiAndroidController ac = new WifiAndroidController();
	            	  ac.setIpAddress(ipAddress);
	            	  wc.setupWirelessConnection(ac, ipAddress);
	            	  pcm = wc.getPhoneCommManager();
	            	  pcm.setAndroidController(ac);
	            	  pcm.setConnectedToPhone(true); // Kludge -- no USB cable, so implicitly connected
	            	  System.out.println("CWirelessButton: Set Connected to Phone.");
	            	  pcm.initReplController();
	            	  pcm.replControllerCreateAndSendAsync(YAIL_NEWLINE, REPL_CONFIRMATION, new Long(0), false);
	              }catch(Exception e){
	            	  System.out.println("It did not work." + e.toString());//return 
	              }
	              
	            }
	          });
	        }});
	}
	private void showWirelessNotice() {
		String title = "Starting the wireless connection.";
		String msgText = "This is your 5 digit code: " + CONNECTING_MSG;
		generateQRCode();
		String path = "/tmp/qrcode.png";
//		java.net.URL imgURL = getClass().getResource(path);
		ImageIcon qrcode = new ImageIcon(path, "This should be the qr code.");
		System.out.println("Made the ImageIcon");
		if (qrcode == null)
			System.out.println ("LOLOL it is null");
		else
			System.out.println ("Looks like qrcode does something");
		JLabel label1 = new JLabel("Scan Me", qrcode, JLabel.CENTER);
		JLabel label2 = new JLabel(msgText);
		JPanel connectingMsg = new JPanel();
		connectingMsg.add(label1);
		connectingMsg.add(label2);
		
	    JOptionPane.showMessageDialog(frame, connectingMsg, title, JOptionPane.PLAIN_MESSAGE, qrcode);
		
	}
	private void generateQRCode() {
		Charset charset = Charset.forName("ISO-8859-1");
	    CharsetEncoder encoder = charset.newEncoder();
	    byte[] b = null;
	    try {
	        // Convert a string to ISO-8859-1 bytes in a ByteBuffer
	        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(CONNECTING_MSG));
	        b = bbuf.array();
	    } catch (CharacterCodingException e) {
	        System.out.println(e.getMessage());
	    }

	    String data = null;
	    try {
	        data = new String(b, "ISO-8859-1");
	    } catch (UnsupportedEncodingException e) {
	        System.out.println(e.getMessage());
	    }

	    // get a byte matrix for the data
	    BitMatrix matrix = null;
	    int h = 100;
	    int w = 100;
	    com.google.zxing.Writer writer = new QRCodeWriter();
	    try {
	        matrix = writer.encode(data,
	        com.google.zxing.BarcodeFormat.QR_CODE, w, h);
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }

	    String filePath = "/tmp/qrpng.png";
	    File file = new File(filePath);
	    try {
	        MatrixToImageWriter.writeToFile(matrix, "PNG", file);
	        System.out.println("printing to " + file.getAbsolutePath());
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	}
}
