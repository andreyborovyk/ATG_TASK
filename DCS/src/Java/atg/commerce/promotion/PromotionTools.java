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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.adapter.composite.CompositeItem;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.messaging.MessageSender;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingEngineService;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingModelProperties;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.Qualifier;
import atg.commerce.util.PipelineUtils;
import atg.core.util.ResourceUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.GenericService;
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RemovedItemException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.xml.GetException;
import atg.repository.xml.GetService;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 * Utility service with methods to help process promotions.
 *
 * @author Joshua Spiewak
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/PromotionTools.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
@SuppressWarnings("unchecked")
public class PromotionTools
extends GenericService
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/PromotionTools.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.promotion.PromotionResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle =
      atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public static final String COPIED_DURING_LOGIN = "copiedDuringLogin";
  public static final String REVOKED_DURING_LOGIN = "revokedDuringLogin";

  /**
   * Key into extra parameters map to an optional Qualifier service reference
   */
  public static final String EXTRA_PARAM_QUALIFIERSERVICE = PricingEngineService.EXTRA_PARAM_QUALIFIERSERVICE;
  
  //-------------------------------------
  // Properties
  //-------------------------------------
  
  //-------------------------------------
  // property: QualifierService
  protected Qualifier mQualifierService = null;

  /**
   * Gets the default Qualifier service to use.
   * 
   * @return Qualifier service reference
   */
  public Qualifier getQualifierService() {
    return mQualifierService;
  }
  
  /**
   * Sets the default Qualifier service to use.
   * 
   * @param pQualifierService Qualifier service reference
   */
  public void setQualifierService( Qualifier pQualifierService ){
    mQualifierService = pQualifierService;
  }
  
  //---------------------------------------------------------------------------
  // property: SiteGroupsPropertyName
  String mSiteGroupsPropertyName;

  /**
   * Set the SiteGroupsPropertyName property.
   */
  public void setSiteGroupsPropertyName(String pSiteGroupsPropertyName) {
    mSiteGroupsPropertyName = pSiteGroupsPropertyName;
  }

  /**
   * Return the SiteGroupsPropertyName property.
   */
  public String getSiteGroupsPropertyName() {
    return mSiteGroupsPropertyName;
  }
  
  //---------------------------------------------------------------------------
  // property: SiteGroupPropertyName
  String mSiteGroupPropertyName;

  /**
   * Set the SiteGroupPropertyName property.
   */
  public void setSiteGroupPropertyName(String pSiteGroupPropertyName) {
    mSiteGroupPropertyName = pSiteGroupPropertyName;
  }

  /**
   * Return the SiteGroupPropertyName property.
   */
  public String getSiteGroupPropertyName() {
    return mSiteGroupPropertyName;
  }
     
  //---------------------------------------------------------------------------
  // property: SiteIdPropertyName
  String mSiteIdPropertyName;

  /**
   * Set the SiteIdPropertyName property.
   */
  public void setSiteIdPropertyName(String pSiteIdPropertyName) {
    mSiteIdPropertyName = pSiteIdPropertyName;
  }

  /**
   * Return the SiteIdPropertyName property.
   */
  public String getSiteIdPropertyName() {
    return mSiteIdPropertyName;
  }
  
  //---------------------------------------------------------------------------
  // property: SitesPropertyName
  String mSitesPropertyName;

  /**
   * Set the SitesPropertyName property.
   */
  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }

  /**
   * Return the SitesPropertyName property.
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }
  
   //-------------------------------------
  // property: FirePromotionGrantedEvent
  // If set to true, PromotionGranted Event will be fired when Promotion is offered.
  boolean mFirePromotionGrantedEvent = false;

  /**
   * Sets property FirePromotionGrantedEvent
   **/
  public void setFirePromotionGrantedEvent( boolean pFirePromotionGrantedEvent ) {
    mFirePromotionGrantedEvent = pFirePromotionGrantedEvent;
  }

  /**
   * Gets property FirePromotionGrantedEvent
   **/
  public boolean isFirePromotionGrantedEvent() {
    return mFirePromotionGrantedEvent;
  }

  //-------------------------------------
  // property: FirePromotionRevokedEvent
  // If set to true, PromotionRevoked Event will be fired when Promotion is revoked.
  boolean mFirePromotionRevokedEvent = false;

  /**
   * Sets property FirePromotionRevokedEvent
   **/
  public void setFirePromotionRevokedEvent( boolean pFirePromotionRevokedEvent ) {
    mFirePromotionRevokedEvent = pFirePromotionRevokedEvent;
  }

  /**
   * Gets property FirePromotionRevokedEvent
   **/
  public boolean isFirePromotionRevokedEvent() {
    return mFirePromotionRevokedEvent;
  }

  //-------------------------------------
  // property: PromotionGrantedMessageSender
  // MessageSender used to fire PromotionGranted, PromotionRevoked Events
  MessageSender mPromotionGrantedMessageSender = null;

  /**
   * Sets property PromotionGrantedMessageSender.  The MessageSender is used to send PromotionGranted
   * Events.
   **/
  public void setPromotionGrantedMessageSender( MessageSender pPromotionGrantedMessageSender ) {
   mPromotionGrantedMessageSender = pPromotionGrantedMessageSender;
  }

  /**
   * Gets property MessageSender.
   * @beaninfo description: A MessageSender
   **/
  public MessageSender getPromotionGrantedMessageSender() {
    return mPromotionGrantedMessageSender;
  }

  //-------------------------------------
  // property: PromotionRevokedMessageSender
  // MessageSender used to fire PromotionRevoked Events
  MessageSender mPromotionRevokedMessageSender = null;

  /**
   * Sets property PromotionRevokedMessageSender.  The MessageSender is used to send PromotionRevoked
   * Events.
   **/
  public void setPromotionRevokedMessageSender( MessageSender pPromotionRevokedMessageSender ) {
    mPromotionRevokedMessageSender = pPromotionRevokedMessageSender;
  }

  /**
   * Gets property PromotionRevokedMessageSender.
   * @beaninfo description: A MessageSender
   **/
  public MessageSender getPromotionRevokedMessageSender() {
    return mPromotionRevokedMessageSender;
  }

  //---------------------------------------------------------------------------
  // property:PipelineManager
  //---------------------------------------------------------------------------
  private PipelineManager mPipelineManager;
  public void setPipelineManager(PipelineManager pPipelineManager) {
    mPipelineManager = pPipelineManager;
  }

  /**
   * The PipelineManager is used to run the pipeline process that sends scenario events
   * @beaninfo description: The PipelineManager is used to run the pipeline process that sends scenario events
   **/
  public PipelineManager getPipelineManager() {
    return mPipelineManager;
  }

  //---------------------------------------------------------------------------
  // property:SendEventOnAddItem
  //---------------------------------------------------------------------------
  private boolean mSendEventOnAddItem = true;
  public void setSendEventOnAddItem(boolean pSendEventOnAddItem) {
    mSendEventOnAddItem = pSendEventOnAddItem;
  }

  /**
   * If this is true, then the ScenarioAddedItemToCart event will be sent
   * If this is false, then the ScenarioAddedItemToCart event will not be sent.
   * The default value is true
   * @beaninfo description: If this is true, then the ScenarioAddedItemToCart event will be sent
   **/
  public boolean isSendEventOnAddItem() {
    return mSendEventOnAddItem;
  }

  //---------------------------------------------------------------------------
  // property:AddItemEventPipeline
  //---------------------------------------------------------------------------
  private String mAddItemEventPipeline = PipelineConstants.SENDSCENARIOEVENT;
  public void setAddItemEventPipeline(String pAddItemEventPipeline) {
    mAddItemEventPipeline = pAddItemEventPipeline;
  }

  /**
   * The name of the pipeline used to send the ScenarioAddedItemToOrder event
   * Defaults to PipelineConstants.SENDSCENARIOEVENT
   * @beaninfo description: The name of the pipeline used to send the ScenarioAddedItemToOrder event
   **/
  public String getAddItemEventPipeline() {
    return mAddItemEventPipeline;
  }
  
  //---------------------------------------------------------------------------
  // property: pricingModelProperties
  PricingModelProperties mPricingModelProperties;

  public void setPricingModelProperties(PricingModelProperties pPricingModelProperties) {
    mPricingModelProperties = pPricingModelProperties;
  }

  /**
   * Returns a bean that contains properties for a PricingModel
   * @beaninfo description: A bean that contains properties for a PricingModel
   */
  public PricingModelProperties getPricingModelProperties() {
    return mPricingModelProperties;
  }

  //---------------------------------------------------------------------------
  // property: activePromotionsProperty
  String mActivePromotionsProperty;

  public void setActivePromotionsProperty(String pActivePromotionsProperty) {
    mActivePromotionsProperty = pActivePromotionsProperty;
  }

  /**
   * Property of the profile to which we add new promotions
   * @beaninfo description: Property of the profile to which we add new promotions
   */
  public String getActivePromotionsProperty() {
    return mActivePromotionsProperty;
  }

  //---------------------------------------------------------------------------
  // property: usedPromotionsProperty
  String mUsedPromotionsProperty;

  public void setUsedPromotionsProperty(String pUsedPromotionsProperty) {
    mUsedPromotionsProperty = pUsedPromotionsProperty;
  }

  /**
   * Property of the profile to which we add consumed promotions
   * @beaninfo description: Property of the profile to which we add consumed promotions
   */
  public String getUsedPromotionsProperty() {
    return mUsedPromotionsProperty;
  }

  //---------------------------------------------------------------------------
  // property: AllowMultipleProperty
  //---------------------------------------------------------------------------

  String mAllowMultipleProperty;

  public void setAllowMultipleProperty(String pAllowMultipleProperty) {
    mAllowMultipleProperty = pAllowMultipleProperty;
  }

  /**
   * Property name of the 'allowMultiple' property in the promotion
   *
   * @beaninfo description: Property of the promotion which determines whether the promotion
   * can be included more than once in the user's profile.
   */
  public String getAllowMultipleProperty() { return mAllowMultipleProperty; }


  //---------------------------------------------------------------------------
  // property: GiveToAnonymousProfilesProperty
  //---------------------------------------------------------------------------

  String mGiveToAnonymousProfilesProperty;

  public void setGiveToAnonymousProfilesProperty(String pGiveToAnonymousProfilesProperty) {
    mGiveToAnonymousProfilesProperty = pGiveToAnonymousProfilesProperty;
  }

  /**
   * Property name of the 'giveToAnonymousProfiles' property in the promotion
   *
   * @beaninfo description: Property of the promotion which determines whether the promotion
   * can be included more than once in the user's profile.
   */

  public String getGiveToAnonymousProfilesProperty() {
    return mGiveToAnonymousProfilesProperty;
  }


  //-------------------------------------
  // property: usesProperty
  String mUsesProperty;

  /**
   * Sets the usesProperty */
  public void setUsesProperty(String pUsesProperty) {
    mUsesProperty = pUsesProperty;
  }

  /**
   * Returns the usesProperty
   *
   * @beaninfo description: Property of a promotion which determines the number of times a
   * promotion can be used
   */
  public String getUsesProperty() {
    return mUsesProperty;
  }


  //-------------------------------------

  String mPromoStatusDescriptorName;

  public void setPromoStatusDescriptorName(String pPromoStatusDescriptorName) {
    mPromoStatusDescriptorName = pPromoStatusDescriptorName;
  }

  /**
   * Returns the descriptor name for the promoStatus descriptor
   * @beaninfo description: the descriptor to be used for the promoStatus object
   **/
  public String getPromoStatusDescriptorName() {
    return mPromoStatusDescriptorName;
  }


  //-------------------------------------

  String mPromoStatusProfileIdProperty;

  public void setPromoStatusProfileIdProperty(String pPromoStatusProfileIdProperty) {
    mPromoStatusProfileIdProperty = pPromoStatusProfileIdProperty;
  }

  /**
   * Returns the promoStatus profile id property name
   * @beaninfo description: the property name of the profile id in the promoStatus item descriptor
   **/
  public String getPromoStatusProfileIdProperty() {
    return mPromoStatusProfileIdProperty;
  }

  //-------------------------------------
  String mPromoStatusNumUsesProperty;

  public void setPromoStatusNumUsesProperty(String pPromoStatusNumUsesProperty) {
    mPromoStatusNumUsesProperty = pPromoStatusNumUsesProperty;
  }

  /**
   * Returns the promoStatus num_uses property name
   *
   * @beaninfo description: the property name of the number of uses property in the promoStatus
   * item descriptor
   **/
  public String getPromoStatusNumUsesProperty() {
    return mPromoStatusNumUsesProperty;
  }

  //-------------------------------------

  String mPromoStatusPromoProperty;

  public void setPromoStatusPromoProperty(String pPromoStatusPromoProperty) {
    mPromoStatusPromoProperty = pPromoStatusPromoProperty;
  }

  /**
   * Returns the promoStatus promo property name
   *
   * @beaninfo description: the property name of the promotion object in the promoStatus item
   * descriptor
   **/
  public String getPromoStatusPromoProperty() {
    return mPromoStatusPromoProperty;
  }


  //---------------------------------------------------------------------------
  // property: ProfileRepository
  //---------------------------------------------------------------------------

  MutableRepository mProfileRepository;

  public void setProfileRepository(MutableRepository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  /**
   * Return the editable profile repository
   **/
  public MutableRepository getProfileRepository() {
    return mProfileRepository;
  }

    String mProfileItemType;
    public void setProfileItemType(String pProfileItemType) {
      mProfileItemType = pProfileItemType;
    }

    public String getProfileItemType() {
      return mProfileItemType;
    }

  //---------------------------------------------------------------------------
  // property: PricingTools
  //---------------------------------------------------------------------------

  PricingTools mPricingTools;

  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * Return the PricingTools component
   **/
  public PricingTools getPricingTools() {
    return mPricingTools;
  }


  //-------------------------------------
  // property: Promotions
  Repository mPromotions;

  /**
   * Sets property Promotions
   **/
  public void setPromotions(Repository pPromotions) {
    mPromotions = pPromotions;
  }

  /**
   * Returns property Promotions
   **/
  public Repository getPromotions() {
    return mPromotions;
  }

  //-------------------------------------
  // property: BasePromotionItemType
  String mBasePromotionItemType;

  /**
   * Sets property BasePromotionItemType
   **/
  public void setBasePromotionItemType(String pBasePromotionItemType) {
    mBasePromotionItemType = pBasePromotionItemType;
  }

  /**
   * Returns property BasePromotionItemType
   **/
  public String getBasePromotionItemType() {
    return mBasePromotionItemType;
  }
  
  //-------------------------------------
  // property: BaseClosenessQualifierItemType
  String mBaseClosenessQualifierItemType = "closenessQualifier";

  /**
   * Sets property BaseClosenessQualifierItemType
   **/
  public void setBaseClosenessQualifierItemType(String pBaseClosenessQualifierItemType) {
    mBaseClosenessQualifierItemType = pBaseClosenessQualifierItemType;
  }

  /**
   * Returns property BaseClosenessQualifierItemType
   **/
  public String getBaseClosenessQualifierItemType() {
    return mBaseClosenessQualifierItemType;
  }

  //-------------------------------------
  // property: UserPricingModelHolderPath
  protected ComponentName mUserPricingModelHolderPath;

  /**
   * Sets property UserPricingModelHolderPath
   **/
  public void setUserPricingModelHolderPath(String pUserPricingModelHolderPath) {
    if (pUserPricingModelHolderPath != null)
      mUserPricingModelHolderPath = ComponentName.getComponentName(pUserPricingModelHolderPath);
    else
      mUserPricingModelHolderPath = null;
  }

  /**
   * Returns property UserPricingModelHolderPath
   **/
  public String getUserPricingModelHolderPath() {
    if (mUserPricingModelHolderPath != null)
      return mUserPricingModelHolderPath.getName();
    else
      return null;
  }

  String [] mAlternateUserPricingModelHolderPaths;
  /**
   * Sets property AlternateUserPricingModelHolderPaths
   *
   **/
  public void setAlternateUserPricingModelHolderPaths(String [] pAlternateUserPricingModelHolderPaths)
  {
    mAlternateUserPricingModelHolderPaths = pAlternateUserPricingModelHolderPaths;
  }

  /**
   * Returns property AlternateUserPricingModelHolderPaths
   * @beaninfo description: An array of component paths to other pricing model holders that are initialized by the
   * initializePricingModels method.
   **/
  public String [] getAlternateUserPricingModelHolderPaths()
  {
    return mAlternateUserPricingModelHolderPaths;
  }
  //---------------------------------------------------------------------------
  // property:GetService
  //---------------------------------------------------------------------------
  private GetService mGetService;
  public void setGetService(GetService pGetService) {
    mGetService = pGetService;
  }

  /**
   * The tool used to convert promotions to xml in <code>getPromotionsAsXML</code>
   * @beaninfo description: The tool used to convert orders to xml in <code>getPromotionsAsXML</code>
   **/
  public GetService getGetService() {
    return mGetService;
  }

  //---------------------------------------------------------------------------
  // property:MappingFileName
  //---------------------------------------------------------------------------
  private String mMappingFileName;
  public void setMappingFileName(String pMappingFileName) {
    mMappingFileName = pMappingFileName;
  }

  /**
   * The name of the mapping file that describes the XML format for <code>getPromotionsAsXML</code>
   * @beaninfo description: The name of the mapping file that describes the XML format for <code>getPromotionsAsXML</code>
   **/
  public String getMappingFileName() {
    return mMappingFileName;
  }
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------
  
  /**
   * Cache of qualifier service path names to service instances 
   */
  protected Map<String,Qualifier> mQualifierServices = new HashMap<String,Qualifier>();

  //-------------------------------------
  /**
   * This method calls createTransientPromotionStatus to create and return a PromotionStatus 
   * repository item.  If the profile is not transient, then it will add the returned PromotionStatus 
   * to the promotions repository.
   * @param pProfile - the profile of the user for whom the promotion status is being created.
   * @param pPromotion - the promotion to be added.
   * @param pNumUses - the number of uses that this promotion can have.
   **/
  public RepositoryItem createPromotionStatus(RepositoryItem pProfile,
                                              RepositoryItem pPromotion,
                                              Integer pNumUses)
    throws RepositoryException
  {
    MutableRepositoryItem promoStatus = createTransientPromotionStatus(pProfile, pPromotion, pNumUses);

    if (!pProfile.isTransient()) {
      MutableRepository profileRepository = getProfileRepository();
      profileRepository.addItem(promoStatus);
    }

    return promoStatus;
  }


  //-------------------------------------
  /**
   * This method creates a PromotionStatus repository item and returns it without saving it. 
   * This method is responsible for setting the expiration date on the promotionStatus if this
   * promotion is a relative expiration promotion.
   * It also sets the grantedDate property to the current date and time.
   * @param pProfile - the profile of the user for whom the promotion status is being created.
   * @param pPromotion - the promotion to be added.
   * @param pNumUses - the number of uses that this promotion can have.
   * @return an initialized promotion status item that has not been saved 
   **/
  public MutableRepositoryItem createTransientPromotionStatus(RepositoryItem pProfile,
                                                               RepositoryItem pPromotion,
                                                               Integer pNumUses)
    throws RepositoryException
  {
    // <TBD> Is it all right to do this cast?
    MutableRepository profileRepository = getProfileRepository();

    MutableRepositoryItem promoStatus = profileRepository.createItem(getPromoStatusDescriptorName());

    promoStatus.setPropertyValue(getPromoStatusProfileIdProperty(), pProfile.getRepositoryId());
    promoStatus.setPropertyValue(getPromoStatusPromoProperty(), pPromotion);
    promoStatus.setPropertyValue(getPromoStatusNumUsesProperty(), pNumUses);
    // COMMERCE-123315 - granted date
    Date currentTime = Calendar.getInstance().getTime();
    String grantedDateProperty = getPricingModelProperties().getPromoStatusGrantedDate(); 
    if (promoStatus.getItemDescriptor().hasProperty(grantedDateProperty)) {
      promoStatus.setPropertyValue(grantedDateProperty, currentTime);
    }
    
    String relativeExpirationDateProperty = getPricingModelProperties().getPromoStatusExpirationDate();
    // If we don't have this property then there is no need to do any of this work.
    if (relativeExpirationDateProperty != null) {
      Date expirationDate = null;

      String relativeExpirationProperty = getPricingModelProperties().getRelativeExpiration();
      if (relativeExpirationProperty != null) {
        Boolean relativeExpiration = (Boolean) pPromotion.getPropertyValue(relativeExpirationProperty);
        if (relativeExpiration != null && (relativeExpiration.booleanValue())) {
          String relativeTimeProperty = getPricingModelProperties().getTimeUntilExpire();
          // If the day property is set then we will just set the expiration date to be
          if (relativeTimeProperty != null) {
            Integer TimeUntilExpire = (Integer) pPromotion.getPropertyValue(relativeTimeProperty);
            if (TimeUntilExpire != null) {
              Calendar calendar = Calendar.getInstance();
              calendar.add(Calendar.MINUTE, TimeUntilExpire.intValue());
              expirationDate = calendar.getTime();
            }
          }
        }
      }

      // If the date was set, then set it in the promoStatus object.
      if (expirationDate != null)
        promoStatus.setPropertyValue(getPricingModelProperties().
                                     getPromoStatusExpirationDate(),
                                     expirationDate);
    }

    return promoStatus;
  }


  /**
   * Initialize the set of pricing models/promotions that a user currently holds
   * while having an active session.
   */
  public void initializePricingModels()
       throws ServletException, IOException
  {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    initializePricingModels(request, null);
  }

  /**
   * Initialize the set of pricing models/promotions that a user currently holds
   * while having an active session.
   */
  public void initializePricingModels(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    PricingModelHolder holder = (PricingModelHolder)pRequest.resolveName(mUserPricingModelHolderPath);
    if (holder != null)
      holder.initializePricingModels();

    //if there are alternate pricing model holders to initialize, do it.
    String [] alternatePMHs = getAlternateUserPricingModelHolderPaths();
    if(alternatePMHs != null && alternatePMHs.length > 0)
    {
      for(int i =0; i < alternatePMHs.length;i++)
      {
        String alternateHolder = alternatePMHs[i];
        PricingModelHolder altholder=null;
        altholder = (PricingModelHolder)pRequest.resolveName(alternateHolder);
        if (altholder != null)
          altholder.initializePricingModels();
      }
    }
  }

  /**
   * For composite promotion items we need to get the item from the promotions repository
   * <br>since for example the promotion may have come from the profile repository which
   * <br>could be composite.
   * <p> See bugs COMMERCE-168557, COMMERCE-168558, COMMERCE-126830, COMMERCE-168127 
   * 
   * @param pPromotion RepositoryItem which may be a CompositeItem
   * @return RepositoryItem from the promotions repository or null if it wasn't found.
   */
  public RepositoryItem getPromotionFromComposite(RepositoryItem pPromotion) {

    if (pPromotion == null){
      return null;
    }
    
    RepositoryItem promotion = pPromotion;
    
    // Now handle composite items if necessary
    if (promotion instanceof CompositeItem){
      
      try {        
        // Composite item so get the primary item from the promotions repository since it will have the correct type.
        String promoId = promotion.getRepositoryId();
        RepositoryItemDescriptor itemDescriptor = promotion.getItemDescriptor();
        promotion = getPromotions().getItem(promoId, itemDescriptor.getItemDescriptorName());
      }
      catch (RepositoryException re) {
        // Failed to get item from the promotions repository
        if (isLoggingError()){     
          Object [] args = {promotion};
          String msg = ResourceUtils.getMsgResource("compositeLookupFailed", MY_RESOURCE_NAME,
              sResourceBundle, args);
          logError(msg, re);
        }
        return promotion;
      }
    }
    
    return (promotion);
  }
  
  /**
   * Method to determine if a passed in repository item or item descriptor is from a promotion.
   * <br>The passed in object can be of any super type of the base promotion type.
   * 
   * @param pObj Object to check, RepositoryItem or RepositoryItemDescriptor
   * 
   * @return true if pObj is of a type which is a sub-type of the base promotion type
   * @throws IllegalArgumentException if pObj is null or not a RepositoryItem or a RepositoryItemDescriptor
   * @throws RepositoryException if pObj is a RepositoryItem and it's type could not be accessed or the
   * base promotion type could not be determined.
   */
  public boolean isPromotionItem(Object pObj) 
  throws IllegalArgumentException, RepositoryException {
    RepositoryItemDescriptor baseType = getPromotions().getItemDescriptor(getBasePromotionItemType());
    return (isItemOfType(pObj, baseType));
  }
  
  /**
   * Method to determine if a passed in repository item or item descriptor is from a closeness qualifier.
   * <br>The passed in object can be of any super type of the base closeness qualifier type.
   * 
   * @param pObj Object to check, RepositoryItem or RepositoryItemDescriptor
   * 
   * @return true if pObj is of a type which is a sub-type of the base closeness qualifier type
   * @throws IllegalArgumentException if pObj is null or not a RepositoryItem or a RepositoryItemDescriptor
   * @throws RepositoryException if pObj is a RepositoryItem and it's type could not be accessed or the
   * base closeness qualifier type could not be determined.
   */
  public boolean isClosenessQualifierItem(Object pObj) 
  throws IllegalArgumentException, RepositoryException {
    RepositoryItemDescriptor baseType = getPromotions().getItemDescriptor(getBaseClosenessQualifierItemType());
    return (isItemOfType(pObj, baseType));
  }
  
  /**
   * Method to determine if a passed in repository item or item descriptor is of a base type.
   * <br>The passed in object can be of any super type of the base type.
   * 
   * @param pObj Object to check, RepositoryItem or RepositoryItemDescriptor
   * @param pBaseType RepositoryItemDescriptor for the base type
   * 
   * @return true if pObj is of a type which is a sub-type of the base type
   * @throws IllegalArgumentException if pBaseType is null or pObj is null or not a RepositoryItem or a RepositoryItemDescriptor
   * @throws RepositoryException if pObj is a RepositoryItem and it's type could not be determined
   */
  public boolean isItemOfType(Object pObj, RepositoryItemDescriptor pBaseType) 
  throws IllegalArgumentException, RepositoryException {
    boolean isItemOfType = false;
    RepositoryItemDescriptor itemType = null;
    
    if (pObj == null) {
      throw new IllegalArgumentException("pObj is null.");
    }
    
    if (pBaseType == null) {
      throw new IllegalArgumentException("pBaseType is null.");
    }
    
    if (pObj instanceof RepositoryItem) {
      itemType = ((RepositoryItem)pObj).getItemDescriptor();
    }
    else if (pObj instanceof RepositoryItemDescriptor){
      itemType = (RepositoryItemDescriptor)pObj;
    }
    else {
      throw new IllegalArgumentException("pObj is not a repository item descriptor or a repository item.");
    }
    
    if ((pBaseType != null) && (itemType != null)) {
      isItemOfType = pBaseType.areInstances(itemType);
    }
    
    return (isItemOfType);
  }
  
  /**
   * Return true if the promotion or the promotionStatus has expired and should not be used in
   * pricing.
   * @param pPromotion the promotion that should be verified
   * @param pNow the current date
   * @return true if the promotion has expired, false otherwise
   */
  public boolean checkPromotionExpiration(RepositoryItem pPromotion, Date pNow)
  {
    PerformanceMonitor.startOperation("PromotionTools_checkPromotionExpiration");
    try {
      if (pPromotion == null)
        return false;

      // Here we will check to see whether what we have is a promotion or a promotionStatus
      // repository item.  If it is a promotionStatus then we will assign it to a local
      // variable and the pPromotion that was passed in will point to the promotion object
      // contained within the promotionStatus.
      RepositoryItem promoStatus = null;

      try {
        if (pPromotion.getItemDescriptor().getItemDescriptorName().equals(getPromoStatusDescriptorName())) {
           promoStatus = pPromotion;
           pPromotion = (RepositoryItem) promoStatus.getPropertyValue(getPromoStatusPromoProperty());
        }
      }
      catch(RepositoryException re) {
        // This means that we couldn't get a hold of the item descriptor which is highly unlikely.
        if (isLoggingError())
          logError(re);
      }

      // this shouldn't happen, but we need to check anyway
      // NOTE - This used to be inside the try block, but that caused a
      // VerifyError with javac
      if(pPromotion == null) {
        // this should throw an exception but I don't want to introduce a migration problem
        if(isLoggingError())
          logError("The promotion status " + promoStatus + " contains a null promotion.");
        return false;
      }

      String enabledProperty = getPricingModelProperties().getEnabled();
      if (enabledProperty != null) {
        // if this promotion is no longer active remove it
        Boolean enabled = (Boolean)pPromotion.getPropertyValue(enabledProperty);
        if ((enabled != null) && (! enabled.booleanValue())) {
          return true;
        }
      }

      String endUsableProperty = getPricingModelProperties().getEndUsable();
      if (endUsableProperty != null) {
        // if this promotion has expired, remove the promotion from the list
        Date endUsable = (Date)pPromotion.getPropertyValue(endUsableProperty);

        // Bug 86595 - start fix
        // create a new date to store a normalized pNow
        
        Date now = pNow;
        if (pNow == null) {
          now = new Date();
        }

        // if endUsable is an instance of java.sql.Date normalize pNow
        if(endUsable instanceof java.sql.Date) {
          now = normalizeDate(now);
        }
        // Bug 86595 - end fix

        // the below has been changed from endUsable.before(pNow) to endUsable.before(mNow)
        // to accommodate Bug 86595
        if (endUsable != null && endUsable.before(now)) {
          return true;
        }
      }

      // If we had been given a promoStatus then we should check to see if it has a relative
      // expiration date.  If so, then we need to check to see what date this promotionStatus
      // is going to expire on.
      if (promoStatus != null) {
        String relativeExpirationProperty = getPricingModelProperties().getRelativeExpiration();
        if (relativeExpirationProperty != null) {
          Boolean relativeExpiration = (Boolean) pPromotion.getPropertyValue(relativeExpirationProperty);
          if ((relativeExpiration != null) && (relativeExpiration.booleanValue())) {
            // This promotion expires relative to when it was received.  Check the expiration
            // date in the promoStatus object.
            String expirationDateProperty = getPricingModelProperties().getPromoStatusExpirationDate();
            if (expirationDateProperty != null) {
              if (isLoggingDebug())
                logDebug("Expiration date property is consulted");
              Date expirationDate = (Date) promoStatus.getPropertyValue(expirationDateProperty);
              if (expirationDate != null && expirationDate.before(pNow)) {
                return true;
              }
            }
          }
        }
      }


      return false;
    }
    finally {
      try {
        PerformanceMonitor.endOperation("PromotionTools_checkPromotionExpiration");
      } catch (Exception e) {
        if (isLoggingError()) {
          logError(e);
        }
      }
    }
  }

  //-------------------------------------
  /**
   * Adds all of the promotions in the collection to the user's activePromotion attribute value.
   */
  public void addAllPromotions(MutableRepositoryItem pProfile, Collection pPromotions)
  {
    addAllPromotions(pProfile, pPromotions, null);
  }

  /**
   * Adds all of the promotions in the collection to the user's activePromotion attribute value.
   *
   * @param pProfile The profile being modified
   * @param pPromotion The promotion being added
   * @param pComment And extra string describe this event.
   */
  public void addAllPromotions(MutableRepositoryItem pProfile, Collection pPromotions, String pComment)
  {
    if (isLoggingDebug())
      logDebug("addAllPromotions: pProfile=" + pProfile + "; pPromotions=" + pPromotions);

    if ((pProfile == null) || (pPromotions == null)) {
      return;
    }

    Object statuses = pProfile.getPropertyValue(getActivePromotionsProperty());

    // remove any null statuses
    if (statuses instanceof Collection) {
      while (((Collection) statuses).contains(null)) {
        ((Collection)statuses).remove(null);
      }
    }

    if (pPromotions instanceof List) {
      List promotions = (List)pPromotions;
      int size = promotions.size();

      for (int c=0; c<size; c++) {
        Object value = promotions.get(c);
        addPromotion(pProfile, (RepositoryItem)value, pComment);
      }
    }
    else {
      Iterator iterator = pPromotions.iterator();
      while (iterator.hasNext()) {
        Object value = iterator.next();
        addPromotion(pProfile, (RepositoryItem)value, pComment);
      }
    }
  }

  //-------------------------------------
  /**
   * Adds the promotion to the user's activePromotion attribute value.
   *
   * @param pProfile The profile being modified
   * @param pPromotion The promotion being added
   * @return true if the promotion was added
   */
  public boolean addPromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion) {
    return addPromotion(pProfile, pPromotion, null);
  }
  
  /**
   * Adds the promotion to the user's activePromotion attribute value.
   *
   * @param pProfile The profile being modified
   * @param pPromotion The promotion being added
   * @param pComment And extra string describe this event.
   * @return true if the promotion was added
   */
  public boolean addPromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion, String pComment) {
    return addPromotion(pProfile, pPromotion, pComment, null);
  }

  /**
   * Adds the promotion to the user's activePromotion attribute value.
   *
   * @param pProfile The profile being modified
   * @param pPromotion The promotion being added
   * @param pComment And extra string describe this event.
   * @param pSiteId site with which the promotion is associated
   * @return true if the promotion was added
   */
  public boolean addPromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion, 
                              String pComment, String pSiteId) {

    try {
      if (pSiteId == null){
        // call grantPromotions() method signature without site ID parameter
        // for backwards compatibility
        grantPromotion(pProfile, pPromotion, pComment);
      }else{
        grantPromotion(pProfile, pPromotion, pComment, pSiteId);
      }
    }
    catch (PromotionException pe) {
      if(isLoggingDebug())
        logDebug(pe);
      return false;
    }

    return true;
  }

  /**
   * Adds the given promotion to the user's "activePromotions" list.
   *
   * @param pProfileId
   * @param pPromotionId
   * @throws PromotionException Thrown if the promotion can't be added for some reason
   *                             (e.g. the promotion is expired, the user already had the promotion)
   */
  public void grantPromotion(String pProfileId, String pPromotionId)
    throws PromotionException {
    grantPromotion(pProfileId, pPromotionId, null);
  }
  
  /**
   * Adds the given promotion to the user's "activePromotions" list.
   *
   * @param pProfileId The profile gaining the promotion
   * @param pPromotionId The promotion being added
   * @param pComment And extra string describe this event.
   * @throws PromotionException Thrown if the promotion can't be added for some reason
   *                             (e.g. the promotion is expired, the user already had the promotion)
   */
  public void grantPromotion(String pProfileId, String pPromotionId, String pComment)
    throws PromotionException {
    grantPromotion(pProfileId, pPromotionId, pComment, null);
  }

 /**
  * Adds the given promotion to the user's "activePromotions" list.
  *
  * @param pProfileId The profile gaining the promotion
  * @param pPromotionId The promotion being added
  * @param pComment And extra string describe this event.
  * @param pSiteId The site with which the promotion is associated
  * @throws PromotionException Thrown if the promotion can't be added for some reason
  *                             (e.g. the promotion is expired, the user already had the promotion)
  */
 public void grantPromotion(String pProfileId, String pPromotionId, String pComment, String pSiteId)
   throws PromotionException {

     RepositoryItem promotion = null;
     MutableRepositoryItem profile = null;
     try {
       promotion = getPromotions().getItem(pPromotionId, getBasePromotionItemType());
       profile = getProfileRepository().getItemForUpdate(pProfileId, getProfileItemType());
     }
     catch(RepositoryException re) {
       throw new PromotionException(re);
     }

     if (profile == null) {
       String[] msgArgs = { pProfileId };
       String msg = ResourceUtils.getMsgResource(PromotionConstants.INVALID_PROFILE,
                                                 MY_RESOURCE_NAME, sResourceBundle, msgArgs);
       throw new PromotionException(PromotionConstants.INVALID_PROFILE, msg);
     }

     if (promotion == null) {
       String[] msgArgs = { pPromotionId };
       String msg = ResourceUtils.getMsgResource(PromotionConstants.INVALID_PROMOTION,
                                                 MY_RESOURCE_NAME, sResourceBundle, msgArgs);
       throw new PromotionException(PromotionConstants.INVALID_PROMOTION, msg);
     }
     
     if (pSiteId == null){
       // call grantPromotions() method signature without site ID parameter
       // for backwards compatibility
       grantPromotion(profile, promotion, pComment);
     }else{
       grantPromotion(profile, promotion, pComment, pSiteId);       
     }     
 }

 /**
  * Adds the given promotion to the user's "activePromotions" list.
  *
  * @param pProfile
  * @param pPromotion
  * @throws PromotionException Thrown if the promotion can't be added for some reason
  *                             (e.g. the promotion is expired, the user already had the promotion)
  */
 public void grantPromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion)
    throws PromotionException
  {
    grantPromotion(pProfile, pPromotion, null);
  }
 
 /**
  * Adds the given promotion to the user's "activePromotions" list.
  *
  * @param pProfile The profile gaining the promotion
  * @param pPromotion The promotion being added
  * @param pComment And extra string describe this event.
  * @throws PromotionException Thrown if the promotion can't be added for some reason
  *                             (e.g. the promotion is expired, the user already had the promotion)
  */
 public void grantPromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion, String pComment)
    throws PromotionException
  {
   grantPromotion(pProfile, pPromotion, pComment, null);
  }

 /**
  * Adds the given promotion to the user's "activePromotions" list.
  *
  * @param pProfile The profile gaining the promotion
  * @param pPromotion The promotion being added
  * @param pComment And extra string describe this event.
  * @param pSiteId The site with which the promotion is associated
  * @throws PromotionException Thrown if the promotion can't be added for some reason
  *                             (e.g. the promotion is expired, the user already had the promotion)
  */
 public void grantPromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion, 
                            String pComment, String pSiteId)
    throws PromotionException
  {
   if (isLoggingDebug()){
     logDebug("addPromotion: pProfile=" + pProfile + "; pPromotion=" + pPromotion);
   }

    // Look at the activePromotions property by default 
    // Will throw exception or return true
    boolean isCheckGrant = checkPromotionGrant(pProfile, pPromotion, 
        new String[]{getActivePromotionsProperty()});
    
    if (!isCheckGrant)
      return;

    Integer uses = (Integer) pPromotion.getPropertyValue(getUsesProperty());
    try {
      RepositoryItem promotionStatus = createPromotionStatus(pProfile, pPromotion, uses);
      Collection promotionStatuses = (Collection)pProfile.getPropertyValue(getActivePromotionsProperty());

      if (promotionStatuses == null)
        promotionStatuses = new ArrayList();      
      
      promotionStatuses.add(promotionStatus);
      pProfile.setPropertyValue(getActivePromotionsProperty(), promotionStatuses);
      
      if (pSiteId != null){
        sendPromotionGrantedEvent(pProfile, pPromotion, pComment, pSiteId);
      }else{
        // call sendPromotionGrantedEvent() method signature without site ID parameter
        // for backwards compatibility
        sendPromotionGrantedEvent(pProfile, pPromotion, pComment);
      }      

      if (isLoggingDebug()){
        logDebug("promotion " + pPromotion + " given to user " + pProfile.getRepositoryId());
        logDebug("promotionStatus: " + promotionStatus + " added."); 
      }

      return;
    }
    catch (RepositoryException re) {
      if (isLoggingError())
        logError(re);
      String[] msgArgs = { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
      String msg = ResourceUtils.getMsgResource(PromotionConstants.REPOSITORY_EXCEPTION,
                                                MY_RESOURCE_NAME, sResourceBundle, msgArgs);
      throw new PromotionException(PromotionConstants.REPOSITORY_EXCEPTION, msg);
    }

  }

  /**
   * Will throw appropriate exception or return true if it's OK to grant the promotion to
   * the passed in profile
   * @param pProfile The profile gaining the promotion
   * @param pPromotion The promotion being added
   * @return true indicates that no checks that may have prevented the grant are applicable
   * @throws PromotionException
   */
  public boolean checkPromotionGrant(RepositoryItem pProfile,
      RepositoryItem pPromotion)
    throws PromotionException {
    return checkPromotionGrant(pProfile, pPromotion,
                               new String[]{getActivePromotionsProperty()});
  }

  /**
   * Will throw appropriate exception or return true if it's OK to grant the promotion to
   * the passed in profile
   * @param pProfile The profile gaining the promotion
   * @param pPromotion The promotion being added
   * @param pCheckProps Array of property names in which to look for granted promotions
   *                    in the profile
   * @return true indicates that no checks that may have prevented the grant are applicable
   * @throws PromotionException
   */
  public boolean checkPromotionGrant(RepositoryItem pProfile,
      RepositoryItem pPromotion, String[] pCheckProps)
    throws PromotionException {

    if (pProfile == null) {
      String msg = ResourceUtils.getMsgResource(PromotionConstants.NULL_PROFILE,
                                                MY_RESOURCE_NAME, sResourceBundle);
      throw new PromotionException(PromotionConstants.NULL_PROFILE, msg);
    }

    if (pPromotion == null) {
      String msg = ResourceUtils.getMsgResource(PromotionConstants.NULL_PROMOTION,
                                                MY_RESOURCE_NAME, sResourceBundle);
      throw new PromotionException(PromotionConstants.NULL_PROMOTION, msg);
    }

    boolean allowMultiple = false;
    try {
      if (checkPromotionExpiration(pPromotion, new Date())) {
        String[] msgArgs = { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
        String msg = ResourceUtils.getMsgResource(PromotionConstants.EXPIRED_PROMOTION,
                                                  MY_RESOURCE_NAME, sResourceBundle, msgArgs);
        throw new PromotionException(PromotionConstants.EXPIRED_PROMOTION_USER_RESOURCE_KEY, msg);
      }

      // Check to see if the promotion can be given to anonymous users
      if (pProfile.isTransient()) {
        if (mGiveToAnonymousProfilesProperty != null) {
          Boolean giveToAnonymous = (Boolean) pPromotion.getPropertyValue(mGiveToAnonymousProfilesProperty);
          if (giveToAnonymous != null && (!giveToAnonymous.booleanValue())) {
            String[] msgArgs = { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
            String msg = ResourceUtils.getMsgResource(PromotionConstants.ANONYMOUS_PROFILE,
                                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs);
            throw new PromotionException(PromotionConstants.ANONYMOUS_PROFILE_USER_RESOURCE_KEY, msg);
          }
        }
      }

      allowMultiple = ((Boolean) pPromotion.getPropertyValue(getAllowMultipleProperty())).booleanValue();
    }
    catch (RemovedItemException rie) {
      if(isLoggingWarning()) {
        Object[] params =
          new Object[] { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
        logWarning(MessageFormat.format(sResourceBundle.getString("promotionRemoved"), params));
      }
      String[] msgArgs = { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
      String msg = ResourceUtils.getMsgResource(PromotionConstants.PROMOTION_REMOVED,
                                                MY_RESOURCE_NAME, sResourceBundle, msgArgs);
      throw new PromotionException(PromotionConstants.PROMOTION_REMOVED_USER_RESOURCE_KEY, msg);
    }

    if (!allowMultiple) {

      // We need to make sure that it hasn't been given either to the active promotions (or 
      // other promotions property on the profile, as in promotions wallet) or have
      // been used already.

      Collection promotionStatuses = new ArrayList();
      for (int i = 0; i < pCheckProps.length; i++) {
        Object value = pProfile.getPropertyValue(pCheckProps[i]);
        if (value instanceof Collection) {
          promotionStatuses.addAll((Collection)value);
        }
      }

      if (isLoggingDebug())
        logDebug("existing promotions for " + pProfile.getRepositoryId() + "=" + promotionStatuses);

      boolean activePromotion = isPromotionInPromotionStatuses(pPromotion, promotionStatuses);

      if (activePromotion) {
        if (isLoggingDebug())
          logDebug("promotion " + pPromotion + " rejected because user " + pProfile.getRepositoryId() +
                   " has it in the active promotions list");
        String[] msgArgs = { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
        String msg = ResourceUtils.getMsgResource(PromotionConstants.DUPLICATE_PROMOTION,
                                                  MY_RESOURCE_NAME, sResourceBundle, msgArgs);
        throw new DuplicatePromotionException(PromotionConstants.DUPLICATE_PROMOTION_USER_RESOURCE_KEY, msg);
      }

      Collection usedPromotions = (Collection) pProfile.getPropertyValue(getUsedPromotionsProperty());

      Iterator usedPromoIterator = usedPromotions.iterator();
      while (usedPromoIterator.hasNext()) {
        RepositoryItem promotion = (RepositoryItem) usedPromoIterator.next();

        if (promotion != null && promotion.getRepositoryId().equals(pPromotion.getRepositoryId())) {
          if (isLoggingDebug())
            logDebug("promotion " + pPromotion + " rejected because user has it in used promotion list ");
          String[] msgArgs = { pPromotion.getRepositoryId(), pProfile.getRepositoryId() };
          String msg = ResourceUtils.getMsgResource(PromotionConstants.USED_PROMOTION,
                                                    MY_RESOURCE_NAME, sResourceBundle, msgArgs);
          throw new PromotionException(PromotionConstants.USED_PROMOTION_USER_RESOURCE_KEY, msg);
        }
      } // while usedPromoIterator.hasNext
    } // Allow Multiple

    return true;
  }

  //-------------------------------------
  /**
   * Sends an PromotionGranted Event through the PromotionGrantedMessageSender
   * if FirePromotionGrantedEvent is set to true.
   * @param pProfile the user's profile to record with the event
   * @param pPromotion pPromotion the promotion to record with the event
   */
  public void sendPromotionGrantedEvent(MutableRepositoryItem pProfile, RepositoryItem pPromotion) {
    sendPromotionGrantedEvent(pProfile, pPromotion, null);
  }

  /**
   * Sends an PromotionGranted Event through the PromotionGrantedMessageSender
   * if FirePromotionGrantedEvent is set to true.
   * @param pProfile the user's profile to record with the event
   * @param pPromotion pPromotion the promotion to record with the event
   * @param pComment An extra comment describing the eveng
   */
  public void sendPromotionGrantedEvent(MutableRepositoryItem pProfile, RepositoryItem pPromotion, String pComment)  {
    sendPromotionGrantedEvent(pProfile, pPromotion, pComment, null); 
  }
  
  /**
   * Sends an PromotionGranted Event through the PromotionGrantedMessageSender
   * if FirePromotionGrantedEvent is set to true.
   * @param pProfile the user's profile to record with the event
   * @param pPromotion pPromotion the promotion to record with the event
   * @param pComment An extra comment describing the event
   * @param pSiteId site id associated with the event 
   */
  public void sendPromotionGrantedEvent(MutableRepositoryItem pProfile, RepositoryItem pPromotion, 
                                        String pComment, String pSiteId)  {

      if (isLoggingDebug())
        logDebug("firing PromotionGranted Event with messageSender " + mPromotionGrantedMessageSender );

      // Let's just take care up front of conditions that would prohibit this event being fired - and report them.

      if (pPromotion==null) {
        if (isLoggingDebug())
          logDebug("PromotionGranted Event cannot be fired without an associated promotion");
        return;
      }

      if (isFirePromotionGrantedEvent()!=true) {
        if (isLoggingDebug())
          logDebug("PromotionGranted Event will not be fired - firePromotionGrantedEvent set to false");
        return;
      }

      if (mPromotionGrantedMessageSender!=null) {
        PromotionGrantedMessage message = new PromotionGrantedMessage();
        message.setProfile(pProfile);
        message.setPromotionId(pPromotion.getRepositoryId());
        if(pComment != null)
          message.setComment(pComment);
        
        if (pSiteId != null){
          message.setSiteId(pSiteId);          
        }else{
          //no site id passed, 
          //so obtaining a current site id from SiteContextManager
          message.setSiteId(SiteContextManager.getCurrentSiteId());
          
          if (isLoggingDebug()){
            logDebug("Setting siteId of PromotionGrantedEvent to current context site id: "+SiteContextManager.getCurrentSiteId());
          }
        }

        try {
          mPromotionGrantedMessageSender.sendCommerceMessage(message);
          if (isLoggingDebug())
            logDebug("PromotionGrantedMessage has been sent.");
          } catch (Exception e) {
            if (isLoggingError())
              logError(e);
          }
      }
  }

  //-------------------------------------
  /**
   * Sends a PromotionRevoked Event through the PromotionRevokedMessageSender
   * if FirePromotionRevokedEvent is set to true.
   * @param pProfile the user's profile to record with the event
   * @param pPromotion pPromotion the promotion to record with the event
   */
  public void sendPromotionRevokedEvent(RepositoryItem pProfile, RepositoryItem pPromotion) {
    sendPromotionRevokedEvent(pProfile, pPromotion, null);
  }

  /**
   * Sends a PromotionRevoked Event through the PromotionRevokedMessageSender
   * if FirePromotionRevokedEvent is set to true.
   * @param pProfile the user's profile to record with the event
   * @param pPromotion pPromotion the promotion to record with the event
   * @param pComment An extra comment describing the event
   */
  public void sendPromotionRevokedEvent(RepositoryItem pProfile, RepositoryItem pPromotion, String pComment) {
    sendPromotionRevokedEvent(pProfile, pPromotion, pComment, null);
  }
  
  /**
   * Sends a PromotionRevoked Event through the PromotionRevokedMessageSender
   * if FirePromotionRevokedEvent is set to true.
   * @param pProfile the user's profile to record with the event
   * @param pPromotion pPromotion the promotion to record with the event
   * @param pComment An extra comment describing the event
   * @param pSiteId site id associated with the event
   */
  public void sendPromotionRevokedEvent(RepositoryItem pProfile, RepositoryItem pPromotion, 
                                        String pComment, String pSiteId) {

      if (isLoggingDebug())
        logDebug("firing PromotionRevoked Event with messageSender " + mPromotionRevokedMessageSender );

      // Let's just take care up front of conditions that would prohibit this event being fired - and report them.

      if (pPromotion==null) {
        if (isLoggingDebug())
          logDebug("PromotionRevoked Event cannot be fired without an associated promotion");
        return;
      }

      if (isFirePromotionRevokedEvent()!=true) {
        if (isLoggingDebug())
          logDebug("PromotionRevoked Event will not be fired - firePromotionRevokedEvent set to false");
        return;
      }

      if (mPromotionRevokedMessageSender!=null) {
        PromotionRevokedMessage message = new PromotionRevokedMessage();
        message.setProfile(pProfile);
        message.setPromotionId(pPromotion.getRepositoryId());
        if(pComment != null)
          message.setComment(pComment);
        
        if (pSiteId != null){
          message.setSiteId(pSiteId);          
        }else{
          //no site id passed, 
          //so obtaining a current site id from SiteContextManager
          message.setSiteId(SiteContextManager.getCurrentSiteId());
          
          if (isLoggingDebug()){
            logDebug("Setting siteId of PromotionRevokedEvent to current context site id: "+SiteContextManager.getCurrentSiteId());
          }
        }

        try {
          mPromotionRevokedMessageSender.sendCommerceMessage(message);
          if (isLoggingDebug())
            logDebug("PromotionRevokedMessage has been sent.");
          } catch (Exception e) {
            if (isLoggingError())
              logError(e);
        }
      }
  }

  /**
   * This method takes a promotion and a collection of promotion statuses, and returns a
   * boolean indicating if the given promotion is represented in the status collection.
   * This method is called when granting a promotion to determine if the promotion already
   * exists among the user's active promotions
   */
  public boolean isPromotionInPromotionStatuses(RepositoryItem pPromotion,
                                                Collection pPromotionStatuses) {

    Iterator statusIterator = pPromotionStatuses.iterator();

    while (statusIterator.hasNext()) {
      // Get the promotionStatus object
      RepositoryItem promotionStatus = (RepositoryItem) statusIterator.next();

      if (promotionStatus != null) {
        // Get the promotion that the promotion Status is referring to.
        RepositoryItem promotionStatusPromotion =
          (RepositoryItem) promotionStatus.getPropertyValue(getPromoStatusPromoProperty());

        if (promotionStatusPromotion != null &&
            promotionStatusPromotion.getRepositoryId().equals(pPromotion.getRepositoryId()))
          return true;
      }
    } // end for each status

    return false;
  }

  //-------------------------------------
  /**
   * This method moves a promotion from the user's activePromotions list to their usedPromotions
   * list. It does this only for promotions marked as "use once".
   * @param pProfile the user's profile
   * @param pPromotion the promotion to consume
   * @return true if the promotion was moved, false if the promotion is not found.
   */
  public boolean consumePromotion(MutableRepositoryItem pProfile, RepositoryItem pPromotion)
  {
    if (isLoggingDebug())
      logDebug("consumePromotion: pProfile=" + pProfile + "; pPromotion=" + pPromotion);

    if ((pProfile == null) || (pPromotion == null)) {
      return false;
    }

    List activePromotions = (List) pProfile.getPropertyValue(getActivePromotionsProperty());
    Set usedPromotions = (Set) pProfile.getPropertyValue(getUsedPromotionsProperty());


    String promoId = pPromotion.getRepositoryId();
    boolean found = false;
    RepositoryItem promo = null;
    MutableRepositoryItem promoStatus = null;

    Iterator iter = activePromotions.iterator();
    while (iter.hasNext()) {
      promoStatus = (MutableRepositoryItem) iter.next();
      if (promoStatus != null) {
        promo = (RepositoryItem) promoStatus.getPropertyValue(getPromoStatusPromoProperty());
        if (promo != null && promoId.equals(promo.getRepositoryId())) {
          found = true;
          break;
        }
      }

    }

    if (! found)
      return false;

    Integer value = (Integer) promoStatus.getPropertyValue(getPromoStatusNumUsesProperty());
    int uses = (value != null ? value.intValue() : 1);

    if (uses > 0) {
      // If there is only one use then we do exactly what we did before.
      if (uses == 1) {
        if (isLoggingDebug())
          logDebug("Only one use remaining, remove the promoStatus object");

        activePromotions.remove(promoStatus);
        usedPromotions.add(promo);
      }
      else {
        if (isLoggingDebug())
          logDebug("Decrement the number of uses to: " + Integer.toString(uses - 1));
        // Otherwise decrement the number used by one.
        promoStatus.setPropertyValue(getPromoStatusNumUsesProperty(), Integer.valueOf(uses - 1));
      }
    }
    else if (uses < 0) {
      // this means this is an infinite use promotion.
      // no need to do anything
    }
    else {
      return false;
    }

    return true;
  }

  //-------------------------------------
  /**
   * Removes the passed in promotion pPromotion from the pProfile's list of active promotions.
   * If the pRemoveAll flag is set then all instances of this promotion are removed, otherwise
   * the first one of it's kind is removed.
   *
   * @param pProfile - the profile of the user from whom the promotion will be taken out
   * @param pPromotion - the promotion to be removed
   * @param pRemoveAll - true if all instances of this promotion should be removed, false otherwise
   * @return true if one or more instances of the promotion was removed from the list of active
   * promotions.
   **/
  public boolean removePromotion(MutableRepositoryItem pProfile,
                                 RepositoryItem pPromotion,
                                 boolean pRemoveAll)
  {
      boolean result = false;
      try {
        result = revokePromotion(pProfile, pPromotion, pRemoveAll);
      }
      catch (PromotionException pe) {
        if(isLoggingDebug())
          logDebug(pe);
        return false;
      }

      return result;
  }

  /**
   * Removes the passed in promotion pPromotion from the pProfile's list of active promotions.
   * If the pRemoveAll flag is set then all instances of this promotion are removed, otherwise
   * the first one of it's kind is removed.
   *
   * @param pProfileId - the profile of the user from whom the promotion will be taken out
   * @param pPromotionId - the promotion to be removed
   * @param pRemoveAll - true if all instances of this promotion should be removed, false otherwise
   * @return true if the promotion was removed, false if nothing happened
   * @exception PromotionException if the promotion could not be removed.
   */
  public boolean revokePromotion(String pProfileId, String pPromotionId, boolean pRemoveAllInstances)
    throws PromotionException {

    MutableRepositoryItem profile = null;
    try {
      profile = getProfileRepository().getItemForUpdate(pProfileId, getProfileItemType());
    }
    catch(RepositoryException re) {
      throw new PromotionException(re);
    }

    RepositoryItem promotion = null;
    try {
      promotion = getPromotions().getItem(pPromotionId, getBasePromotionItemType());
    }
    catch(RepositoryException re) {
      throw new PromotionException(re);
    }

    if (profile == null) {
      String[] msgArgs = { pProfileId };
      String msg = ResourceUtils.getMsgResource(PromotionConstants.INVALID_PROFILE,
                                                MY_RESOURCE_NAME, sResourceBundle, msgArgs);
      throw new PromotionException(PromotionConstants.INVALID_PROFILE, msg);
    }

    if (promotion == null) {
      String[] msgArgs = { pPromotionId };
      String msg = ResourceUtils.getMsgResource(PromotionConstants.INVALID_PROMOTION,
                                                MY_RESOURCE_NAME, sResourceBundle, msgArgs);
      throw new PromotionException(PromotionConstants.INVALID_PROMOTION, msg);
    }

    return revokePromotion(profile, promotion, pRemoveAllInstances);
  }

  /**
   * Removes the passed in promotion pPromotion from the pProfile's list of active promotions.
   * If the pRemoveAll flag is set then all instances of this promotion are removed, otherwise
   * the first one of it's kind is removed.
   *
   * @param pProfile - the profile of the user from whom the promotion will be taken out
   * @param pPromotion - the promotion to be removed
   * @param pRemoveAll - true if all instances of this promotion should be removed, false otherwise
   * @return true if the promotion was removed, false if nothing happened
   * @exception PromotionException if the promotion could not be removed.
   */
  public boolean revokePromotion(MutableRepositoryItem pProfile,
                                 RepositoryItem pPromotion,
                                 boolean pRemoveAll)
    throws PromotionException
  {
    if (isLoggingDebug())
      logDebug("removePromotion: pProfile=" + pProfile + "; pPromotion=" + pPromotion);

    if (pProfile == null) {
      String msg = ResourceUtils.getMsgResource(PromotionConstants.NULL_PROFILE,
                                                MY_RESOURCE_NAME, sResourceBundle);
      throw new PromotionException(PromotionConstants.NULL_PROFILE, msg);
    }
    if (pPromotion == null) {
      String msg = ResourceUtils.getMsgResource(PromotionConstants.NULL_PROMOTION,
                                                MY_RESOURCE_NAME, sResourceBundle);
      throw new PromotionException(PromotionConstants.NULL_PROMOTION, msg);
    }

    boolean result = false;

    Object property = pProfile.getPropertyValue(getActivePromotionsProperty());

    if (property != null && property instanceof List) {
      List promotions = (List) property;
      ListIterator it = promotions.listIterator();

      MutableRepositoryItem promoStatus = null;
      RepositoryItem promo = null;

      while (it.hasNext()) {
        promoStatus = (MutableRepositoryItem) it.next();
        if(promoStatus == null) {
          String [] msgArgs = { pProfile.getRepositoryId() };
          if(isLoggingError())
            logError(ResourceUtils.getMsgResource(PromotionConstants.NULL_ACTIVE_PROMOTION,
                                                  MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          continue;
        }
        promo = (RepositoryItem) promoStatus.getPropertyValue(getPromoStatusPromoProperty());

        if (promo == null) {
          // if there is no promotion, just take it out of the list
          it.remove();
          continue;
        }

        if (promo.getRepositoryId().equals(pPromotion.getRepositoryId())) {
          // remove this promotion
          it.remove();

          // the operation is successful if we find something to remove
          result = true;

          if (!pRemoveAll)
            break;
        }
      }

      if (result) {
        pProfile.setPropertyValue(getActivePromotionsProperty(), promotions);
        sendPromotionRevokedEvent(pProfile, pPromotion);
      }
      else {
        if (isLoggingDebug())
          logDebug("Promotion was not found in the " + getActivePromotionsProperty() + " of the profile");
      }
    }
    else if (property == null) {
      if (isLoggingDebug())
        logDebug("Promotion not removed because the profile's promotions property is null");
    }
    else {
      if (isLoggingError())
        logError("The " + getActivePromotionsProperty() + " property of the profile is not null and is not a List");
    }

    return result;
  }

  /**
   * Returns the promotions used in the order.
   * @param pOrder the order that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   */
  public void getOrderPromotions(Order pOrder, Collection pPromotions)
  {
    getOrderPromotions(pOrder, pPromotions, false);
  }

  /**
   * Returns the promotions used in the tax calculation.
   *
   * @param pOrder the order that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   */
  public void getTaxPromotions(Order pOrder, Collection pPromotions)
  {
    getTaxPromotions(pOrder, pPromotions, false);
  }

  /**
   * Returns the promotions used in the CommerceItem.
   * @param pItem the CommerceItem that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   */
  public void getItemPromotions(CommerceItem pItem, Collection pPromotions)
  {
    getItemPromotions(pItem, pPromotions, false);
  }

  /**
   * Returns the promotions used in the ShippingGroup.
   *
   * @param pGroup the ShippingGroup that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   */
  public void getShippingPromotions(ShippingGroup pGroup, Collection pPromotions)
  {
    getShippingPromotions(pGroup, pPromotions, false);
  }

  /**
   * This method retrieves all the promotions in an order and adds them to their respective
   * collection.
   *
   * @param pOrder the order to retrieve the promotions from
   * @param pOrderPromotions the collection to add the order level promotions to
   * @param pTaxPromotions the collection to add the tax level promotions to
   * @param pItemPromotions the collection to add the item level promotions to
   * @param pShippingPromotions the collection to add the shipping level promotions to
   */
  public void getOrderPromotions(Order pOrder, Collection pOrderPromotions,
         Collection pTaxPromotions, Collection pItemPromotions,
         Collection pShippingPromotions)
  {
    getOrderPromotions(pOrder, pOrderPromotions, pTaxPromotions, pItemPromotions, pShippingPromotions, false);
  }

  /**
   * Returns the promotions used in the order.  If <code>pGetAdjustments</code> is
   * true then <code>pPromotions</code> is populated with each PricingAdjustment associated
   * with a promotion.  If it is false, then <code>pPromotions</code> is populated
   * with the actual pricing model repository items.
   *
   * @param pOrder the order that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   * @param pGetAdjustments If true, get the PricingAdjustments, if false get the promotions
   */
  public void getOrderPromotions(Order pOrder, Collection pPromotions, boolean pGetAdjustments)
  {
    if (pOrder.getPriceInfo() == null)
      return;

    Iterator iter = pOrder.getPriceInfo().getAdjustments().iterator();
    PricingAdjustment adj;

    while (iter.hasNext()) {
      adj = (PricingAdjustment) iter.next();
      if (adj.getPricingModel() != null)
        addPromotionToCollection(pPromotions, adj, pGetAdjustments);
    }
  }

  /**
   * Returns the promotions used in the tax calculation.  If <code>pGetAdjustments</code> is
   * true then <code>pPromotions</code> is populated with each PricingAdjustment associated
   * with a promotion.  If it is false, then <code>pPromotions</code> is populated
   * with the actual pricing model repository items.
   *
   * @param pOrder the order that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   * @param pGetAdjustments If true, get the PricingAdjustments, if false get the promotions
   */
  public void getTaxPromotions(Order pOrder, Collection pPromotions, boolean pGetAdjustments)
  {
    if (pOrder.getTaxPriceInfo() == null)
      return;

    Iterator iter = pOrder.getTaxPriceInfo().getAdjustments().iterator();
    PricingAdjustment adj;

    while (iter.hasNext()) {
      adj = (PricingAdjustment) iter.next();
      if (adj.getPricingModel() != null)
        addPromotionToCollection(pPromotions, adj, pGetAdjustments);
    }
  }

  /**
   * Returns the promotions used in the CommerceItem.  If <code>pGetAdjustments</code> is
   * true then <code>pPromotions</code> is populated with each PricingAdjustment associated
   * with a promotion.  If it is false, then <code>pPromotions</code> is populated
   * with the actual pricing model repository items.
   *
   * @param pItem the CommerceItem that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   * @param pGetAdjustments If true, get the PricingAdjustments, if false get the promotions
   */
  public void getItemPromotions(CommerceItem pItem, Collection pPromotions, boolean pGetAdjustments)
  {
    if (pItem.getPriceInfo() == null)
      return;

    Iterator iter = pItem.getPriceInfo().getAdjustments().iterator();
    PricingAdjustment adj;

    while (iter.hasNext()) {
      adj = (PricingAdjustment) iter.next();
      if (adj.getPricingModel() != null)
        addPromotionToCollection(pPromotions, adj, pGetAdjustments);
    }
  }

  /**
   * Returns the promotions used in the ShippingGroup.  If <code>pGetAdjustments</code> is
   * true then <code>pPromotions</code> is populated with each PricingAdjustment associated
   * with a promotion.  If it is false, then <code>pPromotions</code> is populated
   * with the actual pricing model repository items.
   *
   * @param pGroup the ShippingGroup that the promotions are in
   * @param pPromotions a collection that the promotions are to be added to
   * @param pGetAdjustments If true, get the PricingAdjustments, if false get the promotions
   */
  public void getShippingPromotions(ShippingGroup pGroup, Collection pPromotions, boolean pGetAdjustments)
  {
    if (pGroup.getPriceInfo() == null)
      return;

    Iterator iter = pGroup.getPriceInfo().getAdjustments().iterator();
    PricingAdjustment adj;

    while (iter.hasNext()) {
      adj = (PricingAdjustment) iter.next();
      if (adj.getPricingModel() != null)
        addPromotionToCollection(pPromotions, adj, pGetAdjustments);
    }
  }

  /**
   * This method retrieves all the promotions in an order and adds them to their respective
   * collection.  If <code>pGetAdjustments</code> is true then each promotion collection is
   * populated with each PricingAdjustment associated with a promotion.  If it is false, then
   * each promotion collection is populated with the actual pricing model repository items.
   *
   * @param pOrder the order to retrieve the promotions from
   * @param pOrderPromotions the collection to add the order level promotions to
   * @param pTaxPromotions the collection to add the tax level promotions to
   * @param pItemPromotions the collection to add the item level promotions to
   * @param pShippingPromotions the collection to add the shipping level promotions to
   * @param pGetAdjustments If true, get the PricingAdjustments, if false get the promotions
   */
  public void getOrderPromotions(Order pOrder, Collection pOrderPromotions,
         Collection pTaxPromotions, Collection pItemPromotions,
         Collection pShippingPromotions, boolean pGetAdjustments)
  {
    getOrderPromotions(pOrder, pOrderPromotions, pGetAdjustments);

    getTaxPromotions(pOrder, pTaxPromotions, pGetAdjustments);

    Iterator iter = pOrder.getCommerceItems().iterator();
    while (iter.hasNext()) {
      CommerceItem item = (CommerceItem) iter.next();
      getItemPromotions(item, pItemPromotions, pGetAdjustments);
    }

    // old way
    //Iterator iter = pOrder.getShippingGroups().iterator();
    //while (iter.hasNext()) {
    //  ShippingGroup group = (ShippingGroup) iter.next();
    //  getShippingPromotions(group, pShippingPromotions);
    //}

    List thisGroup = new ArrayList();
    HashMap groupCounts = new HashMap();
    HashMap totalGroupCounts = new HashMap();
    iter = pOrder.getShippingGroups().iterator();
    while (iter.hasNext()) {
      ShippingGroup group = (ShippingGroup) iter.next();
      getShippingPromotions(group, thisGroup, pGetAdjustments);
      Iterator groupPromoIter = thisGroup.iterator();
      while(groupPromoIter.hasNext()) {
        Object objToAdd = groupPromoIter.next();
        Object promo = objToAdd;
        if(objToAdd instanceof PricingAdjustment)
          promo = ((PricingAdjustment) objToAdd).getPricingModel();
        // how many of these does this group have so far
        int groupCount = 1;
        if (groupCounts.containsKey(promo)) {
          groupCount = 1 + ((Integer)groupCounts.get(promo)).intValue();
        }
        groupCounts.put(promo, Integer.valueOf(groupCount));

        int totalCount = 0;
        // how many have been saved so far
        if (totalGroupCounts.containsKey(promo)) {
          totalCount = ((Integer)totalGroupCounts.get(promo)).intValue();
        } // end of if ()

        // if we've seen more, add to the list
        if (groupCount > totalCount) {
          totalGroupCounts.put(promo, Integer.valueOf(totalCount + 1));
          // at this point "objToAdd" might be an adjustment or it might be a promotion
          pShippingPromotions.add(objToAdd);
        } // end of if ()
      }
      thisGroup.clear();
      groupCounts.clear();
    }
  }

  /**
   * If <code>pGetAdjustment</code> is true, then <code>pAdjustment</code> is added to <code>pPromotions</code>.
   * If it is false, then <code>pAdjusmtent.pricingModel</code> is added instead.
   * This method does no input validation and assumes none of the parameters are null.
   *
   * @param pPromotions The collection being modified
   * @param pAdjustment The adjustment containing the promotion being added
   * @param pGetAdjustment If true, add the adjustment, if false, add the pricing model
   */
  private void addPromotionToCollection(Collection pPromotions, PricingAdjustment pAdjustment, boolean pGetAdjustment)
  {
    if(pGetAdjustment)
      pPromotions.add(pAdjustment);
    else
      pPromotions.add(pAdjustment.getPricingModel());
  }

  //-------------------------------------
  /**
   * This method will remove the passed in promotion from the Profile.
   * @param pPromotion the promotion which has expired
   * @param pProfile the profile for which a promotion has expired
   **/
  public void expirePromotion(RepositoryItem pPromotion, RepositoryItem pProfile)
  {
    if(pProfile == null) {
      if(isLoggingDebug())
        logDebug("Profile is null so there is no need to expire the promotion: " + pPromotion);
      return;
    }
    // Get all the active promotions.
    List activePromotions=null;
    try 
    {
      activePromotions = (List)DynamicBeans.getSubPropertyValue(pProfile, getActivePromotionsProperty());
    }
    catch(PropertyNotFoundException pnfe) 
    {
      if(isLoggingDebug())
        logDebug("expirePromotion: " + getActivePromotionsProperty() + " not found in " + pProfile);
    }
    
    boolean found = false;

    if (activePromotions != null) {
      ListIterator i = activePromotions.listIterator();
      RepositoryItem currentPromotionStatus = null;
      RepositoryItem currentPromotion = null;

      // Cycle through all the promotions until we find the one that we are interested in.
      while (i.hasNext() && !found) {
        currentPromotionStatus = (RepositoryItem) i.next();
        if(currentPromotionStatus == null) {
          if(isLoggingError())
            logError("The profile " + pProfile + " contains a null active promotion");
          continue;
        }
        currentPromotion = (RepositoryItem) currentPromotionStatus.getPropertyValue(getPromoStatusPromoProperty());

        if(currentPromotion == null) {
          if(isLoggingError())
            logError("The promotion status " + currentPromotionStatus +
                     " in profile " + pProfile + " contains a null promotion");
          continue;
        }
        if ((currentPromotionStatus.getRepositoryId().equals(pPromotion.getRepositoryId())) ||
            (currentPromotion.getRepositoryId().equals(pPromotion.getRepositoryId()))) {
          // Once we find the promotion we want to remove it.
          found = true;
          i.remove();
        }
      }
    }

  } // end expirePromotion

  public Collection convertPromoStatusToPromo(Collection pPromoStatuses) {

    if (isLoggingDebug()) {
      logDebug("statuses in: " + pPromoStatuses);
    }

    List promotions = null;

    if (pPromoStatuses != null && !pPromoStatuses.isEmpty()) {
      promotions = new ArrayList(pPromoStatuses.size());
      Iterator statusIterator = pPromoStatuses.iterator();
      RepositoryItem status = null;
      while (statusIterator.hasNext()) {
        status = (RepositoryItem) statusIterator.next();
        if (status != null) {
          promotions.add(status.getPropertyValue(getPromoStatusPromoProperty()));
        }
      }
    }
    if (isLoggingDebug()) {
      logDebug("promotions out: " + promotions);
    }

    return promotions;
  } // end convertPromoStatusToPromo

  /**
   * This method is called by the AddItemToOrder scenario action.  It will create
   * a ScenarioAddedItemToOrder scenario event and send it.
   * If <code>sendEventOnAddItem</code> is false, then nothing happens and
   * true is returned.
   *
   * @param pOrder The order that was modified
   * @param pItem The item that was added
   * @param pQuantity The quantity of pItem added to pOrder
   * @return false if there were errors, true otherwise
   */
  public boolean scenarioAddedItemToOrder(Order pOrder, CommerceItem pItem, long pQuantity)
  {
    return scenarioAddedItemToOrder(pOrder, pItem, pQuantity, null);
  }
  
  /**
   * This method is called by the AddItemToOrder scenario action.  It will create
   * a ScenarioAddedItemToOrder scenario event and send it.
   * If <code>sendEventOnAddItem</code> is false, then nothing happens and
   * true is returned.
   *
   * @param pOrder The order that was modified
   * @param pItem The item that was added
   * @param pQuantity The quantity of pItem added to pOrder
   * @param pSiteId The site ID associated with a scenario event
   * @return false if there were errors, true otherwise
   */
  public boolean scenarioAddedItemToOrder(Order pOrder, CommerceItem pItem, long pQuantity, String pSiteId)
  {
    if(!isSendEventOnAddItem()) {
      if(isLoggingDebug())
        logDebug("Scenario added item to order, but we aren't sending scenario events.");
      return true;
    }

    if(isLoggingDebug())
      logDebug("Scenario added " + pItem + " to " + pOrder + ", sending ScenarioAddedItemToOrder event");

    // Send a scenario event on a successfully added item.
    PipelineResult result;
    HashMap params = new HashMap();

    params.put(PipelineConstants.ORDER, pOrder);
    params.put(PipelineConstants.COMMERCEITEM, pItem);
    params.put(PipelineConstants.EVENT, ScenarioAddedItemToOrder.TYPE);
    params.put(PipelineConstants.QUANTITY, Long.valueOf(pQuantity));
    if (pSiteId !=null){
      params.put(PipelineConstants.SITEID, pSiteId);
    }

    try {
      result = getPipelineManager().runProcess(getAddItemEventPipeline(), params);
    } catch(RunProcessException rpe) {
      if(isLoggingError())
        logError(rpe);
      return false;
    }

    return PipelineUtils.processPipelineErrors(result, this, null);
  }

    /**
     * Gets the profile repository item with the given id
     *
     * @param pProfileId the id of the profile to retrieve
     * @return the profie repository item
     * @exception RepositoryException if there was an error while retrieving the profile
     */
    public RepositoryItem getProfile(String pProfileId) throws RepositoryException
    {
        if (isLoggingDebug())
          logDebug("Searching for profile with id=" + pProfileId);

        RepositoryItem profile = null;
        if (pProfileId != null) {
          profile = getProfileRepository().getItem(pProfileId, getProfileItemType());
          if (isLoggingDebug())
            logDebug("Profile found: " + profile + " from id=" + pProfileId);
        }

        return profile;
    }

    /**
     * This method will look up the given profile, and for each available promotion,
     * will return an xml representation of it using the GetService
     * and the <code>mappingFileName</code>.  Both active promotions in the profile
     * and global promotions are returned.
     *
     * @param pProfileId The id of the user whose promotions are returned
     * @return An XML representation of each active promotion in the profile
     * @throws PromotionException
     */
    public String[] getPromotionsAsXML(String pProfileId) throws PromotionException
    {
      Collection promotions = getPromotions(pProfileId);
      String[] promotionsAsXML = new String[promotions.size()];

      Iterator promotionsIter = promotions.iterator();
      for(int i=0; promotionsIter.hasNext(); i++) {
        RepositoryItem promotion = (RepositoryItem) promotionsIter.next();

        if(isLoggingDebug())
          logDebug("Getting promotion " + promotion.getRepositoryId() +
                   " as an xml document using " + getMappingFileName());

        try {
          promotionsAsXML[i] = getGetService().getItemAsXML(promotion, getMappingFileName());
        }
        catch(GetException ge) {
          throw new PromotionException(ge);
        }
      }

      return promotionsAsXML;
    }

   /**
    * This method will look up the given profile and return all of the available
    * promotions for that user.  This will include active promotions in the profile,
    * as well as global promotions.
    *
    * @param pProfileId The id of the user whose promotions are returned
    * @return A collection of promotion items
    * @throws PromotionException
    */
    public Collection getPromotions(String pProfileId)
      throws PromotionException
    {

      RepositoryItem profile = null;
      try {
        profile = getProfile(pProfileId);
      }
      catch(RepositoryException re) {
        throw new PromotionException(re);
      }
      Collection itemPricingModels = getPricingTools().getItemPricingEngine().getPricingModels(profile);
      Collection shippingPricingModels = getPricingTools().getShippingPricingEngine().getPricingModels(profile);
      Collection orderPricingModels = getPricingTools().getOrderPricingEngine().getPricingModels(profile);
      Collection taxPricingModels =  getPricingTools().getTaxPricingEngine().getPricingModels(profile);
      itemPricingModels.addAll(shippingPricingModels);
      itemPricingModels.addAll(orderPricingModels);
      itemPricingModels.addAll(taxPricingModels);
      return itemPricingModels;
  }

   /**
    * This method will return a map containing the count of promotions
    * contained in the given order, and the total value of those promotions
    *
    * @param pOrder the order whose promotions we would like to calculate.
    */
  public Map calculatePromotionsForOrder(Order pOrder) {

    List orderPromotions = new ArrayList(); // for order promotions
    List itemPromotions = new ArrayList(); // for item promotions
    List shippingPromotions = new ArrayList(); // for shipping promotions
    List taxPromotions = new ArrayList(); // for tax (HA!)
    getOrderPromotions(pOrder, orderPromotions, itemPromotions, shippingPromotions,
                       taxPromotions, true);

    // at this point each list is populated with pricing adjustments
    List all = new ArrayList();
    all.addAll(orderPromotions);
    all.addAll(itemPromotions);
    all.addAll(shippingPromotions);
    all.addAll(taxPromotions);

    double total = 0.0;
    int count = all.size();
    for(int i=0;i<all.size();i++) {
      PricingAdjustment adj = (PricingAdjustment) all.get(i);
      total += -adj.getTotalAdjustment();
    }

    HashMap results = new HashMap();
    results.put("total", Double.valueOf(total));
    results.put("count", Integer.valueOf(count));
    return results;
  }
  /**
   * Returns the number of times a promotion appears in the order.
   * @param pOrder the order
   * @param pPromotion the promotion
   * @return number of occurrences
   */
  public int getPromotionCount(Order pOrder, RepositoryItem pPromotion)
  {
    if (isLoggingDebug()) logDebug("In getPromotionCount");


    ArrayList orderPromotions = new ArrayList();
    ArrayList taxPromotions = new ArrayList();
    ArrayList itemPromotions = new ArrayList();
    ArrayList shipPromotions = new ArrayList();

    getOrderPromotions(pOrder, orderPromotions, taxPromotions, itemPromotions, shipPromotions);
    int totalPromotions = 0;

    Iterator iter = orderPromotions.iterator();
    while(iter.hasNext())
    {
      RepositoryItem promotion = (RepositoryItem) iter.next();
      if(promotion.getRepositoryId().equals(pPromotion.getRepositoryId()))
        totalPromotions++;
    }

   iter = taxPromotions.iterator();
    while(iter.hasNext())
    {
      RepositoryItem promotion = (RepositoryItem) iter.next();
      if(promotion.getRepositoryId().equals(pPromotion.getRepositoryId()))
        totalPromotions++;
    }

    iter = itemPromotions.iterator();
    while(iter.hasNext())
    {
      RepositoryItem promotion = (RepositoryItem) iter.next();
      if(promotion.getRepositoryId().equals(pPromotion.getRepositoryId()))
        totalPromotions++;
    }

    iter = shipPromotions.iterator();
    while(iter.hasNext())
    {
      RepositoryItem promotion = (RepositoryItem) iter.next();
      if(promotion.getRepositoryId().equals(pPromotion.getRepositoryId()))
        totalPromotions++;
    }

    if (isLoggingDebug()) logDebug("Finished getPromotionCount");
    return totalPromotions;
  }
  
  /**
   * Creates a Map of promotion id to the use count in the order. 
   * @param pOrder
   */
  public Map createPromotionCountMap(Order pOrder)
  {
    Collection itemPromos = new HashSet();
    Collection orderPromos = new HashSet();
    Collection shippingPromos = new HashSet();
    Collection taxPromos = new HashSet();
    Map promotionCounts = new HashMap();
    
    getOrderPromotions(pOrder,orderPromos,taxPromos,itemPromos,shippingPromos);
    
    Iterator promoerator;
    RepositoryItem promotion;
    int promotionCount;
    
    promoerator = itemPromos.iterator();
    while(promoerator.hasNext())
    {
      promotion = (RepositoryItem)promoerator.next();
      promotionCount = getPromotionCount(pOrder,promotion);  
      promotionCounts.put(promotion.getRepositoryId(),promotionCount);
    }
    promoerator = orderPromos.iterator();
    while(promoerator.hasNext())
    {
      promotion = (RepositoryItem)promoerator.next();
      promotionCount = getPromotionCount(pOrder,promotion);  
      promotionCounts.put(promotion.getRepositoryId(),promotionCount);
    }
    promoerator = shippingPromos.iterator();
    while(promoerator.hasNext())
    {
      promotion = (RepositoryItem)promoerator.next();
      promotionCount = getPromotionCount(pOrder,promotion);  
      promotionCounts.put(promotion.getRepositoryId(),promotionCount);
    }
    promoerator = taxPromos.iterator();
    while(promoerator.hasNext())
    {
      promotion = (RepositoryItem)promoerator.next();
      promotionCount = getPromotionCount(pOrder,promotion);  
      promotionCounts.put(promotion.getRepositoryId(),promotionCount);
    }
    return promotionCounts;
    
  }


  /**
   * This method determines if the given promotion is usable based on the date allowed on the
   * promotion compared to the current date.
   * @param pPromotion the promotion that should be verified
   * @return true if the promotion should be added to the list of promotions which can be used for pricing
   */
  public boolean isUsable(RepositoryItem pPromotion)
  {
    if (pPromotion == null)
      return false;

    return isUsable(pPromotion, new Date());
  }
    
  /**
   * This method determines if the given promotion is usable based on the date allowed on the
   * promotion compared to the passed in date.
   * @param pPromotion the promotion that should be verified
   * @param pNow the current date
   * @return true if the promotion should be added to the list of promotions which can be used for pricing
   */
  public boolean isUsable(RepositoryItem pPromotion, Date pNow)
  {
    if (pPromotion == null)
      return false;

    String beginUsableProperty = getPricingModelProperties().getBeginUsable();
    if (beginUsableProperty != null) {
      Date beginUsable = (Date) pPromotion.getPropertyValue(beginUsableProperty);
      // if this promotion has not started yet
      if (beginUsable != null && beginUsable.after(pNow)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Utility method to get the pmdl version for a given promotion.
   * @param pPricingModel Promotion RepositoryItem to check
   * @return int pmdl version 1 = old type, 2+ = new type.
   */
  public int getPMDLVersion( RepositoryItem pPricingModel ){
    int pmdlVersion = 1;
    
    if (pPricingModel != null){
      Integer version = (Integer)pPricingModel.getPropertyValue(getPricingModelProperties().getPmdlVersion());
      if (version != null){
        pmdlVersion = version.intValue();
      }
    }
    
    return (pmdlVersion);
  }
    
  private Date normalizeDate(Date pDate) {

    Calendar cNow = Calendar.getInstance();
    cNow.setTime(pDate);
    // clear fields to normalize date
    // We now expose hours and minutes COMMERCE-168354
    //cNow.clear(Calendar.HOUR_OF_DAY);
    //cNow.clear(Calendar.HOUR);
    //cNow.clear(Calendar.MINUTE);
    cNow.clear(Calendar.SECOND);
    cNow.clear(Calendar.MILLISECOND);

    return cNow.getTime();
  }
  
  /**
   * Gets the Qualifier service to use for the given promotion item.
   * First checks the extra parameters map for a Qualifier with key EXTRA_PARAM_QUALIFIERSERVICE.
   * Then checks the given promotion item 'qualifierService' property.
   * If this is not null then it tries to resolve it into a Qualifier to use.
   * If we still don't have a Qualifier then the passed in default qualifier service is used.
   * If this is also null then the Qualifier service this service is configured with is used.
   * 
   * @param pPricingModel RepositoryItem of the promotion. May be null.
   * @param pExtraParameters Map of optional extra parameters.  May be null.
   * @param pDefaultQualifier  Qualifier to use if promotion item 'qualifierService' property is null. May be null.
   * @return Qualifier to use.
   */
  public Qualifier getQualifierService(RepositoryItem pPricingModel, Map pExtraParameters, Qualifier pDefaultQualifier) 
  {
    Qualifier qualService = null;
    
    // First check extra parameters map and use it if found
    if (pExtraParameters != null) {
      qualService = (Qualifier)pExtraParameters.get(EXTRA_PARAM_QUALIFIERSERVICE);
    }
    
    // If not in the map then check the promotion item
    if ((qualService == null) && (pPricingModel != null)) {
      // Get 'qualifierService' property name
      String qualifierProperty = getPricingModelProperties().getQualifierService();
      try {
        if (pPricingModel.getItemDescriptor().hasProperty(qualifierProperty)) {
          
          String qualifierPath = (String)pPricingModel.getPropertyValue(qualifierProperty);
          
          if (qualifierPath != null) {
            try {
              // Resolve the String path into a Qualifier reference
              qualService = resolveQualifierService(qualifierPath);
            }
            catch (PricingException pe) {
              // Error resolving reference
              if (isLoggingError()) {      
                logError(pe);
              }
            }
          }
        }
      }
      catch (RepositoryException re) {
        // Error getting qualifier property
        if (isLoggingError()) {
          logError(re);
        }
      }
    }
    
    // If we didn't get a qualifier service from the promotion item then use the passed in default
    if ((qualService == null) && (pDefaultQualifier != null)){
      qualService = pDefaultQualifier;
    }
      
    // Finally if no other qualifier service then use this service's qualifier
    if (qualService == null) {
      qualService = getQualifierService();
    }

    return (qualService);
  }
  
  /**
   * Resolves the given String nucleus path to a Qualifier service object
   * reference.
   * Caches the service references for efficiency.
   * 
   * @param pQualifierPath String nucleus path to resolve.
   * @return Qualifier service
   * @throws PricingException if the qualifier service could not be resolved
   */
  protected Qualifier resolveQualifierService(String pQualifierPath) 
  throws PricingException
  {
    Qualifier qualService = null;

    synchronized (mQualifierServices) 
    {
      if ((qualService = mQualifierServices.get(pQualifierPath)) == null) {
        qualService = (Qualifier)resolveName(pQualifierPath);

        if (qualService != null) {
          mQualifierServices.put(pQualifierPath, qualService);
        }
        else {
          Object [] args = {pQualifierPath};
          String msg = ResourceUtils.getMsgResource("missingQualifier", MY_RESOURCE_NAME,
              sResourceBundle, args);
          throw new PricingException(msg);
        }
      }
    }
    
    return qualService;
  }
} // end of class
