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
 </ATGCOPYRIGHT>
 */

package atg.portal.gear.repview.taglib;

import atg.beans.*;
import atg.portal.framework.*;
import atg.repository.*;
import atg.core.util.StringUtils;

import java.sql.Timestamp;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/PropertyInfoTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PropertyInfoTag extends TagSupport
{

    public static String CLASS_VERSION = 
	"$Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/PropertyInfoTag.java#2 $$Change: 651448 $";

    //----------------------------------------------------
    // input properties
    //----------------------------------------------------

    /** the name of the property to display */
    String mPropertyName;

    //-------------------------------------
    /**
     * Sets the name of the property to display
     **/
    public void setPropertyName(String pPropertyName) {
        mPropertyName = pPropertyName;
    }

    //-------------------------------------
    /**
     * Returns the name of the property to display
     **/
    public String getPropertyName() {
        return mPropertyName;
    }

    /** the repository item for the given itemId */
    RepositoryItem mItem;

    //-------------------------------------
    /**
     * Sets the repository item for the given itemId
     **/
    public void setItem(RepositoryItem pItem) {
        mItem = pItem;
    }

    //-------------------------------------
    /**
     * Returns the repository item for the given itemId
     **/
    public RepositoryItem getItem() {
        return mItem;
    }    

    //----------------------------------------------------
    // output properties
    //----------------------------------------------------    


    /** the value of the property */
    Object mPropertyValue;

    //-------------------------------------
    /**
     * Sets the value of the property
     **/
    public void setPropertyValue(Object pPropertyValue) {
        mPropertyValue = pPropertyValue;
    }

    //-------------------------------------
    /**
     * Returns the value of the property
     **/
    public Object getPropertyValue() {
        return mPropertyValue;
    }



    /** the property descriptor of the requested property */
    DynamicPropertyDescriptor mPropertyDescriptor;

    //-------------------------------------
    /**
     * Sets the property descriptor of the requested property
     **/
    public void setPropertyDescriptor(DynamicPropertyDescriptor pPropertyDescriptor) {
        mPropertyDescriptor = pPropertyDescriptor;
    }

    //-------------------------------------
    /**
     * Returns the property descriptor of the requested property
     **/
    public DynamicPropertyDescriptor getPropertyDescriptor() {
        return mPropertyDescriptor;
    }


    //----------------------------------------------------
    // other properties
    //----------------------------------------------------    

    /** whether to log debug message to the console */
    boolean mLoggingDebug = false;

    //-------------------------------------
    /**
     * Sets whether to log debug message to the console
     **/
    public void setLoggingDebug(boolean pLoggingDebug) {
        mLoggingDebug = pLoggingDebug;
    }

    //-------------------------------------
    /**
     * Returns whether to log debug message to the console
     **/
    public boolean isLoggingDebug() {
        return mLoggingDebug;
    }

    //-------------------------------------
    /**
     *	doStartTag method
     *	@return int
     */
    public int doStartTag()
    {
        pageContext.setAttribute(getId(), this);
        try {
            if (getPropertyName().indexOf(".") == -1) {
                setPropertyValue(getItem().getPropertyValue(getPropertyName()));
                setPropertyDescriptor 
                    ((DynamicPropertyDescriptor)getItem().getItemDescriptor().getPropertyDescriptor(getPropertyName()));
            }
            else {
                String proplist[] = StringUtils.splitStringAtCharacter(getPropertyName(),'.');
                String lastpropname = proplist[proplist.length-1];
                Object value = null;
                RepositoryItem tempRepItem = getItem();
                for (int ii=0; ii<proplist.length-1; ii++) {
                    if (tempRepItem != null)
                      tempRepItem = (RepositoryItem)tempRepItem.getPropertyValue(proplist[ii]);
                }
                if (tempRepItem != null) {
                    setPropertyValue(tempRepItem.getPropertyValue(lastpropname));
                    setPropertyDescriptor
                        ((DynamicPropertyDescriptor)tempRepItem.getItemDescriptor().getPropertyDescriptor(lastpropname));
                }
                if (getPropertyValue() == null)
                  setPropertyValue("");
            }
        }
        catch (RepositoryException e) {
            pageContext.getServletContext().log (e.getMessage());
        }
        return EVAL_BODY_INCLUDE;
    }
    
    /**
     *	doEndTag method.
     *	@return int
     *	@throws JspException
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
	
    /**
     *	Sets all attributes to null, releasing memory.
     */
    public void release() {
        super.release();
        
        setId( null );
        setItem( null );
        setPropertyName( null );
        setPropertyValue( null );
        setPropertyDescriptor( null );
    }
    
} //end class GetItemTag

