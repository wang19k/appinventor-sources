package openblocks.codeblockutil;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.*;
import org.apache.commons.io.*;


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
	private static final String AB = "0123456789abcdefghijklmnpqrstuvwxyz";
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
			      System.out.println("Let's show that notice!");
			      try {
				  System.out.println("Well, I tried.");
				  URL url = new URL(theUrl + CONNECTING_MSG);
				  URLConnection con = url.openConnection();
				  System.out.println("Opening a URL connection");
				  InputStream in = con.getInputStream();
				  System.out.println("Well, input stream worked fine.");
				  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				  System.out.println("BufferedReader worked fine.");
				  String jsonString = reader.readLine();
				  while (jsonString == null) {
				      url = new URL(theUrl + CONNECTING_MSG);
				      con = url.openConnection();
				      System.out.println("Opening a URL connection");
				      in = con.getInputStream();
				      System.out.println("Well, input stream worked fine.");
				      reader = new BufferedReader(new InputStreamReader(in));
				      System.out.println("BufferedReader worked fine.");
				      jsonString = reader.readLine();
				      System.out.println("JSON read the line");
				      Thread.sleep(100);
				  }
				  System.out.println("Cool, it stopped being NULL and connected");
				  JSONObject jsonObject = new JSONObject(jsonString);
				  System.out.println("Made the JSON object");
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
			      } catch(Exception e) {
				  System.out.println("It did not work." + e.toString());//return
				  e.printStackTrace(System.out);
			      }

			  }
		      });
		}});
	}
	private void showError(String message) {
		HTMLPane htmlMsg = new HTMLPane(message);
		String title = "Error";
		JOptionPane.showMessageDialog(frame, htmlMsg, title, JOptionPane.PLAIN_MESSAGE);
	}

	private void showWirelessNotice() {
		String title = "Starting the wireless connection.";
		String msgText = "This is your 5 digit code: " + CONNECTING_MSG;
		HTMLPane htmlMsg = new HTMLPane(msgText);
	    JOptionPane.showMessageDialog(frame, htmlMsg, title, JOptionPane.PLAIN_MESSAGE);

	}
}
