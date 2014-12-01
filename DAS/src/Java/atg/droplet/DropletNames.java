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

import atg.core.util.StringUtils;
import atg.core.util.ResourceUtils;

import java.text.*;

/**
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletNames.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class DropletNames implements DropletConstants {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletNames.java#2 $$Change: 651448 $";
  
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //-------------------------------------
  // Static utilities for manipulating component, property path names.

  //-------------------------------------
  /*
   * @param pPropertyPath the path name in the form: "/path/component.property"
   *
   * @return the component name in the specified property path
   */
  public static String getComponentPath(String pPropertyPath) {
    int i, end;

    // Chop off any property array subscript because it can have its
    // own component path which we're not interested in.
    i = pPropertyPath.indexOf('[');
    if (i != -1)
      pPropertyPath = pPropertyPath.substring(0, i);

    // Locate the property portion and return everything prior to it.
    i = pPropertyPath.lastIndexOf('/');
    if (i == -1) i = 0;
    end = pPropertyPath.indexOf('.', i);

    if (end != -1)
      return pPropertyPath.substring(0, end);
    else
      // Return whole thing if no property specification.  (Nothing
      // should get chopped off above if there' no property spec.)
      return pPropertyPath;
  }

  //-------------------------------------
  /*
   * @param pPropertyPath the path name in the form: "/path/component.property"
   *
   * @return the name of the property in the path (e.g. property in the above
   * example).
   */
  public static String getPropertyName(String pPropertyPath) {
    int ix, endIx;

    /*
     * First strip out the [...] since they don't contribute to the
     * property name.
     */
    while ((ix = pPropertyPath.indexOf('[')) != -1) {
      endIx = pPropertyPath.indexOf(']', ix);
      if (endIx == -1) 
        break;
      pPropertyPath = pPropertyPath.substring(0,ix) + 
      		      pPropertyPath.substring(endIx+1);

    }

    ix = pPropertyPath.lastIndexOf('.');
    if (ix == -1)
      return null;
    return pPropertyPath.substring(ix+1);
  }

  //-------------------------------------
  /**
   * @param pComponentPath a path name of the form: "/path/componentName"
   *
   * @return strips off the path and returns the name of the component
   */
  public static String getComponentName(String pComponentPath) {
    int ind;
    if ((ind = pComponentPath.lastIndexOf('/')) != -1) {
      int len = pComponentPath.length();
      if (len != ind+1) {
        return pComponentPath.substring(ind+1, len);
      }
    }
    return pComponentPath;
  }

  //-------------------------------------
  /**
   * Returns true if the path name is explicitly defined (i.e. we don't
   * check for imports)
   */
  public static boolean isExplicitPath(String pPropertyPath) {
    char c;

    if (pPropertyPath.length() > 1 && 
        ((c = pPropertyPath.charAt(0)) == '/' || c == '.')) 
      return true;

    return false;
  }

  //-------------------------------------
  /**
   * Returns true if the path name is an absolute path (i.e. /xxx/yyy)
   */
  public static boolean isAbsolutePath(String pPropertyPath) {
    if (pPropertyPath.length() > 1 && pPropertyPath.charAt(0) == '/')
      return true;

    return false;
  }

  /**
   * This is a utility method used by the EventReceiver classes to compute
   * the list of path names and list of dimensions for their current 
   * PropertyPath.  It theoretically should be a method in each EventReceiver
   * class, but since they don't share a common ancestor, it is here instead.
   */
  public static void initPathNames(EventReceiver pER) throws DropletException {
    String origPath = pER.getPropertyPath();
    String [] pathNames = DropletNames.getPathNames(origPath);
    int [][] pathDims = null;
    for (int i = 0; i < pathNames.length; i++) {
      // unused int end;
      String path = pathNames[i];
      int start = path.indexOf('[');

      if (start != -1) {
        if (pathDims == null)
          pathDims = new int[pathNames.length][];

        pathDims[i] = getDimensions(path.substring(start), origPath);
        pathNames[i] = path.substring(0, start);
      }
      else if (pathDims != null) 
        pathDims[i] = null;
    }
    pER.setPathNames(pathNames);
    pER.setPathDims(pathDims);
  }

  public static String [] getPathNames(String pPath) {
    String [] paths;

    /*
     * First have to skip to the last '/' (in case path has ../ or ./)
     */
    int start = pPath.lastIndexOf('/');
    if (start == -1)
      start = 0;

    int ix;
    int ct = 1;
    int pos = start;
    /*
     * Count the dots that follow the component part
     */
    while ((ix = pPath.indexOf('.', pos)) != -1) {
      pos = ix + 1;
      ct++;
    }
    paths = new String[ct];
    pos = 0;
    for (int i = 0; i < ct-1; i++) {
      ix = pPath.indexOf('.', start);
      paths[i] = pPath.substring(pos, ix);
      pos = ix + 1;
      start = pos; /* after the first entry start syncs up with pos */
    }
    paths[ct-1] = pPath.substring(start);

    return paths;
  }

  /**
   * Returns the array of dimensions given a string of the form [3][2][4]
   */
  static int [] getDimensions(String pDims, String pPath) throws DropletException {
    int start, end = 0;
    int [] dims = null;
    while ((start = pDims.indexOf('[', end)) != -1) {
      end = pDims.indexOf(']',start);
      if (end == -1) {
        Object[] mPatternArgs = { pPath };
        throw new DropletException(ResourceUtils.getMsgResource
				   ("dropletNamesInvalidDimensions", 
				    MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
      }
      try {
        if (dims == null) dims = new int[1];
        else {
          int [] old = dims;
          dims = new int[dims.length+1];
          for (int j = 0; j < old.length; j++)
            dims[j] = old[j];
        }
        dims[dims.length-1] = Integer.parseInt(pDims.substring(start+1, end).trim());
      }
      catch (NumberFormatException e) {
        Object[] mPatternArgs = { pPath };
        throw new DropletException(ResourceUtils.getMsgResource
				   ("dropletNamesInvalidDimension", 
				    MY_RESOURCE_NAME, sResourceBundle,
				    mPatternArgs), "invalidArrayDimension");  
      }
    }
    return dims;
  }
}
