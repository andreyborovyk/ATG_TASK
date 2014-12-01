/*<ATGCOPYRIGHT>
 * Copyright (C) 2010 Art Technology Group, Inc.
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
package atg.projects.store.dp;

import java.util.List;
import java.util.StringTokenizer;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.UnsupportedFeatureException;
import atg.repository.dp.Derivation;
import atg.repository.dp.DerivationMethodImpl;
import atg.repository.dp.PropertyExpression;
import atg.repository.dp.RepositoryKeyService;
import atg.repository.query.PropertyQueryExpression;

/**
 * This derived property method will derive a property based on the current site and 
 * profile's current locale.
 * 
 * For example:<br>
 * <pre>
 *   &lt;property name="imagePath"&gt;
 *     &lt;derivation user-method="atg.projects.store.dp.StoreContextDerivation"&gt;
 *       &lt;expression&gt;image&lt;/expression&gt;
 *     &lt;/derivation&gt;
 *   &lt;/property&gt;
 * </pre>;
 * 
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/dp/StoreContextDerivation.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 **/
public class StoreContextDerivation extends DerivationMethodImpl {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/dp/StoreContextDerivation.java#3 $$Change: 635816 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  protected static final String DERIVATION_NAME = "storeContextDerivation";
  protected static final String DISPLAY_NAME = "Derive by the current site and locale";
  protected static final String SHORT_DESCRIPTION = "Get value mapped to by current site and locale";
  
  protected static final String KEY_SERVICE_PATH = "/atg/userprofiling/LocaleService";
  
  protected static final String SITE_TAG = "{site}";
  protected static final String LANGUAGE_TAG = "{language}";
  
  protected static final String DEFAULT_LANGUAGE_ATTR = "defaultLanguage";
  protected static final String DEFAULT_SITE_ATTR = "defaultSite";

  //static initializer
  static {
    Derivation.registerDerivationMethod(DERIVATION_NAME,
        StoreContextDerivation.class);
  }
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Set the name, display name and short description properties.
   */
  public StoreContextDerivation() {
    setName(DERIVATION_NAME);
    setDisplayName(DISPLAY_NAME);
    setShortDescription(SHORT_DESCRIPTION);
  }
  
  //--------------------------------------------------
  // Attributes
  //--------------------------------------------------

  //--------------------------------------------------
  // attribute: defaultSite
  //--------------------------------------------------

  private String mDefaultSiteAttribute = null;
  
  public String getDefaultSiteAttribute() {
    if(mDefaultSiteAttribute == null) {
      RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
      mDefaultSiteAttribute = (String) pd.getValue(DEFAULT_SITE_ATTR);
    }
    return mDefaultSiteAttribute;
  }

  //--------------------------------------------------
  // attribute: defaultLanguage
  //--------------------------------------------------

  private String mDefaultLanguageAttribute = null;
  
  public String getDefaultLanguageAttribute() {
    if(mDefaultLanguageAttribute == null) {
      RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
      mDefaultLanguageAttribute = (String) pd.getValue(DEFAULT_LANGUAGE_ATTR);
    }
    return mDefaultLanguageAttribute;
  }

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //-------------------------------
  // property: Logger
  private static ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(StoreContextDerivation.class);

  /**
   * @return ApplicationLogging object for logger.
   */
  private ApplicationLogging getLogger()  {
    return mLogger;
  }
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * We do not support query for this derivation implementation
   */
  protected Query createQuery(int pQueryType, boolean pDerivedPropertyOnLeft,
      boolean pCountDerivedProperty, QueryExpression pOther, int pOperator,
      boolean pIgnoreCase, QueryExpression pMinScore,
      QueryExpression pSearchStringFormat, Query pItemQuery,
      QueryBuilder pBuilder, PropertyQueryExpression pParentProperty,
      List pChildPropertyList) throws RepositoryException {

    throw new UnsupportedFeatureException();
  }

  /**
   * Determine the derived property value.
   *
   * @param pItem the item whose property value is desired
   * @return the derived value
   * @exception RepositoryException if there is a problem deriving the value
   **/
  public Object derivePropertyValue(RepositoryItemImpl pItem)
      throws RepositoryException {

    List exps = getDerivation().getExpressionList();
    
    // get the original property value
    String value = getValue(exps, pItem);
    
    if(!StringUtils.isEmpty(value)) {
      
      // process site 
      if(value.contains(SITE_TAG)) {
        String currentSiteId = SiteContextManager.getCurrentSiteId();
        if(!StringUtils.isEmpty(currentSiteId)) {
          value = value.replace(SITE_TAG, currentSiteId);
        } else {
          // for some reason siteId is not found, use default site          
          value = value.replace(SITE_TAG, getDefaultSiteAttribute());
        }
      }
      
      // process language
      if(value.contains(LANGUAGE_TAG)) {
        
        RepositoryKeyService keyService = getKeyService();
        
        if(keyService != null) {
          String reqLocaleStr = keyService.getRepositoryKey().toString();
          
          if (!StringUtils.isEmpty(reqLocaleStr)) {
            StringTokenizer st = new StringTokenizer(reqLocaleStr, "_");
            String language = null;
            
            if (st.hasMoreTokens()) {
              language = st.nextToken();
            }

            if (!StringUtils.isEmpty(language)) {
              value = value.replace(LANGUAGE_TAG, language);
            }
          }
        } else {
          // we still want to replace LANG_TAG with default language
          value = value.replace(LANGUAGE_TAG, getDefaultLanguageAttribute());
        }
      }
    }
    
    return value;
  }

  /**
   * Determine the derived property value using the specified bean.
   *
   * @param pBean the bean representing a repository item whose property
   * value is desired
   * @return the derived value
   * @exception RepositoryException if there is a problem deriving the value
   **/
  public Object derivePropertyValue(Object pBean) throws RepositoryException {
    throw new UnsupportedFeatureException();
  }

  /**
   * Determine whether the specified property can be used in a query.
   *
   **/
  public boolean isQueryable() {
    return false;
  }
  
  /**
   * Returns value for the given property expression 
   * 
   * @param pExps list of property expressions
   * @param pItem repository item
   * @return repository value
   * @throws RepositoryException
   */
  private String getValue(List pExps, RepositoryItemImpl pItem) throws RepositoryException {
    
    PropertyExpression pe;
    Object value = null;

    try {
      if (pExps == null || pExps.size() == 0) {
        return null;
      }
      else {
        pe = (PropertyExpression)pExps.get(0);
        value = pe.evaluate(pItem);
        return String.valueOf(value);
      }
    }
    catch (IndexOutOfBoundsException ioobe) {
      return null;
    }  
  }
  
  /**
   * Returns LocaleKeyService service.
   * 
   * @return RepositoryKeyService The service for which we will get the locale 
   * to use proper language.
   */
  public RepositoryKeyService getKeyService() {

    RepositoryKeyService repositoryKeyService = null;
    Nucleus nucleus = Nucleus.getGlobalNucleus();

    if (nucleus != null) {
      Object keyService = nucleus.resolveName(KEY_SERVICE_PATH);

      if (!(keyService instanceof RepositoryKeyService)) {
        if (getLogger().isLoggingDebug()){
          getLogger().logDebug("The RepositoryKeyService (" + KEY_SERVICE_PATH +
                             ") does not implement atg.repository.dp.RepositoryKeyService");
        }
      }
      else {
        repositoryKeyService = (RepositoryKeyService)keyService;
      }
    }
    return repositoryKeyService;
  }
}
