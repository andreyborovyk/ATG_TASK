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
import atg.portal.nucleus.NucleusComponents;

import java.sql.Timestamp;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/GetItemTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class GetItemTag extends TagSupport
{

    public static String CLASS_VERSION = 
	"$Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/GetItemTag.java#2 $$Change: 651448 $";

    //----------------------------------------------------
    // input properties
    //----------------------------------------------------

    /** the path to the repository from which to get the item */
    String mRepositoryPath;

    //-------------------------------------
    /**
     * Sets the path to the repository from which to get the item
     **/
    public void setRepositoryPath(String pRepositoryPath) {
        mRepositoryPath = pRepositoryPath;
    }

    //-------------------------------------
    /**
     * Returns the path to the repository from which to get the item
     **/
    public String getRepositoryPath() {
        return mRepositoryPath;
    }


    /** the name of the ItemDescriptor for the item to retrieve */
    String mItemDescriptorName;

    //-------------------------------------
    /**
     * Sets the name of the ItemDescriptor for the item to retrieve
     **/
    public void setItemDescriptorName(String pitemDescriptorName) {
        mItemDescriptorName = pitemDescriptorName;
    }

    //-------------------------------------
    /**
     * Returns the name of the ItemDescriptor for the item to retrieve
     **/
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    /** the id of the item to retrieve */
    String mItemId;

    //-------------------------------------
    /**
     * Sets the id of the item to retrieve
     **/
    public void setItemId(String pItemId) {
        mItemId = pItemId;
    }

    //-------------------------------------
    /**
     * Returns the id of the item to retrieve
     **/
    public String getItemId() {
        return mItemId;
    }
    
    //----------------------------------------------------
    // output properties
    //----------------------------------------------------    

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
        
        
        String id = getItemId();
        Repository rep;
        try {
            rep = (Repository) NucleusComponents.lookup("dynamo:" + getRepositoryPath());
        }
        catch (javax.naming.NamingException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to get repository");
            return EVAL_BODY_INCLUDE;
        }
        
        if ((rep != null) && (id != null)) {
            try {
                if (isLoggingDebug())
                    pageContext.getServletContext().log(" Find item: id=" + id + "; type=" + getItemDescriptorName() + "; repository=" + rep.getRepositoryName());

                setItem(rep.getItem(id, getItemDescriptorName()));
                
                if (getItem() == null) {
                    if (isLoggingDebug())
                        pageContext.getServletContext().log(" Item not found");
                }
            }
            catch (RepositoryException exc) {
                pageContext.getServletContext().log(exc.getMessage());
            }
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
        setItemId( null );
    }
    
} //end class GetItemTag

