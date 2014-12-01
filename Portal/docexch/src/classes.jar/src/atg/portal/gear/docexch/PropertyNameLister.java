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

import atg.beans.*;
import atg.droplet.DropletException;
import atg.nucleus.naming.ParameterName;
import atg.portal.framework.*;
import atg.servlet.*;
import atg.repository.*;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Hashtable;


/**
 * This servlet uses takes in a gear environment and a property name will
 * output a list of the properties of that property.  If the property name 
 * passed in does not refer to a property of the itemType and repository 
 * managed by this gear, we will return null.  If the property name passed
 * in is not a property of type RepositoryItem, we will return null. 
 * Input params:
 * <UL>
 * <LI>gearEnv the gear environment
 * <LI>property the property whose properties we will list
 * <LI>type the type (i.e. String) of properties we want.  if this is 
 *       null, we return all properties 
 * </UL>
 * Output params:
 * <UL>
 * <LI>results: the list of properites of that property that match the desired property type
 * </UL>
 * 
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/PropertyNameLister.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PropertyNameLister extends DynamoServlet
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/PropertyNameLister.java#2 $$Change: 651448 $";


  public static final ParameterName PROPERTY = ParameterName.getParameterName("property");
  public static final ParameterName TYPE = ParameterName.getParameterName("type");
  public static final ParameterName GEAR_ENV = ParameterName.getParameterName("pafEnv");
  
  public static final String RESULTS = "results";
  public static final String OUTPUT = "output";

  public static final String REP_PATH_PARAM_NAME  = "repositoryPath";
  public static final String ITEM_TYPE_PARAM_NAME = "itemDescriptorName";

  

  public void service(DynamoHttpServletRequest pReq,
                      DynamoHttpServletResponse pRes)
    throws ServletException, IOException 
  {
    Object gearEnvObj = pReq.getObjectParameter(GEAR_ENV);
    GearEnvironment gearEnv = (GearEnvironment)gearEnvObj;
    if (gearEnv == null) {
        if (isLoggingError())
            logError("null param pafEnv");
        return;
    }
        

    String propertyName = pReq.getParameter(PROPERTY);
    String reppath = gearEnv.getGearInstanceParameter(REP_PATH_PARAM_NAME);
    String itemdescname = gearEnv.getGearInstanceParameter(ITEM_TYPE_PARAM_NAME);

    if (isLoggingDebug()) {
        logDebug("reppath = " + reppath);
        logDebug("itemdescname = " + itemdescname);
        logDebug("propertyName = " + propertyName);
    }
    
    try {
        //get repository (e.g. DocumentRepository)
        Repository rep = (Repository)pReq.resolveName(reppath);
        if (isLoggingDebug())
            logDebug ("got rep");

        // get item descriptor (e.g. document)
        ItemDescriptorImpl itemdesc = (ItemDescriptorImpl)rep.getItemDescriptor(itemdescname);
        if (isLoggingDebug())
            logDebug ("got itemdesc");
        
        // get property descriptor (e.g. author)
        RepositoryPropertyDescriptor pdesc = (RepositoryPropertyDescriptor)
            itemdesc.getRepositoryPropertyDescriptor(propertyName);
        if (isLoggingDebug())
            logDebug("got pdesc");
        
        RepositoryItemDescriptor subitemdesc = pdesc.getPropertyItemDescriptor();
        if (isLoggingDebug())
            logDebug("got subitemdesc");
        
        if (subitemdesc == null) {
            if (isLoggingDebug())
                logDebug("the property name given does not correspond to a repository item"); 
            return;
        }
        
        // arrange the properties into lists by type
        DynamicPropertyDescriptor props[] = subitemdesc.getPropertyDescriptors();
        String desiredType = pReq.getParameter(TYPE);
        
        Hashtable propsOfDesiredType = new Hashtable();
        String name = null;
        String type = null;
        
        for (int ii=0; ii<props.length; ii++) {
            if (desiredType != null) {
                type = props[ii].getPropertyType().getName();
                if (desiredType.equals(type)) 
                    propsOfDesiredType.put(props[ii].getName(), props[ii].getDisplayName());
            }
            else {
                propsOfDesiredType.put(props[ii].getName(), props[ii].getDisplayName());
            }
        }
        
        pReq.setParameter(RESULTS, propsOfDesiredType);
        pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
    }
    catch (RepositoryException e) {
        if (isLoggingError())
            logError(e);
    }
    
  }
}
