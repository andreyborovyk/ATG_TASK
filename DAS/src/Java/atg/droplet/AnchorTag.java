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
import atg.core.util.SingleEnumeration;
import atg.core.util.ResourceUtils;

import atg.naming.*;
import atg.nucleus.*;

import atg.servlet.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

/**
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/AnchorTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class AnchorTag extends EventSender implements EventReceiver {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/AnchorTag.java#2 $$Change: 651448 $";

  //-------------------------------------
  public final static String TAG_NAME = "a";

  //-------------------------------------
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //-------------------------------------
  // Member variables

  //---------------------------
  /** This is a unique id used to represent this anchor tag */
  String mAnchorId = null;

  //---------------------------
  String mPropertyPath = null;

  //---------------------------
  /** Anchor's name attribute */
  String mName = null;

  //---------------------------
  /* Anchor's href attribute */
  String mHref = null;
  
  //---------------------------
  /** The listing of anchor's href **/
  Set mHrefList = Collections.synchronizedSet(new HashSet());

  //-------------------------------------
  /** The path names of the entries */
  String [] mPathNames;

  //-------------------------------------
  /** The list of dimensions for each entry */
  int [][] mPathDims;

  //-------------------------------------
  /** The converter itself (or null if we aren't using one) */
  TagConverter mConverter = null;

  //-------------------------------------
  /** The arguments to pass to the specified converter */
  Properties mConverterArgs = null;

  //-------------------------------------
  boolean mRequiresSessionConfirmation = true;

  //-------------------------------------  
  /** The droplet event servlet. */
  DropletEventServlet mDropletEventServlet;
  

  //-------------------------------------
  /* No attributes are required on all anchor tags (name, href, etc.) */ 
  public AnchorTag () {
  }

  public AnchorTag (String pAnchorId, DropletEventServlet pServlet) {
    this(pAnchorId, pServlet, null, null);
  }

  //-------------------------------------
  /* This takes the Anchor ID to be used for delivering droplet events */
  public AnchorTag (String pAnchorId, DropletEventServlet pServlet,
  		    TagConverter pConverter, Properties pConverterArgs) {
    mAnchorId = pAnchorId;
    mConverter = pConverter;
    mConverterArgs = pConverterArgs;
    mDropletEventServlet = pServlet;    
    pServlet.addEventSender(pAnchorId, this);

  }

  //---------------------------
  /** 
   * @return the default value to use in a set event if no value was
   * specified 
   */
  public String getDefault() {
    return null;
  }

  /** 
   * @return the submit value to use to override the form value.
   */
  public String getSubmitValue() {
    return null;
  }

  public boolean isImage() {
    return false;
  }

  /**
   * Since anchor's only have a single property value, we don't need
   * to have unique names for them.  This name is used for the anchor's
   * property value.
   */
  public String getName() {
    if (mName == null)
      return DROPLET_ANCHOR_VALUE;
    return mName;
  }

  public void setName(String pName) {
    mName = pName;
  }

  public String getTagName() {
    return TAG_NAME;
  }

  public String getHref() {
    return mHref;
  }

  /*
   * Sets hRef attribute.  
   */
  public void setHref(String pHref) {
    mHref = pHref;
  }

  /**
   * Return the complete path name of the property
   */
  public String getPropertyPath() {
    return mPropertyPath;
  }

  /**
   * Sets the complete path to find the property
   */
  public void setPropertyPath(String pPropertyPath) 
     throws DropletException {
    if (mPropertyPath == null || !mPropertyPath.equals(pPropertyPath)) {
      mPropertyPath = pPropertyPath;
      DropletNames.initPathNames(this);
    }
  }

  /**
   * Return the name of the property for the component 
   */
  public String [] getPathNames() {
    return mPathNames;
  }

  /**
   * Return the name of the property for the component 
   */
  public int [][] getPathDims() {
    return mPathDims;
  }

  /**
   * Sets the list of path names.
   */
  public void setPathNames(String [] pPathNames) {
    mPathNames = pPathNames;
  }

  /**
   * Sets the list of dimensions.
   */
  public void setPathDims(int [][] pPathDims) {
    mPathDims = pPathDims;
  }

  /**
   * Return the path name of the component 
   */
  public String getComponentPath() {
    return mPathNames[0];
  }



  //---------------------------------------------------------------------------
  // property: requiresSessionConfirmation

  /** Whether this form requires session confirmation. Defaults to true. */
  public void setRequiresSessionConfirmation(boolean pRequiresSessionConfirmation) {
    mRequiresSessionConfirmation = pRequiresSessionConfirmation;
  }

  /** Whether this form requires session confirmation. Defaults to true. */  
  public boolean getRequiresSessionConfirmation() {
    return mRequiresSessionConfirmation;
  }
  

  //-------------------------------------
  /**
   * Adds a new action/anchor to the list
   * @param pActionURL the action to be added
   */
  public void addHref (String pHref){
    mHrefList.add (pHref);
  }

  //-------------------------------------
  /**
   * Returns true if the href is exists,
   * false if not.
   */
  public boolean hasHref (String pHref) {
    boolean bResult;
    if ( numHrefs () > 0 ) {
      bResult = mHrefList.contains(pHref);
      // now check if any of the stored Hrefs end with "/" or ".", in this case hrefs in that directory should be allowed
        if (!bResult){ 
        	String str;
        	// iterate through all URL stored in mHrefList
        	Iterator iter = mHrefList.iterator();
        	while (iter.hasNext()){
          	str = (String)iter.next();
          	if (str.endsWith("/") | str.endsWith(".")) {
            	// now check if the directory of the href and the directory of the stored href are the same if they are return true              
              if (str.equals(pHref.substring(0,pHref.lastIndexOf("/")+ 1))) return true;
          	}
        	}   
      	}  
      return bResult;
    }
    return true;  // previous pagecompiled page also work
  }

  //-------------------------------------
  /**
   * Returns the number of hrefs
   */
  public int numHrefs () {
    return mHrefList.size ();
  }

  //-------------------------------------
  /**
   * Returns the hrefList
   */
  public Set getHrefList(){
    return mHrefList;
  }

  //-------------------------------------
  boolean sendEvents (DynamoHttpServletRequest pReq,
  		      DynamoHttpServletResponse pRes)
         throws DropletException, ServletException, IOException {

    if (mDropletEventServlet == null)
      mDropletEventServlet = (DropletEventServlet) pReq.getAttribute(DROPLET_EVENT_ATTRIBUTE);

    if (mDropletEventServlet == null)
      return false;
    
    
    if (getRequiresSessionConfirmation() && 
        !mDropletEventServlet.validateSessionConfirmationNumber(pReq)) {
      // don't do any processing
      pRes.sendError(409,  "Session timed out. Please use the back button, reload and try again.");
      return false;
    }

    
    Vector beforeSetObjects = new Vector(1);
    try {
      return sendEvent(pReq, pRes, this, new Object[1], beforeSetObjects);
    }
    finally {
      callAfterSet(pReq, pRes, beforeSetObjects);
    }
  }

  /**
   * Renders the tag and its content
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
     throws ServletException, IOException {
    ServletOutputStream out = pRes.getOutputStream();
    boolean hasQuery = false;

    if (mPropertyPath != null) {
      if (mAnchorId == null)
	throw new ServletException(ResourceUtils.getMsgResource("anchorTagMissing", 
								MY_RESOURCE_NAME, sResourceBundle));
				   
      pReq.addQueryParameter(DROPLET_ARGUMENTS, mAnchorId);
      if (getRequiresSessionConfirmation()) {
        pReq.addQueryParameter(DropletConstants.DROPLET_SESSION_CONF,
                               Long.toString(pReq.getSessionConfirmationNumber()));
      }
      hasQuery = true;
    }

    out.print("<a");
    if (mHref != null) {
      out.print(" href=\"");
      out.print(pReq.encodeURL(mHref));
      out.print('"');
    }
    else if (hasQuery) {
      out.print(" href=\"");
      out.print(pReq.encodeURL("."));
      out.print('"');
    }
    if (mName != null) {
      out.print(" name=\"");
      out.print(mName);
      out.print('"');
    }

    /* Render any attributes set with setAttribute */
    serviceAttributes(pReq, pRes);

    out.println('>');
    serviceContent(pReq, pRes);
    out.println("</a>");
  }

  Enumeration getEventReceivers() {
    return new SingleEnumeration(this);
  }

  int getNumEventReceivers() {
    return 1;
  }

  public TagConverter getConverter() {
    return mConverter;
  }

  public Properties getConverterArgs() {
    return mConverterArgs;
  }


  //-------------------------------------
}
