/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.core.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Implements an efficient version of a URLConnection that can handle a GET or 
 * a POST (URLConnection is quite slow).  
 *
 * If you call the setPostData method, this will implement a POST, otherwise
 * it implements a GET.
 *
 * Example usage:
 * <PRE>
 *    HTTPConnection c = new HTTPConnection(someURL);
 *    c.setPostData("param1=one&param2=two");
 *    String cType = c.getContentType();
 *    InputStream cStream = c.getInputStream();
 *    ...read back the results from the GET/POST request...
 * </PRE>
 * Bypasses all content-handler creation, to avoid running afoul of security.
 *
 * @author Jeff Vroom (derived from HTTPPostConnection by Joe Berkovitz)
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/HTTPConnection.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class HTTPConnection extends URLConnection {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/HTTPConnection.java#2 $$Change: 651448 $";

  //-------------------------------------
  /** our socket connection to the host */
  protected Socket mSocket;

  /** input and output streams for mSocket */
  protected DataInputStream mIn;  
  protected DataOutputStream mOut;  

  /** some data to be sent when the connection is established */
  protected String mPostData = null;

  /** hash table of headers for returned data, filled in as they're read */
  protected Hashtable mHeaders = new Hashtable(5);

  /** Vector of header strings to be sent out with the request */
  protected Vector mRequestProperties = null;

  /**
   * vector of header keys; preserves the order in which they were received 
   */
  protected Vector mHeaderKeys = new Vector(5);

  /** The status code from the request */
  protected int mStatusCode;

  /** The status message from the request */
  protected String mStatusMessage;

  /** Should we dump out headers as we process the request */
  protected boolean mVerboseHeaders = false;

  //-------------------------------------
  //-------------------------------------
  /**
   * Construct a connection object for a given URL
   */

  public HTTPConnection (URL url) {
    super(url);
  }

  //-------------------------------------
  /**
   * Connect to the URL and post data if we haven't done so yet
   */

  public void connect() throws IOException {
    if (!connected) {
      openSocket();		// open socket to host

      writeRequest();		// write request to socket
      readHeaders();		// suck in headers from the input stream

      connected = true;
    }
  }

  //-------------------------------------
  /**
   * Gets a header field by name. Returns null if not known.
   * @param name the name of the header field
   */
  public String getHeaderField(String name) {
    if (name == null)
      return null;		// no key, no value!

    try { connect(); }
    catch (IOException e) { return null; }
    Object header = mHeaders.get(name); // look up header key in Hashtable
    
    if (header != null && header instanceof Vector) 
    {
        //if it's a vector, return first element
        return (String)((Vector)header).elementAt(0);
    }
    else
        return (String) header; 
  }

  //-------------------------------------
  /**
   * Gets a header field by name. Returns null if not known.
   * returns a vector of values if there are multiple values for the same header
   * @param name the name of the header field
   */
  public Object getHeaderFields(String name) {
    if (name == null)
      return null;		// no key, no value!

    try { connect(); }
    catch (IOException e) { return null; }

    return mHeaders.get(name); // look up header key in Hashtable
  }

  //-------------------------------------
  /**
   * Returns the key for the nth header field. Returns null if
   * there are fewer than n fields.  This can be used to iterate
   * through all the headers in the message.
   */
  public String getHeaderFieldKey(int n) {
    try { connect(); }
    catch (IOException e) { return null; }

    try { return (String) mHeaderKeys.elementAt(n); }
    catch (ArrayIndexOutOfBoundsException e) { return null; }
  }

  //-------------------------------------
  /**
   * Returns the value for the nth header field. Returns null if
   * there are fewer than n fields.  This can be used in conjunction
   * with getHeaderFieldKey to iterate through all the headers in the message.
   */
  public String getHeaderField(int n) {
    try { connect(); }
    catch (IOException e) { return null; }

    return getHeaderField (getHeaderFieldKey(n));
  }

  //-------------------------------------
  /**
   * Calls this routine to get an InputStream that reads from the object.
   * Protocol implementors should use this if appropriate.
   * @exception UnknownServiceException If the protocol does not
   * support input.
   */
  public InputStream getInputStream() throws IOException {
    connect();

    return mIn;
  }

  /**
   * Returns a DataInputStream wrapped around a BufferedInputStream.
   * This is a more efficient way to access the data in the URL
   */
  public DataInputStream getDataInputStream() throws IOException {
    connect();

    return mIn;
  }

  /**
   * Sets the general request property. 
   *
   * @param   key     the keyword by which the request is known
   *                  (e.g., "<code>accept</code>").
   * @param   value   the value associated with it.
   * @since   JDK1.0
   */
  public void setRequestProperty(String key, String value) {
    if (connected)
        throw new IllegalAccessError("Already connected");

    if (mRequestProperties == null) mRequestProperties = new Vector(1);
    mRequestProperties.addElement((key + ": " + value + "\r\n").getBytes());
  }

  //-------------------------------------
  /**
   * Should we dump out headers as we read them in
   */
  public void setVerboseHeaders(boolean pFlag) {
    mVerboseHeaders = pFlag;
  }

  //-------------------------------------
  /**
   * Set the data to be posted as part of the request.  Any encoding
   * to be applied to the data should already have taken place.  Calling
   * this method automatically turns the connection from a "GET" into a "POST".
   * @param iPostData the raw data to be posted
   */
  public void setPostData (String iPostData) {
    if (connected)
      throw new IllegalAccessError("Already connected");
    mPostData = iPostData;
  }

  /**
   * Returns the status code from the HTTP request.  
   */
  public int getStatusCode() {
    return mStatusCode;
  }

  /**
   * Returns the status message from the HTTP request.  This is the
   * the string at the end of the header line:
   *  <pre>HTTP/1.0 <status-code> <status-message></pre>
   */
  public String getStatusMessage() {
    return mStatusMessage;
  }

  String mProxyHost = null;
  int mProxyPort;
  public void setProxyServer(String pHost, int pPort) {
    mProxyHost = pHost;
    mProxyPort = pPort;
  }

  //-------------------------------------
  /** Open the socket for this URL */
  
  protected void openSocket() throws IOException {
    if (mProxyHost == null) {
      int port = url.getPort();
      mSocket = new Socket(url.getHost(),
			   (port >= 0) ? port : 80);
    }
    else {
      mSocket = new Socket(mProxyHost, mProxyPort);
    }
    mIn = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
    mOut = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
  }

  //-------------------------------------
  /* Read in header lines until we see a blank line; each header 
   * is split into key and value pairs and stored for later retrieval
   * via getHeaderField() and its callers.
   */

  protected void readHeaders() throws IOException {
    boolean statusRead = false;

    mStatusCode = 0;
    mStatusMessage = null;

    for (;;) {
      String header = mIn.readLine();

      if (mVerboseHeaders && header != null) {
        System.out.println(header);
      }

      if (header == null || header.length() == 0)
	break;		// saw a blank line
	
      int colonIndex = header.indexOf(':');
      if (colonIndex >= 0) {
	    // split up key and value by colon
	    String key = header.substring(0,colonIndex).trim().toLowerCase();
	    String value = header.substring(colonIndex+1).trim();

        //added by Tarek to account for multiple cookies being set the in the same request
        //check if we already have a header with the same key
        Object oldValue = mHeaders.get(key);
        if (oldValue == null) //first time we see this header
        {        
            // add the header value as a string
	        mHeaderKeys.addElement(key); // append key to sequential list of keys
	        mHeaders.put(key, value); // enter key/value pair in hashtable
        }
        else //we set this header before
        {
            Vector valueList;
            if (oldValue instanceof String) 
            {
                //create a new list of Header values 
                valueList = new Vector();
                //add the header value that was already there
                valueList.addElement(oldValue);
                //put it in the hashtable instead of the old String
                mHeaders.put(key, valueList);
            }
            else //must be a vector, get it
            {
                valueList = (Vector)oldValue;                
            }
            // add the new header value to the vector
            valueList.addElement(value);
        }
      }
      else if (!statusRead) {
        if (header.startsWith("HTTP")) {
          statusRead = true;
          StringTokenizer t = new StringTokenizer(header);
          t.nextToken(); /* Skip the HTTP/1.0 stuff */
          try {
            mStatusCode = Integer.parseInt(t.nextToken());
          }
          catch (NumberFormatException e) {
            mStatusCode = -1;
          }
          mStatusMessage = t.nextToken("\n").trim();
        }
      }
    }
  }

  //-------------------------------------
  /**
   * Closes the connection to the web server down
   */
  public void close() throws IOException {
    if (mIn != null) {
      mIn.close();
      mIn = null;
    }
    if (mOut != null) {
      mOut.close();
      mOut = null;
    }
    if (mSocket != null) {
      mSocket.close();
      mSocket = null;
    }
  }
  
  //-------------------------------------
  /**
   * This method outputs the request data through the socket.  Great pains
   * are taken to avoid the character -> byte conversion for as much of the
   * data as possible.  Even using the deprecated PrintStream appears not
   * to avoid this expensive per character conversion.
   */
  static byte [] sPostBytes = "POST ".getBytes();
  static byte [] sGetBytes = "GET ".getBytes();
  static byte [] sProtoBytes = " HTTP/1.0\r\n".getBytes();
  static byte [] sHeaderBytes = 
    ("User-Agent: Java1.0\r\n" +
    "Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\r\n").getBytes();
  static byte [] sNewlineBytes = "\r\n".getBytes();
  static byte [] sContentBytes =
      ("Content-type: application/x-www-form-urlencoded\r\n" +
      "Content-length: ").getBytes();

  protected void writeRequest() throws IOException {
    // output the request header
    if (mPostData != null)
      mOut.write(sPostBytes);
    else
      mOut.write(sGetBytes);
    /*
     * In this case, we do the entire URL so that the proxy server
     * can properly forward the request
     */
    if (mProxyHost != null)
      mOut.write(url.toExternalForm().getBytes());
    else
      mOut.write(url.getFile().getBytes());
    mOut.write(sProtoBytes);
    mOut.write(sHeaderBytes);
    if (mPostData != null) {
      mOut.write(sContentBytes);
      mOut.write(Integer.toString(mPostData.length()).getBytes());
      mOut.write(sNewlineBytes);
    }
    //
    // Output any additional request headers that have been specified with
    // setRequestProperty
    //
    if (mRequestProperties != null) {
      for (int i = 0; i < mRequestProperties.size(); i++) {
        mOut.write((byte[])mRequestProperties.elementAt(i));
      }
    }
    mOut.write(sNewlineBytes);

    // output the POSTed data
    if (mPostData != null)
      mOut.write(mPostData.getBytes());

    mOut.flush();
  }
  
}

