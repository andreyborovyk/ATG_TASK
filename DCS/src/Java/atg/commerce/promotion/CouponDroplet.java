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

// atg packages
import atg.servlet.*;
import atg.repository.*;
import atg.nucleus.naming.*;

// atg commerce packages
import atg.commerce.claimable.*;
import atg.commerce.CommerceException;

// java packages
import java.io.*;
import javax.servlet.*;
import java.util.*;



/**
 * 
 * This droplet is used to create coupon objects in the claimable repository.
 * A promotion is handed to the droplet via either a promotion id or promotion
 * object parameter.  The droplet then creates a coupon entry in the claimable
 * repository, linking it to the promotion object that was handed in and then
 * finally sets a coupon id on output. Either the promotion or promoId parmeter
 * must be set.
 * 
 * <p>
 * 
 * Here are a list of the parmeters: <br>
 *
 * <p>
 *
 * <ul>
 * <li>displayName - The display name of the coupon
 * <li>redeemableOnPromotionSites - Use the site constraint within the coupon
 * <li>promotion - The promotion object that will be used to create the coupon
 * <li>promotions - The set of promotions that will be used to create the coupon
 * <li>promoId - The id of the promotion that is used to create the coupon
 * <li>output - oparam rendered on successful creation of a coupon object
 * <li>error - oparam rendered if an error occurs during creation of a coupon
 * <li>coupon - parameter set in oparam.  This is the coupon object.  The claim
 * code for this coupon can be obtained by using coupon.id.
 * </ul>
 *
 * @see atg.commerce.claimable.ClaimableManager
 * @see atg.commerce.promotion.PromotionTools
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/CouponDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CouponDroplet
  extends DynamoServlet
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/CouponDroplet.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  protected static final String COUPON = "coupon";
  protected static final ParameterName DISPLAY_NAME = ParameterName.getParameterName("displayName");  
  protected static final ParameterName REDEEMABLE_ON_PROMOTION_SITES = ParameterName.getParameterName("redeemableOnPromotionSites");
  protected static final ParameterName PROMOTION = ParameterName.getParameterName("promotion");
  protected static final ParameterName PROMOTIONS = ParameterName.getParameterName("promotions");
  protected static final ParameterName PROMOID = ParameterName.getParameterName("promoId");
  protected static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  protected static final ParameterName ERROR = ParameterName.getParameterName("error");

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  //---------------------------------------------------------------------------
  // property: ClaimableManager

  /**
   * Used to create the coupon object in the repository.
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
  // property: PromotionTools

  /**
   * If the user hands us a promotionId, then we need to obtain the promotion object.
   * PromotionTools is used to do this.
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


  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Empty Constructor
   *
   */
  public CouponDroplet() {
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * The service method first obtains the promotion object.  This is done by first checking
   * to see if the promotions parameter was set.  If it was not set, then it tries to get 
   * a promotion by obtaining the promotion paramter. If that was not set then is tries to 
   * get the promoId parameter and using PromotionTools to get the promotion object 
   * associated with this id.  If no promotion is found, then the error oparam is rendered.
   * <p>
   * If we have a promotion, then use the createCoupon method to create a coupon
   * using the promotion that was obtained.  Finally, set the coupon in the output
   * and render the output oparam.
   *
   *
   * @param pRequest request object
   * @param pResponse response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException 
  {
    Set promotionList = new HashSet();
    
    if (pRequest.getObjectParameter(PROMOTIONS) != null)
    	promotionList = (Set) pRequest.getObjectParameter(PROMOTIONS);
    
    RepositoryItem promotion = null;
    
    if (promotionList.size() == 0 && pRequest.getObjectParameter(PROMOTION) != null)
      promotion = (RepositoryItem) pRequest.getObjectParameter(PROMOTION);

    //Get the display name for the coupon if entered
    String displayName = pRequest.getParameter(DISPLAY_NAME);
    
    Boolean usePromotionSiteConstraint = null;
    
    //Get the usePromotionSiteConstraintPropertyName if entered
    if (pRequest.getParameter(REDEEMABLE_ON_PROMOTION_SITES)!=null)
    	usePromotionSiteConstraint = new Boolean(pRequest.getParameter(REDEEMABLE_ON_PROMOTION_SITES));
    
    // promotion is null, try to get a promo id and render that
    if (promotionList.size() == 0 && promotion == null) {
      String promoId = pRequest.getParameter(PROMOID);
      if (promoId == null) {
        if (isLoggingWarning()) {
          String errorMsg = PromotionConstants.getStringResource(PromotionConstants.MSG_NO_PROMOTION_SUPPLIED, getUserLocale(pRequest));
          logWarning(errorMsg);
        }
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        return;
      }
      else {
    	promotion = getPromotion(promoId);
        if (promotion == null) {        
          if (isLoggingWarning()) {
            String errorMsg = PromotionConstants.getStringResource(PromotionConstants.MSG_NO_PROMOTION_FOUND, getUserLocale(pRequest));
            logWarning(errorMsg);
          }
          pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
          return;
        }
      }
    }
    
    //if an individual promotion or promoId is passed in and not promotions have then add it to the list for backward compatablity
    if (promotion!=null && promotionList.size() == 0)
    	promotionList.add(promotion);

    // we have a promotion at this point, create a new coupon object and populate with promo
    // get the new coupon id and set this to the localparam couponId
    try {
      RepositoryItem coupon = createCoupon(promotionList,displayName,usePromotionSiteConstraint);
      pRequest.setParameter(COUPON, coupon);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    } 
    catch (CommerceException ce) {
      if (isLoggingError())
        logError(ce);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
    }
  }


  /**
   * Used to create the coupon.  This is done by using the claimable manager
   * and calling createClaimablePromotion on it and then initializing the claimable
   * promotion.  The new repository item is returned.
   *
   * @param pPromotion the promotion object
   * @param pDisplayName the display name for the coupon
   * @param pUsePromotionSiteConstraint
   * @return the coupon
   * @exception CommerceException if an error occurs
   */
  protected RepositoryItem createCoupon(Set<RepositoryItem> pPromotionList, 
		  String pDisplayName,
          Boolean pUsePromotionSiteConstraint) 
    throws CommerceException
  {
    RepositoryItem coupon = getClaimableManager().createClaimablePromotion();
    getClaimableManager().initializeClaimablePromotion(coupon, pPromotionList, pDisplayName, pUsePromotionSiteConstraint);
    return coupon;
  }

  /**
   * Used to create the coupon.  This is done by using the claimable manager
   * and calling createClaimablePromotion on it and then initializing the claimable
   * promotion.  The new repository item is returned.
   *
   * @param pPromotion the promotion object
   * @return the coupon
   * @exception CommerceException if an error occurs
   */
  protected RepositoryItem createCoupon(RepositoryItem pPromotion) 
    throws CommerceException
  {
    RepositoryItem coupon = getClaimableManager().createClaimablePromotion();
    getClaimableManager().initializeClaimablePromotion(coupon, pPromotion);
    return coupon;
  }
  
  /**
   * Obtains a promotion for a given promoId by using PromotionTools.
   *
   * @param pPromoId the id of a promotion
   * @return a promotion object
   */
  protected RepositoryItem getPromotion(String pPromoId) {
    RepositoryItem promotion = null;

    try {
      if (pPromoId == null)
        return null;
      
      Repository repository = mPromotionTools.getPromotions();
      String type = mPromotionTools.getBasePromotionItemType();
      promotion = repository.getItem(pPromoId, type);
    }
    catch (RepositoryException re) {
      if (isLoggingError())
        logError(re);
    }
      
    return promotion;
  }

  /**
   * Get a users locale by trying to get it from the request object.
   * If its not there, then use the default Locale of the VM.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @return the users locale
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

}   // end of class
