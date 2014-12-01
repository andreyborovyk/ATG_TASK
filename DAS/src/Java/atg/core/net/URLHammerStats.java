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
                    
import java.io.*;
import java.util.*;
import atg.core.util.IntHashtable;
import atg.core.util.OrderedHashtable;

/**
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerStats.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class URLHammerStats
     implements Serializable {
    //-------------------------------------
    // Class version string
    
    public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerStats.java#2 $$Change: 651448 $";
    
    static final long serialVersionUID = 6234994987894509718L;
    
    protected SummaryStats summary = new SummaryStats();
    protected IntHashtable errorStats = new IntHashtable();
    protected Dictionary perURLStats = new OrderedHashtable();
    protected Dictionary sessionSummary = new Hashtable();
    protected long totalTime;
    protected int dataLength;
    // Failures that were due to IOExceptions
    protected Vector mIOFailures = new Vector();
    // Failures that were due to other kinds of exceptions
    protected Vector mOtherFailures = new Vector();
    protected Dictionary mAllRequests = null; 
    
    protected URLHammerConfig mConfig;
    
    public URLHammerStats() {}
    
    public URLHammerStats(URLHammerConfig pConfig) {
	mConfig = pConfig;
    }
    
    
    /**
     * Returns the Requests Dictionary
     */
    public Dictionary getAllRequests() {
	return mAllRequests;
    }

    /**
     * Returns the URLHammerConfig
     */
    public URLHammerConfig getURLHammerConfig() {
	return mConfig;
    }
    
    //-------------------------------------
    /**
     * Returns the test's total elapsed time, as reported by URLHammer
     * @return long - the total time elapsed
     */
    public long getTotalTime(){return totalTime;}

    public void setTotalTime(long pLong) { totalTime = pLong; }
    
    //-------------------------------------
    //-------------------------------------
    // Interface for retrieving info from SummaryStats
    /**
     * Returns the total number of successful requests
     * @return int - the number of successes
     */
    public int getNumSuccess() { return summary.numSuccess; }
    /**
     * Returns the total number of errors
     * @return int - the  number of errors
     */
    public int getNumErrors() {return summary.numErrors; }
    /**
     * Returns the total number of stopped requests
     * @return int - the number of stopped requests
     */
    public int getNumStopped() { return summary.numStopped; }
    /**
     * Returns the minimum time in milliseconds taken to complete a request
     * @return int - the minimum request time
     */
     public int getMinMillis() { return summary.minMillis; }
    /**
     * Returns the maximum time in milliseconds taken to complete a request
     * @return int - the maximum request time
     */
    public int getMaxMills() { return summary.maxMillis; }
    /**
     * Returns the total time in milliseconds of all requests
     * @return int - the total time for all requests
     */
    public int getTotalMillis() { return summary.totalMillis; }
    
    //-------------------------------------
    //-------------------------------------
    // Interface for retrieving info from perUrlStats
    public boolean hasMoreThanOneURL(){
	return (perURLStats.size() > 1);
    }
    public Enumeration getPerURLKeys(){
	return perURLStats.keys();
    }
    public int getURLTotalSuccess(String pKey) {
	return ((PerURLStats)perURLStats.get(pKey)).getTotalSuccess();
    }
    public int getURLTotalMillis(String pKey) {
	return ((PerURLStats)perURLStats.get(pKey)).getTotalMillis();
    }
    public boolean urlHasErrors(String pKey) {
	return ((PerURLStats)perURLStats.get(pKey)).hasErrors();
    }
    
    //-------------------------------------
    //-------------------------------------
    public synchronized void addRequest(URLHammerRequest request) {
	summary.addRequest(request);
	if (request.gotError()) {
	    ErrorStats e = (ErrorStats) errorStats.get(request.statusCode);
	    if (e == null) {
		e = new ErrorStats();
		errorStats.put(request.statusCode, e);
		e.statusCode = request.statusCode;
	    }
	    Integer i = (Integer) e.urls.get( request.url );
	    if (i == null) 
		e.urls.put( request.url, new Integer( 1 ));
	    else
		e.urls.put( request.url, new Integer( 1 + i.intValue() ) );
	}
	PerURLStats u = (PerURLStats) perURLStats.get(request.url + ":::" + request.method);
	if (u == null) {
	    u = new PerURLStats();
	    u.url = request.url;
	    u.method = request.method;
	    perURLStats.put(request.url + ":::" + request.method, u);
	}
	u.summary.addRequest(request);
	
	if (mConfig != null && mConfig.recordAllRequests) {
	    if (mAllRequests == null) mAllRequests = new OrderedHashtable(); 
	    Vector v = (Vector) mAllRequests.get(request.url + ":::" + request.method);
	    if (v == null) {
		v = new Vector();
		mAllRequests.put(request.url + ":::" + request.method, v);
	    }
	    v.addElement(request.clone());
	}
    }              
    
    public synchronized void addSessionRequest(URLHammerRequest request) {
	if (request.sessionId != null) {
	    SummaryStats s = (SummaryStats) sessionSummary.get(request.sessionId);
	    if (s == null) {
		s = new SummaryStats();
		sessionSummary.put(request.sessionId, s);
	    }
	    s.addRequest(request);
	}
    }
    
    synchronized void recordOtherFailure(Exception e) {
	mOtherFailures.addElement(e);
    }
    
    public synchronized boolean hasOtherFailures() {
	return !(mOtherFailures.size() == 0); 
    }
    
    synchronized void recordIOFailure(IOException e) {
	mIOFailures.addElement(e);
    }
    
    public synchronized boolean hasIOFailures() {
	return !(mIOFailures.size() == 0); 
    }
    
    public synchronized boolean hasErrors() {
	return !errorStats.isEmpty(); 
    }
    
    public void writeHtmlFile(String htmlFile) {
	PrintWriter out = null;
	try {
	    out = new PrintWriter(new FileOutputStream(new File(htmlFile)));
	    out.println("<html><head><title>URLHammer Output</title></head>");
	    out.println("<body><h1>URLHammer Output</h1>");
	    outputHtmlContent(out);
	    out.println("</body></html>");
	}
	catch (IOException exc) {
	    System.err.println("*** unable to create html stat file: " + htmlFile);
	}
	finally {
	    if (out != null) {
		out.close();
	    }
	}
    }
    
    public void outputHtmlContent(PrintWriter out) {
	out.println("<h2>Summary Statistics</h2>");
	summary.outputHtml(out);
	//out.println("<table cellspacing=5 cellpadding=5>");
	out.println("<table cellpadding=5>");
	for (Enumeration l = errorStats.elements(); l.hasMoreElements(); ) {
	    ErrorStats e = (ErrorStats) l.nextElement();
	    out.print("<tr><th>Count</th><th>");
	    out.print("Errors with status code ");
	    out.print(e.statusCode);
	    out.println(" occurred for these URLs:</th></tr>");
	    Enumeration v = e.urls.elements();
	    Enumeration k = e.urls.keys();
	    while (v.hasMoreElements()) {
		String key = (String)k.nextElement();
		out.print("<tr><td align=right>" + v.nextElement() + "</td><td>");
		out.print("<A HREF=\"");
		out.print(key);
		out.print("\">");
		out.print(key);
		out.print("</A>");
		out.print("</td></td>");
	    }
	}
	out.println("</table>");
	out.println("<hr>");
	out.println("<h2>Per URL Statistics</h2>");
	out.println("<table border>");
	out.println("<tr><th>method</th><th>url</th></tr>");
	for (Enumeration l = perURLStats.elements(); l.hasMoreElements(); ) {
	    PerURLStats u = (PerURLStats) l.nextElement();
	    out.print("<tr><td>");
	    out.print(u.method);
	    out.print("</td><td>");
	    out.print(u.url);
	    out.print("</td></tr>");
	    out.print("<tr><td colspan=2>");
	    u.summary.outputHtml(out);
	    out.print("</td></tr>");
	    if (mAllRequests != null) {
		Vector v = (Vector) mAllRequests.get(u.url + ":::" + u.method); 
		if (v != null) {
		    out.println("<tr><th colspan=2>request times</th></tr>");
		    out.println("<td colspan=2>");
		    for (int i = 0; i < v.size(); i++) {
			URLHammerRequest r = (URLHammerRequest) v.elementAt(i);
			out.print(r.time);
			if (r.gotError())
			    out.print("(error)");
			out.print(" ");
		    }
		}
		out.println("</td>");
	    }
	}
	out.println("</table>");
	out.println("<hr>");
	out.println("<h2>Per Session Statistics</h2>");
	out.println("<table>");
	for (Enumeration l = sessionSummary.keys(); l.hasMoreElements(); ) {
	    String sessionId = (String) l.nextElement();
	    SummaryStats s = (SummaryStats) sessionSummary.get(sessionId);
	    out.print("<tr><td>");
	    out.print("Session id (from script file): ");
	    out.print(sessionId);
	    out.print("</td></tr>");
	    out.print("<tr><td>");
	    s.outputHtml(out);
	    out.print("</td></tr>");
	}
	out.println("</table>");
    }
    
    public String toString() {
	StringBuffer out = new StringBuffer();
	
	out.append("\n+Summary Statistics+\n");
	out.append("====================\n");
	
	out.append(summary.toString());
	
	//out.append("+------------------------------------------------------------------------------+\n");
	for (Enumeration l = errorStats.elements(); l.hasMoreElements(); ) {
	    ErrorStats e = (ErrorStats) l.nextElement();
	    out.append("Count\t\t");
	    out.append("Errors with status code ");
	    out.append(e.statusCode);
	    out.append(" occurred for these URLs:\n");
	    out.append("-----\t\t----------------------------------------------------\n");
	    Enumeration v = e.urls.elements();
	    Enumeration k = e.urls.keys();
	    while (v.hasMoreElements()) {
		String key = (String)k.nextElement();
		out.append("" + v.nextElement() + "\t\t");	
		out.append(key);
		out.append("\n");
	    }
	}
	//out.append("+------------------------------------------------------------------------------+\n");
	out.append("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
	out.append("+Per URL Statistics+\n");
	out.append("====================\n");

	//out.append("\n+------------------------------------------------------------------------------+\n");
	out.append("\nmethod\t\turl\n");
	out.append("------\t\t---\n");
	for (Enumeration l = perURLStats.elements(); l.hasMoreElements(); ) {
	    PerURLStats u = (PerURLStats) l.nextElement();
	    out.append("");
	    out.append(u.method);
	    out.append("\t\t");
	    out.append(u.url);
	    out.append("\n");
	    out.append("\t\t");
	    out.append(u.summary.toString());
	    out.append("\n");
	    if (mAllRequests != null) {
		Vector v = (Vector) mAllRequests.get(u.url + ":::" + u.method); 
		if (v != null) {
		    out.append("request times\n");
		    out.append("-------------\n");

		    out.append("");
		    for (int i = 0; i < v.size(); i++) {
			URLHammerRequest r = (URLHammerRequest) v.elementAt(i);
			out.append(r.time);
			if (r.gotError())
			    out.append("(error)");
			out.append(" ");
		    }
		}
		out.append("\n");
	    }
	}
	//out.append("+------------------------------------------------------------------------------+\n");
	out.append("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
	out.append("+Per Session Statistics+\n");
	out.append("========================\n");
	//out.append("+------------------------------------------------------------------------------+\n");
	for (Enumeration l = sessionSummary.keys(); l.hasMoreElements(); ) {
	    String sessionId = (String) l.nextElement();
	    SummaryStats s = (SummaryStats) sessionSummary.get(sessionId);
	    out.append("");
	    out.append("Session id (from script file): ");
	    out.append(sessionId);
	    out.append("\n");
	    out.append("\t\t");
	    out.append(s.toString());
	    out.append("\n");
	}
	//out.append("+------------------------------------------------------------------------------+\n");
	
	return out.toString();
    }
}

//---------------------------------------------------------
/**
 * These classes implement the statistics gathering functionality for
 * URL hammer
 */


/** Stores generic summary of request stuff */
class SummaryStats
    implements Serializable {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerStats.java#2 $$Change: 651448 $";

    static final long serialVersionUID = -398246896023084466L;
    int numSuccess = 0;
    int numErrors = 0;
    int numStopped = 0;
    int minMillis = -1, maxMillis = -1;
    int minLength = -1, maxLength = -1;
    int totalMillis = 0;
    
    synchronized void addRequest(URLHammerRequest request) {
	if (request.gotError()) numErrors++;
	else if (request.stopped) numStopped++;
	else numSuccess++;
	
	if (request.time < minMillis || minMillis == -1)
	    minMillis = request.time;
	if (request.time > maxMillis || maxMillis == -1)
	    maxMillis = request.time;
	totalMillis += request.time;
	
	if (!request.stopped) {
	    if (minLength == -1 || minLength > request.dataLength)
		minLength = request.dataLength;
	    if (maxLength == -1 || maxLength < request.dataLength)
		maxLength = request.dataLength;
	}
    }
    
    
    void outputHtml(PrintWriter out) {
	int totalRequests = numSuccess + numErrors + numStopped;
	if (totalRequests == 0) {
	    out.println("No requests processed.");
	    return;
	}
	out.println("<table border>");
	out.println("<tr><th># Success</th><th># Errors</th><th># Randomly stopped</th><th>Avg Millis</th><th>Min Millis</th><th>Max Millis</th><th>Ops/second</th><th>Min Length</th><th>Max Length</th>");
	out.print("<tr align=center><td>");
	out.print(numSuccess);
	out.print("</td><td>");
	out.print(numErrors);
	out.print("</td><td>");
	out.print(numStopped);
	out.print("</td><td>");
	out.print(totalMillis / totalRequests);
	out.print("</td><td>");
	out.print(minMillis);
	out.print("</td><td>");
	out.print(maxMillis);
	out.print("</td><td>");
	double throughput = ((double) totalRequests / (double) totalMillis) * 1000.0;
	out.print(atg.core.util.NumberFormat.fixedPoint(throughput,2));
	out.print("</td><td>");
	out.print(minLength);
	out.print("</td><td>");
	out.print(maxLength);
	out.println("</td></tr>");
	out.println("</table>");
    }
    
    public String toString() {
	StringBuffer out = new StringBuffer();
	
	int totalRequests = numSuccess + numErrors + numStopped;
	if (totalRequests == 0) {
	    out.append("No requests processed.\n");
	    return out.toString();
	   }
	out.append("\n-------------------------------------------------------------------\n");
	out.append("#Suc\t#Err\t#Stp\tAvg(ms)\tMin(ms)\tMax(ms)\tOps/Sec\tMin\tMax\n");
	out.append("");
	out.append(numSuccess);
	out.append("\t");
	out.append(numErrors);
	out.append("\t");
	out.append(numStopped);
	out.append("\t");
	out.append(totalMillis / totalRequests);
	out.append("\t");
	out.append(minMillis);
	out.append("\t");
	out.append(maxMillis);
	out.append("\t");
	double throughput = ((double) totalRequests / (double) totalMillis) * 1000.0;
	out.append(atg.core.util.NumberFormat.fixedPoint(throughput,2));
	out.append("\t");
	out.append(minLength);
	out.append("\t");
	out.append(maxLength);
	out.append("\n");
	out.append("-------------------------------------------------------------------\n");
	return out.toString();
    }
} 

/** Stored for each error code that we see */
class ErrorStats
    implements Serializable{

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerStats.java#2 $$Change: 651448 $";

    static final long serialVersionUID = 2285029118943268871L;
    //Vector urls = new Vector();
    Hashtable urls = new Hashtable();
    int statusCode;
}

class PerURLStats
    implements Serializable {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerStats.java#2 $$Change: 651448 $";

    static final long serialVersionUID = -3614022159240475023L;
    SummaryStats summary = new SummaryStats();
    String method;
    String url;
    
    // interface to info in summary stats
    public int getTotalSuccess() {
	return summary.numSuccess;
    }
    public int getTotalMillis() {
	return summary.totalMillis;
    }
    public boolean hasErrors() {
	return (summary.numErrors > 0);
    }
}

