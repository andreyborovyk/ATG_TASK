
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
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import atg.core.util.Base64;

/**
 *
 * <p>This class implements a Thread that will
 * perform a specified number of requests, in serial, before ending.
 * 
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerThread.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class URLHammerThread implements Runnable {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerThread.java#2 $$Change: 651448 $";

  //--------------------------------------
  // Constants

  //----------------------
  /**
   * How long should each thread take between reporting statistics
  */
  static final int NOTIFY_TIME_MILLIS = 60*1000;

  /**
   * Once we've determined that we're going to stop this request, we 
   * will close the socket after this # of reads.
   */
  static final int RANDOM_STOP_INTERVAL = 10;

  //-------------------------------------
  // Member variables

  /** The list of URLInfos to hammer */
  public URLInfo [] mURLs = null;

  /** The statistics gathered during request processing */
  public URLHammerStats mStats = null;

  /** The name of a script file to use for processing */
  protected String mScriptFile = null;

  /** The number of successful requests performed */
  protected int mNumSuccess;
    /**
     * Returns the number of successful requests on this thread
     * @return int - the number of successes
     */
    public int getNumSuccess() { return mNumSuccess; }
    
  /** The number of errors encountered */
  protected int mNumErrors;
    /**
     * Returns the number of errors on this thread
     * @return int - the number of errors
     */
    public int getNumError() { return mNumErrors; }
    
  /** The number of interrupted requests */
  protected int mNumStopped;

  /** The total time taken by those requests */
  protected long mTotalTime;

  /** The buffer for reading */
  protected byte [] mBuf = new byte [4096];

  /** The Thread running this */
  protected Thread mThread;

  /** The cookies that have been received for this request */
  protected Vector mCookies = new Vector();

  /** The list of configuration information for this */
  public URLHammerConfig mConfig;

  /** The workspace object for request data */
  protected URLHammerRequest mRequest;

  /** The index in the script file of the currently executing request */ 
  protected int mRequestIndex;

  /** The workspace object for session request data */
  protected URLHammerRequest mSessionRequest;
  
  /**
   * The random number generator for pause times
   */
  protected Random mRandom = null;

  /**
   * This threads index in a pool of threads
   */
  protected int mIndex = 0;

  /**
   * A local reference to the user-specified cookies
   */
  protected Vector mExtraCookies = null;

  //-------------------------------------
  public URLHammerThread (URLHammerConfig pConfig,
  			  URLHammerStats pStats,
  			  int pIndex,
  			  URLInfo [] pURLs) {
    mIndex = pIndex;
    mConfig = pConfig;
    mURLs = pURLs;
    mStats = pStats;

    /**
     * Create a random number generator with a consistent seed to 
     * minimize variation between subsequent tests
     */
    mRandom = new Random(0);
    mThread = new Thread (this);
    // Don't start the thread yet--
    // weird as it sounds, member variables of an instance of a subclass
    // may not have been created yet!!!!
    //
    //mThread.start ();    // Do this separately, outside the constructor.

    // a local copy of user-specified cookies
    mExtraCookies = mConfig.userCookies;
    // but add them to the main list
    addExtraCookies();
  }

  //-------------------------------------
  public void start() {
     mThread.start();
  }

  //-------------------------------------
  public void join () {
    boolean done = false;
    while (!done) {
      try {
        mThread.join ();
        done = true;
      }
      catch (InterruptedException exc) {}
    }
  }

  //-------------------------------------
  public void run () {


    long statNotifyStartTime = System.currentTimeMillis();
    int requestsSinceLastNotify = 0;
    
    mSessionRequest = createRequest();  
    mRequest = createRequest();
    Object cookie;
    boolean verbose = mConfig.verbose;

    /*
     * If we are running a script, stagger the starting time of each
     * of the request threads
     */
    if (mURLs.length > 1 && mConfig.pauseMillis != URLHammer.NO_PAUSE) {
      stagger();
    }

    for (int i = 0; i < mConfig.numRequests; i++) {
      
      long startTime = System.currentTimeMillis ();
      int j = 0;
      String sessionId = null;

      try {
        /*
         * Choose a session to process requests from for this iteration.
         * Each request for each thread gets a different session id.
         * It will skip all requests that are not for that session.
         */
        if (mConfig.sessions.size() > 0) {
          int sessionIndex = (mIndex + i * mConfig.numThreads) % mConfig.sessions.size();
          sessionId = (String) mConfig.sessions.elementAt(sessionIndex);
        }
        else 
          sessionId = null;

        /*
         * This request object is used to capture info about the entire session
         * we're doing
         */
        //URLHammerRequest sessionRequest = createRequest();  // For accumulated statistics
        mSessionRequest.reset();
        mSessionRequest.sessionId = sessionId;
        /*
         * Start out with a success code and switch to error when
         * we get the first
         * error
         */
        mSessionRequest.statusCode = 200;

             
        boolean firstRequestDone = false;
        InputStream in = null;
        URLHammerRequest request = mRequest;
        URL url;
        boolean errorOccurred = false;
   
        enteringThread();

        //System.out.println( "URLHammer properties:" );
        //System.out.println( "  using cookies = " + mConfig.cookies );
        //System.out.println( "  playing script = " + mConfig.script );

        for (j = 0; j < mURLs.length; j++) {

          //System.out.println( "URL:  " + mURLs[j].url );
          //System.out.println( " URL sessionId  = " + mURLs[j].sessionId );
          //System.out.println( " this sessionId = " + sessionId );
          TestHTTPConnection connection = null;
          errorOccurred = false;
          try {
            /*
             * Skip all requests that are not for this specific session id
             * (if we are keeping track of sessions)
             */
            if (sessionId != null 
               && mURLs[j].sessionId != null
               && !sessionId.equals(mURLs[j].sessionId)) {
              //System.out.println( "--skipping because session ID does not match" );
              continue;
            }
            if ((mConfig.pauseMillis != 0 || mURLs[j].pauseMillis != 0) && 
                  mConfig.pauseMillis != URLHammer.NO_PAUSE) {
              /*
               * Positive value means to sleep for this amount for each
               * request.  A negative value means to choose a random value
               * with this sleep as the max
               */
              try {
                if (mConfig.pauseMillis >= 0) 
                  Thread.sleep(mConfig.pauseMillis + mURLs[j].pauseMillis);
                else
                  Thread.sleep((Math.abs(mRandom.nextInt()) % -mConfig.pauseMillis) + mURLs[j].pauseMillis);
              }
              catch (InterruptedException e) {}
            }      
                   
            //URLHammerRequest request = createRequest();
            request.reset();
            request.url = mURLs[j].url;
            request.sessionId = sessionId;
            request.method = mURLs[j].method;
	          mRequestIndex = j;
      	    /* 
      	     * Start out with this at -1 to indicate we haven't read any data yet 
      	     */
      	    request.dataLength = -1;
   
            beforeRequest();
            long requestStartTime = System.currentTimeMillis();
            
            // Make the connection
            //URL url = new URL (mURLs[j].url);         

      	    // do substitutions on the URL if necessary
            if (  mConfig.substitute ) {
              String newurlstr = performSubstitutions( mURLs[j].URLobject.
      						       toExternalForm() );
              url = new URL ( newurlstr );
            } else {
              url = mURLs[j].URLobject;
            }
            int r = Math.abs(mRandom.nextInt());
            boolean willStop = (r % 100) < mConfig.stopPercentage; 
            /* 1/3 of the time, we stop reading the headers */
            if (willStop && (r % 3) == 0) {
              connection = new TestHTTPConnection(url, true);
            }
            else connection = new TestHTTPConnection(url, false);
            if (mConfig.proxyServer != null) {
              setProxyServer(connection, mConfig.proxyServer);
            }
           
            if (mURLs[j].postData != null) {
	           // do substitutions on post data if necessary
	           if ( mConfig.substitute ) {
  	             String substitution = performSubstitutions(mURLs[j].postData);
		          connection.setPostData(substitution);
	           } else {
		          connection.setPostData(mURLs[j].postData);
	           }
	          }
            if (verbose) connection.setVerboseHeaders(true);
            
      	    if ((mConfig.user != null) && (mConfig.password != null))
      		    setAuthentication(connection);
            
            setExtraHeaders(connection); // added by Ross
      
      	    if (mConfig.cookies || mConfig.script)
                     setCookies(connection);
            
            in = connection.getInputStream ();
      	    
      	    if (in == null && willStop) {
      	      request.stopped = true;
      	      mSessionRequest.stopped = true;
      	      break;
      	    }
          
            if (mConfig.cookies || mConfig.script) {
              cookie = connection.getHeaderFields("set-cookie");
               
              if (cookie != null)
              {
                if (cookie instanceof String) //we have only one cookie
                {
                  addCookie((String)cookie);
                }
                else //must be a vector of cookies, add them all
                {
                  Vector cookieList = (Vector)cookie;
                  int size = cookieList.size();
                  for (int k= 0; k<size; k++)
                  {
                      addCookie((String)cookieList.elementAt(k));
                  }
                }
              }
	         }

            /* Status codes (from HTTP 1.1 -- RFC 2068)
              6.1.1 Status Code and Reason Phrase
         
              The Status-Code element is a 3-digit integer result code of the attempt to understand and satisfy the request. These codes
              are fully defined in section 10. The Reason-Phrase is intended to give a short textual description of the Status-Code. The
              Status-Code is intended for use by automata and the Reason-Phrase is intended for the human user. The client is not required
              to examine or display the Reason-Phrase. 
         
              The first digit of the Status-Code defines the class of response. The last two digits do not have any categorization role. There
              are 5 values for the first digit: 
         
                1xx: Informational - Request received, continuing process 
                2xx: Success - The action was successfully received, understood, and accepted 
                3xx: Redirection - Further action must be taken in order to complete the request 
                4xx: Client Error - The request contains bad syntax or cannot be fulfilled 
                5xx: Server Error - The server failed to fulfill an apparently valid request 
                
               Status-Code    = "100"   ; Continue
                              | "101"   ; Switching Protocols
                              | "200"   ; OK
                              | "201"   ; Created
                              | "202"   ; Accepted
                              | "203"   ; Non-Authoritative Information
                              | "204"   ; No Content
                              | "205"   ; Reset Content
                              | "206"   ; Partial Content
                              | "300"   ; Multiple Choices
                              | "301"   ; Moved Permanently
                              | "302"   ; Moved Temporarily
                              | "303"   ; See Other
                              | "304"   ; Not Modified
                              | "305"   ; Use Proxy
                              | "400"   ; Bad Request
                              | "401"   ; Unauthorized
                              | "402"   ; Payment Required
                              | "403"   ; Forbidden
                              | "404"   ; Not Found
                              | "405"   ; Method Not Allowed
                              | "406"   ; Not Acceptable
                              | "407"   ; Proxy Authentication Required
                              | "408"   ; Request Time-out
                              | "409"   ; Conflict
                              | "410"   ; Gone
                              | "411"   ; Length Required
                              | "412"   ; Precondition Failed
                              | "413"   ; Request Entity Too Large
                              | "414"   ; Request-URI Too Large
                              | "415"   ; Unsupported Media Type
                              | "500"   ; Internal Server Error
                              | "501"   ; Not Implemented
                              | "502"   ; Bad Gateway
                              | "503"   ; Service Unavailable
                              | "504"   ; Gateway Time-out
                              | "505"   ; HTTP Version not supported
                              | extension-code
            */


            int code = connection.getStatusCode();
            request.statusCode = code;

            if (request.gotError()) {
               mSessionRequest.statusCode = request.statusCode;
         
               if (request.gotServerError()) {
                  System.out.println ("===== Server error " + code + " for URL=" + url);
               }
               else if (request.gotClientError()) {
                  System.out.println ("===== Client error " + code + " for URL=" + url);
               }
               errorOccurred = true;
            }
            else

            if ((willStop && (mRandom.nextInt() % RANDOM_STOP_INTERVAL) == 0)) {
              try {
                connection.close();
              }
              catch (IOException e) {}
              connection = null;
              /*
                //Redirects used to be lumped in here but are now handled in the next block.
                  if (!request.wasRedirected()) {
                    request.stopped = true;
                    mSessionRequest.stopped = true;
                  }
              */
            }
            
            /**
              Fixes for PR# 15542 (URLHammer doesn't handle redirects). MWS, July 7, 2000.
              
              As grotesque as it is, the following block of code handles
              recursive redirects. If the page we're redirected to does a redirect which does a
              redirect, etc we will load each one of these pages in succession. To prevent 
              infinite loops, we allow the user to specify a maximum number of redirects. 
              
              Still need to implement a property that limits the depth of this recursion.
             */
            int numRedirects = 0; 
            while(request.wasRedirected() && numRedirects < mConfig.maxRedirects) {
              // unused boolean redirecting = true;
              numRedirects++;
            
              // handle the request using all the current parameters etc.
              String locationHeader = connection.getHeaderField("location");
              
              if(locationHeader!=null) {
                if(verbose) {
                  System.out.println("NOTE: Received a redirect (302) to \"" + locationHeader + "\"");
                  System.out.println("      for original URL, \"" + url + "\"");
                }
                
		if (locationHeader != null) {
                  if (mConfig.getServerName() != null) {
                    if (! locationHeader.startsWith("http://")) {
                      locationHeader = "http://" + mConfig.getServerName() + 
                        locationHeader;
                    }
                    else if (locationHeader.startsWith("http://null")) {
                      // BUG 75128
                      // if host is unknown, set the host to the
                      // configured server, e.g. localhost:7001
                      int skipIndex = locationHeader.indexOf('/', 7);
                      locationHeader = "http://" + mConfig.getServerName() + 
                        locationHeader.substring(skipIndex);
                    }
                  }
		}

                url = new URL(locationHeader);
                
                TestHTTPConnection redirectConnection 
                  = new TestHTTPConnection(new URL(locationHeader), connection.mCloseConnection);
                // effectively clone the settings of the connection that got the redirect
                if (mConfig.proxyServer != null) {
                  setProxyServer(connection, mConfig.proxyServer);
                }
                redirectConnection.setPostData(connection.getPostData());
                redirectConnection.setVerboseHeaders(connection.getVerboseHeaders());
                
                //repeat what we did to prepare the original connection                         
                if ((mConfig.user != null) && (mConfig.password != null))
                  setAuthentication(redirectConnection);
                
                setExtraHeaders(redirectConnection);
                
                if (mConfig.cookies || mConfig.script)
                         setCookies(redirectConnection);
                
                //NOTE: when verbose, the headers are printed as they are received
                in = redirectConnection.getInputStream (); 
                
                if (in == null && willStop) {
                  request.stopped = true;
                  mSessionRequest.stopped = true;
                  break;
                }
                
                if (mConfig.cookies || mConfig.script) {
                  cookie = redirectConnection.getHeaderFields("set-cookie");
                   
                  if (cookie != null)
                  {
                    if (cookie instanceof String) //we have only one cookie
                    {
                      addCookie((String)cookie);
                    }
                    else //must be a vector of cookies, add them all
                    {
                      Vector cookieList = (Vector)cookie;
                      int size = cookieList.size();
                      for (int k= 0; k<size; k++)
                      {
                          addCookie((String)cookieList.elementAt(k));
                      }
                    }
                  }
                }
                
                code = redirectConnection.getStatusCode();
                request.statusCode = code;
    
                if (request.gotError()) {
                   mSessionRequest.statusCode = request.statusCode;
             
                   if (request.gotServerError()) {
                      System.out.println ("===== Server error " + code + " for URL=" + url);
                   }
                   else if (request.gotClientError()) {
                      System.out.println ("===== Client error " + code + " for URL=" + url);
                   }
                   errorOccurred = true;
                }
                else
                  if ((willStop && (mRandom.nextInt() % RANDOM_STOP_INTERVAL) == 0)) {
                    try {
                      redirectConnection.close();
                    }
                    catch (IOException e) {}
                    redirectConnection = null;
                  }
                
                //reset connection to use redirectConnection
                connection = redirectConnection;
                
              } //end if(locationHeader!=null)
              
              else { // locationHeader was null
                // Means we were sent a 302 but no location header telling us what to load instead. Doh!
                // There's no alternative here (that I can think of) but to flag this as an error and be done with it.
                System.err.println("*** received a 302 (redirect) for URL, \"" + url 
                  +"\", without an accompanying location header.");
                // This is what we used to do when we'd get a 302.
                request.stopped = true;
                mSessionRequest.stopped = true;
              }
            } //end while request.wasRedirected() && numRedirects < mMaxRedirects)
            //end of Fixes for PR# 15542 (URLHammer doesn't handle redirects)
            
            // if we hit our limit and we're feeling chatty...
            if(numRedirects == mConfig.maxRedirects) {
                System.out.println("*** Warning: bailed out of redirect handling after " + numRedirects + " redirections.");
            }
            
            // Read until done
            if (connection != null) {
              int len;
	            int count = 2;
	            request.dataLength = 0;
              while ((len = in.read (mBuf)) > 0) {
	              request.dataLength += len;
	              mSessionRequest.dataLength += len;
		            count++;
                if (willStop && (mRandom.nextInt() % count) == 0) {
                  try {
		                connection.close();
                  }
                  catch (IOException e) {}
                  connection = null;
                  request.stopped = true;
		  /* 
		   * Take this off again and reset to 0 so that we don't 
		   * get confused.
		   */
		  mSessionRequest.dataLength -= request.dataLength;
		  request.dataLength = 0;
                  mSessionRequest.stopped = true;
                  break;
                }
         
                // Print response if in verbose mode or if we got a server error:
                if (verbose || request.gotServerError()) {
                  String contentType = connection.getHeaderField("content-type");
                  if (contentType != null && !contentType.startsWith("image"))
                    System.out.write (mBuf, 0, len);
                  else
                    System.out.println("<image data len=" + len + ">");
                }
                processResponseBytes( mBuf, len );
              }
	      if (willStop && connection != null) {
	        try {
	          connection.close();
		}
		catch (IOException e) {}
		request.stopped = true;
		mSessionRequest.stopped = true;
	      }
            }
            request.time = (int) (System.currentTimeMillis() - requestStartTime);
            
            afterRequest();

            if (mConfig.runningStats && !firstRequestDone) {
              firstRequestDone = true;
              long firstDiff = System.currentTimeMillis () - startTime;
         
              System.out.println ( "============ first request complete: " +
                   firstDiff + " msec session=" + sessionId);
         
            }
          }  
          catch (IOException exc) {
            errorOccurred = true;
            exc.printStackTrace();
            System.out.println ("********** caught IOException on URL: " + mURLs[j].url + " " + exc);

	    mStats.recordIOFailure(exc);
            cleanupRequest();
          }
          catch (Exception eek) {
            errorOccurred = true;
            System.out.println( "URLHammerThread: " + eek.toString() );
            eek.printStackTrace();
	    mStats.recordOtherFailure(eek);
            cleanupRequest();
          }
          finally {
            mStats.addRequest(request);  // Accumulate statistics from this request
         
            if (errorOccurred) 
               ++mNumErrors;
            else
            if (request.stopped) 
               ++mNumStopped;
            else
               ++mNumSuccess;
            try {
              if (connection != null) 
                connection.close();
            }
            catch (IOException e) {
            }
            finally {
              if (in != null) {
                try { in.close (); }
                catch (IOException exc) {}
              }
            }
          }
        }

	/*
	 * Erase the cookies after processing a sequence of requests
	 */
        if (mConfig.script && !mConfig.cookies){
          mCookies = new Vector();
          addExtraCookies(); // add any user-specified cookies
        }

        requestsSinceLastNotify += mURLs.length;

        // See how much time it took for this request or sequence of requests
        long completeTime = System.currentTimeMillis();
        long totalTime = completeTime - statNotifyStartTime;

        mSessionRequest.time = (int) totalTime;
        /*
         * Update statistics for this session
         */
        mStats.addSessionRequest(mSessionRequest);
        
        if (mConfig.runningStats && totalTime > NOTIFY_TIME_MILLIS) {
          double throughput = ((double) requestsSinceLastNotify / (double) totalTime) * 1000.0;
          long latency = totalTime / requestsSinceLastNotify;

          // Print the statistics
          System.out.println ("\n==============");
          System.out.println ("[" + mIndex + "]" +  "Number of requests = " + requestsSinceLastNotify);
          System.out.println ("[" + mIndex + "]" +  "Time = " + totalTime + " msec");
          System.out.println ("[" + mIndex + "]" +  "Total errors = " + mNumErrors);
          System.out.println ("[" + mIndex + "]" +  "Throughput = " + throughput + " requests/sec");
          System.out.println ("[" + mIndex + "]" +  "Average latency = " + latency + " msec/request");
          System.out.flush();

          statNotifyStartTime = completeTime;
          requestsSinceLastNotify = 0;
        }

        long timeDiff = completeTime - startTime;
        if (verbose) {

          System.out.println ( "============ " + mURLs.length + " request(s) complete: " +
               timeDiff + " msec");
          System.out.println ();
          System.out.flush ();
        }

        // Add to the statistics
        mTotalTime += timeDiff;
        exitingThread();
      }
      //catch (MalformedURLException exc) {
      //  if (mConfig.script && mConfig.serverName == null)
      //    System.err.println("*** You must supply the server name with -server <serverName> for this test");
      //  System.err.println ("MalformedURLException: " + exc);
      //  return;
      //}
      //catch (IOException exc) {
      //  mNumErrors++;
      //  System.out.println ("********** caught IOException on URL: " + mURLs[j].url + " " + exc);
      //}
      catch (Exception eek) {
         cleanupThread();
      }
      finally {
        //if (in != null) {
        //  try { in.close (); }
        //  catch (IOException exc) {}
        //}
      }
    }
  }

  /**
   * Sets the proxyServer in the HTTPConnection given
   */
  void setProxyServer(HTTPConnection connection, String proxyServer) {
    int ix = proxyServer.indexOf(":");
    if (ix != -1) {
      try {
	connection.setProxyServer(proxyServer.substring(0, ix),
	               Integer.parseInt(proxyServer.substring(ix+1)));
      }
      catch (NumberFormatException exc) {
	System.err.println("*** cannot parse port from proxy server: " + exc);
      }
    }
    else connection.setProxyServer(proxyServer, 80);
  }

  public URLHammerRequest createRequest() {
    return new URLHammerRequest();
  }

  /**
   * If we are running a script, each thread starts up staggered along
   * the request sequence
   */
  void stagger() {
    long totalTime = 0;
    for (int i = 0; i < mURLs.length; i++) 
      totalTime += mURLs[i].pauseMillis;

    long sleepTime = mIndex * totalTime / mConfig.numThreads; 

    try {
      Thread.sleep(sleepTime);
    }
    catch (InterruptedException e) {}
  }

  /**
   * Add a cookie to the vector, making sure that it is unique.
   * @param String the new cookie
   */
  void addCookie(String pCookie) {
    int ind = pCookie.indexOf(';');
    if (ind != -1) {
      pCookie = pCookie.substring(0, ind);
    }
    
    String cookieName = getCookieName(pCookie);
    /*
     * First remove any previous values for this cookie
     */
    
    for (int i = 0; i < mCookies.size(); i++) {
      if (getCookieName((String) mCookies.elementAt(i)).equals(cookieName)) {
        mCookies.removeElementAt(i);
        break;
      }
    }
    //System.out.println( "adding cookie: " + pCookie );
    
    mCookies.addElement(pCookie);
  }

  String getCookieName(String pCookie) {
    int ind = pCookie.indexOf('=');
    if (ind == -1) return pCookie;
    return pCookie.substring(0,ind);
  }

  String makeCookies() {
    if ((mConfig.cookies || mConfig.script) && mCookies.size() > 0) {
      StringBuffer buf = new StringBuffer();
      int size = mCookies.size() - 1;
      for (int i = 0; i < size; i++) {  // First do all but the last, each followed by ;
        buf.append(mCookies.elementAt(i));
        buf.append("; ");
      }
      buf.append(mCookies.elementAt( size ));  // then do the last one

      return buf.toString();
    }
    else 
      return null;
  }

  /**
   * Add user-specified cookies to the main list of cookies
   */
  void addExtraCookies(){

    if (mExtraCookies == null) return;
    
    int size = mExtraCookies.size();
    for (int i=0; i < size; i++){
      addCookie((String)mExtraCookies.elementAt(i));
    }
  }

  void setCookies(URLConnection pConnection) {

    if (mCookies.size() > 0) {
      StringBuffer buf = new StringBuffer();
      int size = mCookies.size() - 1;
      // First do all but the last, each followed by ;
      for (int i = 0; i < size; i++) {  
        buf.append(mCookies.elementAt(i));
        buf.append("; ");
      }
      buf.append(mCookies.elementAt( size ));  // then do the last one

      pConnection.setRequestProperty("Cookie", buf.toString());
    }
  }

  void setAuthentication(URLConnection pConnection) {
    String user = mConfig.user;
    String password = mConfig.password;
    String auth = "Basic " + Base64.encodeToString(user + ":" + password);
    
    pConnection.setRequestProperty("Authorization", auth);
  }

  void setExtraHeaders(URLConnection pConnection) {
     String name;
     for (Enumeration e = mConfig.userHeaders.keys(); e.hasMoreElements(); ) {
        name = (String) e.nextElement();
        pConnection.setRequestProperty( name, 
                                        (String) (mConfig.userHeaders.get( name )));
     }
  }

  /*
   * Called when beginning the thread:
   */
  public void enteringThread() {
  }

  /*
   * Called when thread is about to terminate normally:
   */
  public void exitingThread() {
  }

  /*
   * Called when thread is about to terminate abnormally:
   */
  public void cleanupThread() {
  }

  /*
   * Called before each request:
   */
  public void beforeRequest() throws Exception {
  }

  /*
   * Called 0 or more times during each request, for each buffer of response data:
   */
  public void processResponseBytes( byte mBuf[], int len ) {
  }

  /*
   * Called after each successful request:
   */
  public void afterRequest() {
  }

  /*
   * Called after a failed request:
   */
  public void cleanupRequest() {
  }


  /*
   * perform some substitutions (this is a hack)
   */ 
  String performSubstitutions( String pData ) {

    StringBuffer result = new StringBuffer( pData.length() );

    int varStart = 0; // index of open
    int varEnd = -2;    // index of closing

    while ( true ) {

      // look for the opening __
      varStart = pData.indexOf("__", varEnd + 2 );

      if (varStart == -1) {
	// if there's no openings, tack on whatever's left
	result.append( pData.substring( varEnd + 2 ) );
	break;
      }

      // tack on whatever's before the opening __
      result.append( pData.substring( varEnd + 2, varStart ) );

      // find the closing __
      varEnd = pData.indexOf("__", varStart + 2);

      if (varEnd == -1) {
	// if there's no close, just tack on everything and quit
	result.append( pData.substring( varStart ) );
	break;
      } else {
	// otherwise do the substitution
	String var = pData.substring( varStart, varEnd + 2 );
	result.append( substitute(var) );
      }
    }

    // tack on the last bit (or all of it if no var was found)

    return result.toString();
      
  }


  static int sCounter = 0;

  String substitute( String pVar ) {
    if ( pVar.equals("__RANDOM__") ) {
      return String.valueOf( Math.abs( mRandom.nextInt() ) );
    } else if ( pVar.equals("__COUNTER__") ) {
      return String.valueOf( sCounter++ );
    } else if ( pVar.equals("__TIME__") ) {
      return String.valueOf( System.currentTimeMillis () );
    }

    // if we don't find the substitution, just return
    return pVar;
  }

  //-------------------------------------
}

