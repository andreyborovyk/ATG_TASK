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
import atg.portal.nucleus.NucleusComponents;
import atg.adapter.gsa.*;
import atg.servlet.ServletUtil;

import java.sql.Timestamp;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/GetPropertyListTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class GetPropertyListTag extends TagSupport
{

    public static String CLASS_VERSION = 
	"$Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/GetPropertyListTag.java#2 $$Change: 651448 $";

    //----------------------------------------------------
    // "input" properties
    //----------------------------------------------------

    /** the gear environment */
    GearEnvironment mGearEnv;

    //-------------------------------------
    /**
     * Sets the gear environment.  This is the "input" property.
     *  Everything else comes from here.
     **/
    public void setGearEnv(GearEnvironment pGearEnv) {
        mGearEnv = pGearEnv;
    }

    //-------------------------------------
    /**
     * Returns the gear environment
     **/
    public GearEnvironment getGearEnv() {
        return mGearEnv;
    }

    /** the repository item whose property list we should get */
    RepositoryItem mItem;

    //-------------------------------------
    /**
     * Sets the repository item whose property list we should get
     **/
    public void setItem(RepositoryItem pItem) {
        mItem = pItem;
    }

    //-------------------------------------
    /**
     * Returns the repository item whose property list we should get
     **/
    public RepositoryItem getItem() {
        return mItem;
    }

    /** the repository */
    Repository mRepository;

    //-------------------------------------
    /**
     * Returns the repository
     **/
    public Repository getRepository() {
        try {
            mRepository = (Repository) 
                NucleusComponents.lookup("dynamo:" + getGearEnv().getGearInstanceParameter("repositoryPath"));
        }
        catch (javax.naming.NamingException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to get repository");
        }
        return mRepository;
    }


    /** the repository item descriptor */
    RepositoryItemDescriptor mItemDescriptor;
    
    //-------------------------------------
    /**
     * returns the item descriptor, initializing it if necessary.
     **/
    public RepositoryItemDescriptor getItemDescriptor() {
        try {
            if (getItem() != null) {
                mItemDescriptor = getItem().getItemDescriptor();
            }
            else {
                mItemDescriptor = getRepository().getItemDescriptor(getItemDescriptorName());
            }
        }
        catch (RepositoryException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to get ItemDescriptor");
        }
        
        return mItemDescriptor;
    }

    
    
    /** one of list or oneItem so we know which property list to get */
    String mDisplayType;
    
    //-------------------------------------
    /**
     * Sets one of list or oneItem so we know which property list to get
     **/
    public void setDisplayType(String pDisplayType) {
        mDisplayType = pDisplayType;
    }

    //-------------------------------------
    /**
     * Returns one of list or oneItem so we know which property list to get
     **/
    public String getDisplayType() {
        return mDisplayType;
    }


    /** the name of the itemdescriptor whose properties we will get */
    String mItemDescriptorName;

    //-------------------------------------
    /**
     * Sets the name of the itemdescriptor whose properties we will get
     **/
    public void setItemDescriptorName(String pItemDescriptorName) {
        mItemDescriptorName = pItemDescriptorName;
    }

    //-------------------------------------
    /**
     * Returns the name of the itemdescriptor whose properties we will get
     **/
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }


    //----------------------------------------------------
    // "output" properties
    //----------------------------------------------------
    /*----------------------------------------------------
     * property: id
     */
    String mId;
    
    public void setId( String pId ) {
        mId = pId;
    }
    public String getId() {
        return mId;
    }

    public DynamicPropertyDescriptor mDisplayProperties[];
    //-------------------------------------
    /**
     * Returns the list of properties to display
     **/
    public DynamicPropertyDescriptor [] getDisplayProperties() 
      throws JspException
  {
        String propNames[] = getDisplayPropertyNames();
        mDisplayProperties = new DynamicPropertyDescriptor[propNames.length];
        for (int ii=0; ii<propNames.length; ii++) {
            //pageContext.getServletContext().log("prop name = " + propNames[ii]);
            
            if (propNames[ii].indexOf(".") == -1) {
                mDisplayProperties[ii] = getItemDescriptor().getPropertyDescriptor(propNames[ii]);
            }
            else {
                String proplist[] = StringUtils.splitStringAtCharacter(propNames[ii],'.');
                String lastpropname = proplist[proplist.length-1];
                Object value = null;
                RepositoryItemDescriptor tempDesc = getItemDescriptor();
                for (int jj=0; jj<proplist.length-1; jj++) {
                    if (tempDesc != null) 
                        if (tempDesc instanceof GSAItemDescriptor) {
                            GSAItemDescriptor gsaItDes = (GSAItemDescriptor)tempDesc;
                            GSAPropertyDescriptor pd = (GSAPropertyDescriptor)gsaItDes.getPropertyDescriptor(proplist[jj]);
                            if (pd == null)
                                pageContext.getServletContext().log("No such prop name propName[jj]");
                            tempDesc = pd.getPropertyItemDescriptor();
                            if (tempDesc == null)
                                pageContext.getServletContext().log("Invalid child property in propName[jj]");
                        }
                        else {
                            pageContext.getServletContext().log("Dotted properties allowed only for GSA repositories");
                        }
                }
                if (tempDesc != null) {
                    mDisplayProperties[ii] = tempDesc.getPropertyDescriptor(lastpropname);
                }
            }
            
            
            //if (mDisplayProperties[ii] == null)
            //  pageContext.getServletContext().log("prop is null");
            //else    
            //  pageContext.getServletContext().log("prop display name = " + mDisplayProperties[ii].getDisplayName());
            
        }
        return mDisplayProperties;
    }

    //-------------------------------------
    /**
     * Returns the list of property names to display
     **/
    public String [] getDisplayPropertyNames() 
      throws JspException
  {
        //pageContext.getServletContext().log(" RepView Gear: Initializing the DisplayPropertyNames list");
        
        // get the list from the item if we are looking at an item of a 
        // type other than the main type of this gear
        if (getItemDescriptorName() != null && 
            !getItemDescriptorName().equals(getGearEnv().getGearInstanceParameter("itemDescriptorName")))
            return getItemDescriptor().getPropertyNames();
        

        // get the list of properties to display from the gear instance parameters. 
        // if fullListDisplayPropertyNames is null, then default to listDisplayPropertynames.
        String propList = null;
        if (getDisplayType().equals("shortlist")) 
            propList = getGearEnv().getGearInstanceParameter("shortListDisplayPropertyNames");
        else if (getDisplayType().equals("fulllist")) 
            propList = getGearEnv().getGearInstanceParameter("fullListDisplayPropertyNames");
        else if (getDisplayType().equals("featuredItem")) 
            propList = getGearEnv().getGearInstanceParameter("featuredItemDisplayPropertyNames");
        else
            propList = getGearEnv().getGearInstanceParameter("detailDisplayPropertyNames");
        
        // if the prop list is not specified, let it be "ALL"        
        if (StringUtils.isBlank(propList))
            propList = "ALL";

        if (propList.equalsIgnoreCase("ALL"))
            return getItemDescriptor().getPropertyNames();        
        else
            return StringUtils.splitStringAtCharacter(StringUtils.normalizeWhitespace(propList.trim()),' ');
    }

    /**
     *	doStartTag method
     *	@return int
     */
    public int doStartTag()
    {
      pageContext.setAttribute(getId(), this);
        return EVAL_BODY_INCLUDE;
    } //end doStartTag() method

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
        setGearEnv( null );
        setDisplayType( null );
        setItem( null );
        setItemDescriptorName( null );
    }

} //end class DocExchPageTag

