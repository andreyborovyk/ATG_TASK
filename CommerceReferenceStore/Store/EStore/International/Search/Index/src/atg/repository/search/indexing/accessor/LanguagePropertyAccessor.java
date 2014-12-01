/*<ATGCOPYRIGHT>
 * Copyright (C) 2005-2011 Art Technology Group, Inc.
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


package atg.repository.search.indexing.accessor;

import java.util.HashMap;
import java.util.Map;

import atg.beans.DynamicPropertyDescriptor;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.dp.DerivationMethod;
import atg.repository.dp.DerivedPropertyDescriptor;
import atg.repository.dp.LanguageTranslation;
import atg.repository.dp.RepositoryKeyService;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * This propertyAccessor is use to get and return the correct translation of the property of the item
 * for the process of indexing
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/Search/Index/src/atg/repository/search/indexing/accessor/LanguagePropertyAccessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class LanguagePropertyAccessor extends PropertyAccessorImpl {

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/Search/Index/src/atg/repository/search/indexing/accessor/LanguagePropertyAccessor.java#2 $$Change: 651448 $";
  
  //-----------------------------------------------------------------------------------
  // property: loggingDebug

  private boolean mLoggingDebug=false;

  /** 
   * Set the logging debug. Logs through the IndexingOutputConfig
   * on the {@see atg.repository.search.indexing.Context}. 
   * @param pLoggingDebug A boolean value to enable/disable logging debug.
   */
  public void setLoggingDebug(boolean pLoggingDebug) {
    mLoggingDebug = pLoggingDebug;
  }

  /** 
   * Get the logging debug. Logs through the IndexingOutputConfig
   * on the {@see atg.repository.search.indexing.Context}.
   * @return True if loggingDebug set,False otherwise.
   */
  public boolean isLoggingDebug() {
    return mLoggingDebug;
  }
  //-------------------------------------------------------------------------------

  //-----------------------------------------------------------------------------

  private boolean mLoggingError=false;
  
  /** 
   * Set the logging error Logs through the IndexingOutputConfig
   * on the {@see atg.repository.search.indexing.Context}. 
   * @param pLoggingError A boolean value to enable/disable logging errors.
   */
  public void setLoggingError(boolean pLoggingError) {
    mLoggingError = pLoggingError;
  }

  /** 
   * Get the logging error Logs through the IndexingOutputConfig
   * on the {@see atg.repository.search.indexing.Context}.
   * @return True if logging for errors is enabled,False otherwise.
   */
  public boolean isLoggingError() {
    return mLoggingError;
  }
  //-----------------------------------------------------------------------------------
 //-----------------------------------------------------------------------------

  private boolean mLoggingWarning=false;
  
  /** 
   * Set the logging warning Logs through the IndexingOutputConfig
   * on the {@see atg.repository.search.indexing.Context}.
   * @param pLoggingWarning A boolean value to enable/disable logging warnings.
   */
  public void setLoggingWarning(boolean pLoggingWarning) {
    mLoggingWarning = pLoggingWarning;
  }

  /** 
   * Get the logging warning Logs through the IndexingOutputConfig
   * on the {@see atg.repository.search.indexing.Context}.
   * @return True if logging for warnings is enabled,False otherwise.
   */
  public boolean isLoggingWarning() {
    return mLoggingWarning;
  }

  //---------------------------------------------------------------------------------
  /**
   * This function sets the pContext in the ContextranslationRepositoryKeyService.
   * @param pContext Current locale of the property whose value is needed.
   * @param pItem A RepositoryItem object to get the value of the property passed.
   * @param pPropertyName Name of the property whose value is needed.
   * @param pType Type of the property whose value is needed.
   * @return Object The value of the pPropertyName obtained from TranslationDescriptor.
   */
  protected Object getTextOrMetaPropertyValue(Context pContext,
                                            RepositoryItem pItem,
                                            String pPropertyName,
                                            PropertyTypeEnum pType) {

    if (isLoggingDebug()) {
      pContext.getIndexingOutputConfig().logDebug(
        "LanguagePropertyAccessor.getTextOrMetaPropertyValue(" +
        pItem + " , " + pPropertyName + ")");
    }
    DynamicPropertyDescriptor propDesc = null;
    try {
      propDesc = pItem.getItemDescriptor().getPropertyDescriptor(pPropertyName);

      IndexingLanguageTranslation translationDescriptor
        = getCachedContextTranslationDescriptor(pContext, propDesc);

      if (translationDescriptor == null) {
        if (propDesc instanceof DerivedPropertyDescriptor) {
          
          DerivedPropertyDescriptor dpd = (DerivedPropertyDescriptor)propDesc;
          DerivationMethod dm = dpd.getDerivation().getDerivationMethod();
          
          if(dm instanceof LanguageTranslation) {
            translationDescriptor = new IndexingLanguageTranslation((LanguageTranslation)dm, pContext);
            putCachedContextTranslationDescriptor(pContext, propDesc, translationDescriptor);
          }
        }
        else {
          if (isLoggingDebug()) {
            pContext.getIndexingOutputConfig().logDebug(
              "The property descriptor is not IndexingTranslationDescriptor for " +
              pPropertyName + " of " + pItem);
          }
        }
      }

      if (translationDescriptor != null) {
        Object objResult =
          translationDescriptor.derivePropertyValue(pItem);
        if (isLoggingDebug()) {
          pContext.getIndexingOutputConfig().logDebug(
            "The IndexingTranslationDescriptor returned " + objResult + " for " +
            pPropertyName + " of " + pItem + " using locale of " +
            pContext.getCurrentDocumentLocale());
        }
        return objResult;
      }
      else {
        if (isLoggingDebug()) {
          pContext.getIndexingOutputConfig().logDebug(
            "No derived property descriptor for " + pPropertyName +
            " of " + pItem);
        }
      }
    }
    catch (RepositoryException e) {
      pContext.getIndexingOutputConfig().logError(e);
    }
    
    return super.getTextOrMetaPropertyValue(pContext, pItem, pPropertyName, pType);
  }

  //------------------------------
  /** Get the cached ContextTranslationDescriptor, if any.
   *
   * @param pContext context in which to store the map of
   *   property descriptors to ContextTranslationDescriptors.
   * @param pPropertyDescriptor the property descriptor
   */
  protected IndexingLanguageTranslation getCachedContextTranslationDescriptor (
    Context pContext,
    DynamicPropertyDescriptor pPropertyDescriptor) {

    Map map = (Map)pContext.getAttribute(getContextCacheKey());

    if (map == null) {
      return null;
    }

    IndexingLanguageTranslation result =
      (IndexingLanguageTranslation)map.get(pPropertyDescriptor);

    return result;
  }

  //------------------------------
  /** Cache the specified ContextTranslationDescriptor.
   *
   * @param pContext context in which to store the map of
   *   property descriptors to ContextTranslationDescriptors.
   * @param pPropertyDescriptor the property descriptor
   * @param pIndexingTranslationDescriptor the IndexingTranslationDescriptor to cache.
   */
  protected void putCachedContextTranslationDescriptor (
    Context pContext,
    DynamicPropertyDescriptor pPropertyDescriptor,
    IndexingLanguageTranslation pIndexingTranslationDescriptor) {

    Map map = (Map)pContext.getAttribute(getContextCacheKey());

    if (map == null) {
      map = new HashMap();
      pContext.setAttribute(getContextCacheKey(), map);
    }

    map.put(pPropertyDescriptor, pIndexingTranslationDescriptor);
  }
  
  //-------------------------------------
  /** This is a KeyService replacement that looks at the current Context. */
  static class ContextLocaleRepositoryKeyService implements RepositoryKeyService {
    Context mContext;

    ContextLocaleRepositoryKeyService(Context pContext) {
      mContext = pContext;
    }

    /** Return currentDocumentLocale off of the Context. */
    public Object getRepositoryKey() {
      Object objReturn = mContext.getCurrentDocumentLocale();

      return objReturn;
    }
  } // end class ContextLocaleRepositoryKeyService

  String mContextCacheKey = this.getClass().getName() + ".cache";

  /** Get the Context attribute key used to store of the cache fo
   * ContextFirstWithLocale's for a given property descriptor. */
  public String getContextCacheKey() {
    return mContextCacheKey;
  }
  
  /**
   * This extends TranslationDescriptor to use a keyService that is appropriate for
   * ATG Search indexing
   */ 
  static class IndexingLanguageTranslation extends LanguageTranslation {
    
    /**
     * @param pKeyService
     */
    IndexingLanguageTranslation(LanguageTranslation pTranslationDescriptor, 
                                  Context pContext) {
      setKeyService(new ContextLocaleRepositoryKeyService(pContext));
      setFrom(pTranslationDescriptor);
    }

    RepositoryKeyService mKeyService;

    public void setKeyService(RepositoryKeyService pKeyService) {
      mKeyService = pKeyService;
    }

    /**
     * @return Base key service if indexing is off, else indexing key service.
     */
    public RepositoryKeyService getKeyService(){
      if (mKeyService != null) return mKeyService;
      return super.getKeyService(); 
    }
  }

  /**
   * This function passes the default value of the property passed to it.
   * @param pItem A RepositoryItem object to get the value of the property passed.
   * @param pPropertyName Name of the property whose default value is needed.
   * @return Object The default value of the property passed.
   */
  protected Object getDefaultPropertyValue(RepositoryItem pItem, String pPropertyName) {

    return pItem.getPropertyValue(pPropertyName + "Default");

  }

}
