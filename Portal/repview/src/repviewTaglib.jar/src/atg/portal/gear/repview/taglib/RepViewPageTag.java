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
import atg.core.util.StringUtils;
import atg.portal.framework.*;
import atg.portal.nucleus.NucleusComponents;
import atg.repository.*;
import atg.targeting.DynamicContentTargeter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/RepViewPageTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class RepViewPageTag extends TagSupport
{

    public static String CLASS_VERSION = 
	"$Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/RepViewPageTag.java#2 $$Change: 651448 $";

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

    // Gear parameters
    String getGearInstanceParameter(String pParamName) {        
        String val = (getGearEnv().getGear() != null) ?
            getGearEnv().getGearInstanceParameter(pParamName) :
            getGearEnv().getGearInstanceDefaultValue(pParamName);
        if (StringUtils.isBlank(val))
            return null;
        return val;
    }
    String getGearUserParameter(String pParamName) {
        String val = (getGearEnv().getGear() != null) ?
            getGearEnv().getGearUserParameter(pParamName) :
            getGearEnv().getGearUserDefaultValue(pParamName);
        if (StringUtils.isBlank(val))
            return null;
        return val;
    }
    public String getRepositoryPath() {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String reppath = (String)request.getParameter("reppath");
        if (StringUtils.isBlank(reppath))
            return getGearInstanceParameter("repositoryPath");
        else 
            return reppath;
    }
    public String getItemDescriptorName() {
        return getGearInstanceParameter("itemDescriptorName");
    }
    public String getResourceBundle() {
        return getGearInstanceParameter("resourceBundle");
    }
    public String getDateStyle() {
        return getGearInstanceParameter("dateStyle");
    }
    public String getTimeStyle() {
        return getGearInstanceParameter("timeStyle");
    }
    public String getShortListDisplayPropertyNames() {
        return getGearInstanceParameter("shortListDisplayPropertyNames");
    }
    public String getFullListDisplayPropertyNames() {
        return getGearInstanceParameter("fullListDisplayPropertyNames");
    }
    public String getDetailDisplayPropertyNames() {
        return getGearInstanceParameter("detailDisplayPropertyNames");
    }
    public String getShortListTargeter() {
        return getGearInstanceParameter("shortListTargeter");
    }
    public String getFullListTargeter() {
        return getGearInstanceParameter("fullListTargeter");
    }
    public String getFeaturedItemTargeter() {
        return getGearInstanceParameter("featuredItemTargeter");
    }
    public String getRandomizeFeaturedItem() {
        return getGearInstanceParameter("randomizeFeaturedItem");
    }
    public String getCustomItemDisplayPage() {
        return getGearInstanceParameter("customItemDisplayPage");
    }
    public String getCustomFeaturedItemDisplayPage() {
        return getGearInstanceParameter("customFeaturedItemDisplayPage");
    }
    public String getDisplayShortList() {
        return getGearInstanceParameter("displayShortList");
    }        
    public String getDisplayFeaturedItem() {
        return getGearInstanceParameter("displayFeaturedItem");
    }        
    public String getDisplayMoreItemsLink() {
        return getGearInstanceParameter("displayMoreItemsLink");
    }
    public String getDisplayColumnHeaders() {
        return getGearInstanceParameter("displayColumnHeaders");
    }
    public String getDisplayMainItemLink() {
        return getGearInstanceParameter("displayMainItemLink");
    }
    public String getDelegateConfig() {
        return getGearInstanceParameter("delegateConfig");
    }

    //-----------------------------------------------
    // User parameters
    public String getShortListSize() {
        return getGearUserParameter("shortListSize");
    }
    public String getFullListSize() {
        return getGearUserParameter("fullListSize");
    }
    public String getShortListSortProperty() {
        return getGearUserParameter("shortListSortProperty");
    }
    public String getShortListReverseSort() {
        return getGearUserParameter("shortListReverseSort");
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

    //-------------------------------------
    /**
     * Returns the repository
     **/
    public Repository getRepository() {
        try {
            return (Repository) NucleusComponents.lookup("dynamo:" + getRepositoryPath());
        }
        catch (javax.naming.NamingException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to get repository");
        }
        return null;
    }

    //-------------------------------------
    /**
     * returns the item descriptor, initializing it if necessary.
     **/
    public RepositoryItemDescriptor getBaseItemDescriptor() {
        try {
            return getRepository().getItemDescriptor(getItemDescriptorName());
        }
        catch (RepositoryException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to get ItemDescriptor");
        }
        return null;
    }

    //-------------------------------------
    /**
     * returns the list of properties of the base item descriptor
     **/
    public String [] getBasePropertyList() {
        return getBaseItemDescriptor().getPropertyNames();
    }

    //-------------------------------------
    /**
     * Because the configuration of this gear is confusing and distributed, it is 
     * easy to get the gear into an inconsistent state (e.g. you can set the repository
     * to a new value after setting the targeters and property lists, so now the targeters
     * and property lists no longer belong to the gear's repository).  The job of this 
     * method is to check that all the configuration parameters agree and to unset those
     * values that do not agree. Sure, it's a side effect, but it all happens in this 
     * one place.
     */
    public void validateGearConfig() {
        // get the repository, itemtype, property list
        Repository rep = getRepository();
        String itemdescname = getItemDescriptorName();
        RepositoryItemDescriptor itemdesc = getBaseItemDescriptor();

        // validate all the targeters
        validateTargeter("shortListTargeter", rep, itemdescname);
        validateTargeter("fullListTargeter", rep, itemdescname);
        validateTargeter("featuredItemTargeter", rep, itemdescname);

        // validate all the property lists
        validatePropertyList("shortListDisplayPropertyNames", itemdesc);
        validatePropertyList("fullListDisplayPropertyNames", itemdesc);
        validatePropertyList("detailDisplayPropertyNames", itemdesc);
        validatePropertyList("featuredItemDisplayPropertyNames", itemdesc);      
    }
     
    
    /**
     * if the property list is not related to the given repository and item desc, then 
     * remove it.
     */
    public void validatePropertyList(String pParamName, RepositoryItemDescriptor pItemDesc) {
        String propList = getGearInstanceParameter(pParamName);
        if (propList.equalsIgnoreCase("ALL"))
            return;
        String [] props = StringUtils.splitStringAtCharacter(StringUtils.normalizeWhitespace(propList.trim()),' ');
        for (int ii=0; ii<props.length; ii++) {
            if (!pItemDesc.hasProperty(props[ii])) {
                if (getGearEnv().getGear() != null)
                    getGearEnv().setGearInstanceParameter(pParamName, "ALL");
                getGearEnv().setGearInstanceDefaultValue(pParamName, "ALL");
                return;
            }
        }
    }

    /**
     * if the targeter is not related to the given repository and item desc, then 
     * remove it.
     */
    public void validateTargeter(String pParamName, Repository pRep, String pItemDescName) {
        try {
            String targetername = getGearInstanceParameter(pParamName);
            if (targetername != null) {
                DynamicContentTargeter targ = (DynamicContentTargeter) NucleusComponents.lookup("dynamo:" + targetername);
                if (targ != null &&
                    targ.getRepository().getRepositoryName().equals(pRep.getRepositoryName()) &&
                    targ.getRepositoryView().getItemDescriptor().getItemDescriptorName().equals(pItemDescName)) {
                }
                else {
                    if (getGearEnv().getGear() != null)
                        getGearEnv().setGearInstanceParameter(pParamName, "");
                    getGearEnv().setGearInstanceDefaultValue(pParamName, "");
                }
            }
        }
        catch (RepositoryException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to validate targeters due to repository exception");
        }
        catch (javax.naming.NamingException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to validate targeters due to naming exception");
        }

    }


    //-------------------------------------
    /**
     * Returns the list of property names that need to be configured.  
     **/
    public ArrayList findUnsetRequiredParams() {
        ArrayList unsetParams = new ArrayList();

        if (Boolean.valueOf(getDisplayShortList()).booleanValue() && 
            StringUtils.isBlank(getShortListTargeter()))
          unsetParams.add("shortListTargeter");

        if (Boolean.valueOf(getDisplayMoreItemsLink()).booleanValue() &&
            StringUtils.isBlank(getFullListTargeter()))
           unsetParams.add("fullListTargeter");

        if (Boolean.valueOf(getDisplayFeaturedItem()).booleanValue() &&
            StringUtils.isBlank(getFeaturedItemTargeter()))
          unsetParams.add("featuredItemTargeter");

        /*
        System.out.println("\n\n\n");
        System.out.println("display short list (string) = " + getDisplayShortList());
        System.out.println("display short list (boolean) = " + Boolean.valueOf(getDisplayShortList()).booleanValue());
        System.out.println("shortListTargeter is " + getShortListTargeter());
        System.out.println("is it blank? = " + StringUtils.isBlank(getShortListTargeter()));
        
        System.out.println("display more items link (string)= " + getDisplayMoreItemsLink());
        System.out.println("display more items link (boolean) = " + Boolean.valueOf(getDisplayMoreItemsLink()).booleanValue());
        System.out.println("fullListTargeter = " + getFullListTargeter());
        System.out.println("is it blank? = " + StringUtils.isBlank(getFullListTargeter()));
        
        System.out.println("display featured item (string) = " + getDisplayFeaturedItem());
        System.out.println("display featured item (boolean) = " + Boolean.valueOf(getDisplayFeaturedItem()).booleanValue());
        System.out.println("fullListTargeter = " + getFeaturedItemTargeter());
        System.out.println("is it blank? = " + StringUtils.isBlank(getFeaturedItemTargeter()));
        

        System.out.println("\n\n\n in findUnsetRequiredParams.  size = " + unsetParams.size() + "\n\n\n");
        */

        return unsetParams;
    }

  public boolean isConfigComplete() {
    String repname = getRepositoryPath();
    String itemdescname = getItemDescriptorName();
    
    if((repname == null) ||
       (itemdescname == null))
      return false;
    
    Repository rep = getRepository();
    if(rep == null)
      return false;
    RepositoryItemDescriptor itemdesc = getBaseItemDescriptor();
    if(itemdesc == null)
      return false;
  
    validateGearConfig();
    return findUnsetRequiredParams().isEmpty();
  }
    


    //-------------------------------------
    // Add these if we implement custom prop renderers

    /**
     * Returns the map of custom property renderers 
    public Map getCustomPropertyRenderers() {
    Map mCustomPropertyRenderers;
    boolean mCustomPropertyRenderersInitialized = false;

        if (!mCustomPropertyRenderersInitialized) {
            initCustomPropertyRenderers();           
            if (mCustomPropertyRenderers == null)
                mCustomPropertyRenderers = new HashMap();
            mCustomPropertyRenderersInitialized=true;
        }
        return mCustomPropertyRenderers;
    }

    void initCustomPropertyRenderers() {
        mCustomPropertyRenderers = stringToMap(getGearInstanceParameter("customPropertyRenderers"));
    }

    public void setCustomPropertyRenderers(Map pVal) {
        mCustomPropertyRenderers = pVal;
    }


    Map stringToMap (String s) {
        
      if (s == null || s.trim().length() == 0)
          return null;
      List vals = new ArrayList(), keys = new ArrayList();

      vals = Arrays.asList(
                StringUtils.splitStringAtCharacterWithQuoting(s, ','));
      ListIterator iter = vals.listIterator();
      while (iter.hasNext()) {
          String tmp = (String)iter.next();
          int ix = tmp.indexOf('=');
          if (ix == -1) {
              // i18n me!
              pageContext.getServletContext().log("Not a valid map" + s);
              return null;
          }
          keys.add(tmp.substring(0,ix));
          vals.add(tmp.substring(ix+1));
          iter.set(tmp.substring(ix+1));
      }
      
      Map map = new HashMap();
      for (int i = 0; i < keys.size(); i++)
          map.put(keys.get(i), vals.get(i));
      return map;
    }
     **/


    
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

        // Add these if we implement custom prop renderers
        //setCustomPropertyRenderers( null );
        //mCustomPropertyRenderersInitialized = false;

    }
    
} //end class

