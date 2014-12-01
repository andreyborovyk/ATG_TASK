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

package atg.commerce.catalog;

import atg.dms.registry.MessageTyper;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.nucleus.ServiceMap;
import atg.repository.*;
import atg.beans.*;

import java.beans.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import atg.core.util.ResourceUtils;

/**
 * This class represents a series of helper methods and properties that are used in the
 * management of accessing the product catalog.
 * <P>
 * One can configure this component to define a mapping from a key to a set of alternate
 * catalogs.  This is particularly useful for internationalization.  <BR>
 *
 * For example: CatalogTools.properties
 *
 * <PRE>
 * $class=atg.commerce.catalog.CatalogTools
 *
 * catalog=ProductCatalog
 *
 * baseProductItemType=product
 * baseCategoryItemType=category
 * baseSKUItemType=sku
 * baseMediaItemType=media
 *
 * productItemTypes=product
 * categoryItemTypes=category
 * SKUItemTypes=sku
 *
 * alternateRepositories=\
 *      en_US=/atg/commerce/catalog/ProductCatalog
 *      fr_FR=/atg/commerce/catalog/FrenchProductCatalog
 *      ja_JP=/atg/commerce/catalog/JapaneseProductCatalog
 *      de_DE=/atg/commerce/catalog/GermanProductCatalog
 * 
 * useDefaultCatalog=true
 * </PRE>
 * <P>
 * The behavior of the findProduct(), findCategory(), and findSku() methods can be
 * modified with use of the <code>useDefaultCatalog</code> property. If
 * <code>useDefaultCatagory</code> is false and an alternate repository cannot be found,
 * that matches the pCatalogKey parameter then no repository is searched and null will
 * be returned.
 *
 * @beaninfo
 *   description: A global service which manages the retrieval of catalog data
 *   attribute: functionalComponentCategory Catalog
 *   attribute: featureComponentCategory
 *
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/CatalogTools.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class CatalogTools
  extends GenericService
  implements MessageTyper
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/CatalogTools.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  /** The name of the item descriptor which represents the base definition of all products */
  public static final String DEFAULT_PRODUCT_ITEM_TYPE = "product";
  /** The name of the item descriptor which represents the base definition of all categories */
  public static final String DEFAULT_CATEGORY_ITEM_TYPE = "category";
  /** The name of the item descriptor which represents the base definition of all SKUs */
  public static final String DEFAULT_SKU_ITEM_TYPE = "sku";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  /** Type metadata for the catalog */
  CatalogTypeInfo mTypeInfo = new CatalogTypeInfo();


  //-------------------------------------
  // Properties
  //-------------------------------------

  //----------------------------------------
  /**
   * Return a serializable instance of CatalogTypeInfo that summarizes
   * metadata usable in a client context
   */
  public CatalogTypeInfo getTypeInfo()
  {
    return mTypeInfo;
  }

  /**
   * Sets property CatalogServiceLockName
   **/
  String mCatalogServiceLockName=null;
  public void setCatalogServiceLockName(String pCatalogServiceLockName) {
    mCatalogServiceLockName = pCatalogServiceLockName;
  }

  /**
   * Returns property CatalogServiceLockName
   **/
  public String getCatalogServiceLockName() {
    return mCatalogServiceLockName;
  }


  //---------------------------------------------------------------------------
  // property: CatalogServiceLockTimeOut
  //---------------------------------------------------------------------------
  long mCatalogServiceLockTimeOut = 0L;

  /**
   * Set the maximum time we wait for a lock, in milliseconds.
   **/
  public void setCatalogServiceLockTimeOut(long pCatalogServiceLockTimeOut) {
    mCatalogServiceLockTimeOut = pCatalogServiceLockTimeOut;
  }

  /**
   * Get the maximum time we wait for a lock, in milliseconds.
   * The default value is zero, meaning wait forever for the lock if necessary.
   **/
  public long getCatalogServiceLockTimeOut() {
    return mCatalogServiceLockTimeOut;
  }

  /**
   * Sets property BaseProductItemType
   **/
  public void setBaseProductItemType(String pBaseProductItemType) {
    mTypeInfo.setBaseProductItemType(pBaseProductItemType);
  }

  /**
   * Returns property BaseProductItemType
   **/
  public String getBaseProductItemType() {
    return mTypeInfo.getBaseProductItemType();
  }


  //---------------------------------------------------------------------------
  // property: parentCategoryPropertyName
  String mParentCategoryPropertyName = "parentCategory";

  /**
   * Set the parentCategoryPropertyName property. This indicates the property of the
   * repository item that hold parentCategory information.  Used by the getAncestors
   * method.
   */
  public void setParentCategoryPropertyName(String pParentCategoryPropertyName) {
    mParentCategoryPropertyName = pParentCategoryPropertyName;
  }

  /**
   * Return the parentCategoryPropertyName property.
   */
  public String getParentCategoryPropertyName() {
    return mParentCategoryPropertyName;
  }

  /**
   * Set property ProductItemTypes
   **/
  public void setProductItemTypes(String [] pProductItemTypes) {
    mTypeInfo.setProductItemTypes(pProductItemTypes);
    try {
      mTypeInfo.initializeProductBeanInfos(getCatalog());
    }
    catch (RepositoryException exc) {
      logError(exc);
    }
  }

  /**
   * Returns property ProductItemTypes
   **/
  public String [] getProductItemTypes() {
    return mTypeInfo.getProductItemTypes();
  }

  /**
   * Returns property ProductBeanInfos
   **/
  public Map getProductBeanInfos() {
    return mTypeInfo.mProductBeanInfos;
  }

  /**
   * Sets property BaseCategoryItemType
   **/
  public void setBaseCategoryItemType(String pBaseCategoryItemType) {
    mTypeInfo.setBaseCategoryItemType(pBaseCategoryItemType);
  }

  /**
   * Returns property BaseCategoryItemType
   **/
  public String getBaseCategoryItemType() {
    return mTypeInfo.getBaseCategoryItemType();
  }

  /**
   * Sets property CategoryItemTypes
   **/
  public void setCategoryItemTypes(String [] pCategoryItemTypes) {
    mTypeInfo.setCategoryItemTypes(pCategoryItemTypes);
    try {
      mTypeInfo.initializeCategoryBeanInfos(getCatalog());
    }
    catch (RepositoryException exc) {
      logError(exc);
    }
  }

  /**
   * Returns property CategoryItemTypes
   **/
  public String [] getCategoryItemTypes() {
    return mTypeInfo.getCategoryItemTypes();
  }

  /**
   * Returns property CategoryBeanInfos
   **/
  public Map getCategoryBeanInfos() {
    return mTypeInfo.getCategoryBeanInfos();
  }

  /**
   * Sets property BaseSKUItemType
   **/
  public void setBaseSKUItemType(String pBaseSKUItemType) {
    mTypeInfo.setBaseSKUItemType(pBaseSKUItemType);
  }

  /**
   * Returns property BaseSKUItemType
   **/
  public String getBaseSKUItemType() {
    return mTypeInfo.getBaseSKUItemType();
  }

  /**
   * Sets property SKUItemTypes
   **/
  public void setSKUItemTypes(String [] pSKUItemTypes) {
    mTypeInfo.setSKUItemTypes(pSKUItemTypes);
    try {
      mTypeInfo.initializeSKUBeanInfos(getCatalog());
    }
    catch (RepositoryException exc) {
      logError(exc);
    }
  }

  /**
   * Returns property SKUItemTypes
   **/
  public String [] getSKUItemTypes() {
    return mTypeInfo.getSKUItemTypes();
  }

  /**
   * Returns property SKUBeanInfos
   **/
  public Map getSKUBeanInfos() {
    return mTypeInfo.getSKUBeanInfos();
  }

  //-------------------------------------
  // property: Catalog
  Repository mCatalog;

  /**
   * Sets property Catalog
   **/
  public void setCatalog(Repository pCatalog) {
    mCatalog = pCatalog;
    try {
      mTypeInfo.initialize(pCatalog);
    }
    catch (RepositoryException exc) {
      logError(exc);
    }
  }

  /**
   * Returns property Catalog
   **/
  public Repository getCatalog() {
    return mCatalog;
  }

  //-------------------------------------
  // property: AlternateCatalogs
  ServiceMap mAlternateCatalogs;

  /**
   * Sets property AlternateCatalogs
   **/
  public void setAlternateCatalogs(ServiceMap pAlternateCatalogs) {
    mAlternateCatalogs = pAlternateCatalogs;
  }

  /**
   * Returns property AlternateCatalogs
   **/
  public ServiceMap getAlternateCatalogs() {
    return mAlternateCatalogs;
  }

  //-------------------------------------
  // property: useDefaultCatalog
  boolean mUseDefaultCatalog = true;
  /**
   * Sets property useDefaultCatalog.  Set it to true if you
   * want the default catalog to be used in case the requested
   * catalog is unavailable.  If set to false, then requesting
   * an unavilable alternate catalog will result in an error.
   **/
  public void setUseDefaultCatalog(boolean pUseDefaultCatalog) {
    mUseDefaultCatalog = pUseDefaultCatalog;
  }

  /**
   * Returns property useDefaultCatalog
   **/
  public boolean isUseDefaultCatalog() {
    return mUseDefaultCatalog;
  }

  //---------------------------------------------------------------------------
  // property: baseMediaItemType
  String mBaseMediaItemType;

  public void setBaseMediaItemType(String pBaseMediaItemType) {
    mBaseMediaItemType = pBaseMediaItemType;
  }

  /**
   * Returns property baseMediaItemType
   **/
  public String getBaseMediaItemType() {
    return mBaseMediaItemType;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof CatalogTools
   */
  public CatalogTools() {
  }

  public void doStartService()
    throws ServiceException
  {
    try {
      mTypeInfo.initialize(getCatalog());
    }
    catch (RepositoryException exc) {
      logError(exc);
    }
  }

  //-------------------------------------
  // Methods
  //-------------------------------------
  /**
  * Determines if the object provided is a product repository item
  * @return true if object is a product repository item
  */
  public boolean isProduct(Object pObj) throws RepositoryException {
    return isItemType(pObj, getBaseProductItemType());
  }

  /**
  * Determines if the object provided is a category repository item
  * @return true if object is a category repository item
  */
  public boolean isCategory(Object pObj) throws RepositoryException {
    return isItemType(pObj, getBaseCategoryItemType());
  }

  /**
   * Determines if the given object is an instance of the given catalog repository item-descriptor.
   * @param pObj The object to test.
   * @param pItemType The name of the item descriptor to test against.
   * @return  True of the given item is an instance of the given item type or false if is not an instance or the given
   * item-descriptor does not exist in the catalog repository.
   * @throws RepositoryException If an error occurs getting the item-descriptor associated with the given item type.
   */
  protected boolean isItemType(Object pObj, String pItemType) throws RepositoryException {
    //In the deployment process the passed in pObj will come from a shadow repository, so
    //we can not use the repository configured globally. Get it from the passed in object
    if (!(pObj instanceof RepositoryItem))
        return false;
    
    RepositoryItem item = (RepositoryItem) pObj;
    Repository catalog = item.getRepository();  
    RepositoryItemDescriptor itemDescriptor = catalog.getItemDescriptor(pItemType);
    return itemDescriptor != null && itemDescriptor.isInstance(pObj);
  }

  /**
   * Finds the catalog in the alternateCatalogs that is identified by catalogKey.
   */
  public Repository findCatalog(String pCatalogKey) {
    Repository catalog = getCatalog();
    if (getAlternateCatalogs() != null) {
      if ((pCatalogKey != null) && (getAlternateCatalogs().containsKey(pCatalogKey))) {
        Object obj = getAlternateCatalogs().get(pCatalogKey);
        if (obj instanceof Repository) {
          catalog = (Repository)obj;
          if (isLoggingDebug())
            logDebug("Using alternate repository " + catalog.getRepositoryName());
        }
        else if (! isUseDefaultCatalog())
          catalog = null;
          if (isLoggingDebug())
            logDebug("Alternate catalog not found and the useDefaultCatalog is false, so can not use the default");
      }
    }
    return catalog;
  }


  //--------------------------------------

  /**
   * This method gets the parentCategory property of the repository item passed in, and gets the parentCategory's
   * parentCategory and so forth until there is no more parent.  Then it returns a LinkedList of category repository
   * items.  It obtains the name of the parentCategory property name from the parentCategoryPropertyName property.
   * If you implementation of the catalog uses another name for the parentCategory property the 
   * parentCategoryPropertyName property should be changed.
   * @param pItem The product or category item to get the ancestry from.
   * @return A LinkedList of category repository items.
   * @throws PropertyNotFoundException If the given item does not have a parentCategoryPropertyName property.
   */
  public LinkedList getAncestors(RepositoryItem pItem) {

    // we have a repository item.  if it is a root category, then we are all set.
    if (isLoggingDebug()) logDebug("in getAncestors for " + pItem.getRepositoryId());

    // grab all parents and stick them into ancestors
    LinkedList ancestors = new LinkedList();
    RepositoryItem parent = pItem;
    while (true) {
      if (isLoggingDebug()) logDebug("finding the ancestors.  parent = " + parent.getRepositoryId());
      parent = (RepositoryItem)parent.getPropertyValue(getParentCategoryPropertyName());
      if (parent == null)
        break;
      ancestors.addFirst(parent);
    }
    return ancestors;
  }

  /**
   * With the given id return an object which represents the category
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem findCategory(String pId, String pCatalogKey)
       throws RepositoryException
  {
    if (pId == null)
      return null;
    if (pCatalogKey == null)
      return findCategory(pId);

    Repository catalog = findCatalog(pCatalogKey);
    if (catalog == null) {
      if (isLoggingError())
        logError("No such catalog as " + pCatalogKey);
      return null;
    }
    return catalog.getItem(pId, getBaseCategoryItemType());
  }

  /**
   * With the given id return an object which represents the category
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem findCategory(String pId)
       throws RepositoryException
  {
    if (pId == null)
      return null;

    return getCatalog().getItem(pId, getBaseCategoryItemType());
  }

  /**
   * With the given id return an object which represents the category
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem [] findCategories(String [] pIds, String pCatalogKey)
       throws RepositoryException
  {
    if (pIds == null)
      return null;
    if (pCatalogKey == null)
      return findCategories(pIds);

    Repository catalog = findCatalog(pCatalogKey);
    if (catalog == null) {
      if (isLoggingError())
        logError("No such catalog as " + pCatalogKey);
      return null;
    }
    return catalog.getItems(pIds, getBaseCategoryItemType());
  }

  /**
   * With the given id return an object which represents the category
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem [] findCategories(String [] pIds)
       throws RepositoryException
  {
    if (pIds == null)
      return null;

    return getCatalog().getItems(pIds, getBaseCategoryItemType());
  }

  /**
   * With the given id return an object which represents the product
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem findProduct(String pId, String pCatalogKey)
       throws RepositoryException
  {
    if (pId == null)
      return null;

    if (pCatalogKey == null)
      return findProduct(pId);

    Repository catalog = findCatalog(pCatalogKey);
    if (catalog == null) {
      if (isLoggingError())
        logError("No such catalog as " + pCatalogKey);
      return null;
    }
    return catalog.getItem(pId, getBaseProductItemType());
  }

  /**
   * With the given id return an object which represents the product
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem findProduct(String pId)
       throws RepositoryException
  {
    if (pId == null)
      return null;

    return getCatalog().getItem(pId, getBaseProductItemType());
  }

  /**
   * With the given id return an object which represents the product
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem [] findProducts(String [] pIds, String pCatalogKey)
       throws RepositoryException
  {
    if (pIds == null)
      return null;
    if (pCatalogKey == null)
      return findProducts(pIds);

    Repository catalog = findCatalog(pCatalogKey);
    if (catalog == null) {
      if (isLoggingError())
        logError("No such catalog as " + pCatalogKey);
      return null;
    }

    return catalog.getItems(pIds, getBaseProductItemType());
  }

  /**
   * With the given id return an object which represents the product
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem [] findProducts(String [] pIds)
       throws RepositoryException
  {
    if (pIds == null)
      return null;

    return getCatalog().getItems(pIds, getBaseProductItemType());
  }

  /**
   * With the given id return an object which represents the sku
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem findSKU(String pId, String pCatalogKey)
       throws RepositoryException
  {
    if (pId == null)
      return null;
    if (pCatalogKey == null)
      return findSKU(pId);

    Repository catalog = findCatalog(pCatalogKey);
    if (catalog == null) {
      if (isLoggingError())
        logError("No such catalog as " + pCatalogKey);
      return null;
    }

    return catalog.getItem(pId, getBaseSKUItemType());
  }

  /**
   * With the given id return an object which represents the sku
   * @exception RepositoryException if there was an error attempting to find the item
   */
  public RepositoryItem findSKU(String pId)
       throws RepositoryException
  {
    if (pId == null)
      return null;

    return getCatalog().getItem(pId, getBaseSKUItemType());
  }

  /**
   * With the given ids return an array of objects which represent the skus
   * @exception RepositoryException if there was an error attempting to find the items
   */
  public RepositoryItem[] findSKUs(String[] pIds, String pCatalogKey)
       throws RepositoryException
  {
    if (pIds == null || pIds.length == 0)
      return null;
    if (pCatalogKey == null)
      return findSKUs(pIds);

    Repository catalog = findCatalog(pCatalogKey);
    if (catalog == null) {
      if (isLoggingError())
        logError("No such catalog as " + pCatalogKey);
      return null;
    }

    return catalog.getItems(pIds, getBaseSKUItemType());
  }

  /**
   * With the given ids return an array of objects which represent the skus
   * @exception RepositoryException if there was an error attempting to find the items
   */
  public RepositoryItem[] findSKUs(String[] pIds)
       throws RepositoryException
  {
    if (pIds == null || pIds.length == 0)
      return null;

    return getCatalog().getItems(pIds, getBaseSKUItemType());
  }

  //-------------------------------------
  // MessageTyper Implementation
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns the DynamicBeanInfo associated with a JMS message type
   * and optional message object class.  If a class is provided, the
   * MessageTyper can expect that it is the class to which an object
   * message of this type will belong, and can introspect it to
   * determine the non-dynamic portion of the message metadata.
   *
   * @param pJMSType the JMS message type, which is required
   * @param pMessageClass an optional class which will be used at
   * runtime for an object message.
   **/
  public DynamicBeanInfo getBeanInfo (String pJMSType, Class pMessageClass)
  {
    try {
      return new CatalogDynamicBeanInfo(pMessageClass, this);
    }
    catch (IntrospectionException exc)
    {
      logError(exc);
      return null;
    }
  }


  // Specialized DynamicBeanInfo which overlays properties typed by
  // dynamic info in the CatalogTools on top of the introspected,
  // static info from a Class.

  static class CatalogDynamicBeanInfo implements DynamicBeanInfo
  {
    /** DynamicBeanInfo that represents introspected information from a class */
    DynamicBeanInfo mStaticInfo;

    /** DynamicPropertyDescriptors which overlay the introspected information */
    HashMap mCatalogPropertyDescriptors = new HashMap(5);

    /** Array of all property descriptors, including overlays */
    DynamicPropertyDescriptor[] mDescriptors;

    //----------------------------------------
    /**
     * Construct a CatalogDynamicBeanInfo which overlays dynamic
     * repository-based catalog information on top of an introspected
     * DynamicBeanInfo.
     */
    CatalogDynamicBeanInfo (Class pClass, CatalogTools pCatalogTools)
      throws IntrospectionException
    {
      mStaticInfo = DynamicBeans.getBeanInfoFromType(pClass);
      populateCatalogPropertyDescriptors(pCatalogTools);
    }


    //----------------------------------------
    /**
     * Populate the overlay descriptors for any properties of the
     * appropriate name that exist in the static class.
     */
    void populateCatalogPropertyDescriptors(CatalogTools pCatalogTools)
    {
      // Create individual dynamic property descriptors for
      // repository-based properties of a message bean.
      if (mStaticInfo.hasProperty("catalogRef"))
        addCatalogPropertyDescriptor("catalogRef", pCatalogTools.getSKUBeanInfos());
      if (mStaticInfo.hasProperty("product"))
        addCatalogPropertyDescriptor("product", pCatalogTools.getProductBeanInfos());
      if (mStaticInfo.hasProperty("category"))
        addCatalogPropertyDescriptor("category", pCatalogTools.getCategoryBeanInfos());

      // If any overlay property descriptors were added, then create a
      // modified clone of the static property descriptor array with
      // the overlay descriptors substituted for the original static
      // ones.
      if (mCatalogPropertyDescriptors.size() > 0)
      {
        mDescriptors = (DynamicPropertyDescriptor[]) mStaticInfo.getPropertyDescriptors().clone();
        for (int i = 0; i < mDescriptors.length; i++)
        {
          DynamicPropertyDescriptor dpd =
            (DynamicPropertyDescriptor) mCatalogPropertyDescriptors.get(mDescriptors[i].getName());
          if (dpd != null)
            mDescriptors[i] = dpd;
        }
      }
      else
        mDescriptors = mStaticInfo.getPropertyDescriptors();
    }

    //----------------------------------------
    /**
     * Create a specialized DynamicPropertyDescriptor for a repository-based property,
     * by cloning the original property of the bean and changing its componentPropertyBeanInfo
     * to repository-based info provided by CatalogTools.
     */
    void addCatalogPropertyDescriptor(String pPropertyName, Map pBeanInfos)
    {
      DynamicPropertyDescriptor dpd = mStaticInfo.getPropertyDescriptor(pPropertyName);
      if (RepositoryItem.class.isAssignableFrom(dpd.getPropertyType()))
      {
        DynamicPropertyDescriptor overlayDpd =
          new CatalogPropertyDescriptor(dpd, getMergedCatalogBeanInfo(pBeanInfos));
        mCatalogPropertyDescriptors.put(pPropertyName, overlayDpd);
      }
    }

    /**
     * Merge the bean infos for some object type represented in a CatalogTypeInfo.
     *
     * @param pBeanInfos a Map from type names to DynamicBeanInfo
     * instances.  The type names are ignored; the instances are merged.
     */
    DynamicBeanInfo getMergedCatalogBeanInfo(Map pBeanInfos)
    {
      Collection infos = pBeanInfos.values();
      DynamicBeanInfo[] infoArray = new DynamicBeanInfo[infos.size()];
      infos.toArray(infoArray);
      return new MergedDynamicBeanInfo(infoArray);
    }

    //----------------------------------------
    // DynamicBeanInfo IMPLEMENTATION
    //----------------------------------------

    //-------------------------------------
    /**
     * Returns the DynamicBeanDescriptor for this DynamicBeanInfo.
     */
    public DynamicBeanDescriptor getBeanDescriptor()
    {
      return mStaticInfo.getBeanDescriptor();
    }

    //-------------------------------------
    /**
     * Returns the DynamicPropertyDescriptors for this DynamicBeanInfo.
     */
    public DynamicPropertyDescriptor[] getPropertyDescriptors()
    {
      return mDescriptors;
    }

    /**
     * Returns true if this DynamicBeanInfo supports the given property
     * @param pPropertyName the name of the property
     */
    public boolean hasProperty(String pPropertyName)
    {
      return mStaticInfo.hasProperty(pPropertyName);
    }

    /**
     * Returns the list of the names of the known properties
     */
    public String [] getPropertyNames()
    {
      return mStaticInfo.getPropertyNames();
    }

    /**
     * Returns the PropertyDescriptor for the specified property
     * @return null if the DynamicBeanInfo has no such property.
     * @param pPropertyName the name of the property
     */
    public DynamicPropertyDescriptor getPropertyDescriptor(String pPropertyName)
    {
      DynamicPropertyDescriptor dpd =
        (DynamicPropertyDescriptor) mCatalogPropertyDescriptors.get(pPropertyName);
      if (dpd != null)
        return dpd;

      return mStaticInfo.getPropertyDescriptor(pPropertyName);
    }

    //-------------------------------------
    /**
     * Returns true if the given object belongs to the set of dynamic beans
     * described by this DynamicBeanInfo.  The definition of this
     * set is variable and is determined by the DynamicBeanInfo; however,
     * all objects described by the same DynamicBeanInfo will generally
     * have the same set of dynamic properties with the same behavior
     * and semantics, and the same meta-behavior such as readability,
     * writability, etc.
     */
    public boolean isInstance(Object pObj)
    {
      return mStaticInfo.isInstance(pObj);
    }


    //-------------------------------------
    /**
     * Returns true if all objects described by some DynamicBeanInfo
     * are also described by this one.
     */
    public boolean areInstances(DynamicBeanInfo pDynamicBeanInfo)
    {
      return mStaticInfo.areInstances(pDynamicBeanInfo);
    }
  }

  // Specialized DynamicPropertyDescriptor that clones an existing
  // descriptor, while providing a specific DynamicBeanInfo as the
  // result of calling getPropertyBeanInfo().

  static class CatalogPropertyDescriptor extends DynamicPropertyDescriptor
  {
    DynamicBeanInfo mPropertyBeanInfo;

    CatalogPropertyDescriptor(DynamicPropertyDescriptor pBaseDescriptor,
                              DynamicBeanInfo pPropertyBeanInfo)
    {
      super(pBaseDescriptor);
      mPropertyBeanInfo = pPropertyBeanInfo;
    }

    public DynamicBeanInfo getPropertyBeanInfo()
    {
      return mPropertyBeanInfo;
    }
  }

} // end of class
