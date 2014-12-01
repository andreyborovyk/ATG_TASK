/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.commerce.order.purchase;

import atg.commerce.order.*;
import atg.nucleus.ServiceMap;
import atg.repository.*;
import atg.droplet.*;
import atg.servlet.*;
import atg.commerce.CommerceException;
import atg.commerce.pricing.*;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.service.pipeline.*;
import atg.core.util.StringUtils;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;

import javax.transaction.*;

/**
 * <p>The <code>ShippingGroupFormHandler</code> is used to associate ShippingGroups with the various
 * Order pieces. This component is used during the Order checkout process, and any
 * Order successfully processed by the ShippingGroupFormHandler is ready for the next
 * checkout phase, which may be Payment.
 *
 *
 * <p>The ShippingGroupFormHandler is composed of the following containers:
 *
 * <br>ShippingGroupMapContainer - container for the user's potential ShippingGroups
 *
 * <br>CommerceItemShippingInfoContainer - container for the user's CommerceItemShippingInfo
 * associations for a particular Order's CommerceItems
 *
 * <p>There are 3 main handler methods in the ShippingGroupFormHandler, handleSplitShippingInfos,
 * handleApplyShippingGroups and handleSpecifyDefaultShippingGroup:
 *
 * <ol>
 * <li>handleSplitShippingInfos splits extra CommerceItemShippingInfo objects by quantity.
 * In a form the user might request to split 5 of an original CommerceItem quantity
 * of 10 onto a separate ShippingGroup. This will create a separate CommerceItemShippingInfo
 * object, and adjust the quantity of both the original and the new CommerceItemShippingInfo
 * objects to add up to the original CommerceItem quantity.
 *
 * <li>handleApplyShippingGroups is used when the CommerceItemShippingInfo associations created
 * by the user are ready to be applied to the current Order. The associations are scrutinized
 * and the appropriate business methods are called in the OrderManager family. ShippingGroup
 * validation takes place, via a configurable Pipeline chain.
 *
 * <li>handleSpecifyDefaultShippingGroup is used to specify a particular default ShippingGroup
 * to be used for CommerceItems that aren't explicitly assigned separate ShippingGroups.
 *
 * </ol>
 *
 * <p>In order to conveniently manipulate ShippingGroups and CommerceItemShippingInfo Lists,
 * you can set the <code>ListId</code> and <code>ShippingGroupId</code> properties. This will
 * automatically expose the corresponding ShippingGroup and List in the <code>CurrentList</code>
 * and <code>CurrentShippingGroup</code> properties.
 *
 *
 * <p>The CommerceItemShippingInfo associations created by the user are scrutinized
 * and the appropriate business methods are called in the OrderManager family. ShippingGroup
 * validation takes place, via a configurable Pipeline chain.
 *
 * @author Charles Chen
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 * @beaninfo
 *   description: A form handler which provides multiple ShippingGroup support.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 */
