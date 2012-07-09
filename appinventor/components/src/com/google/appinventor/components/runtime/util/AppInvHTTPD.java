package com.google.appinventor.components.runtime.util;
import java.util.Enumeration;
import java.util.Properties;
import java.io.File;
import java.io.IOException;


public class AppInvHTTPD extends NanoHTTPD {

        private File rootDir;

        public AppInvHTTPD( int port, File wwwroot) throws IOException
        {
	    super(port, wwwroot);
	    this.rootDir = wwwroot;
	}

	/**
	 *
	 * @param uri	Percent-decoded URI without parameters, for example "/index.cgi"
	 * @param method	"GET", "POST" etc.
	 * @param parms	Parsed, percent decoded parameters from URI and, in case of POST, data.
	 * @param header	Header entries, percent decoded
	 * @return HTTP response, see class Response for details
	 */
	public Response serve( String uri, String method, Properties header, Properties parms, Properties files )
	{
		myOut.println( method + " '" + uri + "' " );

		Enumeration e = header.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  HDR: '" + value + "' = '" +
								header.getProperty( value ) + "'" );
		}
		e = parms.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  PRM: '" + value + "' = '" +
								parms.getProperty( value ) + "'" );
		}
		e = files.propertyNames();
		while ( e.hasMoreElements())
		{
		        String fieldname = (String)e.nextElement();
			String tempLocation = (String) files.getProperty(fieldname);
			String filename = (String) parms.getProperty(fieldname);
			if (filename.startsWith("..") || filename.endsWith("..")
			    || filename.indexOf("../") >= 0) {
			    myOut.println(" Ignoring invalid filename: " + filename);
			    filename = null;
			}
			File fileFrom = new File(tempLocation);
			if (filename == null) {
			    fileFrom.delete(); // Cleanup our mess (remove temp file from /sdcard).
			} else {
			    File fileTo = new File(rootDir + "/" + filename);
			    fileFrom.renameTo(fileTo);
			}
			myOut.println( " UPLOADED: '" + filename + "' was at '" + tempLocation + "'");
		}

		return serveFile( uri, header, rootDir, true );
	}
}
