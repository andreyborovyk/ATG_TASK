/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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


/**
 * @author Dynamo Business Commerce solutions set group
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/repository/B2BRepositoryFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
 
package atg.projects.b2bstore.repository;

import atg.beans.*;
import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;
import atg.repository.*;
import atg.repository.servlet.*;
import atg.servlet.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import javax.servlet.ServletException;


/**
 * This form handler extends RepositoryFormHandler to provide automatic
 * management of relationships between existing repository items and
 * repository items that are created and removed by this form handler.
 * <p>
 * For example, you can use this form handler to manage the relationship
 * between organizations and billing addresses, automatically adding each
 * a billing address that is created to a parent organization's list of
 * billing addresses, and automatically removing that address from the
 * parent organization's list when it is removed from the repository.
 * <p>
 * You use this form handler by configuring the repository id and item
 * descriptor type of the parent item whose properties you want to manage,
 * and configuring the name of the property you want to update when a
 * repository item is created or deleted.  This configuration is typically
 * performed using hidden input fields in your jhtml pages, since each
 * use of the form handler may affect a different parent item and a different
 * property of that item.
 * 
 * <P>The following properties will typically be set in a properties file
 *   <dl>
 *   <dt>repository
 *   <dd>This is the repository that contains items managed by this form handler
 *   </dl>
 *
 * <P>The following properties will typically be set in a jhtml file
 *   <dl>
 *   <dt>updateRepositoryId
 *   <dd>This is the repository id of the parent repository item whose
 *       properties are being updated as repository items are created
 *       and deleted.
 *
 *   <dt>updateItemDescriptorName
 *   <dd>This is the item descriptory type of the parent repository item.
 *
 *   <dt>updatePropertyName
 *   <dd>This is the property of the parent repository item that will be
 *       modified by adding (or removing) items created (or deleted) by
 *       this form handler.
 *
 *   <dt>updateKey
 *   <dd>If a repository item is to be assigned to or removed from a map
 *       property of the parent repository item, updateKey identifies which
 *       map entry is to be modified.
 *   </dl>
 *
 * @see atg.repository.servlet.RepositoryFormHandler
 * @author Dynamo Business Commerce solution set team
 **/

public class B2BRepositoryFormHandler extends RepositoryFormHandler
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/repository/B2BRepositoryFormHandler.java#2 $$Change: 651448 $";

  public final static String MY_RESOURCE_NAME  ="atg.projects.b2bstore.repository.UserResources";

  //---------------------------------------------------------------------
  // property: updateRepositoryId

  String mUpdateRepositoryId;

  /**
   * Return the repository id of a "container" or "parent" item that may
   * be updated automatically whenever a repository item is created or
   * deleted by this form handler.  When a new repository item is created,
   * it can be automatically added to a collection- or map-valued property
   * of the update item.  When a repository item is deleted, it can be
   * automatically removed from a collection- or map-valued property of the
   * update item.  
   **/

  public String getUpdateRepositoryId()
  {
    return mUpdateRepositoryId;
  }

  /**
   * Set the repository id of a "container" or "parent" item that may
   * be updated automatically whenever a repository item is created or
   * deleted by this form handler.
   **/
  
  public void setUpdateRepositoryId(String pUpdateRepositoryId)
  {
    mUpdateRepositoryId = pUpdateRepositoryId;
  }


