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

import java.util.Vector;
import java.io.*;
/**
 * The configuration class that stores all of the information needed
 * to configure a URLHammer.  This class is used by URLHammer and the
 * URLHammerThread classes to exchange information about different
 * configuration options
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerConfig.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class URLHammerConfig 
    implements Serializable {

  static final long serialVersionUID = -5154189853190382949L;

  //-------------------------------------
  // Class version string
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerConfig.java#2 $$Change: 651448 $";

  public boolean cookies = false;
  public boolean script = false;
  public boolean verbose = false;
  public long pauseMillis = 0;
  public int numThreads = -1;
  public int numRequests = -1;
  public String serverName = null;
  public String url = null;
  // Overloading the url field is klunky.  Here's one solution:
  public String scriptPath = null;

  public boolean runningStats = false;
  public Vector sessions = new Vector();
  public String htmlStats = null;
  public boolean randomStop = false;
  public int stopPercentage = 0;
  public boolean substitute = false;
  public String proxyServer = null;
  public String user = null;
  public String password = null;
  public boolean recordAllRequests = false;
  public int maxRedirects = 10;
  /**  user can specify additional headers and cookies on the commandline   */
  public java.util.Hashtable userHeaders = new java.util.Hashtable();
  public java.util.Vector userCookies = new java.util.Vector();
  protected boolean debug = false;

  public URLHammerConfig() {}

  protected URLHammerConfig(String pURL, int pNumThreads, int pNumRequests,
                  int pPauseMillis, boolean pVerbose, boolean pUseCookies,
                  boolean pIsScript) {
    url = pURL;
    numThreads = pNumThreads;
    numRequests = pNumRequests;
    pauseMillis = pPauseMillis;
    verbose = pVerbose;
    cookies = pUseCookies;
    script = pIsScript;
  }
  
  public boolean getDebug()         { return debug; }
  public String  getServerName()    { return serverName; }
  public int     getNumThreads()    { return numThreads; }
  public int     getNumRequests()   { return numRequests; }
  public boolean getVerbose()       { return verbose; }
  public boolean getCookies()       { return cookies; }
  public boolean getRunningStats()  { return runningStats; }
  public boolean getRandomStop()    { return randomStop; }
  public String  getHtmlStats()     { return htmlStats; }
  public long    getPauseMillis()   { return pauseMillis; }
  public boolean getScript()        { return script; }
  public String  getUrl()           { return url; }
  public String  getScriptPath()    { return scriptPath; }
  public boolean getSubstitute()    { return substitute; }  
  public boolean getRecordAllRequests()    { return recordAllRequests; }  
  public int     getMaxRedirects()  { return maxRedirects; }

  public void setDebug( boolean pEnabled )         { debug         = pEnabled; }
  public void setServerName( String pName )        { serverName    = pName; } 
  public void setNumThreads( int pNum )            { numThreads    = pNum; }
  public void setNumRequests( int pNum )           { numRequests   = pNum; }
  public void setVerbose( boolean pEnabled )       { verbose       = pEnabled; }
  public void setCookies( boolean pEnabled )       { cookies       = pEnabled; }
  public void setRunningStats( boolean pEnabled )  { runningStats  = pEnabled; }
  public void setRandomStop( boolean pEnabled )    { randomStop    = pEnabled; }
  public void setHtmlStats( String pPath )         { htmlStats     = pPath; }
  public void setPauseMillis( long pNum )          { pauseMillis   = pNum; }
  public void setScript( boolean pEnabled )        { script        = pEnabled; }
  public void setUrl( String pUrl )                { url           = pUrl; }
  public void setScriptPath( String pPath )        { scriptPath    = localizePath( pPath ); }  
  public void setSubstitute( boolean pSub )        { substitute = pSub; }
  public void setRecordAllRequests( boolean pRecordAllRequests ) { 
    recordAllRequests = pRecordAllRequests; 
  }
  public void setMaxRedirects( int pMaxRedirects ) { maxRedirects = pMaxRedirects; }

  // Path localizer:
  public String localizePath( String pPath ) {
    return pPath.replace( '\\', '/' ).replace( '/', java.io.File.separatorChar );
  }
                                                

}

