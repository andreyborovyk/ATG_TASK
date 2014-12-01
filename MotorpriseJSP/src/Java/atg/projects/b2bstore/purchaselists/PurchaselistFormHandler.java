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

package atg.projects.b2bstore.purchaselists;

import atg.commerce.*;
import atg.commerce.gifts.*;
import atg.servlet.*;
import atg.droplet.DropletException;
import atg.repository.*;
import atg.core.util.StringUtils;
import atg.core.util.ResourceUtils;
import atg.servlet.RequestLocale;

import java.io.*;
import java.util.*;
import javax.servlet.*;

/**
 * This class provides convenient form handling methods for operating on
 * the current customer's purchase lists.  It can be used to create new purchase lists,
 * edit purchase lists, and add items to the giftlist during the shopping process.
 *
 * @author <a href="mailto:jlang@atg.com">Jeremy Lang</a>, ATG Dynamo Innovations
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see GenericFormHandler
 * @see GiftlistFormHandler
 */

public class PurchaselistFormHandler
  extends PurchaselistFormHandlerSuper
{
  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistFormHandler.java#2 $$Change: 651448 $";

  public static final String LIST_ID = "listId";
  public static final String EVENTNAME_PROPERTY = "eventName";

  protected static String sBundleName = 
    "atg.projects.b2bstore.purchaselists.UserResources";

  public static final String ERR_NULL_LIST_NAME = "errorNullListName";
  public static final String ERR_DUPLICATE_LIST_NAME = "errorDuplicateListName";
  public static final String ERR_UNABLE_TO_CREATE_LIST = "errorUnableToCreateList";
  
  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property:  purchaseListId
  /**
   * Sets property purchaseListId
   * @param pPurchaseListId The property to store the purchase list ID
   * @beaninfo description:  The property to store the purchase list ID
   **/
  public void setPurchaseListId (String pPurchaseListId) {
    setGiftlistId(pPurchaseListId);
    super.setPurchaseListId(pPurchaseListId);
  }

  //-------------------------------------
  // property:  createPurchaselistSuccessURL
  /**
   * Sets property createPurchaselistSuccessURL
   * @param pCreatePurchaselistSuccessURL The property to store the createPurchaselistSuccessURL
   * @beaninfo description:  The property to store the createPurchaselistSuccessURL
   **/
  public void setCreatePurchaselistSuccessURL (String pCreatePurchaselistSuccessURL) {
    setCreateGiftlistSuccessURL(pCreatePurchaselistSuccessURL);
    super.setCreatePurchaselistSuccessURL(pCreatePurchaselistSuccessURL);
  }

  //-------------------------------------
  // property:  createPurchaselistErrorURL
  /**
   * Sets property createPurchaselistErrorURL
   * @param pCreatePurchaselistErrorURL The property to store the createPurchaselistErrorURL
   * @beaninfo description:  The property to store the createPurchaselistErrorURL
   **/
  public void setCreatePurchaselistErrorURL (String pCreatePurchaselistErrorURL) {
    setCreateGiftlistErrorURL(pCreatePurchaselistErrorURL);
    super.setCreatePurchaselistErrorURL(pCreatePurchaselistErrorURL);
  }

  //-------------------------------------
  // property:  savePurchaselistSuccessURL
  /**
   * Sets property savePurchaselistSuccessURL
   * @param pSavePurchaselistSuccessURL The property to store the savePurchaselistSuccessURL
   * @beaninfo description:  The property to store the savePurchaselistSuccessURL
   **/
  public void setSavePurchaselistSuccessURL (String pSavePurchaselistSuccessURL) {
    setSaveGiftlistSuccessURL(pSavePurchaselistSuccessURL);
    super.setSavePurchaselistSuccessURL(pSavePurchaselistSuccessURL);
  }

  //-------------------------------------
  // property:  savePurchaselistErrorURL
  /**
   * Sets property savePurchaselistErrorURL
   * @param pSavePurchaselistErrorURL The property to store the savePurchaselistErrorURL
   * @beaninfo description:  The property to store the savePurchaselistErrorURL
   **/
  public void setSavePurchaselistErrorURL (String pSavePurchaselistErrorURL) {
    setSaveGiftlistErrorURL(pSavePurchaselistErrorURL);
    super.setSavePurchaselistErrorURL(pSavePurchaselistErrorURL);
  }

  //-------------------------------------
  // property:  updatePurchaselistSuccessURL
  /**
   * Sets property updatePurchaselistSuccessURL
   * @param pUpdatePurchaselistSuccessURL The property to store the updatePurchaselistSuccessURL
   * @beaninfo description:  The property to store the updatePurchaselistSuccessURL
   **/
  public void setUpdatePurchaselistSuccessURL (String pUpdatePurchaselistSuccessURL) {
    setUpdateGiftlistSuccessURL(pUpdatePurchaselistSuccessURL);
    super.setUpdatePurchaselistSuccessURL(pUpdatePurchaselistSuccessURL);
  }

  //-------------------------------------
  // property:  updatePurchaselistErrorURL
  /**
   * Sets property setUpdatePurchaselistErrorURL
   * @param pUpdatePurchaselistErrorURL The property to store the setUpdatePurchaselistErrorURL
   * @beaninfo description:  The property to store the setUpdatePurchaselistErrorURL
   **/
  public void setUpdatePurchaselistErrorURL (String pUpdatePurchaselistErrorURL ) {
    setUpdateGiftlistErrorURL(pUpdatePurchaselistErrorURL);
    super.setUpdateGiftlistErrorURL(pUpdatePurchaselistErrorURL);
  }

  //-------------------------------------
  // property:  updatePurchaselistItemsSuccessURL
  /**
   * Sets property updatePurchaselistItemsSuccessURL
   * @param pUpdatePurchaselistItemsSuccessURL  The property to store the updatePurchaselistItemsSuccessURL
   * @beaninfo description:  The property to store the updatePurchaselistItemsSuccessURL
   **/
  public void setUpdatePurchaselistItemsSuccessURL (String pUpdatePurchaselistItemsSuccessURL) {
    setUpdateGiftlistItemsSuccessURL(pUpdatePurchaselistItemsSuccessURL);
    super.setUpdatePurchaselistItemsSuccessURL(pUpdatePurchaselistItemsSuccessURL);
  }

  //-------------------------------------
  // property:  updatePurchaselistItemsErrorURL
  /**
   * Sets property updatePurchaselistItemsErrorURL
   * @param pUpdatePurchaselistItemsErrorURL  The property to store the updatePurchaselistItemsErrorURL
   * @beaninfo description:  The property to store the updatePurchaselistItemsErrorURL
   **/
  public void setUpdatePurchaselistItemsErrorURL (String pUpdatePurchaselistItemsErrorURL) {
    setUpdateGiftlistItemsErrorURL(pUpdatePurchaselistItemsErrorURL);
    super.setUpdatePurchaselistItemsErrorURL(pUpdatePurchaselistItemsErrorURL);
  }

  //-------------------------------------
  // property:  deletePurchaselistSuccessURL
  /**
   * Sets property deletePurchaselistSuccessURL
   * @param pDeletePurchaselistSuccessURL The property to store the deletePurchaselistSuccessURL
   * @beaninfo description:  The property to store the deletePurchaselistSuccessURL
   **/
  public void setDeletePurchaselistSuccessURL (String pDeletePurchaselistSuccessURL) {
    setDeleteGiftlistSuccessURL(pDeletePurchaselistSuccessURL);
    super.setDeletePurchaselistSuccessURL(pDeletePurchaselistSuccessURL);
  }

  //-------------------------------------
  // property:  deletePurchaselistErrorURL
  /**
   * Sets property deletePurchaselistErrorURL
   * @param pDeletePurchaselistErrorURL The property to store the deletePurchaselistErrorURL
   * @beaninfo description:  The property to store the deletePurchaselistErrorURL
   **/
  public void setDeletePurchaselistErrorURL (String pDeletePurchaselistErrorURL) {
    setDeleteGiftlistErrorURL(pDeletePurchaselistErrorURL);
    super.setAddItemToPurchaselistErrorURL(pDeletePurchaselistErrorURL);
  }

  //-------------------------------------
  // property: addItemToPurchaselistSuccessURL
  /**
   * Sets property addItemToPurchaselistSuccessURL
   * @param pAddItemToPurchaselistSuccessURL  The property to store the addItemToPurchaselistSuccessURL
   * @beaninfo description:  The property to store the addItemToPurchaselistSuccessURL
   **/
  public void setAddItemToPurchaselistSuccessURL (String pAddItemToPurchaselistSuccessURL) {
    setAddItemToGiftlistSuccessURL(pAddItemToPurchaselistSuccessURL);
    super.setAddItemToPurchaselistSuccessURL(pAddItemToPurchaselistSuccessURL);
  }

  //-------------------------------------
  // property:  addItemToPurchaselistErrorURL
  /**
   * Sets property addItemToPurchaselistErrorURL
   * @param pAddItemToPurchaselistErrorURL  The property to store the addItemToPurchaselistErrorURL
   * @beaninfo description:  The property to store the addItemToPurchaselistErrorURL
   **/
  public void setAddItemToPurchaselistErrorURL (String pAddItemToPurchaselistErrorURL) {
    setAddItemToGiftlistErrorURL(pAddItemToPurchaselistErrorURL);
    super.setAddItemToPurchaselistErrorURL(pAddItemToPurchaselistErrorURL);
  }

  //-------------------------------------
  // property:  listName

  /**
   * Sets property listName
   * @param pListName The property to store the listName
   * @beaninfo description:  The property to store the listName
   **/
  public void setListName (String pListName) {
    
    setEventName(pListName);
    
    if (isLoggingDebug()) logDebug("setListName(): event name is " + getEventName());

    super.setListName(pListName);
  }

  //---------------------------------------------------------------------
  // property: userLocale
  String mUserLocale;

  /**
   * Return the userLocale property.
   * @return users locale.
   */
  public String getUserLocale() {
    return mUserLocale;
  }

  /**
   * Set the userLocale property.
   * @param pUserLocale
   */
  public void setUserLocale(String pUserLocale) {
    mUserLocale = pUserLocale;
  }


  
  //-------------------------------------
  // Methods
  //-------------------------------------

  //------------------------------------------
  // method:  handleAddItemToPurchaselist
  //------------------------------------------
  /**
   * handleAddItemToPurchaseList is called when the user hits the submit
   * button on a product page to add an item to the purchase list.  It relies on
   * the atg.commerce.gifts.GiftlistFormHandler.handleAddItemToGiftlist()
   * method to execute this action.
   *
   * For the B2B demo, this is a skeleton method intended to preserve
   * a consistent purchase list (rather than gift list) nomenclature.
   *
   * @see GiftListFormHandler#handleAddItemToGiftlist
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleAddItemToPurchaselist( DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {

    pRequest.addQueryParameter(LIST_ID, getPurchaseListId());

    return super.handleAddItemToGiftlist(pRequest, pResponse);
  }

  //------------------------------------------
  // method:  handleCreatePurchaselist
  //------------------------------------------
  /**
   * Called when the customer selects create to create a new purchase list.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleCreatePurchaselist (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {

    return super.handleCreateGiftlist(pRequest, pResponse);
  }

  //------------------------------------------
  // method:  handleSavePurchaselist
  //------------------------------------------
  /**
   * Called when the customer selects save purchaselist.  Since for the b2b demo,
   * purchase lists are implemented using the DCS5.0 giftlists feature, this will
   * call the manager class to create a giftlist in the repository and add it to the
   * current profile.
   * @see GiftlistFormHandler#handleSaveGiftList
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleSavePurchaselist ( DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {

    populateGiftListFields();
    if (isLoggingDebug())
      logDebug("handleSavePurchaseList: event name is " + getEventName());
    if ( checkForListName(pRequest, pResponse)) {
      return super.handleSaveGiftlist(pRequest, pResponse);
    } // end of if ()
    return true;
  }

  //------------------------------------------
  // method:  handleDeleteGiftlist
  //------------------------------------------
  /**
   * Called when the customer pushes delete giftlist.  This will call the manager class to delete the giftlist from the profile and repository.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleDeletePurchaselist (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
  {
    return super.handleDeleteGiftlist(pRequest, pResponse);
  }

  //------------------------------------------
  // method:  handleUpdatePurchaselist
  //------------------------------------------
  /**
   * Called when the customer selects update purchaselist.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleUpdatePurchaselist   ( DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {

    populateGiftListFields();

    return super.handleUpdateGiftlist(pRequest, pResponse);
  }

  //------------------------------------------
  // method:  populateGiftListFields
  //------------------------------------------
  /**
   * Since the purchase list feature relies on the atg.commerce.gifts package
   * in its implementation, this method is used to populate the individual fields of
   * a giftlist that are needed for manipulating giftlists, but which do not have any
   * direct relevance to the purchase list paradigm.
   */
  public void populateGiftListFields() {
    setIsPublished(Boolean.TRUE);
    //date fields
    Calendar c = Calendar.getInstance();
    setYear((new Integer(c.get(Calendar.YEAR))).toString());
    setMonth(new Integer(c.get(Calendar.MONTH)));
    setDate((new Integer(c.get(Calendar.DAY_OF_MONTH))).toString());
    setEventType(null);
    setDescription(null);
    setComments(null);
    setShippingAddressId(null);
    setInstructions(null);
  }

  //------------------------------------------
  // method:  checkForListName
  //------------------------------------------
  /**
   * This method checks for validity of purchase list name. Its first checks for null
   * and later checks against the current purchase lists of user for duplicate name.
   * if duplicate exists it populates error message on screen.
   * @return true if listname is not null and no purchase list exists with the listname.
   * @return false if listname is null or purchase list exists with this listname.
   **/

  protected boolean checkForListName(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) 
   {

     // Check for blank list name
    if (StringUtils.isBlank(getListName())) {
      addFormException(ERR_NULL_LIST_NAME, pRequest, pResponse);
      return false;
    } // end of if ()

    try {
      
      List giftLists = (List)(getGiftlistManager().getGiftlistTools().getGiftlists(getProfile()));
      Iterator iterator = giftLists.iterator();
      RepositoryItem item;
      String listName;
      // Get all the listnames correponding to user profile
      // iterate thorugh each of them for duplicate list name.
      while ( iterator.hasNext()) {
        item = (RepositoryItem)iterator.next();
        if ( item != null) {
          listName = (String)item.getPropertyValue(EVENTNAME_PROPERTY);

          // if duplicate name exits add form exception..
          if ( listName != null) {
            if ( listName.equals(getListName())) {
              addFormException(ERR_DUPLICATE_LIST_NAME, pRequest, pResponse );
              return false;
            } // end of if ()
          } // end of if ()
        } // end of if ()
      } // end of while ()

    }
    catch(CommerceException ce)
    {

      addFormException(ERR_UNABLE_TO_CREATE_LIST, pRequest, pResponse);
      if (isLoggingError()) {
        logError(ce);        
      } // end of if ()
      return false;
    }
    return true;
  }

  // Utility Methods 

  /**
   * Returns either the Locale from the Request object (if it isn't NULL),
   * or the Locale from the JVM.
   * @param pRequest the servlet's request
   * @return Either the Locale from the Request object, or the Locale 
   * from the JVM.
   */
  protected Locale getLocale (DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
  {
    if (! StringUtils.isBlank(getUserLocale())) {
      return RequestLocale.getCachedLocale(getUserLocale());
    }
    else if (pRequest.getRequestLocale() != null) {
        return pRequest.getRequestLocale().getLocale();
    }
    else {
      return Locale.getDefault();
    }
  }


  /**
   * Adds a form exception to the formhandler.  The exception
   * that is generated is created using the <code>pErrorKey</code>
   * to obtain a resource using the {@link #getStringResource(String, Locale)
   * <code>getStringResource</code>} method.
   *
   * @param pErrorKey the resource key
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void addFormException(String pErrorKey, 
                                  DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
  {
    String msg = getStringResource(pErrorKey, getLocale(pRequest, pResponse));
    
    addFormException(new DropletException(msg, pErrorKey));
  }

  


  /**
   * This method acts as a utility method to obtain a given
   * resource for a particular key.  This is used to lookup error
   * messages for the users locale.
   *
   * @param pResourceName the resource key used to obtain the resource
   * @param pLocale the users locale for which the resource bundle will be 
   *                obtained.
   * @return The String resource
   */
  public String getStringResource (String pResourceName,
                                   Locale pLocale)
  {
    try {
      // Grab our bundle for the given locale.
      ResourceBundle bundle = ResourceUtils.getBundle(sBundleName,
                                                      pLocale);
      return bundle.getString(pResourceName);

    } catch (MissingResourceException exc) {
      // Print the error to to stderr for good measure since these
      // exceptions might be getting called from a static initializer
      // and exceptions thrown during that could have some funky
      // results.
      if (isLoggingError())
        logError(exc);
    }
    return pResourceName;
  }

  
  
}

