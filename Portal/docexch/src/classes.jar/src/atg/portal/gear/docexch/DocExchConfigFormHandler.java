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

package atg.portal.gear.docexch;

import atg.nucleus.Nucleus;
import atg.nucleus.naming.ParameterName;
import atg.repository.*;
import atg.servlet.*;
import atg.beans.*;

import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;

import atg.portal.framework.GearConfigFormHandler;

/**
 *
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocExchConfigFormHandler.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DocExchConfigFormHandler
  extends GearConfigFormHandler 
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocExchConfigFormHandler.java#2 $$Change: 651448 $";

  //Resource Bundle
  static final String RESOURCEBUNDLENAME = "atg.portal.gear.docexch.DocExchConfigFormHandlerResources";

  //Messages
  static final String ERR_MAXFILESIZEFORMATEXCEPTION = "errMaxFileSizeFormatException";

  //-------------------------------------
  // Constants
  //-------------------------------------
  boolean mDirty = true;

  //-------------------------------------
  // property: repository
  Repository mRepository;
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }
  public Repository getRepository() {
    if (mRepository == null || mDirty) {
      String reppath = (String)getValues().get("repositoryPath");
      mRepository = (Repository)Nucleus.getGlobalNucleus().resolveName(reppath);
    }
    return mRepository;
  }
  
  //-------------------------------------
  // property: itemDescriptorChoices
  String [] mItemDescriptorChoices;
  public void setItemDescriptorChoices(String [] pItemDescriptorChoices) {
    mItemDescriptorChoices = pItemDescriptorChoices;
  }
  public String [] getItemDescriptorChoices() {
    if (mItemDescriptorChoices == null || mDirty)
      mItemDescriptorChoices = initItemDescriptorChoices();
    return mItemDescriptorChoices;
  }

  //-------------------------------------
  // property: propertyNameChoices
  Map mPropertyNameChoices;
  public void setPropertyNameChoices(Map pPropertyNameChoices) {
    mPropertyNameChoices = pPropertyNameChoices;
  }
  public Map getPropertyNameChoices() {
    if (mPropertyNameChoices == null || mDirty)
      mPropertyNameChoices = initPropertyNameChoices();
    return mPropertyNameChoices;
  }


  String [] initItemDescriptorChoices() {
    return getRepository().getItemDescriptorNames();    
  }

  String getDefaultItemDescriptorName() {
    String [] idc = getItemDescriptorChoices();
    if (idc != null && idc.length != 0)
      return idc[0];
    else 
      return "";
  }

  RepositoryItemDescriptor getItemDescriptor() 
    throws RepositoryException 
  {
    String itemDescriptorName = (String)getValues().get("itemDescriptorName");
    
    if (isLoggingDebug())
      logDebug("itemDescriptorName is " + itemDescriptorName);
    if (itemDescriptorName == null) {
      itemDescriptorName = getDefaultItemDescriptorName();
      if (isLoggingDebug())
        logDebug("second try itemDescriptorName is " + itemDescriptorName);
    }
    return getRepository().getItemDescriptor(itemDescriptorName);
  }

  Map initPropertyNameChoices() {
    if (isLoggingDebug())
      logDebug("in initPropertyNameChoices.............................");
    HashMap results = new HashMap();
    try {
      RepositoryItemDescriptor itemdesc = getItemDescriptor();
      if (itemdesc != null) {
      
        // arrange the properties into lists by type
        DynamicPropertyDescriptor props[] = itemdesc.getPropertyDescriptors();
        String name = null;
        String type = null;
        for (int ii=0; ii<props.length; ii++) {
          name = props[ii].getName();
          type = props[ii].getPropertyType().getName();
          if (isLoggingDebug()) {
            logDebug("name=" + name + " type="+type);
          }
          List propsOfType = (List)results.get(type);
          if (propsOfType == null)
            propsOfType = new ArrayList(); 
          propsOfType.add(name);
          results.put(type, propsOfType);
        }
      }
    }
    catch (RepositoryException e) {
      if (isLoggingError())
        logError(e);
    }
    return results;
  }  


  //-------------------------------------
  // property: stringProperties
  public List getStringProperties() {
    return (List)getPropertyNameChoices().get("java.lang.String");
  }

  //-------------------------------------
  // property: blobProperites
  public List getBlobProperties() {
    return (List)getPropertyNameChoices().get("[B");
  }
  
  //-------------------------------------
  // property: timestampProperties
  public List getTimestampProperties() {
    return (List)getPropertyNameChoices().get("java.sql.Timestamp");
  }

  //-------------------------------------
  // property: intProperties
  public List getIntProperties() {
    return (List)getPropertyNameChoices().get("java.lang.Integer");
  }

  //-------------------------------------
  // property: repositoryItemProperties
  public List getRepositoryItemProperties() {
    return (List)getPropertyNameChoices().get("atg.repository.RepositoryItem");
  }
  

  /**
   * Override this method to do any pre-processing or verification of form input.
   * Processing will be stopped if there are any errors found.  In the extension
   * of this method, if you encounter an error condition, you may use addFailureMessage()
   * or userInfo() to communicate the situation to the user.
   */
  
  public void preConfirm(DynamoHttpServletRequest pRequest,
                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    //Check maxFileSize is number
    Map values = getValues();
   
    if(values != null) {
      try { 
        String maxFileSizeValue = (String)values.get("maxFileSize"); 
        if(maxFileSizeValue != null) {
          Integer maxFileSize = Integer.valueOf(maxFileSizeValue);
        }
      } catch(NumberFormatException nfe) {
        addFailureMessage(RESOURCEBUNDLENAME,ERR_MAXFILESIZEFORMATEXCEPTION,null);
      }
    }    
  }
  
}

