/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.commerce.promotion;

// atg classes
import atg.servlet.*;
import atg.droplet.*;
import atg.dtm.*;

// atg userprofiling classes
import atg.userprofiling.Profile;
import atg.repository.*;

// atg commerce classes
import atg.commerce.claimable.*;

// java classes
import java.util.Locale;
import java.io.*;
import javax.servlet.*;
import javax.transaction.*;
import javax.transaction.xa.*;

/**
 *
 * This FormHandler is used to "claim" a promotion and then stick this promotion into
 * the users current list of promotions.  The idea is that a coupon is just another mechanism
 * to deliver a promotion to the user, i.e. they explicitly obtain the promotion by
 * entering a coupon claim number and then obtaining the promotion.
 *
 * <P>
 *
 * The handleClaimCoupon method is responsible for actually doing the work and obtaining
 * the promotion.
 *
 * @beaninfo
 *   description: A form handler which can be used to obtain a coupon
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ashley J. Streb
 * @see atg.commerce.promotion.PromotionTools
 * @see atg.commerce.claimable.ClaimableTools
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/CouponFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CouponFormHandler
  extends GenericFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/CouponFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  static final String TRANSACTION_NOT_SUPPORTED = "NOT_SUPPORTED";
  static final String TRANSACTION_SUPPORTS = "SUPPORTS";
  static final String TRANSACTION_REQUIRED = "REQUIRED";
  static final String TRANSACTION_REQUIRES_NEW = "REQUIRES_NEW";
  static final String TRANSACTION_MANDATORY = "MANDATORY";
  static final String TRANSACTION_NEVER = "NEVER";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: TransactionManager

  /**
   * The TransactionManager to handle the transaction
   */
  TransactionManager mTransactionManager;

  /**
   * Set the TransactionManager property.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * Return the TransactionManager property.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  //---------------------------------------------------------------------------
  // property: TransactionType

  /**
   * The TransactionType
   */
  String mTransactionType;

  /**
   * Set the TransactionType property.
   */
  public void setTransactionType(String pTransactionType) {
    mTransactionType = pTransactionType;
  }

  /**
   * Return the TransactionType property.
   */
  public String getTransactionType() {
    return mTransactionType;
  }


  private int getConvertedTransactionType() {
    if (TRANSACTION_NOT_SUPPORTED.equals(getTransactionType()))
      return TransactionDemarcation.NOT_SUPPORTED;
    if (TRANSACTION_SUPPORTS.equals(getTransactionType()))
      return TransactionDemarcation.SUPPORTS;
    if (TRANSACTION_REQUIRED.equals(getTransactionType()))
      return TransactionDemarcation.REQUIRED;
    if (TRANSACTION_REQUIRES_NEW.equals(getTransactionType()))
      return TransactionDemarcation.REQUIRES_NEW;
    if (TRANSACTION_MANDATORY.equals(getTransactionType()))
      return TransactionDemarcation.MANDATORY;
    if (TRANSACTION_NEVER.equals(getTransactionType()))
      return TransactionDemarcation.NEVER;
    return 0;

  }

  //---------------------------------------------------------------------------
  // property: ClaimableTools

  /**
   * The tools package that is used to claim the coupon
   */
  ClaimableTools mClaimableTools;

  /**
   * Set the ClaimableTools property.
   */
  public void setClaimableTools(ClaimableTools pClaimableTools) {
    mClaimableTools = pClaimableTools;
  }

  /**
   * Return the ClaimableTools property.
   */
  public ClaimableTools getClaimableTools() {
    return mClaimableTools;
  }

  //---------------------------------------------------------------------------
  // property: claimCouponSuccessURL

  /**
   * Where to go after successfully claiming a coupon.
   */
  String mClaimCouponSuccessURL;

  /**
   * Set the claimCouponSuccessURL property.
   */
  public void setClaimCouponSuccessURL(String pClaimCouponSuccessURL) {
    mClaimCouponSuccessURL = pClaimCouponSuccessURL;
  }

  /**
   * Return the claimCouponSuccessURL property.
   */
  public String getClaimCouponSuccessURL() {
    return mClaimCouponSuccessURL;
  }


  //---------------------------------------------------------------------------
  // property: claimCouponErrorURL

  /**
   * Where to go if an error occurs while trying to claim a coupon.
   */
  String mClaimCouponErrorURL;

  /**
   * Set the claimCouponErrorURL property.
   */
  public void setClaimCouponErrorURL(String pClaimCouponErrorURL) {
    mClaimCouponErrorURL = pClaimCouponErrorURL;
  }

  /**
   * Return the claimCouponErrorURL property.
   */
  public String getClaimCouponErrorURL() {
    return mClaimCouponErrorURL;
  }


  //---------------------------------------------------------------------------
  // property: couponClaimCode

  /**
   * The code that is used to try and claim a coupon from the ClaimableTools
   */
  String mCouponClaimCode;

  /**
   * Set the couponClaimCode property.
   */
  public void setCouponClaimCode(String pCouponClaimCode) {
    mCouponClaimCode = pCouponClaimCode;
  }

  /**
   * Return the couponClaimCode property.
   */
  public String getCouponClaimCode() {
    return mCouponClaimCode;
  }

  //---------------------------------------------------------------------------
  // property: Profile

  /**
   * Users profile
   */
  Profile mProfile;

  /**
   * Set the Profile property.
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Return the Profile property.
   */
  public Profile getProfile() {
    return mProfile;
  }


  //---------------------------------------------------------------------------
  // property: PromotionTools

  /**
   * PromotionTools that is used to stick the promotion into the users profile
   */
  PromotionTools mPromotionTools;

  /**
   * Set the PromotionTools property.
   */
  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  /**
   * Return the PromotionTools property.
   */
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }


  //---------------------------------------------------------------------------
  // property: ClaimableManager

  /**
   * Used to claim coupons
   */
  ClaimableManager mClaimableManager;

  /**
   * Set the ClaimableManager property.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Return the ClaimableManager property.
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }


  //---------------------------------------------------------------------------
  // property:ValidCouponItemTypes
  //---------------------------------------------------------------------------

  private String[] mValidCouponItemTypes;
  public void setValidCouponItemTypes(String[] pValidCouponItemTypes) {
    mValidCouponItemTypes = pValidCouponItemTypes;
  }

  /**
   * The list of acceptable item types for promotions when claiming a
   * coupon.  Used in <code>handleClaimCoupon</code>
   **/
  public String[] getValidCouponItemTypes() {
    return mValidCouponItemTypes;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Empty Constructor
   *
   */

  public CouponFormHandler() {
  }

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * Claim the coupon. Called by handleClaimCoupon() to perform the
   * actual claiming of the coupon. This method simply calls
   * claimableManager.claimCopon(). Override this method to customize
   * the coupon claiming process.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @throws ClaimableException
   */
  protected void claimCoupon( DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse ) throws ClaimableException
  {
    getClaimableManager().claimCoupon( getProfile().getRepositoryId(),
      getCouponClaimCode() );
  }

  /**
   * Method that is invoked to claim a coupon.  First, the property
   * couponClaimCode is checked to make sure that the user entered
   * a claim code for the coupon.  Next, using that claim code the claimableManager
   * is used to try and actually claim the coupon using the claimItem call.
   * If no coupon object is returned, than an error is logged. Else promotionTools
   * is used to add the promotion to the users profile.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleClaimCoupon(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try {
      claimCoupon( pRequest, pResponse );
    }
    catch(ClaimableException ce) {
      Throwable sourceException = ce.getSourceException();
      if (sourceException instanceof PromotionException) {
	PromotionException pe = (PromotionException) sourceException;
        processError(pe.getErrorCode(), pe, pRequest);
      }
      else
        processError(ce.getErrorCode(), ce, pRequest);

      return checkFormRedirect(mClaimCouponSuccessURL, mClaimCouponErrorURL,
			       pRequest, pResponse);
    }

    return checkFormRedirect(mClaimCouponSuccessURL, mClaimCouponErrorURL,
                             pRequest, pResponse);
  }

  /**
   * If an error occurs, this method will obtain the correct Msg resource
   * string based upon the users locale from the getUserLocale method.
   *
   * @param pErrorCodeId the error key that is used to obtain the error string
   */
  protected void processError(String pErrorCodeId,
                              Exception e,
                              DynamoHttpServletRequest pRequest)
    throws ServletException, IOException
  {
    Locale usersLocale = getUserLocale(pRequest);
    if (pErrorCodeId == null)
      pErrorCodeId = PromotionConstants.MSG_ERROR_CLAIMING_COUPON;    
    String errorMsg =
      PromotionConstants.getStringResource(pErrorCodeId, usersLocale);
    if (e == null)
      addFormException(new DropletException(errorMsg, pErrorCodeId));
    else
      addFormException(new DropletException(errorMsg, e, pErrorCodeId));
  }

  /**
   * Obtain the users locale by examining the request object first and then
   * getting the default Locale object.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @return a value of type 'Locale'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected Locale getUserLocale(DynamoHttpServletRequest pRequest)
    throws ServletException, IOException
  {
    RequestLocale requestLocale = pRequest.getRequestLocale();
    if (requestLocale != null)
      return requestLocale.getLocale();

    return Locale.getDefault();
  }

  /**
   * Checks to see if the type of the promotion is valid.  All valid
   * types appear in the list <code>validCouponItemTypes</code>
   *
   * @param pPromotion
   * @return true if the item type is in the <code>validCouponItemTypes</code> list.
   **/
  public boolean checkPromotionType(RepositoryItem pPromotion)
  {
    return getClaimableManager().checkPromotionType(pPromotion);
  }
}   // end of class
