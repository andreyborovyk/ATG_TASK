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

package atg.portal.gear.docexch.taglib;

import atg.beans.*;
import atg.portal.framework.*;
import atg.repository.*;
import atg.core.util.StringUtils;
import atg.portal.nucleus.NucleusComponents;

import java.sql.Timestamp;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * This class is the main part of the tag library.  It is a useful wrapper
 * around the various options provided by the Portal Framework.
 *
 * @author Portal Team
 * @version $Id: //app/portal/version/10.0.3/docexch/docexchTaglib.jar/src/atg/portal/gear/docexch/taglib/DocExchPageTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DocExchPageTag extends TagSupport
{
    public static String CLASS_VERSION = 
    "$Id: //app/portal/version/10.0.3/docexch/docexchTaglib.jar/src/atg/portal/gear/docexch/taglib/DocExchPageTag.java#2 $$Change: 651448 $";

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


    /** color for highlight text if none is set in the page's color palette */
    String mDefaultHighlightTextColor = "000000";

    //-------------------------------------
    /**
     * Sets color for highlight text if none is set in the page's color palette
     **/
    public void setDefaultHighlightTextColor(String pDefaultHighlightTextColor) {
        mDefaultHighlightTextColor = pDefaultHighlightTextColor;
    }

    //-------------------------------------
    /**
     * Returns color for highlight text if none is set in the page's color palette
     **/
    public String getDefaultHighlightTextColor() {
        return mDefaultHighlightTextColor;
    }

    
    /** color for highlight Background if none is set in the page's color palette */
    String mDefaultHighlightBGColor = "cccccc";

    //-------------------------------------
    /**
     * Sets color for highlight Background if none is set in the page's color palette
     **/
    public void setDefaultHighlightBGColor(String pDefaultHighlightBGColor) {
        mDefaultHighlightBGColor = pDefaultHighlightBGColor;
    }

    //-------------------------------------
    /**
     * Returns color for highlight Background if none is set in the page's color palette
     **/
    public String getDefaultHighlightBGColor() {
        return mDefaultHighlightBGColor;
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
	

    /** the color palette for the gear */
    ColorPalette mColorPalette;

    //-------------------------------------
    /**
     * Sets the color palette for the gear
     **/
    public void setColorPalette(ColorPalette pColorPalette) {
        mColorPalette = pColorPalette;
    }

    //-------------------------------------
    /**
     * Returns the color palette for the gear
     **/
    public ColorPalette getColorPalette() {
        if (mColorPalette == null)
            mColorPalette = getGearEnv().getPage().getColorPalette();
        return mColorPalette;
    }

    /** color for highlight text */
    String mHighlightTextColor;

    //-------------------------------------
    /**
     * Sets color for highlight text
     **/
    public void setHighlightTextColor(String pHighlightTextColor) {
        mHighlightTextColor = pHighlightTextColor;
    }

    //-------------------------------------
    /**
     * Returns color for highlight text
     **/
    public String getHighlightTextColor() {
        if (mHighlightTextColor == null) {
            mHighlightTextColor = getColorPalette().getHighlightTextColor();
            if (mHighlightTextColor == null || mHighlightTextColor.trim().equals(""))
                mHighlightTextColor = getDefaultHighlightTextColor();
        }
        return mHighlightTextColor;
    }


    /** color for highlight background */
    String mHighlightBGColor;

    //-------------------------------------
    /**
     * Sets color for highlight background
     **/
    public void setHighlightBGColor(String pHighlightBGColor) {
        mHighlightBGColor = pHighlightBGColor;
    }

    //-------------------------------------
    /**
     * Returns color for highlight background
     **/
    public String getHighlightBGColor() {
        if (mHighlightBGColor == null) {
            mHighlightBGColor = getColorPalette().getHighlightBackgroundColor();
            if (mHighlightBGColor == null || mHighlightBGColor.trim().equals(""))
                mHighlightBGColor = getDefaultHighlightBGColor();
        }
        return mHighlightBGColor;
    }

    // Gear parameters related to the repository.
    public String getRepositoryPath() {
        return getGearEnv().getGearInstanceParameter("repositoryPath");
    }
    public String getItemDescriptorName() {
        return getGearEnv().getGearInstanceParameter("itemDescriptorName");
    }
    public String getFileDataPropertyName() {
        return getGearEnv().getGearInstanceParameter("fileDataPropertyName");
    }
    public String getMimeTypePropertyName() {
        return getGearEnv().getGearInstanceParameter("mimeTypePropertyName");
    }
    public String getFileSizePropertyName() {
        return getGearEnv().getGearInstanceParameter("fileSizePropertyName");
    }
    public String getFilenamePropertyName() {
        return getGearEnv().getGearInstanceParameter("filenamePropertyName");
    }   
    public String getGearIdPropertyName() {
        return getGearEnv().getGearInstanceParameter("gearIdPropertyName");
    }
    public String getAnnotationRefPropertyName() {
        return getGearEnv().getGearInstanceParameter("annotationRefPropertyName");
    }
    public String getTitlePropertyName() {
        return getGearEnv().getGearInstanceParameter("titlePropertyName");
    }

    public String getCreateDatePropertyName() {
        return getGearEnv().getGearInstanceParameter("createDatePropertyName");
    }
    public String getDescriptionPropertyName() {
        return getGearEnv().getGearInstanceParameter("descriptionPropertyName");
    }    
    public String getStatusPropertyName() {
        return getGearEnv().getGearInstanceParameter("statusPropertyName");
    }
    public String getAuthorPropertyName() {
        return getGearEnv().getGearInstanceParameter("authorPropertyName");
    }    
    public String getAuthorDisplayProp1() { 
        return getGearEnv().getGearInstanceParameter("authorDisplayProp1");
    }
    public String getAuthorDisplayProp2() { 
        return getGearEnv().getGearInstanceParameter("authorDisplayProp2");
    }
    public String getAuthorFirstNameCombinedProp() {
        return new StringBuffer(getAuthorPropertyName())
                   .append(".")
                   .append(getAuthorDisplayProp1())
                   .toString();
    }
    public String getAuthorLastNameCombinedProp() {
        return new StringBuffer(getAuthorPropertyName())        
                   .append(".")
                   .append(getAuthorDisplayProp2())
                   .toString();
    }

    public String getDocumentId(Object pBean) { 
        RepositoryItem item = (RepositoryItem)pBean;
        return item.getRepositoryId();
    }

    public String getTitle(Object pBean) { 
        String val = (String)getPropertyFromBean(pBean, getTitlePropertyName());
        if (val == null)
            return "";
        return val;
    }

    public String getDescription(Object pBean) { 
        String val = (String)getPropertyFromBean(pBean, getDescriptionPropertyName());
        if (val == null)
            return "";
        return val;
    }

    public RepositoryItem getAuthor(Object pBean) { 
        return (RepositoryItem)getPropertyFromBean(pBean, getAuthorPropertyName());
     }

    public Date getCreateDate(Object pBean) { 
        return (Date)getPropertyFromBean(pBean, getCreateDatePropertyName());
    }

    public String getStatus(Object pBean) { 
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(getResourceBundle());
        Integer status = (Integer)getPropertyFromBean(pBean, getStatusPropertyName());
        if (status == null)
            return "";
        String key = "statusChoice" + status.toString();
        String statusString = bundle.getString(key);
        return statusString;
    }

    public String getFilename(Object pBean) { 
        return (String)getPropertyFromBean(pBean, getFilenamePropertyName());
    }

    public String getDiscussionId(Object pBean) { 
        return (String)getPropertyFromBean(pBean, getAnnotationRefPropertyName());
    }

    public String getAuthorFirstName(Object pBean) {
        if (getAuthor(pBean)==null)
          return null;
        return (String)getPropertyFromBean(getAuthor(pBean), getAuthorDisplayProp1());
    }

    public String getAuthorLastName(Object pBean) {
        if (getAuthor(pBean)==null)
          return null;
        return (String)getPropertyFromBean(getAuthor(pBean), getAuthorDisplayProp2());
    }

    public String getResourceBundle() {
        return getGearEnv().getGearInstanceParameter("resourceBundle");
    }
    public String getDiscResourceBundle() {
        return getGearEnv().getGearInstanceParameter("discResourceBundle");
    }
    public String getDateStyle() {
        return getGearEnv().getGearInstanceParameter("dateStyle");
    }
    public String getTimeStyle() {
        return getGearEnv().getGearInstanceParameter("timeStyle");
    }
 
    // Conditional display variables.  These determine what gets shown to the user.
    public String getDisplayColumnHeaders() { 
        return getGearEnv().getGearInstanceParameter("displayColumnHeaders");
    }
    public String getDisplayTitle() { 
        return getGearEnv().getGearInstanceParameter("displayTitle");
    }   
    public String getDisplayAuthor() { 
        return getGearEnv().getGearInstanceParameter("displayAuthor");
    }
    public String getDisplayDescription() { 
        return getGearEnv().getGearInstanceParameter("displayDescription");
    }
    public String getDisplayCreateDate() { 
        return getGearEnv().getGearInstanceParameter("displayCreateDate");
    }
    public String getDisplayStatus() { 
        return getGearEnv().getGearInstanceParameter("displayStatus");
    }      
    public String getAttachmentRequired() {
        return getGearEnv().getGearInstanceParameter("attachmentRequired");
    }
    public String getEnableSearch() {
        return getGearEnv().getGearInstanceParameter("enableSearch");
    }
    public String getEnableDiscussion() {
        return getGearEnv().getGearInstanceParameter("enableDiscussion");
    }
    public String getReadOnly() {
        return getGearEnv().getGearInstanceParameter("readOnly");
    }
    public String getMaxFileSize() {
        return getGearEnv().getGearInstanceParameter("maxFileSize");
    }
    public String getTitleMaxLength() {
        return getPropertyMaxLength(getTitlePropertyName());
    }
    public String getDescriptionMaxLength() {
        return getPropertyMaxLength(getDescriptionPropertyName());
    }

    /** the repository item descriptor for a document */
    RepositoryItemDescriptor mItemDesc;

    //-------------------------------------
    /**
     * Sets the repository item descriptor for a document
     **/
    public void setItemDesc(RepositoryItemDescriptor pItemDesc) {
        mItemDesc = pItemDesc;
    }

    //-------------------------------------
    /**
     * Returns the repository item descriptor for a document
     **/
    public RepositoryItemDescriptor getItemDesc() {
        if (mItemDesc == null)
            initItemDesc();
        return mItemDesc;
    }

    private void initItemDesc() {
        try {
            String reppath = new StringBuffer("dynamo:").append(getRepositoryPath()).toString();
            Repository rep = (Repository) NucleusComponents.lookup(reppath);
            setItemDesc(rep.getItemDescriptor(getItemDescriptorName()));
        }
        catch (javax.naming.NamingException e) {
            pageContext.getServletContext().log(" DocExch Gear: Unable to get docexch repository");
        }
        catch(RepositoryException e) {
            pageContext.getServletContext().log(" DocExch Gear: Unable to get docexch repository");
        }
    }
 
    private String getPropertyMaxLength(String pPropName) {
        DynamicPropertyDescriptor pd = getItemDesc().getPropertyDescriptor(pPropName);
        Object maxLengthObject =  pd.getValue("maxLength");
        if (maxLengthObject != null)
            return maxLengthObject.toString(); 
        return null;
    }

    //----------------------------------------------
    // permissions
    /** map permission settings to resource keys for display */
    HashMap mPermissionMap;

    //-------------------------------------
    /**
     * Sets map permission settings to resource keys for display
     **/
    public void setPermissionMap(HashMap pPermissionMap) {
        mPermissionMap = pPermissionMap;
    }

    //-------------------------------------
    /**
     * Returns map permission settings to resource keys for display
     **/
    public HashMap getPermissionMap() {
        if (mPermissionMap == null)
            initializePermissionMap();
        return mPermissionMap;
    }

    void initializePermissionMap() {
        mPermissionMap = new HashMap();
        mPermissionMap.put("anyone", "anyoneLabel");
        mPermissionMap.put("anyRegUser", "anyRegUserLabel");
        mPermissionMap.put("guest", "guestLabel");
        mPermissionMap.put("member", "memberLabel");        
        // leader and admin are synonymous.
        mPermissionMap.put("administrator", "adminLabel");
        mPermissionMap.put("leader", "adminLabel");
    }
    
    protected String getPermissionKey(String pPermissionRole) {
        String defaultRole = "guest";
	String permissionKey = null;
        Map permissionMap = getPermissionMap();
        if (StringUtils.isBlank(pPermissionRole)) {
            // XXX Should be i18n
            String msg = "***ERROR**** DocExchPageTag: permissionRole is null," 
                         + " defaulting to " + defaultRole;
            System.out.println(msg);
            permissionKey = (String) permissionMap.get(defaultRole);
            return permissionKey;
        }
        
        permissionKey = (String) permissionMap.get(pPermissionRole);
        if (StringUtils.isBlank(permissionKey)) {
            String msg = "***ERROR**** DocExchPageTag: role = [" 
                         + pPermissionRole + "] key = [" 
                         + permissionKey + "] defaulting to " + defaultRole;
            System.out.println(msg);
            permissionKey = (String) permissionMap.get(defaultRole);
        }
        return permissionKey;
    }   
   
    public String getWritePermissionRole() {
        return getGearEnv().getGearInstanceParameter("writePermissionRole");
    }
    public String getWritePermissionRoleKey() {

        return getPermissionKey(getWritePermissionRole());
    }
    public String getDiscussPermissionRole() {
        return getGearEnv().getGearInstanceParameter("discussPermissionRole");
    }
    public String getDiscussPermissionRoleKey() {
        return getPermissionKey(getDiscussPermissionRole());
    }

    public String getUpdateStatusPermissionRole() {
        return getGearEnv().getGearInstanceParameter("updateStatusPermissionRole");
    }
    public String getUpdateStatusPermissionRoleKey() {
        return getPermissionKey(getUpdateStatusPermissionRole());
    }

    //-----------------------------------------------
    // User parameters
    public String getShortListSize() {
        return getGearEnv().getGearUserParameter("shortListSize");
    }
    public String getFullListPageSize() {
        return getGearEnv().getGearUserParameter("fullListPageSize");
    }

    //-----------------------------------------------
    // private methods
    Object getPropertyFromBean(Object pBean, String pPropertyName) {
        try {
            return DynamicBeans.getPropertyValue(pBean, pPropertyName);
        }
        catch (PropertyNotFoundException e) {
            // XXX should be i18n
            System.out.println("***ERROR**** DocExchPageTag.java: " + e.getMessage());
            return null;
        }
    }

    Object getSubPropertyFromBean(Object pBean, String pPropertyName) {
        try {
            return DynamicBeans.getSubPropertyValue(pBean, pPropertyName);
        }
        catch (PropertyNotFoundException e) {
            // XXX Should be i18n
            System.out.println("***ERROR**** DocExchPageTag.java: " + e.getMessage());
            return null;
        }
    }

    public String getServletContext() {
        if (getGearEnv().getGear() != null)
            return getGearEnv().getGear().getServletContext();
        else if (getGearEnv().getGearDefinition() != null)
            return getGearEnv().getGearDefinition().getServletContext();
        else return null;
    }

    /**
     * creates a url relative to the servlet context of this gear
     */
    public String getRelativeUrl(String pUrl) {
        return new StringBuffer(getServletContext()).append(pUrl).toString(); 
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
        setHighlightTextColor( null );
        setHighlightBGColor( null );
    }
    
} //end class DocExchPageTag

