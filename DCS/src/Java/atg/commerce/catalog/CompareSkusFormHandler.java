/*<ATGCOPYRIGH
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

package atg.commerce.catalog;

import atg.repository.RepositoryItem;
import atg.repository.Repository;
import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.core.util.ResourceUtils;
import atg.servlet.RequestLocale;
import atg.commerce.catalog.UserResourceLookup; //error constants

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 *
 * This form handler is used to compare two SKUs with each another.
 * As a user is looking at a Product, they can decided to add all of
 * its associated SKUs to a list of SKUs they may want to compare side by side.
 * When they are ready to do a comparison, they select two of the SKUs from
 * the list they have built up, and a table is displayed comparing
 * the two SKUs.
 *
 * All handle methods in this form handler mirror a similar pattern.  Each handleXXX process has an
 * associated preXXX and postXXX method.  So, for example, the <code>handleCompareSkus</code> has an
 * associated <code>preCompareSkus</code> and <code>postCompareSkus</code>.  These pre/post
 * methods provide an easy way for a user to extend the functionality of this form handler.
 *
 * @beaninfo
 *   description: A form handler which can be used to compare two SKUs to each other.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @deprecated As of Dynamo 6, use
 * {@link atg.commerce.catalog.comparison.ProductComparisonList ProductComparisonList} and
 * {@link atg.commerce.catalog.comparison.ProductListHander ProductListHander} to create
 * and manage product comparison lists.
 *
 * @see atg.droplet.GenericFormHandler
 * @author Katerina Dobes
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/CompareSkusFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CompareSkusFormHandler
extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/CompareSkusFormHandler.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Empty Constructor
  //-------------------------------------
  /**
   * Empty Constructor.
   */
  public CompareSkusFormHandler() {
  }

  //-------------------------------------
  // property: Repository
  //-------------------------------------

  Repository mRepository;

  /**
   * Sets the Repository the Products/SKUs are in.
   * This property is typically set initially in this FormHandler's properties file.
   * @param pRepository The repository containing the Products and SKUs the user will be comparing.
   **/
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }

  /**
   * Gets the Repository the Products/SKUs are in.
   * @return The repository containing the Products and SKUs the user will be comparing.
   **/
  public Repository getRepository() {
    return mRepository;
  }

  //-------------------------------------
  // property: ProductCompareList
  //-------------------------------------
  List mProductCompareList = new ArrayList (); //not null...empty.

  /**
   * Sets the list of Products the user wants to compare against.
   * This list contains Product RepositoryItems.  NOTE: This list will
   * most likely contain duplicates.   If the Product has x number of SKUs, the Product
   * will be added to the ProductCompareList x times so that the length of the
   * ProductCompareList and the SkuCompareList is always the same.  So, given a
   * specific List Index x, you can fetch the xth element from either the ProductCompareList
   * or the SkuCompareList, and the values in the xth element will correspond with each other.
   * @param pProductCompareList A list of Products the user is comparing (contains duplicates).
   **/
  public void setProductCompareList(List pProductCompareList) {
    mProductCompareList = pProductCompareList;
  }

  /**
   * This method returns the list of Products the user wants to compare against.
   * This list contains Product RepositoryItems. NOTE: This list will
   * most likely contain duplicates.   If the Product has x number of SKUs, the Product
   * will be added to the ProductCompareList x times so that the length of the
   * ProductCompareList and the SkuCompareList is always the same.  So, given a
   * specific List Index, it is possible to retrieve the correct Product AND SKU.
   * @return Returns a list of Products the user is comparing (contains duplicates).
   **/
  public List getProductCompareList() {
    return mProductCompareList;
  }

  //-------------------------------------
  // property: SkuCompareList
  //-------------------------------------
  List mSkuCompareList = new ArrayList (); //not null...empty.

  /**
   * Sets the list of SKUs the user wants to compare against.
   * This list contains SKU RepositoryItems.  This list will not
   * typically contain duplicates.
   * @param pSkuCompareList A list of SKUs the user is comparing.
   **/
  public void setSkuCompareList(List pSkuCompareList) {
    mSkuCompareList = pSkuCompareList;
  }

  /**
   * This method returns the list of SKUs the user wants to compare against.
   * This list contains SKU RepositoryItems.  This list will not
   * typically contain duplicates.
   * @return Returns a list of SKUs the user is comparing.
   **/
  public List getSkuCompareList() {
    return mSkuCompareList;
  }

  //-------------------------------------
  // property: SelectedIndex1
  //-------------------------------------
  String mSelectedIndex1;

  /**
   * Sets the index of the first item the user wants to compare.
   * This value is used as an index into the ProductCompareList or SkuCompareList.
   * @param newVal The new value to set the SelectedIndex1 property to.  Should be between
   *               0 and the length of the SkuCompareList/ProductCompareList.
   **/
  public void setSelectedIndex1(String newVal) {
    mSelectedIndex1 = newVal;
    if (isLoggingDebug())
      logDebug("SelectedIndex1 set to: " + newVal);
  }

  /**
   * This method returns the index of the first item the user wants to compare.
   * This value is used as an index into the ProductCompareList or SkuCompareList.
   * @return Returns the Index into the SkuCompareList or ProductCompareList of the first
   *         SKU the user wants to compare.
   **/
  public String getSelectedIndex1() {
    return mSelectedIndex1;
  }

  //-------------------------------------
  // property: SelectedIndex2
  //-------------------------------------
  String mSelectedIndex2;

  /**
   * Sets the index of the second item the user wants to compare.
   * This value is used as an index into the ProductCompareList or SkuCompareList.
   * @param newVal The new value to set the SelectedIndex2 property to.  Should be between
   *               0 and the length of the SkuCompareList/ProductCompareList.
   **/
  public void setSelectedIndex2(String newVal) {
    mSelectedIndex2 = newVal;
    if (isLoggingDebug())
      logDebug("SelectedIndex2 set to: " + newVal);
  }

  /**
   * Returns the index of the second item the user wants to compare.
   * This value is used as an index into the ProductCompareList or SkuCompareList.
   * @return Returns the Index into the SkuCompareList or ProductCompareList of the 2nd
   *         SKU the user wants to compare.
   **/
  public String getSelectedIndex2() {
    return mSelectedIndex2;
  }

  //-------------------------------------
  // property: ProductToCompare
  //-------------------------------------
  String mProductToCompare = null;

  /**
   * Sets the productId of the Product to add to the Compare Lists.
   * This property contains the productId represented as a string.
   * The Product/SKUs don't get added to the Compare Lists
   * until the handleAddItemToCompareList method gets invoked.
   * @param productId The Id of the Product whose SKUs the user wants added to the Compare Lists.
   **/
  public void setProductToCompare(String productId) {
    mProductToCompare = productId;
    if (isLoggingDebug())
      logDebug("ProductToCompare:" + productId);
  }

  /**
   * Gets the productId of the Product to add to Compare List.
   * @return Returns the Id (as a string) or the Product the user wants to compare.
   **/
  public String getProductToCompare() {
    return mProductToCompare;
  }

  //-------------------------------------
  // property: AddToCompareListSuccessURL
  //-------------------------------------
  String mAddToCompareListSuccessURL;

  /**
   * Sets the property AddToCompareListSuccessURL.  This property
   * is normally set on a jhtml page.  It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the button which adds
   * a specific Product (and its SKUs) to the Compare Lists.
   * @param pAddToCompareListSuccessURL The URL (as a string) of the page to redirect to if
   *                                    the handleAddToCompareList method succeeds without error.
   **/
  public void setAddToCompareListSuccessURL(String pAddToCompareListSuccessURL) {
    mAddToCompareListSuccessURL = pAddToCompareListSuccessURL;
  }

  /**
   * Returns property AddToCompareListSuccessURL.  It indicates
   * which page (ie URL) we should redirect to if NO errors
   * occur when the user pushes the button which adds
   * a specific Product (and its SKUs) to the Compare Lists.
   * No redirect occurs if the current URL is the same as this URL.
   * @return The URL (as a string) of the page to redirect to if the handleAddToCompareList method
   *         succeeds without error.
   **/
  public String getAddToCompareListSuccessURL() {
    return mAddToCompareListSuccessURL;
  }

  //-------------------------------------
  // property: AddToCompareListErrorURL
  //-------------------------------------
  String mAddToCompareListErrorURL;

  /**
   * Sets property AddToCompareListErrorURL.  This property
   * is normally set on a jhtml page.  It indicates
   * which page we should redirect to if ERRORS
   * occur when the user pushes the button which adds
   * a specific Product (and its SKUs) to the Compare Lists.
   * @param pAddToCompareListErrorURL The URL (as a string) of the page to redirect to if
   *                                  the handleAddToCompareList method generates an error.
   **/
  public void setAddToCompareListErrorURL(String pAddToCompareListErrorURL) {
    mAddToCompareListErrorURL = pAddToCompareListErrorURL;
  }

  /**
   * Returns property AddToCompareListErrorURL.   It indicates
   * which page we should redirect to if ERORRS
   * occur when the user pushes the button which adds
   * a specific Product (and its SKUs) to the Compare Lists.
   * No redirect occurs if the current URL is the same as this URL.
   * @return The URL (as a string) of the page to redirect to if the handleAddToCompareList method
   *         generates an error.
   **/
  public String getAddToCompareListErrorURL() {
    return mAddToCompareListErrorURL;
  }

  //-----------------------------------
  // Method: preAddToCompareList
  //-----------------------------------
  /**
   * This method provides an easy way for users to extend the functionality of the
   * handleAddToCompareList method.  The preAddToCompareList code will be executed
   * at the start of the handleAddToCompareList method.
   * This default implementation does nothing.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void preAddToCompareList(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
                                  throws ServletException,
                                         IOException
  {
    if (isLoggingDebug())
      logDebug("in CompareSkusFormHandler.preAddToCompareList");
  }

  //-----------------------------------
  // Method: postAddToCompareList
  //-----------------------------------
  /**
   * This method provides an easy way for users to extend the functionality of the
   * handleAddToCompareList method.  The postAddToCompareList code will be executed
   * at the end of the handleAddToCompareList method.
   * This default implementation does nothing.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void postAddToCompareList(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
                                   throws ServletException,
                                          IOException
  {
    if (isLoggingDebug())
      logDebug("in CompareSkusFormHandler.postAddToCompareList");
  }

  //-----------------------------------
  // Method: handleAddToCompareList
  //-----------------------------------
  /**
   * This method is called when the user adds a Product (and all associated
   * SKUs) to the Compare Lists.  If the Product has x number of SKUs, the Product
   * will be added to the ProductCompareList x times so that the length of the
   * ProductCompareList and the SkuCompareList is always the same.  So, given a
   * specific List Index, it is possible to retrieve the correct Product and SKU.
   * This is necessary if the side-by-side comparison chart needs to exhibit both
   * Product attributes and SKU attributes.  Before adding the Skus for a Product
   * to the compare lists, the code checks if the Product was already added before.
   * If it was, the Skus are not added again, and the method returns a successful status.
   * If the method succeeds, it redirects to the AddToCompareListSuccessURL (if
   * different from the current URL).  If the method doesn't succeed, it redirects to the
   * AddToCompareListErrorURL (if different from the current URL).  If the
   * success or failure URLs are the same as the current URL, no redirect takes place.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if processing was successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleAddToCompareList(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
                                        throws ServletException, IOException
  {
    if (isLoggingDebug())
      logDebug("in CompareSkusFormHandler.handleAddToCompareList");

    //If any form errors found, redirect to Error URL
    if (! checkFormRedirect(null, getAddToCompareListErrorURL(), pRequest, pResponse))
       return false;

    //Call any custom code before the main AddToCompareList processing:
    preAddToCompareList(pRequest, pResponse);

    try {

      RepositoryItem product;
      String productId;

      RepositoryItem SKU;
      String SKUId;

      //Fetch the Product ID of the Produt they want to add to the Compare Lists:
      productId = getProductToCompare();

      //Check if a legal value was passed in:
      if (!productId.equals("")) {

        //Check if this Product was already added to the Compare List.
        //If so, don't bother adding it again:
        boolean productAlreadyInList = false;
        Iterator i = getProductCompareList().iterator();
        while ((!productAlreadyInList) && (i.hasNext())) {
          product = (RepositoryItem)i.next();
          if (product.getRepositoryId().equals(productId)) {
            productAlreadyInList = true;
          }
        } //while

        //If the Product wasn't already added to the Compare Lists before:
        if (!productAlreadyInList) {

          if (isLoggingDebug())
            logDebug ("Adding ProductID: " + productId + " to the Compare Lists...");

          //Fetch the Product Repository Item, given the productId:
          product = getRepository().getItem(productId, "product");

          //If a Product with the productId was NOT found in the repository:
          if (product != null) {

            //Fetch all childSKUs for this Product:
            List childSKUs = (List) product.getPropertyValue("childSKUs");

            //Save current index before adding any SKUs:
            int firstNewIndex = 0;
            if (!(getProductCompareList() == null)) {
              //This will be the index of the first SKU we add:
              firstNewIndex = getProductCompareList().size();
            }

            //For each childSKU...
            Iterator iterator = childSKUs.iterator();
            while (iterator.hasNext()) {

              //Fetch the SKU Repository Item:
              SKU = (RepositoryItem)iterator.next();

              //If the SKU is not null:
              if (SKU != null) {

                //Fetch the SKU's ID:
                SKUId = SKU.getRepositoryId();

                if (isLoggingDebug())
                  logDebug ("Processing SKU with id: " + SKUId);

                //TODO: SHOULD I WRAP A SYNCHRONIZE AROUND THIS????

                //For each SKU, and one item to the ProductList (so ProductCompareList and
                //SkuCompareList maintain same length, so one Index can be used to access both):
                getProductCompareList().add(product);

                //Add the SKU to the SkuCompareList:
                getSkuCompareList().add(SKU);

                //If the First Index hasn't be set before, set it now.
                //If it has a value, don't change it so that the first SKU
                //stays the same, and the second one changes to the newest
                //Product added to compare list;
                if (getSelectedIndex1() == null) {
                  setSelectedIndex1("0");
                }

                if (getSelectedIndex2() == null) {
                  setSelectedIndex2("0");
                }
                else {
                  //Assign the index of the first SKU we just added to the list:
                  setSelectedIndex2(Integer.toString(firstNewIndex));
                }

              }

              //if SKU was null, something is wrong:
              else {
                processError(null, UserResourceLookup.ERROR_SKU_NULL, pRequest, pResponse);
              }
            }
          }

          //Product is null:
          else {
            processError(null, UserResourceLookup.ERROR_PRODUCT_NULL, pRequest, pResponse);
          }
        }

        //Product was already there.  Don't add again:
        else {
          if (isLoggingDebug())
            logDebug ("This Product was already in the ProductCompareList.  Not added again..");
        }
      } //if productId not ""

      else {
        processError(null, UserResourceLookup.NO_PRODUCT_TO_ADD, pRequest, pResponse);
      }

    } //main Try
    catch (Exception e) {
      processError(e, UserResourceLookup.ERROR_ADD_TO_COMPARE_LIST, pRequest, pResponse);
    }

    //Call any custom code after the main CompareSkus processing:
    postAddToCompareList(pRequest, pResponse);

    //If NO form errors are found, redirect to the success URL.
    //If form errors are found, redirect to the error URL.
    return checkFormRedirect (getAddToCompareListSuccessURL(), getAddToCompareListErrorURL(), pRequest, pResponse);
  }

  //-------------------------------------
  // property: CompareSkusSuccessURL
  //-------------------------------------
  String mCompareSkusSuccessURL;

  /**
   * Sets property CompareSkusSuccessURL.  This property
   * is normally set on a jhtml page.  It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the COMPARE button
   * on a page which compares SKUs.
   * @param pCompareSkusSuccessURL The URL (as a string) of the page to redirect to if
   *                                  the handleCompareSkus method succeeds with no errors.
   **/
  public void setCompareSkusSuccessURL(String pCompareSkusSuccessURL) {
    mCompareSkusSuccessURL = pCompareSkusSuccessURL;
  }

  /**
   * Returns property CompareSkusSuccessURL.  It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the COMPARE button
   * on a page which compares SKUs.
   * @return The URL (as a string) of the page to redirect to if the handleCompareSkus method
   *         generates no errors and succeeds.
   **/
  public String getCompareSkusSuccessURL() {
    return mCompareSkusSuccessURL;
  }

  //-------------------------------------
  // property: CompareSkusErrorURL
  //-------------------------------------
  String mCompareSkusErrorURL;

  /**
   * Sets property CompareSkusErrorURL.  This property
   * is normally set on a jhtml page.  It indicates
   * which page we should redirect to if errors
   * occur when the user pushes the COMPARE button
   * on a page which compares SKUs.
   * @param pCompareSkusErrorURL The URL (as a string) of the page to redirect to if
   *                                  the handleCompareSkus method generates errors.
   **/
  public void setCompareSkusErrorURL(String pCompareSkusErrorURL) {
    mCompareSkusErrorURL = pCompareSkusErrorURL;
  }

  /**
   * This method returns property CompareSkusErrorURL.  It indicates
   * which page we should redirect to if errors
   * occur when the user pushes the COMPARE button
   * on a page which compares SKUs.
   * @return The URL (as a string) of the page to redirect to if the handleCompareSkus method
   *         generates an ERROR.
   **/
  public String getCompareSkusErrorURL() {
    return mCompareSkusErrorURL;
  }

  //-----------------------------------
  // Method: preCompareSkus
  //-----------------------------------
  /**
   * This method provides an easy way for users to extend the functionality of the
   * handleCompareSkus method.  The preCompareSkus code will be executed
   * at the start of the handleCompareSkus method.
   * This default implementation does nothing.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void preCompareSkus(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
                              throws ServletException,
                                     IOException
  {
    if (isLoggingDebug())
      logDebug("in CompareSkusFormHandler.preCompareSkus");
  }

  //-----------------------------------
  // Method: postCompareSkus
  //-----------------------------------
  /**
   * This method provides an easy way for users to extend the functionality of the
   * handleCompareSkus method.  The postCompareSkus code will be executed
   * at the end of the handleCompareSkus method.
   * This default implementation does nothing.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void postCompareSkus(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
                              throws ServletException,
                                     IOException
  {
    if (isLoggingDebug())
      logDebug("in CompareSkusFormHandler.postCompareSkus");
  }

  //-----------------------------------
  // Method: handleCompareSkus
  //-----------------------------------
  /**
   * This method is called when the user goes to compare 2 selected SKUs with each other.
   * This method is invoked when the user pushes the COMPARE button after
   * they have selected two items they want to compare.
   * This method assumes that the SelectedIndex1 and SelectedIndex2 properties
   * were filled in before this method is called.
   * Redirects to getCompareSkusSuccessURL() if all goes well.
   * Redirects to getCompareSkusErrorURL() if any errors are found.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if processing was successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleCompareSkus(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException
  {

    if (isLoggingDebug())
      logDebug("in CompareSkusFormHandler.handleCompareSkus");

    //If any form errors found, redirect to Error URL
    if (! checkFormRedirect(null, getCompareSkusErrorURL(), pRequest, pResponse))
       return false;

    //Call any custom code before the main CompareSkus processing:
    preCompareSkus(pRequest, pResponse);

    try {

      //verify that the user selected 2 SKUs before continuing
      //SHOULD I VERIFY THAT THE SKUS AREN'T THE SAME:
      if (getSelectedIndex1().equals("")) {
        processError(null, UserResourceLookup.MUST_SELECT_SKUS_TO_COMPARE, pRequest, pResponse);
      }

      if (getSelectedIndex2().equals("")) {
        processError(null, UserResourceLookup.MUST_SELECT_SKUS_TO_COMPARE, pRequest, pResponse);
      }

      //Page will do most of the work to fetch/display comparison data...

    }
    catch (Exception e) {
      processError(e, UserResourceLookup.ERROR_COMPARE_SKUS, pRequest, pResponse);
    }

    //Call any custom code after the main CompareSkus processing:
    postCompareSkus(pRequest, pResponse);

    //If NO form errors are found, redirect to the success URL.
    //If form errors are found, redirect to the error URL.
    return checkFormRedirect (getCompareSkusSuccessURL(), getCompareSkusErrorURL(), pRequest, pResponse);
  }

  //-----------------------------------
  // Method: getUserLocale
  //-----------------------------------

  /**
   * Returns either the Locale from the Request object (if it isn't NULL),
   * or the Locale from the JVM.
   * @param pRequest the servlet's request
   * @return Either the Locale from the Request object, or the Locale from the JVM.
   */
  protected Locale getUsersLocale (DynamoHttpServletRequest pRequest) {

    //Try to get the locale from the Request object:
    RequestLocale requestLocale = pRequest.getRequestLocale();
    if (requestLocale != null) {
      return requestLocale.getLocale();
    }

    //If no locale value in the Request object, get the locale from the JVM:
    return Locale.getDefault();
  }

  // -------------------------------
  // processError
  // -------------------------------
  /**
   * This method is called by this form handler whenever an error is found.
   * It first looks-up the error in the associated Resource Bundle (taking
   * into account the user's locale), and then adds the localized error
   * as a form exception.  It also logs the error if isLoggingError is
   * enabled.   Any errors which are dealt with by this form handler should
   * be added to the UserResourceLookup.java file, and the UserResources.properties
   * file.
   * @pException The exception to be processed.  Pass in NULL if error wasn't
   *             a result of an Exception being thrown.
   * @pResourceLookupKey The LookupKey of the Resource message
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet IO
   */
  public void processError(Exception pException,
                           String pResourceLookupKey,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
                           throws ServletException,
                                  IOException
  {
    //Using the UserResourceLookup object, fetch the correct error message
    //based on user's locale.
    //NOTE: See UserResourceLookup.java for error message constants and methods.
    //      See UserResources.properties file for key/error message mappings.
    String errorMsg = UserResourceLookup.getStringResource (pResourceLookupKey,
                                                            getUsersLocale(pRequest));

    //Create the DropletException both with the translated user message, and
    //with the LookupKey (in case we want to do translation on the JHTML page
    //at a later date):
    addFormException(new DropletException(errorMsg, pException, pResourceLookupKey));

    //if ErrorLogging is enabled, log the error w/ the correct Locale:
    if (isLoggingError()) {
      logError(errorMsg);
    }
  }

} // end of CompareSkusFormHandler class
