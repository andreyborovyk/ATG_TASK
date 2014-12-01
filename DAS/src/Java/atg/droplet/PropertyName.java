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

import java.util.Vector;
import java.util.StringTokenizer;

import javax.servlet.*;

import atg.nucleus.naming.ComponentName;
import atg.nucleus.naming.ParameterName;
import atg.core.util.UnsynchronizedHashtable;
import atg.core.util.StringUtils;
import atg.core.util.ResourceUtils;
import atg.core.util.NumberTable;

/*
 * A PropertyName object can represent any property name 
 * used in DropletDescriptor.getPropertyValue() and 
 * DropletDescriptor.setPropertyValue()
 *<p>
 * To get the unique PropertyName for a given String call the static getPropertyName() 
 * method.
 *
 * @author Allan Scott
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/PropertyName.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class PropertyName implements DropletConstants
{  
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/PropertyName.java#2 $$Change: 651448 $";
  
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  

  //-------------------------------------
  /** A global hashtable which stores interned PropertyNames keyed by String */
  static UnsynchronizedHashtable sPropertyNames = new UnsynchronizedHashtable();

  //-------------------------------------
  public static PropertyName getPropertyName(String pName)  
    throws ServletException 
  {
    PropertyName name = (PropertyName) sPropertyNames.get(pName);
    
    if (name != null) 
      return name;
    
    name = parsePropertyName(pName);

    return name;
  } 

  //-------------------------------------
  public static PropertyName createPropertyName(String pName)  
  {
    try {
      return getPropertyName(pName);
    }
    catch (ServletException exc) {
    }
    return null;
  } 

   //-------------------------------------
  static PropertyName parsePropertyName(String pPropertyPath)
    throws ServletException
  {
    synchronized (sPropertyNames) {
      if (pPropertyPath == null)
	throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorInvalidPropPath",
								MY_RESOURCE_NAME, sResourceBundle));
      
      String componentNameString = DropletNames.getComponentPath(pPropertyPath);
    
      if (componentNameString == null) {
	Object[] mPatternArgs = { pPropertyPath };
	throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorInvalidPropPathPattern",
								MY_RESOURCE_NAME, sResourceBundle, 
								mPatternArgs));
      }  
      ComponentName componentName = ComponentName.getComponentName(componentNameString);

      PropertyName result;
      String path = pPropertyPath; 
      // unused int length = pPropertyPath.length();
      int ind;
      ind = pPropertyPath.indexOf('.', componentNameString.length());

      /*
       * This means that we just have the component, and no property name
       */
      if (ind == -1) {
	result = new PropertyName( pPropertyPath, 
				   componentName,    
				   null,
				   null);
      }
      else {	
	/*
	 * Strip off the component name
	 */
	Vector subnames = new Vector();
	PropertyName[] subNames = null;
	
	path = path.substring(ind+1);

	path = parseSubPropertyNames(pPropertyPath, path, subnames);
	
	// create subname array.
	if (subnames.size() > 0) {
	  subNames = new PropertyName[subnames.size()];
	  subnames.copyInto(subNames);
	}
	
	result = new PropertyName(pPropertyPath,
				  componentName,
				  subNames,
				  null);
      }

      // Place the result in the table 
      sPropertyNames.put(pPropertyPath, result);
      return result;
    }
  }

  static PropertyName parseSimplePropertyName(String pName)
    throws ServletException
  {
    return new PropertyName(pName, null, null, null);
  }

 
  static String parseSubPropertyNames(String pOriginalPath, 
				      String pPath, Vector names)
    throws ServletException
  {
    Vector dimensions = null;
    PropertyName lastName = null;
    // unused String nextToken;
    // unused String lastToken = null;
    Object[] dims; 
    
    String path = pPath;
       
    
    while (path != null &&
	   path.length() > 0) {
      int ind = path.indexOf('.');
      int startDimension = path.indexOf('[');
      dims = null;

      // A [ is the next delimitter, so parse out the
      // dimensions
      if ((startDimension > -1) &&
	  ((ind == -1)  ||
	   (startDimension < ind))) {
	
	if (dimensions == null) 
	  dimensions = new Vector();
	else dimensions.setSize(0);

	lastName = parseSimplePropertyName(path.substring(0,startDimension));
	names.addElement(lastName);

	path = parseDimensions(pOriginalPath, path, dimensions);

	// create subname array.
	if (dimensions.size() > 0) {
	  dims = new Object[dimensions.size()];
	  dimensions.copyInto(dims);
	}
	if (lastName != null)
	  lastName.setDimensions(dims);
      } else {
	// case where there is no more sub-properties
	if (ind == -1) { 
	  lastName = parseSimplePropertyName(path);
	  names.addElement(lastName);	
	}
	else { 
	  if (ind > 0) {
	    lastName = parseSimplePropertyName(path.substring(0, ind));
	    names.addElement(lastName);	
	  }
	}	
	if (ind == -1)
	  path = null;
	else path = path.substring(ind+1);
      }
    }
    return path;
  }


  static String parseDimensions(String pOriginalPath,
				String pPath, 
				Vector pDimensions) 
    throws ServletException
  {
    int startDimension, endDimension, ind;
    String symbol;

    String path = pPath;
    while (path != null) {
      startDimension = path.indexOf('[');
      ind = path.indexOf('.');

       if ((startDimension != -1)   &&
	   ((ind == -1) ||
	    (startDimension < ind))) {	 
	 // Case where the dimensions are before the next . 
	 endDimension = path.indexOf(']');
	 
	 // Case where no end-dimensions were specified
	 if (endDimension == -1) {
	   Object[] mPatternArgs = {pOriginalPath};
	   throw new DropletException(ResourceUtils.getMsgResource
				      ("dropletDescriptorInvalidDimensions", 
				       MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
	 }
	 symbol = path.substring(startDimension+1, endDimension);

	 // A symbol between [  and  ].
	 int start, len;
	 int pos = 0;
	 int end = symbol.length();
	 boolean isParam = false;
	 boolean isPrimitive = false;

	 start = symbol.indexOf(DROPLET_PARAM_PREFIX, pos);
	 if (start != -1) {
	   isParam = true;
	   len = DROPLET_PARAM_PREFIX.length();
	 }
	 else {
	   start = symbol.indexOf(DROPLET_BEAN_PREFIX, pos);
	   if (start == -1) {
	     start = symbol.indexOf(DROPLET_PROPERTY_PREFIX, pos);
	     if (start == -1) {
	       start = 0;
	       isPrimitive = true;
	       len = 0;
	     }
	     else len = DROPLET_PROPERTY_PREFIX.length();
	   } else  
	     len = DROPLET_BEAN_PREFIX.length();
	   isParam = false;
	 }
	 Object symbolValue;
	 symbol = symbol.substring(start + len, end);
	 if (isParam) {
	   symbolValue = ParameterName.getParameterName(symbol);
	   if (symbolValue == null) {
	     Object[] mPatternArgs = {symbol, 
				      pOriginalPath};	   
	     throw new DropletException(ResourceUtils.getMsgResource
					("dropletDescriptorArrayParameterNotDefined", 
					 MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
	   }
	 }
	 else if (isPrimitive) {
	   symbolValue = Integer.valueOf(symbol.trim()); 
	 } 
	 else {
	   symbolValue = PropertyName.getPropertyName(symbol);
	   
	   if (symbolValue == null){
	     Object[] mPatternArgs = {symbol, 
				      pOriginalPath};	   
	     throw new DropletException(ResourceUtils.getMsgResource
					("dropletDescriptorArrayPropertyNotDefined",
					 MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
	   }
	 }
	    
	 if (symbolValue != null)
	   pDimensions.addElement(symbolValue);

	 if (endDimension == path.length())
	   return null;
	 else {
	   path = path.substring(endDimension+1);
	 }
       }
       else break;	 
    }
      
    return path;
  }
  

  String mName;
  ComponentName mComponent;
  PropertyName[] mSubNames;
  Object[] mDimensions;

  PropertyName(String pName,
	       ComponentName pComponent,
	       PropertyName[] pSubNames,
	       Object[] pDimensions)
  {
    mName = pName;
    mComponent = pComponent;
    mSubNames = pSubNames;
    mDimensions = pDimensions;
  }
  
  public String getName() 
  {
    return mName;
  }

  public ComponentName getComponentName() 
  {
    return mComponent;
  }

  /**
   * Does this component name have subnames (delimmitted by ".")?
   */
  public boolean hasSubNames()
  {
    return (mSubNames != null);
  }
  
  /**
   * Get the sub names for this component.  null will be returned 
   * if there are no sub-names
   */
  public PropertyName[] getSubNames() 
  {
    return mSubNames;
  }

  /**
   * Does this parameter name have array dimensions (delimmitted by "[" and "]")?
   */
  public boolean hasDimensions()
  {
    return (mDimensions != null);
  }

  /**
   * Retrieve the dimensions of this parameter name.  If there are 
   * no dimensions null will be returned.
   */
  public Object[] getDimensions() 
  {
    return mDimensions;
  }

  /**
   * Set the dimensions of this parameter name.  
   */
  void setDimensions(Object[] pDimensions) 
  {
    mDimensions = pDimensions;
  }
  
  /**
   * toString()
   */
  public String toString()
  {
    return getName();
  }
}
