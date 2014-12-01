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

import javax.servlet.*;

import atg.servlet.*;

import java.io.*;
import java.util.*;

/**
 * This class implements a simple generic HTML tag.  This class
 * can either serve as a base class for other HTML tags or can be used
 * to represent any simple HTML tag directly. 
 * <p>
 * To render an input tag you can use: 
 * <code>
 * Tag t = new Tag();
 * t.setTagName("input");
 * t.setAttribute("type", "submit");
 * t.setAttribute("value", "Submit");
 * t.service(request, response);
 * </code>
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Tag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class Tag extends GenericServlet {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Tag.java#2 $$Change: 651448 $";

  //----------------------------------------
  /** Internal attribute that we use to store the name of the tag */
  final static String TAG_NAME_ATTRIBUTE = "_name";

  /** The set of attributes defined for this tag */
  Dictionary mAttributes = null;

  /** The current form that this tag is in (if any) */
  FormTag mForm = null;

  /** The current imports for this tag */
  DropletImports mImports = null;

  /** The tag whose content encloses this tag */
  ComplexTag mParentTag;

  /**
   * Sets the name of this tag.
   * 
   * @param pTagName the name of the tag.
   */
  public void setTagName(String pTagName) {
    /*
     * By default, store the name into the extra attributes hashtable.
     * Most subclass will override this, but this will allow the Tag
     * class to be used to specify any simple HTML tag
     */
    mAttributes = new Hashtable();
    mAttributes.put(TAG_NAME_ATTRIBUTE, pTagName);
  }

  /**
   * Returns the name of this tag or null if no name has been specified.
   */
  public String getTagName() {
    if (mAttributes == null) return null;
    return (String) mAttributes.get(TAG_NAME_ATTRIBUTE);
  }

  /**
   * Sets the parentTag of this tag.  This method is called automatically
   * by the parent's addContent method. 
   *
   * @param pTag the parent ComplexTag object
   */
  void setParentTag(ComplexTag pTag) {
    if (mParentTag == pTag) return;

    ComplexTag oldParentTag = mParentTag;

    mParentTag = pTag;

    /*
     * Propagate the form and imports down the hierarchy when the parent is
     * changed only if this tag previously inherited the value of the form
     * or imports from its old parent (i.e. they were unset if the parent
     * is not set or they were the same as the old parent).
     */
    if (oldParentTag == null) {
      if (mForm == null) {
        setForm(mParentTag.getForm());
      }
      if (mImports == null)
        setImports(mParentTag.getImports());
    }
    else {
      if (oldParentTag.getForm() == mForm)
        setForm(mParentTag.getForm());
      if (oldParentTag.getImports() == mImports)
        setImports(mParentTag.getImports());
    }
  }

  /**
   * Returns the parentTag for this tag
   */
  ComplexTag getParentTag() {
    return mParentTag;
  }

  /**
   * Sets the current form of this tag.  This is the form that encloses
   * this tag by one or more levels (if any).  This is called automatically
   * when the form's parent is set.
   */
  void setForm(FormTag pTag) {
    mForm = pTag;
  }

  /**
   * Returns the current form for this tag.  This will return null if
   * the form is not defined
   */
  FormTag getForm() {
    return mForm;
  }

  /**
   * Sets the current form of this tag.  This is the form that encloses
   * this tag by one or more levels (if any).  This is called automatically
   * when the form's parent is set.
   */
  void setImports(DropletImports pImports) {
    mImports = pImports;
  }

  /**
   * Returns the current form for this tag.  This will return null if
   * the form is not defined
   */
  DropletImports getImports() {
    return mImports;
  }

  /**
   * This sets the "Attributes" property for the Tag.  This contains a
   * Dictionary of attribute values that are defined at runtime for the
   * tag object.
   * 
   * @param pAttributes the attributes dictionary.
   */
  public void setAttributes(Dictionary pAttributes) {
    mAttributes = pAttributes;
  }

  /**
   * Returns the Attributes dictionary for this tag.
   */
  public Dictionary getAttributes() {
    return mAttributes;
  }

  /**
   * Sets the value of a specific attribute in the Attributes dictionary.
   * @param pName The name of the attribute
   * @param pValue the value of the attribute.  If this value is null, the
   * attribute is deleted.  If the attribute is an empty string, the attribute
   * displayed without the "=" sign.
   */ 
  public void setAttribute(String pName, Object pValue) {
    if (mAttributes == null) mAttributes = new Hashtable();
    if (pValue == null)
      mAttributes.remove(pName);
    mAttributes.put(pName, pValue);
  }

  /**
   * Returns the value of the specified attribute.
   */
  public Object getAttribute(String pName) {
    return mAttributes.get(pName);
  }

  /**
   * Vector off service requests to the dynamo specific routine.
   * We need to encode URLs for dynamo requests
   */
  public void service(ServletRequest pReq, ServletResponse pRes) 
       throws ServletException, IOException {
    if (pReq instanceof DynamoHttpServletRequest &&
        pRes instanceof DynamoHttpServletResponse)
      service((DynamoHttpServletRequest) pReq, 
              (DynamoHttpServletResponse) pRes);
  }

  /**
   * Renders the tag for the given request.  This method assumes that 
   * all attributes defined for the tag are in the Attributes dictionary
   * property. Subclasses can also override this method if they want to
   * define their property values more efficiently as member variables.
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes)
     throws ServletException, IOException {
    ServletOutputStream out = pRes.getOutputStream();

    out.print('<');
    out.print(getTagName());

    serviceAttributes(pReq, pRes);

    out.println('>');
  }

  /**
   * Renders the attributes defined for this tag.  Subclasses can use this
   * method to display the attributes in the Attributes dictionary in their
   * service method.
   */
  protected void serviceAttributes(DynamoHttpServletRequest pReq, 
                                   DynamoHttpServletResponse pRes) 
     throws ServletException, IOException {
    ServletOutputStream out = pRes.getOutputStream();
    
    if (mAttributes != null) {
      for (Enumeration e = mAttributes.keys(); e.hasMoreElements(); ) {
        String attName = (String) e.nextElement();
        if (attName.equals(TAG_NAME_ATTRIBUTE)) continue;

        Object value = mAttributes.get(attName);
        out.print(' ');
        out.print(attName);
        String valueStr = value.toString();
        if (valueStr != null && !valueStr.equals("")) {
          out.print('=');
          out.print(valueStr);
        }
      }
    }
  }
}
