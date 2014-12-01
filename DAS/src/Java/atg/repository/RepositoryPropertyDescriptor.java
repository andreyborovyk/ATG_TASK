/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.repository;

import atg.beans.DynamicPropertyDescriptor;
import atg.beans.DynamicPropertyConverterImpl;
import atg.beans.DynamicBeanInfo;

import java.beans.FeatureDescriptor;
import java.beans.PropertyEditor;
import java.beans.IntrospectionException;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;

import java.util.ResourceBundle;

import org.w3c.dom.*;

/**
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/RepositoryPropertyDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class RepositoryPropertyDescriptor
extends DynamicPropertyDescriptor implements Cloneable
{
  static final long serialVersionUID = 4273176819496571710L;

  protected static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList(0));
  protected static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(0));
  protected static final Set EMPTY_SET = Collections.unmodifiableSet(new HashSet(0));

  /**
   * If your property descriptor refers to an item in another repository,
   * it can find the name of that repository by looking for a feature
   * descriptor attribute with this name.
   */
  public static final String FOREIGN_REPOSITORY_NAME = "foreignRepositoryName";

  /**
   * If your property descriptor refers to an item in another repository,
   * it can find the path of that repository by looking for a feature
   * descriptor attribute with this name.
   */
  public static final String FOREIGN_REPOSITORY_PATH = "foreignRepositoryPath";

  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/repository/RepositoryPropertyDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants

  //-------------------------------------
  // Member variables

  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: Queryable
  boolean mQueryable = true;

  /**
   * Sets property Queryable
   **/
  public void setQueryable(boolean pQueryable) {
    mQueryable = pQueryable;
  }

  /**
   * Returns property Queryable
   **/
  public boolean isQueryable() {
    return mQueryable;
  }

  //-------------------------------------
  // property: Cacheable
  boolean mCacheable = true;

  /**
   * Sets property Cacheable.  This should be set to true for properties
   * that can be cached.
   **/
  public void setCacheable(boolean pCacheable) {
    mCacheable = pCacheable;
  }

  /**
   * Returns property Cacheable
   **/
  public boolean isCacheable() {
    return mCacheable;
  }

  //--------- Property: Versionable -----------
  // property: Versionable
  boolean mVersionable = true;
  boolean mVersionableSet = false;
  /**
   * Sets the property Versionable.  Should concurrent update checks be
   * applied to this property?  Set this to false for properties whose
   * consistency should not be maintained by the item descriptor.
   *
   * To clarify: This property is not related to the version repository
   * implementation. It is only used for concurrency checking.
   *
   * @beaninfo description:
   * @param pVersionable new value to set
   */
  public void setVersionable(boolean pVersionable) {
    mVersionable = pVersionable;
    mVersionableSet = true;
  }
  /**
   * @return The value of the property Versionable.
   */
  public boolean isVersionable() {
    return mVersionable;
  }
  /**
   * Returns true if someone explicitly specified a value for the
   * versionable property.
   */
  protected boolean isVersionableSet() {
    return mVersionableSet;
  }

  //-------------------------------------
  // property: DefaultValue
  Object mDefaultValue;

  /**
   * Sets property DefaultValue
   **/
  public void setDefaultValue(Object pDefaultValue) {
    mDefaultValue = pDefaultValue;
  }

  /**
   * Returns property DefaultValue
   **/
  public Object getDefaultValue() {
    // Except for Collections, all types are unmodifiable.
    if (mDefaultValue == EMPTY_LIST) {
      return new ArrayList();
    } else if (mDefaultValue == EMPTY_SET) {
      return new HashSet();
    } else if (mDefaultValue == EMPTY_MAP) {
      return new HashMap();
    }
    return mDefaultValue;
  }

  /**
   * Returns property UnmodifiableDefaultValue
   **/
  public Object getUnmodifiableDefaultValue() {
    if (mDefaultValue == EMPTY_LIST
            || mDefaultValue == EMPTY_SET
            || mDefaultValue == EMPTY_MAP)
      return mDefaultValue;
    else
      return getDefaultValue();
  }

  //--------- Property: IdProperty -----------
  // property: IdProperty
  boolean mIdProperty = false;
  /**
   * Sets the property IdProperty.
   *
   * @beaninfo description:
   * @param pIdProperty new value to set
   */
  public void setIdProperty(boolean pIdProperty) {
    mIdProperty = pIdProperty;
  }

  /**
   * @return The value of the property IdProperty.  This returns true
   * if this property corresponds to the id column itself.
   */
  public boolean isIdProperty() {
    return mIdProperty;
  }


  //---------- Property: derived ----------
  /** description: <code>true</code> if this is a derived property, else
   * <code>false</code>. The default is <code>false</code>. **/
  boolean mDerived = false;

  /**
   * Set property <code>derived</code>
   *
   * @param pDerived the new value to set
   **/
  protected void setDerived(boolean pDerived)
  {mDerived = pDerived;}

  /**
   * Test property <code>derived</code>
   *
   * @beaninfo description: description: <code>true</code> if this is a
   * derived property, else <code>false</code>. The default is <code>false</code>.
   * @return <code>derived</code>
   **/
  public boolean isDerived()
  {return mDerived;}


  //---------- Property: persistent ----------
  /** flag indicating if this is a persistent property. **/
  boolean mPersistent = false;

  /**
   * Set property <code>persistent</code>. This is a flag indicating if this
   * is a persistent property.
   * @param pPersistent new value to set
   **/
  protected void setPersistent(boolean pPersistent)
  {mPersistent = pPersistent;}

  /**
   * Test property <code>persistent</code>. This is a flag indicating if this
   * is a persistent property.
   *
   * @beaninfo description: flag indicating if this is a persistent property.
   * @return <code>persistent</code>
   **/
  public boolean isPersistent()
  {return mPersistent;}


  //-------------------------------------
  // Constructors

  //-------------------------------------
  /**
   * Constructs a new RepositoryPropertyDescriptor.
   **/
  public RepositoryPropertyDescriptor()
  {
    super();
  }

  //-------------------------------------
  /**
   * Constructs a new RepositoryPropertyDescriptor.
   **/
  public RepositoryPropertyDescriptor(String pPropertyName)
  {
    super(pPropertyName);
  }

  //-------------------------------------
  /**
   * Constructs a new RepositoryPropertyDescriptor with the given
   * property name, property type, and short description.
   **/
  public RepositoryPropertyDescriptor(String pPropertyName,
              Class pPropertyType,
              String pShortDescription)
  {
    super(pPropertyName);
    setPropertyType(pPropertyType);
    setShortDescription(pShortDescription);
  }


  public RepositoryPropertyDescriptor (RepositoryPropertyDescriptor pDescriptor) {
    super(pDescriptor);

    setPropertyType(pDescriptor.getPropertyType());
    setShortDescription(pDescriptor.getShortDescription());

    setQueryable(pDescriptor.isQueryable());
    setCacheable(pDescriptor.isCacheable());
    setDefaultValue(pDescriptor.getDefaultValue());
    setIdProperty(pDescriptor.isIdProperty());
    setDerived(pDescriptor.isDerived());

    // don't go through the property setter, because we don't want to recalculate the
    // default value.  We get that from getDefaultValue(), above.
    mDefaultValueString = pDescriptor.getDefaultValueString();

    setCascadeInsert(pDescriptor.getCascadeInsert());
    setCascadeUpdate(pDescriptor.getCascadeUpdate());
    setCascadeDelete(pDescriptor.getCascadeDelete());
    setItemDescriptor(pDescriptor.getItemDescriptor());
    setPropertyItemDescriptor(pDescriptor.getPropertyItemDescriptor());
    setComponentItemDescriptor(pDescriptor.getComponentItemDescriptor());

    if (pDescriptor.getResourceBundleName() != null)
      setResourceBundleName(pDescriptor.getResourceBundleName());
  }

  //-------------------------------------
  /**
   * Returns true if the given property value is a valid value for
   * this property, false otherwise.  Specifically, verifies that the
   * property value has a valid type.
   **/
  public boolean isValidValue(Object pPropertyValue) {
    if (pPropertyValue == null) {
      // null values are ok unless this is required.
      if (isRequired()) return false;
      return true;
    }
    return getPropertyType().isAssignableFrom(pPropertyValue.getClass());
  }

  //--------- Property: DefaultValueString -----------
  // property: IgnoreNullValue
  boolean mIgnoreNullValue = false;

  /**
   * Sets property IgnoreNullValue, indicating whether NULL value will be ignore for
   * this property.
   * @beaninfo description: whether NULL value will be ignore
   **/
  public void setIgnoreNullValue(boolean pIgnoreNullValue) {
    mIgnoreNullValue = pIgnoreNullValue;
  }

  /**
   * Returns property IgnoreNullValue
   **/
  public boolean isIgnoreNullValue() {
    return mIgnoreNullValue;
  }

  //--------- Property: DefaultValueString -----------
  // property: DefaultValueString
  String mDefaultValueString;

  /**
   * Sets the property DefaultValueString.  This is a string version
   * of the default value for this property if it is not explicitly
   * set.  If it is null, no default is used.  When you set this
   * property, the value is automatically converted to the correct
   * type and set.<p>
   *
   * You should set this after you've defined the data type for the
   * descriptor so that the conversion can be performed correctly.  In some
   * cases, the data type is determined by the type of the column that this
   * is associated with so make sure that the column is defined too.
   *
   * @beaninfo description: default value of the property as a String
   * @param pDefaultValueString value to set
   * @exception IllegalArgumentException if the conversion to the correct
   * data type fails
   **/
  public void setDefaultValueString(String pDefaultValueString)
    throws IllegalArgumentException
  {
    mDefaultValueString = pDefaultValueString;
    if (mDefaultValueString == null) {
      setDefaultValue(null);
    }
    else {
      if (getPropertyType() == null)
        throw new IllegalArgumentException("No type for property: " + getName() +
                        " to set default value: " + pDefaultValueString);

      PropertyEditor p = createPropertyEditor();
      if (p == null) {
        /* Try just the string itself */
        setDefaultValue(pDefaultValueString);
      }
      else {
        p.setAsText(pDefaultValueString);
        setDefaultValue (p.getValue());
      }
    }
  }

  /**
   * @return The value of the property DefaultValueString.
   */
  public String getDefaultValueString()
  {
    return mDefaultValueString;
  }

  //------------------------------------

  protected boolean mCollectionOrMap = false;

  public boolean isCollectionOrMap() {
    return mCollectionOrMap;
  }

  public void setPropertyType(Class pPropertyType) {
    super.setPropertyType(pPropertyType);
    if (pPropertyType != null) {
      if (java.util.List.class.isAssignableFrom(pPropertyType)) {
        setDefaultValue(EMPTY_LIST);
        mCollectionOrMap = true;
      }
      else if (java.util.Set.class.isAssignableFrom(pPropertyType)) {
        setDefaultValue(EMPTY_SET);
        mCollectionOrMap = true;
      }
      else if (java.util.Map.class.isAssignableFrom(pPropertyType)) {
        setDefaultValue(EMPTY_MAP);
        mCollectionOrMap = true;
      }
    }
  }

  //--------- Property: CascadeInsert -----------
  // property: CascadeInsert
  boolean mCascadeInsert = false;
  /**
   * Sets the property CascadeInsert.  If this is true on a property
   * that refers to another item, the create operation will automatically
   * create a new item in the referenced repository and set the value
   * of this property to it.  Typically this should be used in conjunction
   * with CascadeUpdate so that this value gets added and updated at the
   * same time as well.
   * The default is false.  You cannot set this to true for properties which
   * do not refer to other items.
   *
   * @beaninfo description:
   * @param pCascadeInsert new value to set
   */
  public void setCascadeInsert(boolean pCascadeInsert) {
    mCascadeInsert = pCascadeInsert;
  }
  /**
   * @return The value of the property CascadeInsert.
   */
  public boolean getCascadeInsert() {
    /*
     * Only properties which refer to other items can be considered as owned
     */
    if (mPropertyItemDescriptor == null && mComponentItemDescriptor == null)
      return false;
    return mCascadeInsert;
  }

  //--------- Property: CascadeUpdate -----------
  // property: CascadeUpdate
  boolean mCascadeUpdate = false;
  /**
   * Sets the property CascadeUpdate.  If this is true on a property
   * that refers to another item (or items), the add and update operations apply
   * recursively to the referenced item (or items).
   * More specifically, setting this property to true on a property descriptor
   * changes the behavior of the system in the following ways:
   *  <ul>
   *   <li>when you have a mutable version of the item, any referenced items
   *   are returned as mutable when you call getPropertyValue.
   *   <li>when you add the item, any referenced items that are transient
   * are automatically added
   *   <li>when you update the item, any referenced items that have been
   * modified are automatically updated.
   * </ul>
   * The default is false.  You cannot set this to true for properties which
   * do not refer to other items.
   *
   * @beaninfo description:
   * @param pCascadeUpdate new value to set
   */
  public void setCascadeUpdate(boolean pCascadeUpdate) {
    mCascadeUpdate = pCascadeUpdate;
  }
  /**
   * @return The value of the property CascadeUpdate.
   */
  public boolean getCascadeUpdate() {
    /*
     * Only properties which refer to other items can be considered as owned
     */
    if (mPropertyItemDescriptor == null && mComponentItemDescriptor == null)
      return false;
    return mCascadeUpdate;
  }

  //--------- Property: CascadeDelete -----------
  // property: CascadeDelete
  boolean mCascadeDelete = false;
  /**
   * Sets the property CascadeDelete.  If this is true on a property
   * that refers to another item (or items), the remove operation applies
   * recursively to the referenced item (or items).
   * More specifically, setting this property to true on a property descriptor
   * changes the behavior of the system in the following ways:
   *  <ul>
   *   <li>when an item is removed, any items referenced by a cascade
   *   delete=true property are also removed.
   * </ul>
   * The default is false.  You cannot set this to true for properties which
   * do not refer to other items.
   *
   * @beaninfo description:
   * @param pCascadeDelete new value to set
   */
  public void setCascadeDelete(boolean pCascadeDelete) {
    mCascadeDelete = pCascadeDelete;
  }
  /**
   * @return The value of the property CascadeDelete.
   */
  public boolean getCascadeDelete() {
    /*
     * Only properties which refer to other items can be considered as owned
     */
    if (mPropertyItemDescriptor == null && mComponentItemDescriptor == null)
      return false;
    return mCascadeDelete;
  }

  //-------------------------------------
  // property: Descriptor
  /** descriptor in which this attribute is defined **/
  RepositoryItemDescriptor mItemDescriptor;

  /**
   * Set property ItemDescriptor.  This is the item descriptor that
   * contains this property descriptor.  Do not confuse this with the
   * propertyItemDescriptor or componentItemDescriptor properties which
   * define the type of a property which refers to other items.
   *
   * @beaninfo description: descriptor in which this attribute is
   * defined
   * @param pItemDescriptor new value to set
   **/
  public void setItemDescriptor(RepositoryItemDescriptor pItemDescriptor)
  {mItemDescriptor = pItemDescriptor;}

  /**
   * Get property ItemDescriptor
   *
   * @beaninfo description: descriptor in which this attribute is
   * defined
   * @return ItemDescriptor
   **/
  public RepositoryItemDescriptor getItemDescriptor()
  {return mItemDescriptor;}


  //--------- Property: PropertyItemDescriptor -----------
  // property: PropertyItemDescriptor
  RepositoryItemDescriptor mPropertyItemDescriptor;
  /**
   * Sets the property PropertyItemDescriptor.  If this property refers
   * to another item's type, this is the item descriptor for that type.
   *
   * @beaninfo description:
   * @param pPropertyItemDescriptor new value to set
   */
  public void setPropertyItemDescriptor(RepositoryItemDescriptor pPropertyItemDescriptor) {
    mPropertyItemDescriptor = pPropertyItemDescriptor;
    /*
     * Our property type is a RepositoryItem in this case
     */
    if (mPropertyItemDescriptor != null)
      setPropertyType(RepositoryItem.class);
  }
  /**
   * @return The value of the property PropertyItemDescriptor.
   */
  public RepositoryItemDescriptor getPropertyItemDescriptor() {
    return mPropertyItemDescriptor;
  }

  //-------------------------------------------
  /**
   * Overrides the getPropertyBeanInfo from the super type.  If this
   * property refers to another item descriptor, that item descriptor is
   * the bean info for this property.
   */
  public DynamicBeanInfo getPropertyBeanInfo() throws IntrospectionException {
    if (mPropertyItemDescriptor != null) return mPropertyItemDescriptor;
    return super.getPropertyBeanInfo();
  }

  //-------------------------------------------
  /**
   * Overrides the getPropertyBeanInfo from the super type.  If this
   * property refers to another item descriptor, that item descriptor is
   * the bean info for this property.
   */
  public DynamicBeanInfo getComponentPropertyBeanInfo() throws IntrospectionException {
    if (mComponentItemDescriptor != null) return mComponentItemDescriptor;
    return super.getComponentPropertyBeanInfo();
  }

  //--------- Property: ComponentItemDescriptor -----------
  // property: ComponentItemDescriptor
  RepositoryItemDescriptor mComponentItemDescriptor;
  /**
   * Sets the property ComponentItemDescriptor.  If this is a collection
   * of items, this is the item descriptor of the component type.
   *
   * @beaninfo description:
   * @param pComponentItemDescriptor new value to set
   */
  public void setComponentItemDescriptor(RepositoryItemDescriptor pComponentItemDescriptor) {
    mComponentItemDescriptor = pComponentItemDescriptor;
    if (mComponentItemDescriptor != null)
      setComponentPropertyType(RepositoryItem.class);
  }
  /**
   * @return The value of the property ComponentItemDescriptor.
   */
  public RepositoryItemDescriptor getComponentItemDescriptor() {
    return mComponentItemDescriptor;
  }


  //-------------------------------------
  // Ability to retrieve/save values to the repository item
  //-------------------------------------

  //-------------------------------------
  /**
   * This method is called to retrieve a read-only value for this property.
   *
   * Once a repository has computed the value it would like to return for
   * this property, this property descriptor gets a chance to modify it
   * based on how the property is defined.  For example, if null is to
   * be returned, we return the default value.
   */
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    if (pValue == null)
      return getDefaultValue();
    return pValue;
  }

  //-------------------------------------
  /**
   * Sets the property of this type for the item descriptor provided.
   */
  public void setPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    pItem.setPropertyValue(this, pValue);
  }

  //---------------------------------------
  /**
   * Returns the name used for this type in the property descriptor.  For
   * properties which refer to built-in property types, this returns the name
   * of the data type.  For example, an "int" property will return "int" for
   * this method.  All other property types should return null from this
   * method.
   */
  public String getTypeName() {
    return null;
  }

  //-------------------------------------
  // Static type to name registry for RepositoryPropertyDescriptors.
  //-------------------------------------

  /**
   * descriptors of that type.
   */
  static Map sPropertyDescriptorClassTable = new HashMap();

  /**
   * Creates a new RepositoryPropertyDescriptor of a given type.
   */
  public static RepositoryPropertyDescriptor
                        createPropertyDescriptorFromType(String pType) {
    Class cl = (Class) sPropertyDescriptorClassTable.get(pType.toLowerCase());
    if (cl == null) return null;
    try {
      return (RepositoryPropertyDescriptor) cl.newInstance();
    }
    catch (InstantiationException ex1) {
      System.err.println("Can't create instance of property descriptor class: "
                          + cl + " exception=" + ex1);
    }
    catch (IllegalAccessException ex2) {
      System.err.println("Can't create instance of property descriptor class: "
                          + cl + " exception=" + ex2);
    }
    return null;
  }

  //-------------------------------------
  /**
   * Adds a new type of PropertyDescriptor to repositories which support
   * user-defined property descriptors.  These property descriptors implement
   * the getPropertyValue operation given a RepositoryItem.  They compute the
   * return the value of a property of a given name.
   */
  public static void registerPropertyDescriptorClass(String pType, Class pClass) {
    if (!RepositoryPropertyDescriptor.class.isAssignableFrom(pClass))
      System.err.println("Invalid PropertyDescriptor class: " + pClass +
                         " for type=" + pType);
    else
      sPropertyDescriptorClassTable.put(pType.toLowerCase(), pClass);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }


  //--------- Read only property: multiValued -----------
  /**
   * Convenience method for retrieving multi valued status.
   */
  public boolean isMultiValued()
  {
    /*
     * We have a few special multi valued types (set, list, map).
     */
    if (mCollectionOrMap) return true;
    return super.isMultiValued();
  }

  //-------------------------------------

  //--------- Property: LoggingDebug -----------
  // property: LoggingDebug
  boolean mLoggingDebug = false;
  /**
   * Log operations using this property descriptor.  The get method returns
   * true if we are debugging the item type, or the repository as well.
   *
   * @beaninfo description:
   * @param pLoggingDebug new value to set
   */
  public void setLoggingDebug(boolean pLoggingDebug) {
    mLoggingDebug = pLoggingDebug;
  }
  /**
   * @return The value of the property LoggingDebug.
   */
  public boolean isLoggingDebug() {
    return mLoggingDebug;
  }

  //------------------------------------------------------
  /**
   * Override the getResourceBundle method which is used to localize
   * values for this property descriptor.  If a resource bundle is not
   * defined specifically for this property, we look and use the one for the
   * item-descriptor to which this property belongs.
   */
  public ResourceBundle getResourceBundle() {
    ResourceBundle b = super.getResourceBundle();
    if (b == null && getItemDescriptor() != null &&
        getItemDescriptor().getBeanDescriptor() != null) {
      String bundleName = (String) getItemDescriptor().getBeanDescriptor().
                                getValue(BUNDLE_ATTRIBUTE_NAME);
      if (bundleName == null) return null;
      b = ResourceBundle.getBundle(bundleName, atg.service.dynamo.LangLicense.getLicensedDefault());
    }
    return b;
  }

  /**
   * Returns the bundle name used by this resource bundle
   */
  public String getResourceBundleName() {
    ResourceBundle b = super.getResourceBundle();
    if (b != null) return (String) getValue(BUNDLE_ATTRIBUTE_NAME);
    return (String) getItemDescriptor().getBeanDescriptor().getValue(BUNDLE_ATTRIBUTE_NAME);
  }

  /**
   * Sets the bundle name used for this resource bundle.  This is to make
   * it look like we have a resourcebundlename property.  Its to make the
   * i18n stuff added into the GSA templates useable from the HTML and
   * XML simple repositories, so that the Quincy funds demo's can be localized.
   * The other option was to add functionality to allow setting of arbitrary
   * feature descriptor properties, but for now this does the trick for i18n,
   * and is the most localized [pun intended] fix.
   *
   */
  public void setResourceBundleName(String pResourceBundleName) {
    setValue(BUNDLE_ATTRIBUTE_NAME, pResourceBundleName);
  }

  /**
   * Associate a named attribute with this feature.
   *
   * @param attributeName  The locale-independent name of the attribute
   * @param value  The value.
   */
  public void setValue(String pAttributeName, Object pValue) {
     super.setValue(pAttributeName, pValue);

     if (pAttributeName != null && pAttributeName.equals("versionable") &&
         pValue != null) {
       setVersionable(pValue.toString().equalsIgnoreCase("true"));
     }
  }

  //-------------------------------------
  /**
   * Determine if properties of this type can be assigned values from objects
   * of the specified property descriptor. This is done strictly, which means
   * that two Lists are not considered compatible unless their component type
   * is also the same.
   *
   * @param pOther the property descriptor defining the other type, must be
   * non-null
   * @return <code>true</code> if objects of this property descriptor can be
   * assigned values from objects of the specified property descriptor, else
   * <code>false</code>.
   **/
  public boolean
  isAssignableFrom(RepositoryPropertyDescriptor pOther)
  {
    if (pOther == null)
      return false;

    // get all the type info
    Class thisType = getPropertyType();

    // if for some reason the type has not been specified, return false
    if (thisType == null)
      return false;

    Class thatType = pOther.getPropertyType();

    RepositoryItemDescriptor thisItemDesc = getPropertyItemDescriptor();
    RepositoryItemDescriptor thatItemDesc = pOther.getPropertyItemDescriptor();

    Class thisComponentType = getComponentPropertyType();
    Class thatComponentType = pOther.getComponentPropertyType();

    RepositoryItemDescriptor thisComponentItemDesc =
      getComponentItemDescriptor();

    RepositoryItemDescriptor thatComponentItemDesc =
      pOther.getComponentItemDescriptor();

    /*
     * Deal with each type. These tests could be rearranged if this method is
     * heavily used and it is expected that it will mostly return true (or
     * false). However this mthoed is currently only called at startup of a
     * repository.
     */

    // repository item
    if (thisItemDesc != null)
      {
        // the item descriptors must match
        return thisItemDesc.areInstances(thatItemDesc);
      }

    // collection of repository items
    if (thisComponentItemDesc != null)
      {
        return thisComponentItemDesc.areInstances(thatComponentItemDesc);
      }

    // collection of primitive type
    if (thisComponentType != null)
      {
        return thatComponentType != null &&
          thisComponentType.isAssignableFrom(thatComponentType);
      }

    // plain old primitive type
    return thisType.isAssignableFrom(thatType);
  }

  //-------------------------------------
  // Object overrides

  //-------------------------------------
  /**
   * Return a string representation of this object
   * @return a string representation of this object
   **/
  public String toString()
  {
    StringBuffer b = new StringBuffer();

    /* simple name of type */
    String className = getPropertyType().getName();
    int start = className.lastIndexOf('.') + 1;
    if (start < 0)
      start = 0;

    // shouldn't get an exception here
    String type = null;
    try { type = className.substring(start); }
    catch (IndexOutOfBoundsException e) { type = className; }

    /* bean infos */
    DynamicBeanInfo pbi = null;
    try { pbi = getPropertyBeanInfo(); }
    catch (IntrospectionException ie) {}

    DynamicBeanInfo cbi = null;
    try { cbi = getComponentPropertyBeanInfo(); }
    catch (IntrospectionException ie) {}

    b.append("\n{").append(getName());
    b.append(",pType=").append(type);
    b.append(",IDesc=").append(getItemDescriptor());
    b.append("\n  ");
    b.append(",pBI=").append(pbi);
    b.append(",pIDesc=").append(getPropertyItemDescriptor());
    b.append("\n  ");
    b.append(",cType=").append(getComponentPropertyType());
    b.append(",cBI=").append(cbi);
    b.append(",cIDesc=").append(getComponentItemDescriptor());

    b.append('}');

    return b.toString();
  }

}
