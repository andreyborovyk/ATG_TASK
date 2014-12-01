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

package atg.commerce.order;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.nucleus.naming.ParameterName;
import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.ObjectStates;

import java.io.*;
import java.util.*;

/**
 * This droplet is used to format a state of an order or its comonent in a humanly readable way.
 *
 * <P>
 *
 * Example:
 * <PRE>
 * <importbean bean="/atg/dynamo/droplet/OrderStatesDetailed">
 * <droplet bean="StateDroplet">
 *   <param name="state" value="param:order.state">
 *   <oparam name="output">
 *     <valueof param="detailedState"></valueof>
 *   </oparam>
 * </droplet>
 * </PRE>
 * 
 * Usage:
 * <dl>
 *
 * <dt>states
 * <dd>List of all possible states that might occur. This is different for each order component:
 * PaymentGroup, ShippingGroup, etc.
 * <i>Should be set in the properties file</i>
 * 
 * <dt>state
 * <dd>A number, representing a state
 *
 * <dt>output
 * <dd>This parameter is serviced if the instance of this class is used as a droplet
 *
 * <dt>detailedState
 * <dd>Available from within the <code>output</code> parameter, this is the state explanatin displayed
 * </dl>
 *
 * @author Misha Rutman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/StateDetailDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class StateDetailDroplet extends DynamoServlet
{
  //-------------------------------------
  // Class version string
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/StateDetailDroplet.java#2 $$Change: 651448 $";

  //-------------------------------------
  public final static ParameterName STATE  = ParameterName.getParameterName("state");
  public final static ParameterName OUTPUT = ParameterName.getParameterName("output");
  public final static ParameterName ERROR  = ParameterName.getParameterName("error");

  static final String DETAILED_STATE = "detailedState";

  //-------------------------------------
  private ObjectStates mStates;

  /**
   * Sets the universe of states in question
   */
  public void setStates(ObjectStates pStates) {
	  mStates = pStates;
  }

  /**
   * Returns the universe of states in question
   */
  public ObjectStates getStates() {
	  return mStates;
  }

  //---------------------------------------------------------------------------
  // property:UseResourcedStateDescriptions
  //---------------------------------------------------------------------------

  private boolean mUseResourcedStateDescriptions;
  public void setUseResourcedStateDescriptions(boolean pUseResourcedStateDescriptions) {
    mUseResourcedStateDescriptions = pUseResourcedStateDescriptions;
  }

  /**
   * If this is true, then the state descriptions are pulled from a resource file
   * @see atg.commerce.states.ObjectStates
   **/
  public boolean isUseResourcedStateDescriptions() {
    return mUseResourcedStateDescriptions;
  }

  //-------------------------------------
  /** 
   * Given state representation, converts it from an integer representation
   * into a readable string, and returns that string
   * @param pState the number representing a state
   * @return a string -- readable representation of the param
   */
  String getStateDetail(int pState) throws CommerceException
  {
	  ObjectStates states = getStates();
	  if (states == null)
      throw new InvalidParameterException();

	  try {
      if(isUseResourcedStateDescriptions())
        return states.getStateDescriptionAsUserResource(pState);
      else
        return states.getStateDescription(pState);
	  } catch (MissingResourceException e) {
	    if (isLoggingError())
  		  logError(e);
	  }

    return null;
  }

  /**
   * Takes the formatted currency String and sets a request parameter named
   * <code>detailedState</code>, then services the <code>output</code>
   * parameter.
   * @param DynamoHttpServletRequest
   * @param DynamoHttpServletResponse
   */    
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
  	      throws ServletException, IOException
  {
	  ObjectStates states = getStates();
	  if (states == null) 
     throw new ServletException("No States Defined");

	  try {
	    String state = pRequest.getParameter(STATE);
	    String detailedState = getStateDetail(Integer.parseInt(state));    	    
	    pRequest.setParameter(DETAILED_STATE, detailedState);
	    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	    
	  } catch (CommerceException e) {
      if (isLoggingError())
	      logError(e);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
	  }
  }
}
