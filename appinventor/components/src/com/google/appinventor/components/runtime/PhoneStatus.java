package com.google.appinventor.components.runtime;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.YaVersion;

/**
 * This component keeps count of steps using the accelerometer.
 *
 */
@DesignerComponent(version = YaVersion.PHONESTATUS_COMPONENT_VERSION,
                   description = "Component that returns information about the phone.",
                   category = ComponentCategory.INTERNAL,
                   nonVisible = true,
                   iconName = "images/pedometer.png")
@SimpleObject

public class PhoneStatus extends AndroidNonvisibleComponent implements Component {
	
	private static Activity activity;
	
	  public PhoneStatus(ComponentContainer container) {
		super(container.$form());
		activity = container.$context();
	}

	  @SimpleFunction(description = "Returns the IP address of the phone in the form of a String")
	  public static String GetWifiIpAddress(){
		  DhcpInfo ip;
		  Object wiman = activity.getSystemService("wifi");
		  ip = ((WifiManager) wiman).getDhcpInfo();
	      int s_ipAddress=(ip.ipAddress);
	      String whatWeWant;
	      if (isConnected())
	    	  whatWeWant = intToIp(s_ipAddress);
	      else
	    	  whatWeWant = "Error: No Wifi Connection";
		  return whatWeWant;
	  }
	  
	  @SimpleFunction(description = "Returns TRUE if the phone is on Wifi, FALSE otherwise")
	  public static boolean isConnected() {
		    ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService("connectivity");
		    NetworkInfo networkInfo = null;
		    if (connectivityManager != null) {
		        networkInfo =
		            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		    }
		    return networkInfo == null ? false : networkInfo.isConnected();
		}
	  
	  public static String intToIp(int i) {

	  		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >>24) & 0xFF);
		}
}
