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

import java.util.*;
import java.io.*;

/**
 * This class both serves as the base class for any tags that contain content
 * and also can be used to dynamically render any HTML object.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ComplexTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class ComplexTag extends Tag {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ComplexTag.java#2 $$Change: 651448 $";

  Vector mContent;

  /*
   * If we are changing the form of a complex tag that has children
   * propagate the form down the HTML hierarchy so that all input tags
   * even under other nodes will have the form.
   */
  synchronized void setForm(FormTag pForm) {
    if (pForm != mForm) {
      mForm = pForm;
      if (mContent != null) {
        for (int i = 0; i < mContent.size(); i++) {
          Object obj = mContent.elementAt(i);
          if (obj instanceof Tag)
            ((Tag) obj).setForm(mForm);
        }
      }
    }
  }

  /*
   * If we are changing the form of a complex tag that has children
   * propagate the form down the HTML hierarchy so that all input tags
   * even under other nodes will have the form.
   */
  synchronized void setImports(DropletImports pImports) {
    if (pImports != mImports) {
      mImports = pImports;
      if (mContent != null) {
        for (int i = 0; i < mContent.size(); i++) {
          Object obj = mContent.elementAt(i);
          if (obj instanceof Tag)
            ((Tag) obj).setImports(mImports);
        }
      }
    }
  }


  /**
   * Sets the Content Vector.  This is a Vector of Strings, Servlets,
   * and Tags that are to be rendered as the content for this tag.
   * Generally users add content to the Content Vector using the addContent 
   * method.
   *
   * @see #addContent
   */
  public void setContent(Vector pContent) {
    mContent = pContent;
  }

  /**
   * Returns the value of Content property.  This is a Vector of String,
   * Servlets and Tags that are rendered as the content for this tag.
   */
  public Vector getContent() {
    return mContent;
  }

  /**
   * Adds a Servlet to the content for this tag.  This Servlet will be
   * serviced when the content for the Tag is rendered
   */
  public void addContent(Servlet pServlet) {
    if (mContent == null) mContent = new Vector();
    if (pServlet instanceof Tag)
      addContent((Tag) pServlet);
    mContent.addElement(pServlet);
  }

  /**
   * Adds a Tag to the content for this tag.  This Tag will be
   * rendered when the content for the parent Tag is rendered
   */
  public void addContent(Tag pTag) {
    if (mContent == null) mContent = new Vector();
    mContent.addElement(pTag);
    pTag.setParentTag(this);
  }

  /**
   * Adds a String to the content for this tag.  This string will
   * rendered when the content for this parent Tag is rendered.
   */
  public void addContent(String pContent) {
    if (mContent == null) mContent = new Vector();
    mContent.addElement(pContent);
  }

  /**
   * Renders the tag and its content
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
     throws ServletException, IOException {
    ServletOutputStream out = pRes.getOutputStream();

    super.service(pReq, pRes);
    serviceContent(pReq, pRes);
    out.print("</");
    out.print(getTagName());
    out.println(">");
  }

  /**
   * This method just renders the content.  Subclasses can call this method
   * after they have rendered their start tag
   */
  protected void serviceContent(DynamoHttpServletRequest pReq, 
                                DynamoHttpServletResponse pRes) 
     throws ServletException, IOException {
    if (mContent == null) return;
    ServletOutputStream out = pRes.getOutputStream();

    for (int i = 0; i < mContent.size(); i++) {
      Object obj = mContent.elementAt(i);
      if (obj instanceof Servlet) {
        ((Servlet)obj).service((ServletRequest)pReq, pRes);
      }
      else if (obj instanceof String) {
        out.print((String) obj);
      }
    }
  }
}
