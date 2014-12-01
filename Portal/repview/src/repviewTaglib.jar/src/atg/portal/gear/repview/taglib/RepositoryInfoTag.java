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

import atg.adapter.gsa.*;
import atg.nucleus.Nucleus;
import atg.nucleus.GenericService;
import atg.nucleus.GenericContext;
import atg.portal.framework.*;
import atg.portal.nucleus.NucleusComponents;
import atg.repository.*;
import atg.repository.nucleus.RepositoryMultiContainerService;
import atg.repository.nucleus.RepositoryContainer;
import atg.servlet.ServletUtil;
import atg.targeting.DynamicContentTargeter;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/RepositoryInfoTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class RepositoryInfoTag extends TagSupport
{

    public static String CLASS_VERSION = 
	"$Id: //app/portal/version/10.0.3/repview/repviewTaglib.jar/src/atg/portal/gear/repview/taglib/RepositoryInfoTag.java#2 $$Change: 651448 $";


    public static final String TARGETER_REGISTRY_NUCLEUS_PATH="/atg/registry/RepositoryTargeters/";

    //----------------------------------------------------
    // "input" properties
    //----------------------------------------------------


    /**  the nucleus path to the repository whose item descriptor names we want to get */
    String mRepositoryPath;

    //-------------------------------------
    /**
     * Sets  the nucleus path to the repository whose item descriptor names we want to get
     **/
    public void setRepositoryPath(String pRepositoryPath) {
        mRepositoryPath = pRepositoryPath;
    }

    //-------------------------------------
    /**
     * Returns  the nucleus path to the repository whose item descriptor names we want to get
     **/
    public String getRepositoryPath() {
        return mRepositoryPath;
    }

    /** the repository */
    Repository mRepository;

    //-------------------------------------
    /**
     * Returns the repository
     **/
    public Repository getRepository() {
        try {
          javax.naming.InitialContext ctx = new javax.naming.InitialContext();
          mRepository = (Repository) ctx.lookup("dynamo:" + getRepositoryPath());
        }
        catch (javax.naming.NamingException e) {
            pageContext.getServletContext().log(" RepView Gear: Unable to get repository");
        }
        return mRepository;
    }

    //-------------------------------------
    /**
     * Sets the repository
     **/
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }


    /**  the name of the item descriptor for which to get targeters */
    String mItemDescriptorName;

    //-------------------------------------
    /**
     * Sets  the name of the item descriptor for which to get targeters
     **/
    public void setItemDescriptorName(String pItemDescriptorName) {
        mItemDescriptorName = pItemDescriptorName;
    }

    //-------------------------------------
    /**
     * Returns  the name of the item descriptor for which to get targeters
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


    /*----------------------------------------------------
     * property: itemdescriptors
     */
    public String [] getItemDescriptors() {
      return getRepository().getItemDescriptorNames();    
    }

    /*----------------------------------------------------
     * property: targeters
     */
    public RepositoryContainer.Entry [] getTargeters() {        
        if (getRepository() == null)
            return null;
        if (getItemDescriptorName() == null)
            return null;



        boolean isDefaultItemDesc = false;
        if (getRepository().getDefaultViewName().equals(getItemDescriptorName()))
          isDefaultItemDesc = true;
        
        RepositoryMultiContainerService targeterRegistry = null;
        Nucleus n = Nucleus.getGlobalNucleus();
        Object obj = n.resolveName(TARGETER_REGISTRY_NUCLEUS_PATH);
        
        if (obj instanceof RepositoryMultiContainerService)
          targeterRegistry = (RepositoryMultiContainerService) obj; 
        else 
          System.out.println("\n\n\n" + obj.getClass().getName() + "\n\n\n");
                
        if (targeterRegistry == null)
          return null;
        if (getItemDescriptorName() == null)                
          return targeterRegistry.listEntries(getRepository().getRepositoryName());
        
        return targeterRegistry.listViewEntries(getRepository().getRepositoryName(),
                                                getItemDescriptorName(),
                                                isDefaultItemDesc);
        
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
        setRepository( null );
        setRepositoryPath( null );
        setItemDescriptorName( null );
    }

} //end class GetItemDescriptorsForRepositoryTag

