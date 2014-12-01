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

/**
 * Created for each thread; reset for each request
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerRequest.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class URLHammerRequest 
    implements Cloneable, 
	       Serializable
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/net/URLHammerRequest.java#2 $$Change: 651448 $";
  
  static final long serialVersionUID =  5664934193114546563L;
    
  public int time;
  public String sessionId;
  public String method;
  public String url;   // why does this have to be public?
  public int statusCode;
  public boolean stopped = false;
  public int dataLength;

 /* Status codes (from HTTP 1.1 -- RFC 2068)
  6.1.1 Status Code and Reason Phrase

  The Status-Code element is a 3-digit integer result code of the attempt to understand and satisfy the request. These codes
  are fully defined in section 10. The Reason-Phrase is intended to give a short textual description of the Status-Code. The
  Status-Code is intended for use by automata and the Reason-Phrase is intended for the human user. The client is not required
  to examine or display the Reason- Phrase. 

  The first digit of the Status-Code defines the class of response. 
  The last two digits do not have any categorization role. There
  are 5 values for the first digit: 

       1xx: Informational - Request received, continuing process 
       2xx: Success - The action was successfully received, understood, and accepted 
       3xx: Redirection - Further action must be taken in order to complete the request 
       4xx: Client Error - The request contains bad syntax or cannot be fulfilled 
       5xx: Server Error - The server failed to fulfill an apparently valid request 
       */

  // Obsolete:  from original code
  protected boolean getError() {
    return statusCode != 200 && statusCode != 302;
  }
  
  // These follow the RFC:
  public boolean isOkay() {
     return( statusCode >= 100 && statusCode < 300 );
  }

  public boolean wasRedirected() {
     return( statusCode >= 300 && statusCode < 400 );
  }

 /* 
  * If no response headers were received, 0 is returned.  If no data
  * was returned, it is an error unless it was a redirect response
  * in which case we probably didn't do the error anyway
  */
  public boolean gotError() {
    /* If we stopped, it is not an error */
    if (stopped) return false;

    /* Zero status code is an error always */
    if (statusCode == 0) return true;

    /* If no data was returned and it is not a redirect, it is an error */
    if (dataLength == 0 && statusCode != 302) return true;

    /* Otherwise, look for the error status codes */
    return (statusCode >= 400 && statusCode < 600 );
  }

  public boolean gotClientError() {
     return( statusCode >= 400 && statusCode < 500 );
  }

  public boolean gotServerError() {
     return( statusCode >= 500 && statusCode < 600 );
  }

  public void reset() {
     time = 0;
     stopped = false;
     statusCode = 200;
     sessionId = null;
     method = null;
     url = null;
     dataLength = 0;
  }

  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException exc) {
      throw new InternalError();
    }
  }

}
