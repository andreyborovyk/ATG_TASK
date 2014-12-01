/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.droplet;

import atg.core.util.IntVector;
import atg.core.exception.BadValueException;
import atg.core.net.URLUtils;

import atg.naming.*;
import atg.nucleus.*;

import atg.servlet.pipeline.*;
import atg.servlet.DynamoHttpServletRequest;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletImports.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class DropletImports implements DropletConstants, Serializable {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletImports.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  //-------------------------------------
  /** List of DropletComponents */
  Vector mDropletComponents = new Vector();

  //-------------------------------------
  public DropletImports () {
  }

  //-------------------------------------
  /*
   * @param pComponentName the component's path name in the form: 
   * (absolute) "/path/component" or just "component" (relative).
   * 
   * @return the DropletComponent object that you can use to get quickly get
   * hold of this component later on.
   */ 
  public boolean addImport (String pComponentPath) {
    int size = mDropletComponents.size();
    int i;

    String pComponentName = DropletNames.getComponentName(pComponentPath);

    for (i = 0; i < size; i++) {
      String path = (String) mDropletComponents.elementAt(i);
      if (path.equals(pComponentPath))
        break;
      if (pComponentName.equals(DropletNames.getComponentName(path)))
        return false;
    }
    if (i == size) {
      mDropletComponents.addElement(pComponentPath);
    }
    return true;
  }

  /**
   * Takes a partially specified property path and completes it to a normalized
   * form for this component.  This will prepend the prefix of any matching
   * component.
   * 
   * @return the path name we use for this property including the complete
   * component name.
   */ 
  public String getCompletePath(String pPropertyPath) {
    if (pPropertyPath == null) return null;

    if (DropletNames.isExplicitPath(pPropertyPath))
      return pPropertyPath;

    String componentPath = DropletNames.getComponentPath(pPropertyPath);

    /*
     * If just the component was specified, that is OK as we allow imports
     * on <droplet name="/xxx/yyy"> constructs too. 
     */
    if (componentPath == null)
      componentPath = pPropertyPath;

    for (int i = 0; i < mDropletComponents.size(); i++) {
      String path = (String) mDropletComponents.elementAt(i);

      /*
       * If the component is specified the same way that the property is,
       * just use the propertyPath the way that it is 
       */
      if (path.equals(componentPath))
        return pPropertyPath;

      int ind = path.lastIndexOf(componentPath);
      if (ind != -1) {
        if (ind > 0 && path.charAt(ind-1) == '/' && 
            ind + componentPath.length() == path.length()) {
          return path + pPropertyPath.substring(componentPath.length());
        }
      }
    }
    return pPropertyPath;
  }

  /**
   * First takes the path name and maps it against any imports that are
   * in effect.  If the path is still relative, we then have to 
   * make the path be absolute relative to the current request
   */
  public String getAbsolutePath(DynamoHttpServletRequest pRequest, 
  				String pPropertyPath) {
    if (pPropertyPath == null) return null;
    String path = getCompletePath(pPropertyPath);
    if (!DropletNames.isAbsolutePath(path)) {
      String pathInfo = pRequest.getPathInfo();
      if (pathInfo != null) {
        String ctxDir = URLUtils.URIDirectory(pathInfo);
        if (ctxDir != null) {
          if (!ctxDir.endsWith("/"))
            path = pRequest.getDocRootServicePrefix() + ctxDir + '/' + pPropertyPath;
          else
            path = pRequest.getDocRootServicePrefix() + ctxDir + pPropertyPath;
        }
      }
    }
    return path;
  }
}
