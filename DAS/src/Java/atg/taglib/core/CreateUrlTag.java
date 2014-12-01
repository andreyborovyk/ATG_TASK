/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.taglib.core;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * The core:CreateUrl tag is used to dynamically construct a url with
 * param/value pairs. The param/value pairs are defined using the
 * core:UrlParam child tag. This allows a variable number of param/values to be
 * defined. The tag processes its core:UrlParam subtags and exports a
 * property called newUrl which contains the constructed url. The
 * toString method of this tag object is overridden to make a call to
 * getNewUrl. So one could access the newUrl property by simply
 * referencing the id of the tag.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;core:CreateUrl id="myUrl" url="http://foo.bar/cheese" encode="false"&gt;
 *   &lt;core:UrlParam param="name" value="stephen"/&gt;
 *   &lt;core:UrlParam param="age" value="23"/&gt;
 * 
 *   Hey, why not click &lt;a href="&lt;%= myUrl.getNewUrl() %&gt;"&gt;this&lt;/a&gt; link.
 * &lt;/core:CreateUrl&gt;
 * </pre>
 * </code>
 * <p>
 * In the above example, the expression <code>&lt;%= myUrl.getNewUrl() %&gt;</code>
 * would get evaluated as
 * <code>http://foo.bar/cheese?name=stephen&age=23</code>. The expression
 * <code>&lt;%= myUrl %&gt;</code> would also get evaluated to the same string.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CreateUrlTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class CreateUrlTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CreateUrlTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // the default value for encoding urls
    private boolean kEncodeURLsDefaultValue = true;

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Id
    private String mId;
    /**
     * set Id
     * @param pId the Id
     */
    public void setId(String pId) { mId = pId; }
    /**
     * get Id
     * @return the Id
     */
    public String getId() { return mId; }

    //----------------------------------------
    // Url
    private String mUrl;
    /**
     * set Url
     * @param pUrl the Url
     */
    public void setUrl(String pUrl) { mUrl = pUrl; }
    /**
     * get Url
     * @return the Url
     */
    public String getUrl() { return mUrl; }

    //----------------------------------------
    // Encode
    private boolean mEncode;
    /**
     * set Encode
     * @param pEncode the Encode
     */
    public void setEncode(String pEncode) { mEncode = Boolean.valueOf(pEncode).booleanValue(); }
    public void setEncode(boolean pEncode) { mEncode = pEncode; }
    /**
     * get Encode
     * @return the Encode
     */
    public boolean isEncode() { return mEncode; }

    //----------------------------------------
    // Xml
    private boolean mXml;
    /**
     * set Xml
     * @param pXml the Xml
     */
    public void setXml(String pXml) { mXml = Boolean.valueOf(pXml).booleanValue(); }
    public void setXml(boolean pXml) { mXml = pXml; }
    /**
     * get Xml
     * @return the Xml
     */
    public boolean isXml() { return mXml; }

    //----------------------------------------
    // NewUrl
    private String mNewUrl;
    /**
     * set NewUrl
     * @param pNewUrl the NewUrl
     */
    public void setNewUrl(String pNewUrl) { mNewUrl = pNewUrl; }
    /**
     * get NewUrl
     * @return the NewUrl
     */
    public String getNewUrl() { return mNewUrl; }

    //----------------------------------------
    // FirstParamValuePair
    private boolean mFirstParamValuePair;
    /**
     * set FirstParamValuePair
     * @param pFirstParamValuePair the FirstParamValuePair
     */
    public void setFirstParamValuePair(boolean pFirstParamValuePair) { mFirstParamValuePair = pFirstParamValuePair; }
    /**
     * get FirstParamValuePair
     * @return the FirstParamValuePair
     */
    public boolean isFirstParamValuePair() { return mFirstParamValuePair; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof CreateUrlTag
     */
    public CreateUrlTag()
    {
	setEncode(kEncodeURLsDefaultValue);
	setFirstParamValuePair(true);
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * append a param/value pair
     */
    public void append(String pParam,
		       String pValue)
    {
	if(isFirstParamValuePair()) {
	    setNewUrl(getNewUrl().concat("?" + pParam + "=" + pValue));
	    setFirstParamValuePair(false);
	}
	else {
	    if(isXml())
		setNewUrl(getNewUrl().concat("&amp;" + pParam + "=" + pValue));
	    else
		setNewUrl(getNewUrl().concat("&" + pParam + "=" + pValue));
	}
    }

    //----------------------------------------
    /**
     * start executing the tag
     */
    public int doStartTag()
	throws JspException
    {
	if(getUrl() != null) {
	    // Bug#66737: If there's already a '?' in the URL,
	    // then there's already a first param in the URL.
	    if (getUrl().indexOf('?') != -1)
		setFirstParamValuePair(false);

	    // put the session id in the new url
	    if(isEncode())
		setNewUrl(((HttpServletResponse) getPageContext().getResponse()).encodeURL(getUrl()));
	    else
		setNewUrl(getUrl());

	    getPageContext().setAttribute(getId(), this);

	    return EVAL_BODY_INCLUDE;
	}

	return SKIP_BODY;
    }

    //----------------------------------------
    /**
     * end this tag
     */
    public int doEndTag()
	throws JspException
    {
	setFirstParamValuePair(true);

	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	
	setFirstParamValuePair(true);
	setId(null);
	setUrl(null);
	setEncode(true);
    }

    //----------------------------------------
    /**
     * override toString to return the new url
     */
    public String toString()
    {
	return getNewUrl();
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
