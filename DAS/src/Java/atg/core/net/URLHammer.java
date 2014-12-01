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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * <p>This is a class which is used to "hammer" a particular URL with
 * repeated requests, gathering statistics on the amount of time
 * required to handle those requests.
 *
 * <p>The requests are sent by multiple Threads.  Each Thread will
 * perform a specified number of requests, in serial before ending.
 * The Threads, of course, all operate in parallel.
 * 
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class URLHammer {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammer.java#2 $$Change: 651448 $";

  /**
   * A magic number for the pause milliseconds that causes us not to pause
   * at all
   */
  public static final int NO_PAUSE = -123456;

  //-------------------------------------
  // Member variables

  public URLHammerThread [] mThreads;  
  public URLInfo [] mURLs;

    
  //----------------------------------
  // property Stats
  public URLHammerStats mStats;
  
  public URLHammerStats getStats() {
      return mStats;
  }
  //----------------------------------
  // property encodePostParameters
  static boolean sEncodePostParameters = true;
  
  
  //----------------------------------
  // property config
  public URLHammerConfig mConfig = null; 

  public void setConfig(URLHammerConfig pConfig) {
      mConfig = pConfig;

      
      mThreads = new URLHammerThread [pConfig.numThreads];

      if (pConfig.script) {
	  mURLs = parseScript(pConfig);

      }
      else {
	  mURLs = new URLInfo[1];
	  try {
	      mURLs[0] = new URLInfo(pConfig.serverName, pConfig.url, 0, null, null);
	  }
	  catch (MalformedURLException exc) {
	      System.err.println ("MalformedURLException: " + exc);
	  }
      }
  }  
  
  public URLHammerConfig getConfig() {
      return mConfig;
  }  

  public URLHammer() {
  }

  //-------------------------------------
  /**
   * Constructs and starts a new URLHammer
   * @param pURLStr the URL to be hammered
   * @param pNumThreads the number of threads to be run
   * @param pNumRequests the number of requests to be run by each thread
   * @param pVerbose if true, then the results of the requests will
   */
  public URLHammer (String pURLStr, 
                    int pNumThreads, 
                    int pNumRequests,
                    boolean pVerbose) {
    this(new URLHammerConfig(pURLStr, pNumThreads, pNumRequests, 0, pVerbose, false, false));
  }

  //-------------------------------------
  /**
   * Constructs a new URLHammer
   * @param pConfig Contains the configuration parameters for this instance
   * of URLHammer.
   */
  public URLHammer (URLHammerConfig pConfig)
  {
      setConfig(pConfig);
  }

  //-------------------------------------
  /**
   * Starts this URLHammer and waits for completion.
   */
  public void run() {
    mStats = createStats();
    long startTime = System.currentTimeMillis ();

    startThreads();
    waitForThreads();
        
    long totalTime = System.currentTimeMillis () - startTime;
    mStats.totalTime = totalTime;
  }
  
 //-------------------------------------
 /*
  *  Makes it possible to use subclassed threads:
  */
  public URLHammerThread createThread( int i ) {
     return new URLHammerThread ( mConfig, mStats, i, mURLs);
  }
  
  public URLHammerStats createStats() {
     return new URLHammerStats(mConfig);
  }


  //-------------------------------------
  // Start all the Threads:
  public void startThreads() {
    for (int i = 0; i < mThreads.length; i++) {
      mThreads [i] = createThread( i );
    }
    for (int i = 0; i < mThreads.length; i++) {
      mThreads [i].start();
    }
  }

  //-------------------------------------
  // Wait for all the Threads to complete:
  public void waitForThreads() {
    for (int i = 0; i < mThreads.length; i++) {
      mThreads [i].join ();
    }
  }

  //-------------------------------------
  public void reportStats() {

    // Gather the statistics
    int numsuccess = 0;
    int numerror = 0;
    for (int i = 0; i < mThreads.length; i++) {
      numsuccess += mThreads [i].mNumSuccess;
      numerror += mThreads [i].mNumErrors;
    }
    long totalTime = mStats.totalTime;
    int totalrequests = numsuccess + numerror;
    if (totalrequests % mConfig.numRequests != 0) {
       System.out.println( "Warning:  Total requests did not equal a multiple of " + mConfig.numRequests );
    }
    if (totalTime == 0) totalTime = 1;
    double throughput = ((double) totalrequests / (double) totalTime) * 1000.0;
    
    // Note that, for threads > 1, latency (as computed here) is NOT the inverse of throughput.
    // We compute the average latency by disregarding the number of threads, because the total time
    // is also independent of the number of threads.  Thus latency is a good indicator of the
    // server's response time under load.

    // We need to calculate the requests per thread so that a script having multiple URL's will
    // give correct results:
    long requestsPerThread = totalrequests / mThreads.length;
    long latency;
    if (requestsPerThread == 0) latency = 0;
    else latency = (totalTime + (requestsPerThread / 2)) / requestsPerThread; // round to nearest millisecond
        
    String formattedThroughput = atg.core.util.NumberFormat.fixedPoint(throughput, 2);
    // Print the statistics
    //System.out.println ("Number of successes = " + numsuccess);
    //System.out.println ("Number of errors = " + numerror);
    System.out.println ("Time = " + totalTime + " ms" 
                        + "   (" + formattedThroughput + " requests/s; average latency = "
                        + latency + " ms)" );
    System.out.println ("" + numerror + " error" + (numerror == 1 ? "" : "s") + " out of " + totalrequests + " request" + (totalrequests == 1 ? "" : "s") + "" );
    //System.out.println ("Throughput = " + throughput + 
    //                    " requests/sec");
    //System.out.println ("Average latency = " + latency + " msec/request");

    if (mConfig.htmlStats != null)
      mStats.writeHtmlFile(mConfig.htmlStats);
  }

  //-------------------------------------
  // Main routine
  //-------------------------------------
  /**
   * Prints the version control information for this file
   */
  static public void main (String [] pArgs) {
    URLHammerConfig config = new URLHammerConfig();
    // unused String userIfAny = null;
    // unused String passwordIfAny = null;

    // use gui OR commandline
    if (pArgs.length == 1 && (pArgs[0].equalsIgnoreCase("-gui"))) {
      (new UrlHammerGui()).doExit(0);
    }
    else {
      for (int i = 0; i < pArgs.length; i++) {
        if (pArgs[i].startsWith("-")) {
          if (pArgs[i].equalsIgnoreCase("-verbose"))
            config.verbose = true;
          else if (pArgs[i].equalsIgnoreCase("-cookies"))
            config.cookies = true;
          else if (pArgs[i].equalsIgnoreCase("-runningStats"))
            config.runningStats = true;
          else if (pArgs[i].equalsIgnoreCase("-randomStop")) {
            config.randomStop = true;
	          config.stopPercentage = 20;
	        } 
          else if (pArgs[i].equalsIgnoreCase("-stop")) {
      	    try {
      	      config.stopPercentage = Integer.parseInt(pArgs[++i]);
      	    }
      	    catch (NumberFormatException exc) {
      	      usage("invalid integer percent value for -stop option: " + exc);
      	    }
	        }
          else if (pArgs[i].equalsIgnoreCase("-htmlStats"))
            config.htmlStats = pArgs[++i];
          else if (pArgs[i].equalsIgnoreCase("-nopause"))
            config.pauseMillis = URLHammer.NO_PAUSE;
          else if (pArgs[i].equalsIgnoreCase("-script")) {
            config.script = true;
          }
          else if (pArgs[i].equalsIgnoreCase("-server")) {
            config.serverName = pArgs[++i];
          }
          else if (pArgs[i].equalsIgnoreCase("-proxy")) {
            config.proxyServer = pArgs[++i];
          }
          else if (pArgs[i].equalsIgnoreCase("-pause")) {
	          try {
              config.pauseMillis = Integer.valueOf(pArgs[++i]).intValue();
	          }
	          catch (NumberFormatException exc) {
	            usage("bad value for -pause (expecting a # of milliseconds): " + exc);
	          }
          }
          else if (pArgs[i].equalsIgnoreCase("-substitute")) {
            config.substitute = true;
          }
      	  else if (pArgs[i].equalsIgnoreCase("-user")) {
                  config.user = pArgs[++i];
                }
          else if (pArgs[i].equalsIgnoreCase("-password")) {
                  config.password = pArgs[++i];
                }
          else if (pArgs[i].equalsIgnoreCase("-maxRedirects")) {
                  try {
                    config.maxRedirects = Integer.valueOf(pArgs[++i]).intValue();
                  }
                  catch(NumberFormatException exc) {
	                  usage("bad value for maxRedirects: " + exc);
	                }
          }
      	  else if (pArgs[i].equalsIgnoreCase("-recordAll")) {
                  config.recordAllRequests = true;
                }
          else if (pArgs[i].equalsIgnoreCase("-addHeader")) {
             String pair = pArgs[++i];
             int sep = pair.indexOf( "=" );
             if (sep == -1) {
                System.out.println( "Syntax error in -addHeader argument: " + pair );
             }
             else {
                config.userHeaders.put(pair.substring(0, sep), pair.substring( sep + 1 ));
                //System.out.println( "Added header:  " + pair.substring(0, sep ) + " = " + pair.substring( sep + 1 ) );
             }
          }
          else if (pArgs[i].equalsIgnoreCase("-addCookie")) {
            // each cookie is expected to be of the form "key=value"
            String cookie = pArgs[++i];
            // test it
            if (cookie.indexOf('=') < 1){
              System.out.println("Syntax error in -addCookie argument: " + cookie);
            }else{ // put it on the vector in URLHammerConfig
              config.userCookies.add(cookie);
            }
          }
          else {
            usage("Unrecognized option: " + pArgs[i]);
          }
        }
        else {
          if (config.url == null) {
             config.url = pArgs[i];
             config.scriptPath = pArgs[i];  // stop overloading the url field
          }
          else if (config.numThreads == -1) {
	    try {
              config.numThreads = Integer.valueOf (pArgs [i]).intValue ();
	    }
	    catch (NumberFormatException exc) {
	      usage("bad value for # of threads: " + exc);
	    }
	  }
          else if (config.numRequests == -1) {
	    try {
              config.numRequests = Integer.valueOf (pArgs [i]).intValue ();
	    }
	    catch (NumberFormatException exc) {
	      usage("bad value for # of requests: " + exc);
	    }
	  }
          else {
            usage("Too many arguments: " + pArgs[i]);
          }
        }
      }
      if ((config.password == null)
	  ^ (config.user == null))
	  usage ("Both password and user must be provided, or neither");

      if (config.numRequests == -1)
        usage("Too few arguments! ");
      URLHammer uh = new URLHammer (config);
      uh.run();
      uh.reportStats();
    }
  }

  //-------------------------------------
  /**
   * Parses the script file supplied returning an array of URLInfo objects.
   * The URLInfo objecs contain all of the info for each line in the
   * script
   */
  static URLInfo [] parseScript(URLHammerConfig pConfig) {
     /*
      * We need to localize the path BEFORE passing it to the File constructor;
      * otherwise, getParent() will return null on Windows (it expects backslashes).
      */
     Vector v = parseScriptFile( new File( localizePath( pConfig.scriptPath ) ), pConfig );
     URLInfo [] array = new URLInfo[v.size()];

     /*
      * Here are all the URL's to be requested:
      */
     if (pConfig.verbose) { 
        for (Enumeration e = v.elements(); e.hasMoreElements();) {
           System.out.println( "  " + ((URLInfo) e.nextElement()).url );
        }
     }
     
     v.copyInto(array);
     return array;
  }

  /*
   * Pathname localizer:
   */
  static String localizePath( String pPath ) {
      return pPath.replace( '\\', '/' ).replace( '/', java.io.File.separatorChar );
   }
 
  /*
   * Parsing method for files (recursive):
   */
  static Vector parseScriptFile( File pFile, URLHammerConfig pConfig ) {
    BufferedReader b = null;
    Vector v = new Vector();
    String str;
    try {
      b = new BufferedReader(new InputStreamReader(new FileInputStream( pFile )));
      while ((str = b.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(str);
        int nTokens = st.countTokens();
        if (nTokens == 0)
          continue;
        else {
          int pause = 0;
          int nPostLines = -1;
          String postData = null;
          String sessionId = null;
          
          String url = st.nextToken();
          /*
           * Recursively read #include'd files:
           */
          if ( url.equals("#include") && (nTokens > 1) ) {
             //String incPath = (String) st.nextToken(); 
             File incFile = new File( pFile.getParent(), localizePath( (String) st.nextToken() ) );
                          
             //System.out.println( "Reading included script:  " + str );
             Vector included = parseScriptFile( incFile, pConfig );
             if (included.size() == 0) {
               System.out.println( "Warning:  Nothing read from #include'd script " + incFile );
             }
             else
               for (Enumeration e = included.elements(); e.hasMoreElements(); ) 
                 v.addElement( e.nextElement() );

             //for (Enumeration e = v.elements(); e.hasMoreElements(); ) 
                //System.out.println( "  " + e.nextElement() );
             continue;
          }
             
          /*
           * Skip comments
           */
          if (url.startsWith("#")) continue;
          
          // Decode URL:
          //url = atg.core.net.URLUtils.unescapeUrlString( url );

          if (nTokens > 4) {
            System.err.println("Warning: skipping bad line: " + str + "\n in script file: " + pFile);
            continue;
          }           
          if (nTokens > 1)
            pause = Integer.valueOf(st.nextToken()).intValue();
          if (nTokens > 2)
            nPostLines = Integer.valueOf(st.nextToken()).intValue();
          else nPostLines = -1;

          if (nTokens > 3) {
            sessionId = st.nextToken();
            /*
             * Gather up the list of sessions in the URLConfig object
             */
            if (!pConfig.sessions.contains(sessionId))
              pConfig.sessions.addElement(sessionId);
          }
          else 
            sessionId = null;

          if (nPostLines >= 0)
            postData = parsePostData(nPostLines, b, pFile.toString());
          else 
            postData = null;

          v.addElement(new URLInfo(pConfig.serverName, url, pause, postData, sessionId));
        }
      }
    }
    catch (java.net.MalformedURLException exc) {
      if (pConfig.script && pConfig.serverName == null)
        System.err.println("*** You must supply the server name with -server <serverName> for this test");
      System.err.println ("MalformedURLException: " + exc);
    }
    catch (IOException e) {
      System.err.println("Can't read URLHammer script file: " + e );
    }
    finally {
      try {
        if (b != null) b.close();
      }
      catch (IOException e) {}
    }
    /*
    URLInfo [] array = new URLInfo[v.size()];
    v.copyInto(array);
    return array;
    */
    return v;
  }

  /**
   * Read in a properties file and return it in postData format
   */
  static String parsePostData(int pCount, BufferedReader pReader, String pFileName) {
    String str;
    try {
      StringBuffer sb = new StringBuffer();
      boolean first = true;

      while (--pCount >= 0 && (str = pReader.readLine()) != null) {
        if (str.startsWith("#")) {
          ++pCount;  // ignore commented lines
          continue;
        }
        int eq = str.indexOf('=');
        if (eq == -1) { 
          if (str.length() > 0)
            System.err.println("*** bad postData line: " + str + " in file: " + pFileName);
          continue;
        }
        if (!first) sb.append("&");
        else first = false;
        if (sEncodePostParameters)
          sb.append(URLUtils.escapeUrlString(str.substring(0, eq)));
        else 
          sb.append(str.substring(0, eq));
        sb.append('=');
        if (sEncodePostParameters)
          sb.append(URLUtils.escapeUrlString(str.substring(eq+1)));
        else
          sb.append(str.substring(eq+1));
      }

      return sb.toString();
    }
    catch (IOException e) {
      System.err.println("Can't read post data file: " + pFileName);
    }
    return null;
  }


  public static void usage(String pWhy) {
    if (pWhy != null) System.err.println (pWhy);
    System.err.println ("usage: URLHammer ");
    System.err.println ("                 <URL/(or script filename if -script is specified)>");
    System.err.println ("                 <number of threads>");
    System.err.println ("                 <requests/thread>");
    System.err.println ("                 [-verbose]  (dump output of requests)");
    System.err.println ("                 [-server <server-name>] (with -script to set server:port)");
    System.err.println ("                 [-cookies] (handle cookies)");
    System.err.println ("                 [-runningStats] (print info periodically for each thread)");
    System.err.println ("                 [-script] (see atg.servlet.pipeline.RecordingServlet)");
    System.err.println ("                 [-nopause] (ignore pause info in script file)");
    System.err.println ("                 [-htmlStats <file>] (output statistics to html file)");
    System.err.println ("                 [-recordAll] (record times for all requests in htmlStats file)");
    System.err.println ("                 [-randomStop] (simulate the browsers's stop button)");
    System.err.println ("                 [-stop <0-100>] (simulate the browsers's stop button every % requests)");
    System.err.println ("                 [-pause <time>] (pause between requests in millis)");
    System.err.println ("                 [-substitute] (do __KEYWORD__ substitutions - keywords are RANDOM, COUNTER, TIME)");
    System.err.println ("                 [-user <username> -password <password>] (provide authentication information)");
    System.err.println ("                 [-addHeader name=value] (set header value)");
    System.err.println ("                 [-addCookie name=value] (set a cookie value)");
    System.exit(1);
  }

  //-------------------------------------
  /**
   * Indicates if URLHammer will encode post parameters read from a script file
   * @return true if url hammer will encode post parameters from its script file
   */
  public static boolean isEncodePostParameters() {
    return sEncodePostParameters;
  }

  /**
   * Controls if URLHammer encodes post parameters read from a script file.
   * The default is true.
   * @param pDoEncode Controls if URL hammer should encode post parameters read from a script file
   */
  public static void setEncodePostParameters(boolean pDoEncode) {
    sEncodePostParameters = pDoEncode;
  }

}
