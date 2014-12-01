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
 </ATGCOPYRIGHT>*/ 

package atg.portal.gear.soapclient.taglib;

// Java classes
import javax.servlet.jsp.tagext.TagSupport;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import javax.servlet.jsp.*;
import javax.naming.*;

// PAF classes
import atg.portal.nucleus.NucleusComponents;
import atg.portal.framework.GearEnvironment;
import atg.portal.gear.soapclient.*;

// DAS classes
import atg.core.util.StringUtils;
import atg.service.soap.*;
import atg.service.wsdl.*;
import atg.core.util.ResourceUtils;
import atg.core.util.BeanUtils;
import atg.repository.*;
import atg.servlet.ServletUtil;
import atg.core.exception.*;


/**
 * @version $Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/taglib/GetInstalledWSDLValuesTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetInstalledWSDLValuesTag extends TagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/taglib/GetInstalledWSDLValuesTag.java#2 $$Change: 651448 $";

    
    private static SOAPRepositoryUtils sRepositoryUtils;
    private static SOAPPropertyManager sPropMgr;

    //---------------------------------------------------------------------
    // property: gearDefId
    String mGearDefId;
    
    /**
     * Return the gearDefId property.
     * @return
     */
    public String getGearDefId() {
	return mGearDefId;
    }

    /**
     * Set the gearDefId property.
     * @param pGearDefId
     */
    public void setGearDefId(String pGearDefId) {
	mGearDefId = pGearDefId;
    }
    
    //---------------------------------------------------------------------
    // property: targetServiceURL
    String mTargetServiceURL;
    
    /**
     * Return the targetServiceURL property.
     * @return
     */
    public String getTargetServiceURL() {
	return mTargetServiceURL;
    }

    /**
     * Set the targetServiceURL property.
     * @param pTargetServiceURL
     */
    public void setTargetServiceURL(String pTargetServiceURL) {
	mTargetServiceURL = pTargetServiceURL;
    }

    //---------------------------------------------------------------------
    // property: targetMethodName
    String mTargetMethodName;
    
    /**
     * Return the targetMethodName property.
     * @return
     */
    public String getTargetMethodName() {
	return mTargetMethodName;
    }

    /**
     * Set the targetMethodName property.
     * @param pTargetMethodName
     */
    public void setTargetMethodName(String pTargetMethodName) {
	mTargetMethodName = pTargetMethodName;
    }

    //---------------------------------------------------------------------
    // property: targetNameSpaceURI
    String mTargetNameSpaceURI;
    
    /**
     * Return the targetNameSpaceURI property.
     * @return
     */
    public String getTargetNameSpaceURI() {
	return mTargetNameSpaceURI;
    }

    /**
     * Set the targetNameSpaceURI property.
     * @param pTargetNameSpaceURI
     */
    public void setTargetNameSpaceURI(String pTargetNameSpaceURI) {
	mTargetNameSpaceURI = pTargetNameSpaceURI;
    }
    
    
    //---------------------------------------------------------------------
    // property: soapActionURI
    String mSOAPActionURI;
    
    /**
     * Return the soapActionURI property.
     * @return
     */
    public String getSOAPActionURI() {
	return mSOAPActionURI;
    }

    /**
     * Set the SOAPActionURI property.
     * @param pSOAPActionURI
     */
    public void setSOAPActionURI(String pSOAPActionURI) {
	mSOAPActionURI = pSOAPActionURI;
    }
    
    
    boolean mWSDLLoaded;
    public boolean isWSDLLoaded() {
	return mWSDLLoaded;
    }
    
    
    private RepositoryItem mInstallServiceConfigItem;
    
    

    static {
	try {
	    
	    
	    sRepositoryUtils = (SOAPRepositoryUtils) 
		NucleusComponents.lookup(InvokeSOAPServiceTag.REPOSITORY_UTILS_JNDI_NAME);
	    
	    sPropMgr = (SOAPPropertyManager) 
		NucleusComponents.lookup(InvokeSOAPServiceTag.PROP_MGR_JNDI_NAME);
	}
	catch (NamingException ne) {
	    
	    ne.printStackTrace();
	}
    }


    public int doStartTag() throws JspException {

	mWSDLLoaded = setInstallServiceConfigItem();
	if (mWSDLLoaded) {
	    setTargetServiceURL(getInstallParamValueOf(sPropMgr.getTargetURLPropertyName()));
	    setTargetMethodName(getInstallParamValueOf(sPropMgr.getTargetMethodNamePropertyName()));
	    setTargetNameSpaceURI(getInstallParamValueOf(sPropMgr.getTargetNamespacePropertyName()));
	    setSOAPActionURI(getInstallParamValueOf(sPropMgr.getSOAPActionURIPropertyName()));
	}

	 pageContext.setAttribute(getId(), this);
	 return EVAL_BODY_INCLUDE;
	
    }

    protected String getInstallParamValueOf(String pPropertyName) {
	
	RepositoryItem serviceConfigItem = null;

	if (mInstallServiceConfigItem != null) {
	    serviceConfigItem = (RepositoryItem) mInstallServiceConfigItem.getPropertyValue(sPropMgr.getServiceConfigPropertyName());		
	}
	
	
	if ( serviceConfigItem != null) {
	    String value = (String) serviceConfigItem.getPropertyValue(pPropertyName);
	    return value;
	}
	return null;
    }

    protected boolean setInstallServiceConfigItem() {
	RepositoryItem installServiceConfigItem = null;
	

	try {
	    installServiceConfigItem = 
		sRepositoryUtils.getInstallServiceConfig(getGearDefId());		}
	catch (RepositoryException re) {
	    System.out.println(re.getMessage());
	}

	mInstallServiceConfigItem = installServiceConfigItem;

	if (mInstallServiceConfigItem != null) {
	    return true;
	}

	return false;
    }
	
    
    
    public void release() {
	mGearDefId = null;
	mTargetServiceURL = null;
	mTargetNameSpaceURI = null;
	mTargetMethodName = null;
	mSOAPActionURI = null;
    }

}    
