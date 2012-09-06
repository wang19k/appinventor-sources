package openblocks.codeblockutil;
import openblocks.yacodeblocks.AndroidController;
import openblocks.yacodeblocks.AndroidControllerException;
import openblocks.yacodeblocks.ExternalStorageException;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class WifiAndroidController implements AndroidController {

    private String ipAddress = null; // IP Address to push assets to

    public WifiAndroidController() {
    }

    public void deviceConnected(String serialNumber) {
	return;			// Nothing to do for Wifi (yet)
    }

    public void deviceDisconnected(String serialNumber) {
	return;			// Nothing to do for Wifi (yet)
    }

    public void setDeviceListener(DeviceConnectionListener listener) {
	return;
    }

    public boolean androidInitializeCommunicationBridge(String adbLocation) {
	return true;
    }

    public boolean androidRestartBridge() {
	return true;
    }

    public void androidSyncAndInstallStarterApplication()  {
	return;
    }

    public void androidSyncAndInstallSpecificApplication(String apkFilePath, String appName,
							 String packageName) {
	return;
    }

    public boolean androidIsStarterApplicationRunning() {
	return true;
    }

    public void androidStartStarterApplication() {
	return;
    }

    public void androidKillStarterApplication() {
	return;
    }

    public void androidKillSpecificApplication(String packageName) {
	return;
    }

    public boolean androidForwardTcpPort(int localPort, int remotePort) {
	return true;
    }

    public void androidCleanUpBeforeExit() {
	return;
    }

    public void pushFileToDevice(String fileToPush, String remotePath)
	throws AndroidControllerException, ExternalStorageException {
	// This is where we will push assets to the phone.. TBD XXX
	System.out.println("pushFileToDevice: fileToPush: \"" + fileToPush + "\" remotePath: \"" + remotePath + "\"");
	String [] r = remotePath.split("/"); // We only want the last part!
	String filename = r[r.length - 1];
	System.out.println("pushFileToDevice: filename = \"" + filename + "\"");
	try {
	    (new Uploader()).SendFile(ipAddress, fileToPush, filename);
	} catch (Exception e) {
	    e.printStackTrace(System.out);
	    throw new AndroidControllerException(e.toString());
	}
    }

    public void selectDevice(String device) {
	return;
    }

    public void selectDevice(String device, String ipAddress) {
	return;
    }

    public String getSelectedDevice() {
	return ("WiFi");
    }

    // JIS: Below is not part of the AndroidController interface -- it is used to inform this controller of the IP address
    // to push assets to.

    public void setIpAddress(String ipAddress) {
	this.ipAddress = ipAddress;
    }

    // Do this actual stuffing!

    private class Uploader {

	private static final String CrLf = "\r\n";

	private void SendFile(String ipAddress, String fileToPush, String filename) throws IOException {

	    FileInputStream is = null;
	    OutputStream os = null;
	    InputStream cin = null;

	    try {
		URL url = new URL("http://" + ipAddress + ":8000");
		File file = new File(fileToPush);
		long filelength = file.length();

		System.out.println("url:" + url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		String postData = "";

		is = new FileInputStream(file);

		String message1 = "";
		message1 += "-----------------------------4664151417711" + CrLf;
		message1 += "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + filename + "\"" + CrLf;
		message1 += "Content-Type: application/octet-string" + CrLf;
		message1 += CrLf;

		// the image is sent between the messages in the multipart message.

		String message2 = "";
		message2 += CrLf + "-----------------------------4664151417711--"
		    + CrLf;

		conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=---------------------------4664151417711");
		// might not need to specify the content-length when sending chunked
		// data.
		conn.setRequestProperty("Content-Length", String.valueOf((message1
									  .length() + message2.length() + filelength)));

		os = conn.getOutputStream();

		os.write(message1.getBytes());

		// SEND THE FILE
		int index = 0;
		int size = 1024;
		byte [] data = new byte[size];
		int r;
		do {
		    r = is.read(data, 0, size);
		    if (r > 0)
			os.write(data, 0, r);
		} while (r > 0);

		os.write(message2.getBytes());
		os.flush();

		System.out.println("open is");
		cin = conn.getInputStream();

		int len;
		do {
		    System.out.println("READ");
		    len = cin.read(data, 0, size);

		    if (len > 0) {
			System.out.println(new String(data, 0, len));
		    }
		} while (len > 0);

		System.out.println("DONE");
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		System.out.println("Close connection");
		try {
		    os.close();
		} catch (Exception e) {
		}
		try {
		    is.close();
		} catch (Exception e) {
		}
		try {
		    cin.close();
		} catch (Exception e) {
		}
	    }

	}
    }
}

