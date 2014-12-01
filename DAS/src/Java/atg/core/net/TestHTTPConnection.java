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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Jeff Vroom (derived from HTTPPostConnection by Joe Berkovitz)
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/TestHTTPConnection.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class TestHTTPConnection extends HTTPConnection {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/TestHTTPConnection.java#2 $$Change: 651448 $";

  /** Random number generator to use for choosing error count */
  Random mRandom = new Random(0);

  /** True if we should close the connection at startup */
  boolean mCloseConnection;

  //-------------------------------------
  //-------------------------------------
  /**
   * Construct a connection object for a given URL
   */

  public TestHTTPConnection (URL url, boolean pCloseConnection) {
    super(url);
    mCloseConnection = pCloseConnection;
  }

  //-------------------------------------
  /**
   * Connect to the URL and post data if we haven't done so yet.
   * 
   * Returns false if we simulated an error condition, true otherwise.
   */
  public boolean connectWithError() throws IOException {
    int shouldError;
    if (!connected) {
      openSocket();		// open socket to host

      if (mCloseConnection) 
        shouldError = Math.abs(mRandom.nextInt()) % 3;
      else
        shouldError = -1;

      if (shouldError != 0) {
        writeRequest(shouldError == 1);// write request to socket
	if (shouldError != 1) 
          readHeaders(shouldError == 2);// suck in headers from the input stream
      }
      if (shouldError != -1) {
        close();
	return false;
      }
      connected = true;
    }
    return true;
  }

  //-------------------------------------
  /**
   * Calls this routine to get an InputStream that reads from the object.
   * Protocol implementors should use this if appropriate.
   * @exception UnknownServiceException If the protocol does not
   * support input.
   */
  public InputStream getInputStream() throws IOException {
    if (!connectWithError()) {
      if (mVerboseHeaders) System.out.println("--- stopped request");
      return null;
    }

    return mIn;
  }

  //-------------------------------------
  /* Read in header lines until we see a blank line; each header 
   * is split into key and value pairs and stored for later retrieval
   * via getHeaderField() and its callers.
   */

  protected void readHeaders(boolean pShouldError) throws IOException {
    boolean statusRead = false;
    int errorCase;

    mStatusCode = 0;
    mStatusMessage = null;

    if (pShouldError)
      errorCase = mRandom.nextInt() % 10;
    else
      errorCase = -1;

    int i = 0;

    for (;;) {
      String header = mIn.readLine();

      if (i == errorCase) {
        if (mVerboseHeaders)
	  System.out.println("**** stopping response after header number: " + i);
	return;
      }

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

  protected void writeRequest(boolean pShouldError) throws IOException {
    int errorCase;
    int nprops = mRequestProperties == null ? 0 : mRequestProperties.size();
    if (pShouldError)
      errorCase = mRandom.nextInt() % (12 + nprops);
    else
      errorCase = -1;

    // output the request header
    if (mPostData != null)
      mOut.write(sPostBytes);
    else
      mOut.write(sGetBytes);

    if (errorCase == 1) return;
    /*
     * In this case, we do the entire URL so that the proxy server
     * can properly forward the request
     */
    if (mProxyHost != null)
      mOut.write(url.toExternalForm().getBytes());
    else
      mOut.write(url.getFile().getBytes());

    if (errorCase == 2) return;
    mOut.write(sProtoBytes);
    if (errorCase == 3) return;
    mOut.write(sHeaderBytes);
    if (errorCase == 4) return;

    if (mPostData != null) {
      mOut.write(sContentBytes);
      if (errorCase == 5) return;
      mOut.write(Integer.toString(mPostData.length()).getBytes());
      if (errorCase == 6) return;
      mOut.write(sNewlineBytes);
      if (errorCase == 7) return;
    }
    //
    // Output any additional request headers that have been specified with
    // setRequestProperty
    //
    if (mRequestProperties != null) {
      for (int i = 0; i < mRequestProperties.size(); i++) {
        mOut.write((byte[])mRequestProperties.elementAt(i));
	if (errorCase == i + 8) return;
      }
    }
    mOut.write(sNewlineBytes);
    if (errorCase == nprops + 9) return;

    // output the POSTed data
    if (mPostData != null) {
      /* Write a fraction of the post data */
      if (errorCase == nprops + 10) {
        int size = mPostData.length();
	size = size - (mRandom.nextInt() % size);
	if (size < 0) size = 0;
        mOut.write(mPostData.substring(0,size).getBytes());
      }
      else {
        mOut.write(mPostData.getBytes());
      }
    }

    if (errorCase == nprops + 11) return;

    mOut.flush();
  }
  
  
  /**
   * Get the data to be posted as part of the request.  Any encoding
   * to be applied to the data will have taken place.
   */
  public String getPostData () {
    return mPostData ;
  }

  /**
   * Are we dumping out headers as we read them in?
   */
  public boolean getVerboseHeaders() {
    return mVerboseHeaders;
  }
  

}

