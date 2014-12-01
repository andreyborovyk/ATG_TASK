/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.commerce.order.processor;

import atg.repository.*;
import atg.commerce.order.*;
import atg.beans.*;

import java.beans.IntrospectionException;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class contains utility methods which the load and save processors use to access
 * the OrderRepository.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/OrderRepositoryUtils.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class OrderRepositoryUtils {
  
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/OrderRepositoryUtils.java#2 $$Change: 651448 $";
  
  //-------------------------------------
  /**
   * Returns a List of RepositoryItem references which refer to the CommerceIdentifier
   * objects which are supplied in pCommerceIdentifierList.
   *
   * @param pMutRep a reference to the repository which the given item exists in
   * @param pCommerceIdentifierList the List of CommerceIdentifier objects which the
   *        RepositoryItems map to
   * @param pOrderTools a reference to the OrderTools
   * @return a List of MutableRepositoryItems
   * @exception IntrospectionException if an exception occurs while introspecting the item
   * @exception RepositoryException if an an exception occurs while accessing the Repository
   */
  public static List getRepositoryItems(MutableRepository pMutRep,
                                    List pCommerceIdentifierList,
                                    List pSaveList,
                                    OrderTools pOrderTools)
                        throws IntrospectionException, RepositoryException
  {
    List list = pSaveList;
    Object o;
    String beanName;
    MutableRepositoryItem item = null;
    CommerceIdentifier ci;
    
    list.clear();
    Iterator iter = pCommerceIdentifierList.iterator();
    while (iter.hasNext()) {
      o = iter.next();
      
      if (o instanceof CommerceIdentifier) {
        ci = (CommerceIdentifier) o;
        
        item = null;
        if (ci instanceof ChangedProperties)
          item = ((ChangedProperties) ci).getRepositoryItem();

        if (item == null) {
          beanName = ci.getClass().getName();
          item = pMutRep.getItemForUpdate(ci.getId(), pOrderTools.getMappedItemDescriptorName(beanName));
          if (ci instanceof ChangedProperties)
            ((ChangedProperties) ci).setRepositoryItem(item);
        }
        
        list.add(item);
      }
      else {
        list.add(o);
      // TBD: fix
      //  throw new RuntimeException("Invalid Object: " + o.getClass().getName());
      }
    }
    
    return list;
  }

  //-------------------------------------
  /**
   * Returns a Map of RepositoryItem references which refer to the CommerceIdentifier
   * objects which are supplied in pCommerceIdentifierList.
   *
   * @param pMutRep a reference to the repository which the given item exists in
   * @param pCommerceIdentifierMap the Map of CommerceIdentifier objects which the
   *        RepositoryItems map to
   * @param pOrderTools a reference to the OrderTools
   * @return a Map of MutableRepositoryItems
   * @exception IntrospectionException if an exception occurs while introspecting the item
   * @exception RepositoryException if an an exception occurs while accessing the Repository
   */
  public static Map getRepositoryItems(MutableRepository pMutRep,
                                    Map pCommerceIdentifierMap,
                                    Map pSaveMap,
                                    OrderTools pOrderTools)
                        throws IntrospectionException, RepositoryException
  {
    Map map = pSaveMap;
    Object o;
    String key, beanName;
    MutableRepositoryItem item = null;
    CommerceIdentifier ci;

    map.clear();
    Iterator iter = pCommerceIdentifierMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      key = (String)entry.getKey();
      o = entry.getValue();
      
      if (o instanceof CommerceIdentifier) {
        ci = (CommerceIdentifier) o;
        
        item = null;
        if (ci instanceof ChangedProperties)
          item = ((ChangedProperties) ci).getRepositoryItem();

        if (item == null) {
          beanName = ci.getClass().getName();
          item = pMutRep.getItemForUpdate(ci.getId(), pOrderTools.getMappedItemDescriptorName(beanName));
          if (ci instanceof ChangedProperties)
            ((ChangedProperties) ci).setRepositoryItem(item);
        }
        
        map.put(key, item);
      }
      else {
        map.put(key, o);
      }
    }
    
    return map;
  }

  //-------------------------------------
  /**
   * Returns a Set of RepositoryItem references which refer to the CommerceIdentifier
   * objects which are supplied in pCommerceIdentifierList.
   *
   * @param pMutRep a reference to the repository which the given item exists in
   * @param pCommerceIdentifierSet the Set of CommerceIdentifier objects which the
   *        RepositoryItems map to
   * @param pOrderTools a reference to the OrderTools
   * @return a Set of MutableRepositoryItems
   * @exception IntrospectionException if an exception occurs while introspecting the item
   * @exception RepositoryException if an an exception occurs while accessing the Repository
   */
  public static Set getRepositoryItems(MutableRepository pMutRep,
                                    Set pCommerceIdentifierSet,
                                    Set pSaveSet,
                                    OrderTools pOrderTools)
                        throws IntrospectionException, RepositoryException
  {
    Set set = pSaveSet;
    Object o;
    String beanName;
    MutableRepositoryItem item = null;
    CommerceIdentifier ci;

    pSaveSet.clear();
    Iterator iter = pCommerceIdentifierSet.iterator();
    while (iter.hasNext()) {
      o = iter.next();

      if (o instanceof CommerceIdentifier) {
        ci = (CommerceIdentifier) o;
        
        item = null;
        if (ci instanceof ChangedProperties)
          item = ((ChangedProperties) ci).getRepositoryItem();
        
        if (item == null) {
          beanName = ci.getClass().getName();
          item = pMutRep.getItemForUpdate(ci.getId(), pOrderTools.getMappedItemDescriptorName(beanName));
          if (ci instanceof ChangedProperties)
            ((ChangedProperties) ci).setRepositoryItem(item);
        }

        set.add(item);
      }
      else {
        set.add(o);
      }
    }

    return set;
  }

  //-------------------------------------
  /**
   * Returns an array of RepositoryItem references which refer to the CommerceIdentifier
   * objects which are supplied in pCommerceIdentifierArray.
   *
   * @param pMutRep a reference to the repository which the given item exists in
   * @param pCommerceIdentifierArray the Array of CommerceIdentifier objects which the
   *        RepositoryItems map to
   * @param pOrderTools a reference to the OrderTools
   * @return an Array of MutableRepositoryItems
   * @exception IntrospectionException if an exception occurs while introspecting the item
   * @exception RepositoryException if an an exception occurs while accessing the Repository
   */
  public static Object[] getRepositoryItems(MutableRepository pMutRep,
                                    Object[] pCommerceIdentifierArray,
                                    OrderTools pOrderTools)
                        throws IntrospectionException, RepositoryException
  {
    Object[] list = new Object[pCommerceIdentifierArray.length];
    Object o;
    String beanName;
    MutableRepositoryItem item = null;
    CommerceIdentifier ci;
    
    for (int i = 0; i < pCommerceIdentifierArray.length; i++) {
      o = pCommerceIdentifierArray[i];
      
      if (o instanceof CommerceIdentifier) {
        ci = (CommerceIdentifier) o;
        
        item = null;
        if (ci instanceof ChangedProperties)
          item = ((ChangedProperties) ci).getRepositoryItem();

        if (item == null) {
          beanName = ci.getClass().getName();
          item = pMutRep.getItemForUpdate(ci.getId(), pOrderTools.getMappedItemDescriptorName(beanName));
          if (ci instanceof ChangedProperties)
            ((ChangedProperties) ci).setRepositoryItem(item);
        }
        
        list[i] = item;
      }
      else {
        list[i] = o;
      }
    }
    return list;
  }
  
  //-------------------------------------
  /**
   * Returns a RepositoryItem reference which refers to the CommerceIdentifier
   * object which is supplied in pCommerceIdentifier.
   *
   * @param pMutRep a reference to the repository which the given item exists in
   * @param pCommerceIdentifier the CommerceIdentifier object which the RepositoryItem maps to
   * @param pOrderTools a reference to the OrderTools
   * @return a MutableRepositoryItem
   * @exception IntrospectionException if an exception occurs while introspecting the item
   * @exception RepositoryException if an an exception occurs while accessing the Repository
   */
  public static RepositoryItem getRepositoryItem(MutableRepository pMutRep,
                                    CommerceIdentifier pCommerceIdentifier,
                                    OrderTools pOrderTools)
                        throws IntrospectionException, RepositoryException
  {
    MutableRepositoryItem item = null;
    
    if (pCommerceIdentifier instanceof ChangedProperties)
      item = ((ChangedProperties) pCommerceIdentifier).getRepositoryItem();

    if (item == null) {
      String beanName = pCommerceIdentifier.getClass().getName();
      item = pMutRep.getItemForUpdate(pCommerceIdentifier.getId(), pOrderTools.getMappedItemDescriptorName(beanName));
      if (pCommerceIdentifier instanceof ChangedProperties)
        ((ChangedProperties) pCommerceIdentifier).setRepositoryItem(item);
    }
    
    return item;
  }

  //-------------------------------------
  /**
   * Saves the given value to the given repository property.
   *
   * @param pMutRep the repository that contains the item to be saved
   * @param pMutItem the repository item to be saved
   * @param pPropertyName the repository item name that the item is saved to
   * @param pValue the value of the property to save
   * @param pOrderTools the OrderTools Nucleus component
   * @exception RepositoryException if an exception occurs while accessing the Repository
   * @exception IntrospectionException if an exception occurs while introspecting the item to be saved
   */
  public static void saveRepositoryItem(MutableRepository pMutRep, MutableRepositoryItem pMutItem,
                        String pPropertyName, Object pValue, OrderTools pOrderTools)
                        throws IntrospectionException, RepositoryException
  {
     try {
      if (pValue == null)
        pMutItem.setPropertyValue(pPropertyName, pValue);
        
      else if (pValue instanceof java.util.Date &&
              (! (pValue instanceof Timestamp || pValue instanceof java.sql.Date)))
        pMutItem.setPropertyValue(pPropertyName, new Timestamp( ((java.util.Date) pValue).getTime() ));
        
      else if (pValue instanceof List)
        pMutItem.setPropertyValue(pPropertyName, getRepositoryItems(pMutRep, (List) pValue,
                                    (List) pMutItem.getPropertyValue(pPropertyName), pOrderTools));
                                    
      else if (pValue instanceof Map)
        pMutItem.setPropertyValue(pPropertyName, getRepositoryItems(pMutRep, (Map) pValue, 
                                    (Map) pMutItem.getPropertyValue(pPropertyName), pOrderTools));
        
      else if (pValue instanceof Set)
        pMutItem.setPropertyValue(pPropertyName, getRepositoryItems(pMutRep, (Set) pValue,
                                    (Set) pMutItem.getPropertyValue(pPropertyName), pOrderTools));
        
      else if (pValue.getClass().isArray())
        pMutItem.setPropertyValue(pPropertyName, getRepositoryItems(pMutRep, (Object[]) pValue, pOrderTools));
        
      else if (pValue instanceof CommerceIdentifier)
        pMutItem.setPropertyValue(pPropertyName, getRepositoryItem(pMutRep, (CommerceIdentifier) pValue, pOrderTools));
        
      else
        pMutItem.setPropertyValue(pPropertyName, pValue);
        
    }
    catch (IllegalArgumentException e) {
      if (pOrderTools.isLoggingDebug()) {
        pOrderTools.logDebug("Property: " + pPropertyName + 
              " is not a valid property for itemDescriptor: " + 
              pMutItem.getItemDescriptor().getItemDescriptorName() + ". The property was not saved.");
      }
    }
  }
  
  //-------------------------------------
  /**
   * Introspects the given object for the property and returns its value. If the given property
   * is not local to the given object, then it is parsed, the object found and the value returned.
   * The keyword "this" refers to the current order. If a property name is of the form this.prop.prop2
   * then the property value is prop2 in the bean named prop in the order. If a property is of the
   * form prop.prop2 them the property value is prop2 in the bean named prop in the current item which
   * is passed in as pBean.
   *
   * @param pOrder the current order context
   * @param pBean the object which the property is found in
   * @param pPropertyName the name of the property
   * @exception PropertyNotFoundException if the the property is not found in the bean
   */
  public static Object getPropertyValue(Order pOrder, Object pBean, String pPropertyName)
          throws PropertyNotFoundException
  {
    Object value;
    
    if (pPropertyName.indexOf('.') != -1) {
      if (pPropertyName.toLowerCase().startsWith("this")) {
        pPropertyName = pPropertyName.substring(4);
        value = pOrder;
      }
      else {
        value = pBean;
      }
  
      StringTokenizer st = new StringTokenizer(pPropertyName, ".");
      while (st.hasMoreTokens()) {
        value = DynamicBeans.getPropertyValue(value, st.nextToken());
        if (value == null)
          return null;
      }
    }
    else {
      if (pPropertyName.equalsIgnoreCase("this"))
        value = pOrder;
      else
        value = DynamicBeans.getPropertyValue(pBean, pPropertyName);
    }
    
    return value;
  }

  //-------------------------------------
  /**
   * Uses introspection on the given object to set the given property value. If the given property
   * is not local to the given object, then it is parsed, the object found and the value set.
   * The keyword "this" refers to the current order. If a property name is of the form this.prop.prop2
   * then the property value is prop2 in the bean named prop in the order. If a property is of the
   * form prop.prop2 them the property value is prop2 in the bean named prop in the current item which
   * is passed in as pBean.
   *
   * @param pOrder the current order context
   * @param pBean the object which the property is in
   * @param pPropertyName the name of the property
   * @param pValue the value to set
   * @exception IntrospectionException if an exception occurs while introspecting the item to be saved
   * @exception PropertyNotFoundException if the the property is not found in the bean
   */
  public static void setPropertyValue(Order pOrder, Object pBean, String pPropertyName, Object pValue)
          throws PropertyNotFoundException, IntrospectionException
  {
    if (pPropertyName.indexOf('.') != -1) {
      if (pPropertyName.toLowerCase().startsWith("this")) {
        pPropertyName = pPropertyName.substring(4);
        pBean = pOrder;
      }
  
      StringTokenizer st = new StringTokenizer(pPropertyName, ".");
      int loop = st.countTokens() - 1;
      for (int i = 0; i < loop; i++)
        pBean = DynamicBeans.getPropertyValue(pBean, st.nextToken());

      pPropertyName = st.nextToken();
    }
    
    if (pValue == null) {
      DynamicPropertyDescriptor pd = DynamicBeans.getBeanInfo(pBean).getPropertyDescriptor(pPropertyName);
      if (pd == null || pd.getPropertyType().isPrimitive())
        return;
    }
      
    DynamicBeans.setPropertyValue(pBean, pPropertyName, pValue);
  }

  //-------------------------------------
  /**
   * Introspects the given object for the property and returns true if the property exists and false
   * otherwise. If the given property is not local to the given object, then it is parsed, the object
   * found and the value returned. The keyword "this" refers to the current order. If a property name
   * is of the form this.prop.prop2 then the property value is prop2 in the bean named prop in the order.
   * If a property is of the form prop.prop2 them the property value is prop2 in the bean named prop in
   * the current item which is passed in as pBean.
   *
   * @param pOrder the current order context
   * @param pBean the object which the property is found in
   * @param pPropertyName the name of the property
   * @exception IntrospectionException if an exception occurs while introspecting the item to be saved
   */
  public static boolean hasProperty(Order pOrder, Object pBean, String pPropertyName)
          throws IntrospectionException
  {
    Object value;
    boolean ret;
    
    if (pPropertyName.indexOf('.') != -1) {
      if (pPropertyName.toLowerCase().startsWith("this")) {
        pPropertyName = pPropertyName.substring(4);
        value = pOrder;
      }
      else {
        value = pBean;
      }
  
      StringTokenizer st = new StringTokenizer(pPropertyName, ".");
      String tok;
      while (st.hasMoreTokens()) {
        tok = st.nextToken();
        
        ret = DynamicBeans.getBeanInfo(value).hasProperty(tok);
        if (ret == false)
          return ret;
        
        try {
          value = DynamicBeans.getPropertyValue(value, tok);
        }
        catch (PropertyNotFoundException e) {
          return false; // should never happen because we already checked for existence
        }
      }
      
      ret = true;
    }
    else {
      if (pPropertyName.equalsIgnoreCase("this"))
        ret = true;
      else
        ret = DynamicBeans.getBeanInfo(pBean).hasProperty(pPropertyName);
    }
    
    return ret;
  }
}
