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


package atg.projects.b2bstore.userprofiling;

import atg.userprofiling.MultiProfileUpdateFormHandler;
import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import javax.servlet.ServletException;


/**
 * This form handler extends MultiProfileUpdateFormHandler to clean up 
 * any existing references before deleting a user profile.  This is
 * required because organizations maintain lists of their approvers and
 * administrators; trying to remove a user profile without removing it
 * from these lists will lead to referential integrity constraint
 * violations in the database.
 * <p>
 * This form handler uses the preDeleteUser method to clean up references
 * to each profile just before it is deleted.
 *
 * @author Dynamo Business Commerce solutions set group
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/userprofiling/B2BDeleteProfileFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
 
public class B2BDeleteProfileFormHandler extends MultiProfileUpdateFormHandler
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/userprofiling/B2BDeleteProfileFormHandler.java#2 $$Change: 651448 $";

  public final static String MY_RESOURCE_NAME  ="atg.projects.b2bstore.userprofiling.UserResources";

  //---------------------------------------------------------------------------

  /**
   * Find and remove any references to the user profile that is about
   * to be deleted.
   * <p>
   * This method assumes that the profile id has been stored in the
   * <code>repositoryId</code> property of the form handler, and that
   * the corresponding item descriptor type has been stored in
   * <code>itemDescriptorName</code>.  The <code>handleDelete</code>
   * method in the superclass is responsible for setting these
   * properties before calling <code>preDeleteUser</code>.
   **/
  
  protected void preDeleteUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try
    {
      // Let the superclass do any standard cleanup it might want to
      super.preDeleteUser(pRequest, pResponse);

      // Now remove any remaining references to the item.
      if (isLoggingDebug())
        logDebug("About to clean up references to user profile " + getRepositoryId());
      RepositoryUtils.removeReferencesToItem(getCurrentItem());
    }
    catch (RepositoryException re)
    {
      String msg = getUserMessage("ErrorDeletingProfile", pRequest, getRepositoryId());
      addFormException(new DropletException(msg, re));
      if (isLoggingError())
        logError(re); 
    }
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

  //---------------------------------------------------------------------------

  /**
   * Get the repository item that this form is operating on.
   **/
   
  protected RepositoryItem getCurrentItem()
    throws RepositoryException
  {
    return getRepository().getItem(getRepositoryId(), getItemDescriptorName());
  }
  
  //---------------------------------------------------------------------------

  /**
   * Return a message from a resource file, taking into account the
   * locale used for user-visible messages.
   *
   * @see atg.core.util.getUserMsgResource
   **/
  
  protected String getUserMessage(String pMessageKey, DynamoHttpServletRequest pRequest)
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
   * @see atg.core.util.getUserMsgResource
   **/
  
  protected String getUserMessage(String pMessageKey, DynamoHttpServletRequest pRequest, Object pArg)
  {
    String template = getUserMessage(pMessageKey, pRequest);
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