//---------------------------------------------------------------------
  // property: mUpdateItemDescriptorName

  String mUpdateItemDescriptorName;

  /**
   * Return the item descriptor name for the item specified by
   * <code>updateRepositoryId</code>.
   **/
  
  public String getUpdateItemDescriptorName()
  {
    return mUpdateItemDescriptorName;
  }
        
  /**
   * Set the item descriptor name for the item specified by
   * <code>updateRepositoryId</code>.
   **/
  
  public void setUpdateItemDescriptorName(String pUpdateItemDescriptorName)
  {
    mUpdateItemDescriptorName = pUpdateItemDescriptorName;      
  }


  //---------------------------------------------------------------------
  // property: updatePropertyName

  String mPropertyName;

  /**
   * Return the property of the update item to modify when this form
   * handler creates or deletes a repository item.  For example, if
   * <code>updateRepositoryId</code> refers to a repository item of
   * type Organization, and the <code>updatePropertyName</code> is
   * <code>billingAddrs<code>, then creating a new repository item
   * will automatically add it to that organization's
   * <code>billingAddrs</code> property, and removing a repository
   * item will automatically remove it from that organization's
   * <code>billingAddrs</code>.
   **/

  public String getUpdatePropertyName()
  {
    return mPropertyName;
  }
 

  /**
   * Set the property of the update item to modify when this form
   * handler creates or deletes a repository item.
   **/
  
  public void setUpdatePropertyName(String pPropertyName)
  {
    mPropertyName = pPropertyName;      
  }

  //---------------------------------------------------------------------
  // property: updateKey

  String mUpdateKey;

  /**
   * Return the key that identifies the map entry to modify, in cases
   * where <code>updatePropertyName</code> specifies a map-valued property.
   **/

  public String getUpdateKey()
  {
    return mUpdateKey;
  }

  /**
   * Set the key that identifies the map entry to modify, in cases
   * where <code>updatePropertyName</code> specifies a map-valued property.
   **/

  public void setUpdateKey(String pUpdateKey)
  {
        mUpdateKey = pUpdateKey;  
  }

  //---------------------------------------------------------------------
  // property: requiredFields

  String[] mRequiredFields;

  /**
   * Return the list of required property names. When any item is created or
   * updated using this form handler, the preCreate or preUpdate method
   * checks to make sure all of the required properties have been set.  If
   * any required properties are missing, a form exception is added to the
   * form for each missing property.
   */

  public String[] getRequiredFields() {
    return mRequiredFields;
  }

  /**
   * Specify a list of property names that must be set by the form
   * using this form handler.  When any item is created or updated using
   * this form handler, the preCreate or preUpdate method checks to make
   * sure all of the required properties have been set.  If any required
   * properties are missing, a form exception is added to the form for
   * each missing property.
   */

  public void setRequiredFields(String[] pRequiredFields) {
    mRequiredFields = pRequiredFields;
  }


  //---------------------------------------------------------------------
  // property: UserLocale
  
  String mUserLocale;

  /**
   * Return the preferred Locale for user-vislble error messages.  
   */

  public String getUserLocale() {
    return mUserLocale;
  }

  /**
   * Set the preferred Locale for user-vislble error messages.  If
   * not set, the Locale of the request will be used, if available.
   * Otherwise, the default server Locale will be used.
   */

  public void setUserLocale(String pUserLocale) {
    mUserLocale = pUserLocale;
  }

  //---------------------------------------------------------------------
  
  /**
   * Return the name of the resource bundle to use for this class.
   **/

  public static String getResourceBundleName() {
    return MY_RESOURCE_NAME;
  }

  //-------------------------------------

  /**
   * Makes sure all the required repository item properties have
   * values submitted in the form.  If any errors occur, form
   * exceptions are added.
   *
   * @exception RepositoryException if there was an error while
   * accessing the repository
   **/

  protected void checkForRequiredProperties(DynamoHttpServletRequest pRequest)
  {
    String[] requiredFields = getRequiredFields();
    Hashtable valueMap = (Hashtable) getValue();

    // If there are no required fields, just return

    if (requiredFields == null || requiredFields.length == 0)
      return;

    // If there's no value map in the form handler, generate a form exception
    // for each field we wanted to find, then return.

    if (valueMap == null || valueMap.size() == 0)
    {
      for (int i = 0 ; i < requiredFields.length ; i++)
      {
        addFormException(new DropletException(
          getMsgResource("MissingRequiredProperty", pRequest, requiredFields[i])));
      }
      return;
    }

    // Otherwise check the valueMap for each required field
        
    for (int i = 0 ; i < requiredFields.length ; i++)
    {
      String currentKey = requiredFields[i];
      Object currentValue = valueMap.get(currentKey);
      
      if (currentValue == null || isBlank(currentValue.toString()))
      {
        DropletException e = new DropletException(
          getMsgResource("MissingRequiredProperty", pRequest, currentKey));

        addFormException(e);
        if(isLoggingError())
          logError(e);
      }
    }
  }

  //---------------------------------------------------------------------------

  /**
   * This method is called just before the item creation process is
   * started to verify that all required properties have been specified
   * in the form.
   **/

  protected void preCreateItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  { 
    
    try
    {
      checkForRequiredProperties(pRequest);
      if(getFormError())
        return;
      Object updatableProperty = getUpdatableProperty();
     
      if ((updatableProperty instanceof Map) && isBlank(getUpdateKey()))
      { 
        DropletException e = new DropletException(
          getMsgResource("MissingConfigurationProperty", pRequest, "Nickname"));

        addFormException(e);
      }

    }
    catch(RepositoryException re)
    {
      addFormException(new DropletException(getMsgResource("ErrorAddingItem", pRequest), re));
      
      if(isLoggingError())
        {
          logError(re);
        }
    }
    super.preCreateItem(pRequest, pResponse);
  }

  //---------------------------------------------------------------------------

  /**
   * This method is called just before the an item is updated to verify
   * that all required properties have been specified in the form.
   **/

  protected void preUpdateItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    
    checkForRequiredProperties(pRequest);
    super.preUpdateItem(pRequest, pResponse);
  }

  //---------------------------------------------------------------------------

  /**
   * This method is called just after a new repository item has been
   * created to add the new item to some property of the update item.
   * It finds the repository item identified by <code>updateRepositoryId</code>
   * and the property identified by <code>updatePropertyName</code>, then
   * calls <code>addItemToProperty</code> to add the new item to the named
   * property of the existing item.
   **/

  protected void postCreateItem(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    

    // If the user didn't specify a repository id to update, bail out.

    if (isBlank(getUpdateRepositoryId())) {
      if (isLoggingDebug())
        logDebug("No repository id specified on item creation -- skipping postCreateItem");
      return;
    }

    // Otherwise insert the newly created item into some property of the update item.
    
    try
    {
      if(getFormError())
        return;

      RepositoryItem newItem = getCurrentItem();
      Object updatableProperty = getUpdatableProperty();
      addItemToProperty(newItem, updatableProperty);
    }
    catch(RepositoryException re)
    {
      String msg = getMsgResource("ErrorAddingItem", pRequest, getUpdatePropertyName());
      addFormException(new DropletException(msg, re));
      if(isLoggingError())
        logError(re);
    }
    super.postCreateItem(pRequest, pResponse);
  }

  //---------------------------------------------------------------------------

  /**
   * This method is called just before a repository item is deleted
   * to remove the item from some property of the update item.
   * It finds the repository item identified by
   * <code>updateRepositoryId</code> and the property identified by
   * <code>updatePropertyName</code>, then calls
   * <code>removeItemFromProperty</code> to remove the item being
   * deleted from that property of the existing item.
   **/
  
  protected void preDeleteItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // If the user didn't specify a repository id to update, bail out.

    if (isBlank(getUpdateRepositoryId())) {
      if (isLoggingDebug())
        logDebug("No repository id specified on item deletion -- skipping preDeleteItem");
      return;
    }

    // Otherwise remove the item being deleted from some property of the update item.
    
    try
    {
      RepositoryItem currentItem = getCurrentItem();
      Object updateProperty = getUpdatableProperty();

      // If the updatable property is a map, we need an updateKey to continue
      
      if ((updateProperty instanceof Map) && isBlank(getUpdateKey()))
      {
        String msg = getMsgResource("MissingConfigurationProperty", pRequest, "updateKey");
        addFormException(new DropletException(msg));
        if (isLoggingError())
          logError(msg);
        return;
      }

      // Modify property and update parent item immediately so the
      // new value appears in the cache and deleteItem won't think
      // the parent is still holding a reference to the thing we're
      // trying to delete.
      
      removeItemFromProperty(currentItem, updateProperty);
      getRepository().updateItem(getUpdatableItem());
    }
    catch (RepositoryException re)
    {
      String msg = getMsgResource("ErrorRemovingItem", pRequest, getUpdatePropertyName());
      addFormException(new DropletException(msg, re));
      if(isLoggingError())
        logError(re);
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Override deleteItem method to check for references to the item being
   * deleted by other repository items.  If the item is unreferenced it will
   * be deleted by calling the superclass's deleteItem.  If references to
   * the item do exist, the delete operation is silently converted into a
   * no-op.
   **/
  
  protected void deleteItem(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try
    {  
      RepositoryItem item = getCurrentItem();
      if (RepositoryUtils.anyReferencesToItem(item) == false)
        super.deleteItem(pRequest, pResponse);
    }
    catch (RepositoryException re)
    {
      String msg = getMsgResource("ErrorDeletingItem", pRequest);
      addFormException(new DropletException(msg, re));
      if (isLoggingError())
        logError(re); 
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Based on whether the argument is of type Map or Collection, this method calls
   * addToMap or addToCollection respectively.  Override this method to support other
   * property types you may want to manage automatically.
   **/

  protected void addItemToProperty (RepositoryItem pItem, Object updatableProperty)
  {
    if (updatableProperty instanceof Map)
      addToMap(pItem, (Map)updatableProperty);
    else if(updatableProperty instanceof Collection)
      addToCollection(pItem, (Collection)updatableProperty);   
  }

  //---------------------------------------------------------------------------

  /**
   * This method adds the newly created repository item to the Map property of
   * the updateRepositoryItem.
   **/

  protected void addToMap(RepositoryItem pItem, Map updatableProperty)
  {
    updatableProperty.put(getUpdateKey(), pItem);
  }

  //---------------------------------------------------------------------------
  
  /**
   * This method adds the newly created repository item to the Collection
   * property of the updateRepositoryItem.
   **/

  protected void addToCollection(RepositoryItem pItem, Collection updatableProperty)
  {
    updatableProperty.add(pItem);
  }

  //---------------------------------------------------------------------------

  /**
   * Based on whether the argument is of type Map or Collection, this method calls
   * removeFromMap or removeFromCollection respectively.  Override this method to
   * support other property types you may want to manage automatically.
   **/

  protected void removeItemFromProperty (RepositoryItem pItem, Object updatableProperty)
  {
    if (updatableProperty instanceof Map)
      removeFromMap(pItem, (Map)updatableProperty);
    else if(updatableProperty instanceof Collection)
      removeFromCollection(pItem, (Collection)updatableProperty);   
  }

  //---------------------------------------------------------------------------

  /**
   * This method removes a specified repository item from a specified map.
   **/

  protected void removeFromMap(RepositoryItem pItem, Map updatableProperty)
  {
    updatableProperty.remove(getUpdateKey());
  }

  //---------------------------------------------------------------------------
  
  /**
   * This method removes a specified repository item from a specified collection.
   **/

  protected void removeFromCollection(RepositoryItem pItem, Collection updatableProperty)
  {
    updatableProperty.remove(pItem);
  }

  //---------------------------------------------------------------------------

  /**
   * Get the repository item that this form is operating on.  In
   * the case of item creation, this will return the item just created.
   * In the case of item update or removal, this will return the item
   * being modified or deleted.
   **/
   
  protected RepositoryItem getCurrentItem()
    throws RepositoryException
  {
    return getRepository().getItem(getRepositoryId(), getItemDescriptorName());
  }
  
  //---------------------------------------------------------------------------

  /**
   * Get a mutable version of the repository item whose id
   * <code>updateRepositoryId</code> and whose type is
   * <code>updateItemDescriptorName</code>.
   **/

  protected MutableRepositoryItem getUpdatableItem()
    throws RepositoryException
  {
    return getRepository().getItemForUpdate(getUpdateRepositoryId(), getUpdateItemDescriptorName());
  }
  
  //---------------------------------------------------------------------------

  /**
   * Get the property of the update repository item that is to be updated
   * when a new repository item is created.  Return null if no such property
   * can be found.
   **/
  
  protected Object getUpdatableProperty()
    throws RepositoryException
  {
      return getUpdatableItem().getPropertyValue(getUpdatePropertyName());
  }
  
  //---------------------------------------------------------------------------

  /**
   * Return a message from a resource file, taking into account the
   * locale used for user-visible messages.
   *
   * @see atg.core.util.getMsgResource
   **/
  
  protected String getMsgResource(String pMessageKey, DynamoHttpServletRequest pRequest)
  {
    String bundle_name = getResourceBundleName();

    try
    {
      ResourceBundle bundle = ResourceUtils.getBundle(bundle_name, getLocale(pRequest));
      return ResourceUtils.getUserMsgResource(pMessageKey, bundle_name, bundle);
    }
    catch (MissingResourceException mre)
    {
      if (isLoggingError())
        logError("Unable to load resource " + bundle_name + "->" + pMessageKey + ": " + mre);
      return pMessageKey;
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Return a message from a resource file, taking into account the
   * locale used for user-visible messages and substituting the value
   * of a specified argument for the {0} placeholder in the message text.
   *
   * @see atg.core.util.getMsgResource
   **/
  
  protected String getMsgResource(String pMessageKey, DynamoHttpServletRequest pRequest, Object pArg)
  {
    String template = getMsgResource(pMessageKey, pRequest);
    return MessageFormat.format(template, new Object[] { pArg });
  }

  //---------------------------------------------------------------------------

  /**
   * Get the locale to use for user-visible error messages.  Returns the
   * locale specified by the <code>userLocale</code> property if set, otherwise
   * returns the request locale from <code>pRequest</code> if set, otherwise
   * returns the default server locale.
   **/
  
  protected Locale getLocale(DynamoHttpServletRequest pRequest)
  {
    if (!isBlank(getUserLocale()))
      return RequestLocale.getCachedLocale(getUserLocale());
    else if (pRequest.getRequestLocale() != null)
      return pRequest.getRequestLocale().getLocale();
    else
      return Locale.getDefault();
  }

  //---------------------------------------------------------------------------

  /**
   * Return true if pStr is null, the empty string, or consists
   * entirely of whitespace where whitespace is defined by the
   * String.trim method.
   *
   * @see java.lang.String
   **/
  
  public static boolean isBlank(String pStr)
  {
    return (pStr == null || pStr.length() == 0 || pStr.trim().length() == 0);
  }
}
