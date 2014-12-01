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

package atg.servlet.exittracking;

import java.util.*;

/**
 *
 * <p>An ExitTrackingEvent encapsulates information related to a
 * redirect caused by exit tracking.
 *
 * @author Robert Brazile
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingEvent.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class ExitTrackingEvent
extends EventObject
{
  static final long serialVersionUID = -1617330141066722432L;

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingEvent.java#2 $$Change: 651448 $";

  /** The Handler URL */
  String mHandlerURL;

  /** Parameters to be passed to the Handler URL */
  String mHandlerParameterName;

  //-------------------------------------
  /**
   * Constructs a new ServiceEvent
   * @param pHandlerURL The Handler URL
   * @param pHandlerParameterName The parameter name to be passed to the URL
   **/
  public ExitTrackingEvent (Object pSourceObject, String pHandlerURL, String pHandlerParameterName)
  {
    super (pSourceObject);
    mHandlerURL = pHandlerURL;
    mHandlerParameterName = pHandlerParameterName;
  }

  public String getHandlerURL()
  {
    return mHandlerURL;
  }

  public void setHandlerURL(String URL)
  {
    mHandlerURL = URL;
  }

  public String getHandlerParameterName()
  {
    return mHandlerParameterName;
  }

  public void setHandlerParameterName(String parm)
  {
    mHandlerParameterName = parm;
  }
}