public class ShippingGroupFormHandler
  extends PurchaseProcessFormHandler
{

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_INVALID_SHIPPING_SPLIT_NUMBER = "invalidShippingSplitNumber";
  public static final String MSG_ERROR_UPDATE_SHIPPINGGROUP = "errorUpdateShippingGroup";
  public static final String MSG_NO_DEFAULT_SHIPPING_GROUP = "noDefaultShippingGroup";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //------------------

  protected CommerceItemShippingInfoTools mCommerceItemShippingInfoTools;
  /**
   * Returns the tools component containing the API for modifying
   * CommerceItemShippingInfos 
   * @return
   */
  public CommerceItemShippingInfoTools getCommerceItemShippingInfoTools()
  {
    return mCommerceItemShippingInfoTools;
  }

  public void setCommerceItemShippingInfoTools(
      CommerceItemShippingInfoTools pCommerceItemShippingInfoTools)
  {
    mCommerceItemShippingInfoTools = pCommerceItemShippingInfoTools;
  }
  //---------------------------------------------------------------------------
  // property: ShippingGroupInitializers
  //---------------------------------------------------------------------------
  ServiceMap mShippingGroupInitializers;

  /**
   * Set the ShippingGroupInitializers property.
   * @param pShippingGroupInitializers a <code>ServiceMap</code> value
   */
  public void setShippingGroupInitializers(ServiceMap pShippingGroupInitializers) {
    mShippingGroupInitializers = pShippingGroupInitializers;
  }

  /**
   * Return the ShippingGroupInitializers property.
   * @return a <code>ServiceMap</code> value
   */
  public ServiceMap getShippingGroupInitializers() {
    return mShippingGroupInitializers;
  }


  protected boolean mConsolidateShippingInfosBeforeApply=false;
  /**
   * @return Returns the consolidateShippingInfosBeforeApply.
   */
  public boolean isConsolidateShippingInfosBeforeApply()
  {
    return mConsolidateShippingInfosBeforeApply;
  }

  /**
   * Sets the consolidateShippingInfosBeforeApply property.
   * <p>
   * This property is used to control is the CommerceItemShippingInfo objects are
   * consolidated prior to being applied to the order.
   * <p>
   * @see #consolidateShippingInfos(DynamoHttpServletRequest, DynamoHttpServletResponse)
   * @param pConsolidateShippingInfosBeforeApply The consolidateShippingInfosBeforeApply to set.
   */
  public void setConsolidateShippingInfosBeforeApply(
      boolean pConsolidateShippingInfosBeforeApply)
  {
    mConsolidateShippingInfosBeforeApply = pConsolidateShippingInfosBeforeApply;
  }



  protected boolean mValidateShippingGroups = true;

  /**
   * Sets property ValidateShippingGroups
   * @beaninfo description: flag indicating if the shipping groups should be validated
   * after being applied in <code>handleApplyShippingGroups</code>
   * <p>
   * The default setting is true.
   **/
  public void setValidateShippingGroups(boolean pValidateShippingGroups) {
    mValidateShippingGroups = pValidateShippingGroups;
  }

  /**
   * Returns property ValidateShippingGroups.
   **/
  public boolean isValidateShippingGroups() {
    return mValidateShippingGroups;
  }

  //---------------------------------------------------------------------------
  // property: ListId
  //---------------------------------------------------------------------------
  String mListId;

  /**
   * Set the ListId property.
   * <p>
   * Sets the currentList property to the list of commerce item infos mapped by the listId.
   * @param pListId a <code>String</code> value. The commerce item id used to look up the list
   * of commerce item infos in the commerceItemShippingInfoContainer. If pListId is null or empty
   * the current list is set to all commerce item infos in the container.
   * @see CommerceItemShippingInfoContainer#getAllCommerceItemShippingInfos()
   * @beaninfo description: The id of the current CommerceItemShippingInto List.
   */
  public void setListId(String pListId) {
    //if there's no list id provided then set the list to the cached on
    if(StringUtils.isBlank(pListId))
      setCurrentList(getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos());
    else
      setCurrentList(getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(pListId));

    mListId = pListId;
  }

  /**
   * Return the ListId property.
   * @return a <code>String</code> value
   */
  public String getListId() {
    return mListId;
  }

  //---------------------------------------------------------------------------
  // property: CurrentList
  //---------------------------------------------------------------------------
  List mCurrentList = null;

  /**
   * Set the CurrentList property.
   * @param pCurrentList a <code>List</code> value
   * @beaninfo description: The current CommerceItemShippingInto List.
   */
  public void setCurrentList(List pCurrentList) {
    mCurrentList = pCurrentList;
  }

  /**
   * Return the CurrentList property.
   * @return a <code>List</code> value
   */
  public List getCurrentList() {
    return mCurrentList;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupId
  //---------------------------------------------------------------------------
  String mShippingGroupId;

  /**
   * Set the ShippingGroupId property.
   * @param pShippingGroupId a <code>String</code> value
   * @beaninfo description: The id of the current ShippingGroup.
   */
  public void setShippingGroupId(String pShippingGroupId) {
    mShippingGroupId = pShippingGroupId;
    setCurrentShippingGroup(getShippingGroup(pShippingGroupId));
  }

  /**
   * Return the ShippingGroupId property.
   * @return a <code>String</code> value
   */
  public String getShippingGroupId() {
    return mShippingGroupId;
  }

  //---------------------------------------------------------------------------
  // property: CurrentShippingGroup
  //---------------------------------------------------------------------------
  ShippingGroup mCurrentShippingGroup = null;

  /**
   * Set the CurrentShippingGroup property.
   * @param pCurrentShippingGroup a <code>ShippingGroup</code> value
   * @beaninfo description: The current ShippingGroup.
   */
  public void setCurrentShippingGroup(ShippingGroup pCurrentShippingGroup) {
    mCurrentShippingGroup = pCurrentShippingGroup;
  }

  /**
   * Return the CurrentShippingGroup property.
   * @return a <code>ShippingGroup</code> value
   */
  public ShippingGroup getCurrentShippingGroup() {
    return mCurrentShippingGroup;
  }

  //---------------------------------------------------------------------------
  // property: ApplyShippingGroupsSuccessURL
  //---------------------------------------------------------------------------
  String mApplyShippingGroupsSuccessURL;

  /**
   * Set the ApplyShippingGroupsSuccessURL property.
   * @param pApplyShippingGroupsSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setApplyShippingGroupsSuccessURL(String pApplyShippingGroupsSuccessURL) {
    mApplyShippingGroupsSuccessURL = pApplyShippingGroupsSuccessURL;
  }

  /**
   * Return the ApplyShippingGroupsSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getApplyShippingGroupsSuccessURL() {
    return mApplyShippingGroupsSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: ApplyShippingGroupsErrorURL
  //---------------------------------------------------------------------------
  String mApplyShippingGroupsErrorURL;

  /**
   * Set the ApplyShippingGroupsErrorURL property.
   * @param pApplyShippingGroupsErrorURL a <code>String</code> value
   * @beaninfo description:  Error URL for redirection.
   */
  public void setApplyShippingGroupsErrorURL(String pApplyShippingGroupsErrorURL) {
    mApplyShippingGroupsErrorURL = pApplyShippingGroupsErrorURL;
  }

  /**
   * Return the ApplyShippingGroupsErrorURL property.
   * @return a <code>String</code> value
   */
  public String getApplyShippingGroupsErrorURL() {
    return mApplyShippingGroupsErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SplitShippingInfosSuccessURL
  //---------------------------------------------------------------------------
  String mSplitShippingInfosSuccessURL;

  /**
   * Set the SplitShippingInfosSuccessURL property.
   * @param pSplitShippingInfosSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setSplitShippingInfosSuccessURL(String pSplitShippingInfosSuccessURL) {
    mSplitShippingInfosSuccessURL = pSplitShippingInfosSuccessURL;
  }

  /**
   * Return the SplitShippingInfosSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSplitShippingInfosSuccessURL() {
    return mSplitShippingInfosSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: SplitShippingInfosErrorURL
  //---------------------------------------------------------------------------
  String mSplitShippingInfosErrorURL;

  /**
   * Set the SplitShippingInfosErrorURL property.
   * @param pSplitShippingInfosErrorURL a <code>String</code> value
   * @beaninfo description: Error URL for Redireciton.
   */
  public void setSplitShippingInfosErrorURL(String pSplitShippingInfosErrorURL) {
    mSplitShippingInfosErrorURL = pSplitShippingInfosErrorURL;
  }

  /**
   * Return the SplitShippingInfosErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSplitShippingInfosErrorURL() {
    return mSplitShippingInfosErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SpecifyDefaultShippingGroupErrorURL
  //---------------------------------------------------------------------------
  String mSpecifyDefaultShippingGroupErrorURL;

  /**
   * Set the SpecifyDefaultShippingGroupErrorURL property.
   * @param pSpecifyDefaultShippingGroupErrorURL a <code>String</code> value
   * @beaninfo description: Error URL for redirection.
   */
  public void setSpecifyDefaultShippingGroupErrorURL(String pSpecifyDefaultShippingGroupErrorURL) {
    mSpecifyDefaultShippingGroupErrorURL = pSpecifyDefaultShippingGroupErrorURL;
  }

  /**
   * Return the SpecifyDefaultShippingGroupErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSpecifyDefaultShippingGroupErrorURL() {
    return mSpecifyDefaultShippingGroupErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SpecifyDefaultShippingGroupSuccessURL
  //---------------------------------------------------------------------------
  String mSpecifyDefaultShippingGroupSuccessURL;

  /**
   * Set the SpecifyDefaultShippingGroupSuccessURL property.
   * @param pSpecifyDefaultShippingGroupSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setSpecifyDefaultShippingGroupSuccessURL(String pSpecifyDefaultShippingGroupSuccessURL) {
    mSpecifyDefaultShippingGroupSuccessURL = pSpecifyDefaultShippingGroupSuccessURL;
  }

  /**
   * Return the SpecifyDefaultShippingGroupSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSpecifyDefaultShippingGroupSuccessURL() {
    return mSpecifyDefaultShippingGroupSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: DefaultShippingGroupName
  //---------------------------------------------------------------------------
  String mDefaultShippingGroupName;

  /**
   * Set the DefaultShippingGroupName property.
   * @param pDefaultShippingGroupName a <code>String</code> value
   * @beaninfo description: The name of the default ShippingGroup.
   */
  public void setDefaultShippingGroupName(String pDefaultShippingGroupName) {
    mDefaultShippingGroupName = pDefaultShippingGroupName;
  }

  /**
   * Return the DefaultShippingGroupName property.
   * @return a <code>String</code> value
   */
  public String getDefaultShippingGroupName() {
    return mDefaultShippingGroupName;
  }

  //---------------------------------------------------------------------------
  // property: ApplyDefaultShippingGroup
  //---------------------------------------------------------------------------
  boolean mApplyDefaultShippingGroup;

  /**
   * Set the ApplyDefaultShippingGroup property.
   * @param pApplyDefaultShippingGroup a <code>boolean</code> value
   * @beaninfo description: Should the default ShippingGroup apply?
   */
  public void setApplyDefaultShippingGroup(boolean pApplyDefaultShippingGroup) {
    mApplyDefaultShippingGroup = pApplyDefaultShippingGroup;
  }

  /**
   * Return the ApplyDefaultShippingGroup property.
   * @return a <code>boolean</code> value
   */
  public boolean isApplyDefaultShippingGroup() {
    return mApplyDefaultShippingGroup;
  }

  //---------------------------------------------------------------------------
  // property: Locale
  //---------------------------------------------------------------------------
  Locale mLocale;

  /**
   * Set the Locale property.
   * @param pLocale a <code>Locale</code> value
   * @beaninfo description: The Locale.
   */
  public void setLocale(Locale pLocale) {
    mLocale = pLocale;
  }

  /**
   * Return the Locale property.
   * @return a <code>Locale</code> value
   */
  public Locale getLocale() {
    return mLocale;
  }

  //---------------------------------------------------------------------------
  // property: ValidateShippingGroupsChainId
  //---------------------------------------------------------------------------
  String mValidateShippingGroupsChainId;

  /**
   * Set the ValidateShippingGroupsChainId property.
   * @param pValidateShippingGroupsChainId a <code>String</code> value
   * @beaninfo description: ChainId to execute for validating ShippingGroups.
   */
  public void setValidateShippingGroupsChainId(String pValidateShippingGroupsChainId) {
    mValidateShippingGroupsChainId = pValidateShippingGroupsChainId;
  }

  /**
   * Return the ValidateShippingGroupsChainId property.
   * @return a <code>String</code> value
   */
  public String getValidateShippingGroupsChainId() {
    return mValidateShippingGroupsChainId;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>ShippingGroupFormHandler</code> instance.
   *
   */
  public ShippingGroupFormHandler() {}

  //--------------------------------------------------
  // ApplyShippingGroups Code
  //--------------------------------------------------

  /**
   * <code>handleApplyShippingGroups</code> is used when the user has supplied the
   * Shipping information for this order, and is ready to proceed with the next checkout phase.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   *
   */
  public boolean handleApplyShippingGroups(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    Transaction tr = null;
    String myHandleMethod = "ShippingGroupFormHandler.handleApplyShippingGroups";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  if (!checkFormRedirect(null, getApplyShippingGroupsErrorURL(), pRequest, pResponse))
    return false;

  preApplyShippingGroups(pRequest, pResponse);

  if (!checkFormRedirect(null, getApplyShippingGroupsErrorURL(), pRequest, pResponse))
    return false;

  applyShippingGroups(pRequest, pResponse);

  if (!checkFormRedirect(null, getApplyShippingGroupsErrorURL(), pRequest, pResponse))
    return false;

  postApplyShippingGroups(pRequest, pResponse);
  try {
    if(isValidateShippingGroups())
        runProcessValidateShippingGroups(getShoppingCart().getCurrent(),getUserPricingModels(),
             getLocale(), getProfile(),null);

    if (!checkFormRedirect(null, getApplyShippingGroupsErrorURL(), pRequest, pResponse))
      return false;

    getOrderManager().updateOrder(getShoppingCart().getCurrent());
  } catch (Exception exc) {
    if (isLoggingError()) {
      logError(exc);
    }
  }
  return checkFormRedirect (getApplyShippingGroupsSuccessURL(),
          getApplyShippingGroupsErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }

  }

  /**
   * <code>preApplyShippingGroups</code> is for work that must happen before
   * the ShippingGroups are applied.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preApplyShippingGroups(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postApplyShippingGroups</code> is for work that must happen after
   * the ShippingGroups are applied.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postApplyShippingGroups(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>applyShippingGroups</code> removes all non-gift ShippingGroups from
   * the Order and then iterates over the supplied CommerceItemShippingInfos
   * for each of the CommerceItems. Each CommerceItemShippingInfo is used
   * to update the Order.
   * <p>
   * If property <code>consolidateShippingInfosBeforeApply</code> is true, the commerce
   * item shipping info objects are first consolidated by calling the <code>consolidateShippingInfos</code>
   * method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void applyShippingGroups(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {


    try 
    {
      getCommerceItemShippingInfoTools().applyCommerceItemShippingInfos(getCommerceItemShippingInfoContainer(),getShippingGroupMapContainer(),
          getShoppingCart().getCurrent(),isConsolidateShippingInfosBeforeApply(),isApplyDefaultShippingGroup());
    } catch (CommerceException exc) 
    {
      if (isLoggingError()) {
        logError(exc);
      }
      processException(exc, MSG_ERROR_UPDATE_SHIPPINGGROUP, pRequest, pResponse);
    }
  }

  /**
   * <code>applyCommerceItemShippingInfo</code> get the Relatonship type
   * from the CommerceItemShippingInfo then invoke the business logic in CommerceItemManager
   * to update the order.
   *
   * @param pCommerceItemShippingInfo a <code>CommerceItemShippingInfo</code> value
   * @param order an <code>Order</code> value
   * @see CommerceItemShippingInfoTools#applyCommerceItemShippingInfo(CommerceItemShippingInfo, Order, ShippingGroupMapContainer)
   * @deprecated
   */
  protected void applyCommerceItemShippingInfo(CommerceItemShippingInfo pCommerceItemShippingInfo,
                                                     Order order)
  {
    if (isLoggingDebug()) {
      logDebug("ShippingGroupFormHandler applyCommerceItemShippingInfo " + pCommerceItemShippingInfo);
    }

    try 
    {

      getCommerceItemShippingInfoTools().applyCommerceItemShippingInfo(pCommerceItemShippingInfo,order,getShippingGroupMapContainer());
    } 
    catch (CommerceException exc) 
    {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }

  /**
   * <code>applyDefaultShippingGroup</code> checks to see if there is a
   * defaultShippingGroup. If so, this ShippingGroup is added to the
   * remaining quantity of each CommerceItem whose quantity is not already
   * fully assigned to ShippingGroups.
   *
   * @param pOrder an <code>Order</code> value
   * @see CommerceItemShippingInfoTools#applyDefaultShippingGroup(Order, ShippingGroupMapContainer)
   * @deprecated
   */
  protected void applyDefaultShippingGroup (Order pOrder)
  {
    try {
      getCommerceItemShippingInfoTools().applyDefaultShippingGroup(pOrder,getShippingGroupMapContainer());
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }

  /**
   * <code>isShippingGroupInOrder</code> is used to determine if the ShippingGroup
   * is already in the Order.
   *
   * @param pOrder an <code>Order</code> value
   * @param pShippingGroupId a <code>String</code> value
   * @return a <code>boolean</code> value
   * @deprecated This method is moved to ShippingGroupManager.
   */
  protected boolean isShippingGroupInOrder (Order pOrder, String pShippingGroupId) {
    return getShippingGroupManager().isShippingGroupInOrder(pOrder,pShippingGroupId);
  }

  /**
   * <code>getShippingGroup</code> gets the ShippingGroup with the given name
   * from the ShippingGroupMapContainer.
   *
   * @param pShippingGroupId a <code>String</code> value
   * @return a <code>ShippingGroup</code> value
   * @deprecated
   * @see CommerceItemShippingInfoTools#getShippingGroup(ShippingGroupMapContainer, String)
   */
  protected ShippingGroup getShippingGroup(String pShippingGroupId) {
    return getCommerceItemShippingInfoTools().getShippingGroup(getShippingGroupMapContainer(),pShippingGroupId);
   }

  //-------------------------------------
  // splitShippingInfos code
  //-------------------------------------

  /**
   * <code>handleSplitShippingInfos</code> is used when the user is ready to
   * split a particular CommerceItemShippingInfo by quantity.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSplitShippingInfos(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "ShippingGroupFormHandler.handleSplitShippingInfos";
    Transaction tr = null;

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  if (!checkFormRedirect(null, getSplitShippingInfosErrorURL(), pRequest, pResponse))
    return false;

  preSplitShippingInfos(pRequest, pResponse);

  if (!checkFormRedirect(null, getSplitShippingInfosErrorURL(), pRequest, pResponse))
    return false;

  splitShippingInfos(pRequest, pResponse);
  try {
    getCommerceItemShippingInfoTools().consolidateShippingInfos(getCommerceItemShippingInfoContainer());
  }
  catch (CommerceException e) {
    if(isLoggingError())
      logError(e);
  }

  if (!checkFormRedirect(null, getSplitShippingInfosErrorURL(), pRequest, pResponse))
    return false;

  postSplitShippingInfos(pRequest, pResponse);

  return checkFormRedirect (getSplitShippingInfosSuccessURL(),
          getSplitShippingInfosErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * <code>preSplitShippingInfos</code> is for work that must happen before
   * the ShippingGroups are split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSplitShippingInfos(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postSplitShippingInfos</code> is for work that must happen after
   * the ShippingGroups are split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSplitShippingInfos(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>splitShippingInfos</code> is used to split the quantities for the
   * CommerceItems into different CommerceItemShippingInfos
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void splitShippingInfos(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    CommerceItemShippingInfoContainer container = getCommerceItemShippingInfoContainer();
    List commerceItemShippingInfos = container.getAllCommerceItemShippingInfos();
    Iterator iter;
    CommerceItemShippingInfo cisi;

    // split commerceItemShippingInfos
    iter = commerceItemShippingInfos.listIterator();
    while (iter.hasNext()) {
      cisi = (CommerceItemShippingInfo) iter.next();
      long splitQuantity = cisi.getSplitQuantity();
      long quantity = cisi.getQuantity();
      if (splitQuantity > 0) {
        if (splitQuantity > quantity) {
          String msg = formatUserMessage(MSG_INVALID_SHIPPING_SPLIT_NUMBER,pRequest,pResponse);
          String propertyPath = generatePropertyPath("splitShippingInfos()");
          addFormException(new DropletFormException(msg, propertyPath, MSG_INVALID_SHIPPING_SPLIT_NUMBER));
        } else {
          splitCommerceItemShippingInfoByQuantity (cisi, splitQuantity);
        }
      }
    }
  }

  /**
   * <code>consolidateShippingInfos</code> consolidates CommerceItemShippingInfos
   * by ensuring there is no redundant data.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @deprecated
   * @see CommerceItemShippingInfoTools#consolidateShippingInfos(CommerceItemShippingInfoContainer)
   */
  protected void consolidateShippingInfos(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    
    try
    {
      getCommerceItemShippingInfoTools().consolidateShippingInfos(getCommerceItemShippingInfoContainer());
    } 
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }
  }

  /**
   * <code>mergeCommerceItemShippingInfos</code> merges 2 CommerceItemShippingInfos
   * into one by combining their quantities.
   *
   * @param cisi1 a <code>CommerceItemShippingInfo</code> value
   * @param cisi2 a <code>CommerceItemShippingInfo</code> value
   * @return a <code>CommerceItemShippingInfo</code> value
   * @deprecated
   * 
   */
  protected CommerceItemShippingInfo mergeCommerceItemShippingInfos(CommerceItemShippingInfo cisi1,
                                                                                CommerceItemShippingInfo cisi2) {
    try
    {
      return getCommerceItemShippingInfoTools().mergeCommerceItemShippingInfos(cisi1,cisi2);
    } 
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }
    return cisi1;
  }

  /**
   * <code>splitCommerceItemShippingInfoByQuantity</code> splits a CommerceItemShippingInfo
   * by quantity. 
   * @see CommerceItemShippingInfoTools#splitCommerceItemShippingInfoByQuantity(CommerceItemShippingInfoContainer, CommerceItemShippingInfo, long)
   *
   * @param pCommerceItemShippingInfo a <code>CommerceItemShippingInfo</code> value
   * @param pSplitQuantity a <code>long</code> value
   * @deprecated
   * @see CommerceItemShippingInfoTools#splitCommerceItemShippingInfoByQuantity(CommerceItemShippingInfoContainer, CommerceItemShippingInfo, long)
   */
  protected void splitCommerceItemShippingInfoByQuantity (CommerceItemShippingInfo pCommerceItemShippingInfo,
                                                                long pSplitQuantity) {
    try
    {
      getCommerceItemShippingInfoTools().splitCommerceItemShippingInfoByQuantity(getCommerceItemShippingInfoContainer(),pCommerceItemShippingInfo,pSplitQuantity);
    } 
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }
  }

  //-------------------------------------
  // validation of ShippingGroups
  //-----------------------------------------

  /**
   * <code>runProcessValidateShippingGroups</code> runs a configurable Pipeline chain
   * to validate ShippingGroups or prepare for the next checkout phase.
   *
   * @param pOrder an <code>Order</code> value
   * @param pPricingModels a <code>PricingModelHolder</code> value
   * @param pLocale a <code>Locale</code> value
   * @param pProfile a <code>RepositoryItem</code> value
   * @param pExtraParameters a <code>Map</code> value
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessValidateShippingGroups(Order pOrder, PricingModelHolder pPricingModels,
                                                 Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    PipelineResult result = runProcess(getValidateShippingGroupsChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, pExtraParameters);
    processPipelineErrors(result);
  }

  //-------------------------------------
  // specifyDefaultShippingGroup code
  //-------------------------------------

  /**
   * <code>handleSpecifyDefaultShippingGroup</code> is used to let the user specify
   * a default ShippingGroup to use.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSpecifyDefaultShippingGroup(DynamoHttpServletRequest pRequest,
                                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "ShippingGroupFormHandler.handleSpecifyDefaultShippingGroup";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  if (!checkFormRedirect(null, getSpecifyDefaultShippingGroupErrorURL(), pRequest, pResponse))
    return false;

  preSpecifyDefaultShippingGroup(pRequest, pResponse);

  if (!checkFormRedirect(null, getSpecifyDefaultShippingGroupErrorURL(), pRequest, pResponse))
    return false;

  specifyDefaultShippingGroup(pRequest, pResponse);

  if (!checkFormRedirect(null, getSpecifyDefaultShippingGroupErrorURL(), pRequest, pResponse))
    return false;

  postSpecifyDefaultShippingGroup(pRequest, pResponse);

  return checkFormRedirect (getSpecifyDefaultShippingGroupSuccessURL(),
          getSpecifyDefaultShippingGroupErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * <code>preSpecifyDefaultShippingGroup</code> is for work that must happen before
   * the default ShippingGroup is set.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSpecifyDefaultShippingGroup(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postSpecifyDefaultShippingGroup</code> is for work that must happen after
   * the default ShippingGroup is set.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSpecifyDefaultShippingGroup(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>specifyDefaultShippingGroup</code> sets the defaultShippingGroupName in
   * the ShippingGroupMapContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @see CommerceItemShippingInfoTools#specifyDefaultShippingGroup(ShippingGroupMapContainer, String)
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void specifyDefaultShippingGroup(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (StringUtils.isBlank(getDefaultShippingGroupName())) {
      String msg = formatUserMessage(MSG_NO_DEFAULT_SHIPPING_GROUP, pRequest, pResponse);
      String propertyPath = generatePropertyPath("defaultShippingGroupName");
      addFormException(new DropletFormException(msg, propertyPath, MSG_NO_DEFAULT_SHIPPING_GROUP));
      return;
    }
    getCommerceItemShippingInfoTools().specifyDefaultShippingGroup(getShippingGroupMapContainer(),getDefaultShippingGroupName());
  }

  /**
   * Removes a shipping group from the shipping group map container
   * @param pShippingGroup
   * @param pKey
   */
  protected void removeShippingGroupFromMap(String pKey)
  {
    //remove all shipping groups from container
    ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();
    sgmc.removeShippingGroup(pKey);

  }
  /**
   * Adds a shipping groups to the shipping group map container
   * @param pShippingGroup
   * @param pKey
   */
  protected void addShippingGroupToMap(ShippingGroup pShippingGroup, String pKey)
  {
    //remove all shipping groups from container
    ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();
    sgmc.addShippingGroup(pKey,pShippingGroup);

  }

  List mGiftShippingGroups=null;
  /**
   * Returns a list of all shipping groups that contain gifts.
   * @return
   */

  public List getGiftShippingGroups()
  {
    if(mGiftShippingGroups != null)
      return mGiftShippingGroups;
    try
    {
      mGiftShippingGroups = getGiftlistManager().getGiftShippingGroups(getOrder());
      return mGiftShippingGroups;
    }
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }

    return null;
  }

  protected List mElectronicShippingGroups = null;
  /**
   * Returns a list of all electronic groups that contain commerce items.
   * @return
   */
  public List getElectronicShippingGroups() {
    if (mElectronicShippingGroups == null) {
      mElectronicShippingGroups = getShippingGroupManager().getElectronicShippingGroups(getOrder());
    }
    return mElectronicShippingGroups;
  }

  /**
   * Returns the number of non-gift hardgood shipping groups
   * with commerce item relationships
   * <p>
   * @return the number of non-empty non-gift hardgood shipping groups
   */
  public int getNonGiftHardgoodShippingGroupCount()  {
    return getShippingGroupManager().getNonGiftHardgoodShippingGroupCount(getOrder());
  }

  /**
   * Returns true if the order has at least one hargood shipping group
   * with commerce item relationships. Both non-gift and gift shipping
   * groups are considered.
   *
   * @return boolean true if the order has at least one non-empty hardgood shipping group.
   */
  public boolean isAnyHardgoodShippingGroups()  {
    return getShippingGroupManager().isAnyHardgoodShippingGroups(getOrder());
  }

  /**
   * Returns true if the order has at least one electronic shipping group
   * with commerce item relationships.
   *
   * @return boolean true if the order has at least one non-empty hardgood shipping group.
   */
  public boolean isAnyElectronicShippingGroups()  {
    return getShippingGroupManager().isAnyElectronicShippingGroups(getOrder());
  }

  /**
   * Returns true if the order has more than one non-gift hardgood shipping group
   * with commerce item relationships
   * <p>
   * @return boolean true if the order has more than one non-gift hardgood shipping group.
   */
  public boolean isMultipleNonGiftHardgoodShippingGroups()  {
    return getShippingGroupManager().isMultipleNonGiftHardgoodShippingGroups(getOrder());
  }

  /**
   * Returns true if the order has more than one hardgood shipping group
   * with commerce item relationships
   * <p>
   * @return boolean true if the order has more than one hardgood shipping group.
   */
  public boolean isMultipleHardgoodShippingGroupsWithRelationships() {
    return getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(getOrder());
  }

  /**
   * Determines if there are any non-gift hardgood shipping groups with
   * relationships
   *
   * <p>
   * @return true if any non-gift hardgood shipping groups with relationships are found
   */
  public boolean isAnyNonGiftHardgoodShippingGroups()  {
    return getShippingGroupManager().isAnyNonGiftHardgoodShippingGroups(getOrder());
  }

  /**
   * Returns the non-gift hardgood shipping groups
   * with commerce item relationships
   * <p>
   * @returns a list of non-gift hardgood shipping groups
   * with commerce item relationships
   */
  public List getNonGiftHardgoodShippingGroups() {
    return getShippingGroupManager().getNonGiftHardgoodShippingGroups(getOrder());
  }

  /**
   * Returns the first non-gift hardgood shipping group with relationships from the order
   * @return the first non-gift hardgood shipping group or null if there isn't one.
   */
  public HardgoodShippingGroup getFirstNonGiftHardgoodShippingGroupWithRels() {
    return getShippingGroupManager().getFirstNonGiftHardgoodShippingGroupWithRels(getOrder());
  }

  protected List mAllHardgoodCommerceItemShippingInfos=null;
  /**
   * Get the List of all the CommerceItemShippingInfos for hardgoods
   * from the CommerceItemShippingInfoMap. If a CommerceItemShippingInfo
   * has no shipping group, assume the item represents hardgoods.
   *
   * @return a <code>List</code> value
   */
  public List getAllHardgoodCommerceItemShippingInfos()
  {
    if(mAllHardgoodCommerceItemShippingInfos != null)
      return mAllHardgoodCommerceItemShippingInfos;

    ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();
    mAllHardgoodCommerceItemShippingInfos = getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos();
    Iterator iter = mAllHardgoodCommerceItemShippingInfos.iterator();
    while (iter.hasNext()) {
      CommerceItemShippingInfo info = (CommerceItemShippingInfo)iter.next();
      String nickname = info.getShippingGroupName();
      if (nickname != null && !(sgmc.getShippingGroup(nickname) instanceof HardgoodShippingGroup)) {
        iter.remove();
      }
    }
    return mAllHardgoodCommerceItemShippingInfos;
  }
}
