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

package atg.droplet;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.core.util.ResourceUtils;
import atg.core.util.NumberTable;

import java.io.*;
import java.util.*;
import java.text.*;

import java.lang.reflect.*;

/**
 * This servlet renders its <i>output</i> parameter 
 * <i>howMany</i> times.  The <i>howMany</i> parameter must be a
 * String representation of an int.
 * <p>
 * Each iteration will set the parameters <i>index</i> and <i>count</i> to 
 * the current loop index (0-based) and count (1-based), respectively.
 * <dl>
 *
 * <dt>howMany
 * <dd>The parameter that defines the number of times to render the output
 * parameter.
 *
 * <dt>output
 * <dd>This parameter is rendered the number of times specified by the howmany
 * parameter.
 *
 * <dt>index
 * <dd>This parameter is set to the number of the current iteration of
 * the loop.  Its value for the first iteration of the loop is 0.
 *
 * <dt>count
 * <dd>This parameter is set to the number of the current iteration of
 * the loop.  Its value for the first iteration of the loop is 1.
 *
 * </dl>
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/For.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class For extends DynamoServlet {

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/For.java#2 $$Change: 651448 $";

  public final static String INDEX = "index";
  public final static String COUNT = "count";
  public final static String OUTPUT = "output";
  public final static String HOW_MANY = "howMany";
  // for backward compatibility
  public final static String INDEX_NAME = "indexName";
  public final static String HOW_MANY_OLD = "howmany";
  
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
       throws ServletException, IOException 
  {    
    String howManyParam = pReq.getParameter(HOW_MANY);
    
    if (howManyParam == null) {
      // for backward compatibility
      howManyParam = pReq.getParameter(HOW_MANY_OLD);
      if (howManyParam == null) {
        if (isLoggingError()) 
          logError(ResourceUtils.getMsgResource("noHowMany", MY_RESOURCE_NAME, sResourceBundle));
        return;
      }
    }

    String indexName = pReq.getParameter(INDEX_NAME);

    if (indexName == null)  
      indexName = INDEX;
    
    int howMany;
    try {
      howMany = Integer.parseInt(howManyParam);
      if (howMany < 0) {
	if (isLoggingError()) {
	  Object[] args = { NumberTable.getInteger(howMany) };
	  logError(ResourceUtils.getMsgResource("illegalHowMany", MY_RESOURCE_NAME, 
						sResourceBundle, args));
	}
	return;
      }
    }
    catch (NumberFormatException e) {
      if (isLoggingError()) {
	Object[] args = { howManyParam };
	logError(ResourceUtils.getMsgResource("illegalHowMany", MY_RESOURCE_NAME, 
					      sResourceBundle, args));
      }
      return;
    }

    for (int i = 0; i < howMany; i++) {
      pReq.setParameter(indexName, NumberTable.getInteger(i));
      pReq.setParameter(COUNT, NumberTable.getInteger(i + 1));
      pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
    }    
  }
}

