/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.commerce.gears.orderstatus;

import atg.droplet.GenericFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.commerce.states.OrderStates;

import atg.nucleus.naming.ParameterName;
import atg.userprofiling.Profile;
import atg.droplet.DropletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.transaction.*;
import java.io.IOException;
import java.util.*;

/**
 * This formHandler is used in the full view of the Order Status gear. It
 * stores the different properties displayed in the full view of the order status gear.
 *
 * @author Naveen Gabrani
 * @version $Id: //product/DCS/version/10.0.3/gears/OrderStatus/classes.jar/src/atg/commerce/gears/orderstatus/OrderStatusFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A form handler that can be used to display full view of the order status gear.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory CommerceGears
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @see GenericFormHandler
 *
 */

public class OrderStatusFormHandler
extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/gears/OrderStatus/classes.jar/src/atg/commerce/gears/orderstatus/OrderStatusFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // property: SuccessURL
  private String mSuccessURL;

  //-------------------------------------
  // property: FailureURL
  private String mFailureURL;


  /** This property stores the array of string that correspond to the states selected by the user in the full view of the order gear. */
  private String[] mOrderStates={"all"};

  /** These are the states that are shown to the user in the shared view of the order gear. */
  private String []mSharedViewStates;

  /** Stores the sorting order. */
  private String mSortBy;

  /** Stores whether the results are in ascending or descending order. */
  private boolean mSortAscending;

  /**
   * Sets property OrderStates
   * @param pOrderStates  The property to store the Order States specified by the user.
   * @beaninfo description:  The property to store the Order States specified by the user.
   **/
  public void setOrderStates(String []pOrderStates) {
    mOrderStates = pOrderStates;
  }

  /**
   * Returns property OrderStates
   * @return The value of the property pOrderStates.
   **/
  public String[] getOrderStates() {
    return mOrderStates;
  }


  /**
   * Sets property sharedViewStates
   * @param pSharedViewStates  The property to store the Shared View of the States specified by the user.
   * @beaninfo description:  The property to store the Shared View of States specified by the user.
   **/
  public void setSharedViewStates(String []pSharedViewStates) {
    mSharedViewStates = pSharedViewStates;
  }

  /**
   * Returns property sharedViewStates
   * @return The value of the property pSharedViewStates.
   **/
  public String[] getSharedViewStates() {
    return mSharedViewStates;
  }


  /**
   * Sets property SortBy
   * @param pSortBy  The property to store the Sorting column specified by the user.
   * @beaninfo description:  The property to store the Sorting column specified by the user.
   **/
  public void setSortBy(String pSortBy) {
    mSortBy = pSortBy;
  }

  /**
   * Returns property SortBy
   * @return The value of the property pSortBy.
   **/
  public String getSortBy() {
    return mSortBy;
  }



  /**
   * Sets property SortAscending
   * @param pSortAscending  The property to store the sorting order - ascending or descending.
   * @beaninfo description:  The property to store the sorting order - ascending or descending.
   **/
  public void setSortAscending(boolean pSortAscending) {
    mSortAscending = pSortAscending;
  }

  /**
   * Returns property SortAscending
   * @return The value of the property pSortAscending.
   **/
  public boolean isSortAscending() {
    return mSortAscending;
  }

  /**
   * Sets property FailureURL
   * @param pFailureURL  The property to store the success URL for Full view of order status gear.
   * @beaninfo description:  The property to store the success URL for Full view of order status gear.
   **/
  public void setFailureURL(String pFailureURL) {
    mFailureURL = pFailureURL;
  }

  /**
   * Returns property FailureURL
   * @return The value of the property pFailureURL.
   **/
  public String getFailureURL() {
    return mFailureURL;
  }



  /**
   * Sets property SuccessURL
   * @param pSuccessURL  The property to store the success URL for Full view of order status gear.
   * @beaninfo description:  The property to store the success URL for Full view of order status gear.
   **/
  public void setSuccessURL(String pSuccessURL) {
    mSuccessURL = pSuccessURL;
  }

  /**
   * Returns property SuccessURL
   * @return The value of the property pSuccessURL.
   **/
  public String getSuccessURL() {
    return mSuccessURL;
  }


  /* The handle method just redirects to the success url. Its main
   * purpose is to make sure the selections made by the user in the full
   * view of the gear are saved.
   */
  public boolean handleShowOrders(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
     throws IOException, ServletException
  {
    if(isLoggingDebug()) {
      logDebug("Success url is " + getSuccessURL());
      logDebug("no of elems in array is " + mOrderStates.length);
    }

    pResponse.sendLocalRedirect(getSuccessURL(),pRequest);
    return false;
  }


}


